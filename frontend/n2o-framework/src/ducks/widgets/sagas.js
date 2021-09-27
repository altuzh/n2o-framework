import { call, fork, put, select, takeEvery } from 'redux-saga/effects'
import isEmpty from 'lodash/isEmpty'
import isEqual from 'lodash/isEqual'
import get from 'lodash/get'
import last from 'lodash/last'
import { reset } from 'redux-form'

import { dataProviderResolver } from '../../core/dataProviderResolver'
import { PREFIXES } from '../models/constants'
import { removeModel, setModel, clearModel } from '../models/store'
import { makePageRoutesByIdSelector } from '../pages/selectors'
import { getLocation, rootPageSelector } from '../global/store'
import { makeGetModelByPrefixSelector } from '../models/selectors'
import { FETCH_WIDGET_DATA } from '../../core/api'
import { generateErrorMeta } from '../../utils/generateErrorMeta'
import { id } from '../../utils/id'
import fetchSaga from '../../sagas/fetch'

import { routesQueryMapping } from './sagas/routesQueryMapping'
import {
    makeModelIdSelector,
    makeSelectedIdSelector,
    makeWidgetByIdSelector,
    makeWidgetDataProviderSelector,
    makeWidgetPageIdSelector,
} from './selectors'
import {
    changeCountWidget,
    changePageWidget,
    dataFailWidget,
    dataSuccessWidget,
    resetWidgetState,
    setWidgetMetadata,
    dataRequestWidget,
    disableWidget,
    resolveWidget,
} from './store'
import { isActive } from './sagas/isActive'

// region fetch

const isQueryEqual = (() => {
    const lastQuery = {}

    return (id, newPath, newQuery) => {
        let equal = true
        const query = lastQuery[id]

        if (query) {
            equal = isEqual(query.path, newPath) && isEqual(query.query, newQuery)
        }
        lastQuery[id] = { path: newPath, query: { ...newQuery } }

        return equal
    }
})()

let prevSelectedId = null

/**
 * сайд-эффекты на экшен DATA_REQUEST
 */
function* dataRequest(action) {
    const {
        payload: { widgetId, options, modelId },
    } = action
    const selectedId = yield select(makeSelectedIdSelector(widgetId))
    // TODO удалить селектор, когда бекенд начнёт присылать modelId для экшонов, которые присылает в конфиге
    const model = modelId || (yield select(makeModelIdSelector(widgetId)))

    yield fork(handleFetch, model, widgetId, options, isQueryEqual, prevSelectedId)

    if (prevSelectedId !== selectedId) {
        prevSelectedId = selectedId
    }
}

/**
 * Подготовка данных
 * @param {string} widgetId
 * @param {string} modelId
 * @returns {IterableIterator<*>}
 */
export function* prepareFetch(widgetId, modelId) {
    const state = yield select()
    const location = yield select(getLocation)
    // selectors options: size, page, filters, sorting
    const widgetState = yield select(makeWidgetByIdSelector(widgetId))
    const currentPageId = (yield select(makeWidgetPageIdSelector(widgetId))) ||
        (yield select(rootPageSelector))
    const routes = yield select(makePageRoutesByIdSelector(currentPageId))
    const dataProvider = yield select(makeWidgetDataProviderSelector(widgetId))
    const currentDatasource = yield select(
        makeGetModelByPrefixSelector(PREFIXES.datasource, modelId),
    )

    return {
        state,
        location,
        widgetState,
        routes,
        dataProvider,
        currentDatasource,
    }
}

const REQUEST_CACHE_TIMEOUT = 300
const requestMap = Object.create(null)

/**
 * Обёртка над запросом за данными.
 * Нужен для предотвращения параллельных (плюс небольшая задержка = REQUEST_CACHE_TIMEOUT) запросов с одинаковыми
 * параметрами от нескольких виджетов, использующих одну и ту же модель (modelId)
 * @param {string} modelId
 * @param {{ basePath: string, baseQuery: string , headersParams: object }} provider
 */
export function* doFetch(modelId, provider) {
    const cached = requestMap[modelId]

    if (cached && isEqual(provider, cached.provider)) {
        return yield call(() => cached.request)
    }

    const { basePath, baseQuery, headersParams } = provider
    const request = (yield fork(fetchSaga, FETCH_WIDGET_DATA, {
        basePath,
        baseQuery,
        headers: headersParams,
    })).toPromise()

    requestMap[modelId] = { request, provider }
    request.then(() => {
        setTimeout(() => {
            delete requestMap[modelId]
        }, REQUEST_CACHE_TIMEOUT)
    })

    return yield call(() => request)
}

export function* afterFetch(
    data,
    modelId,
    widgetId,
    widgetState,
    currentDatasource,
) {
    yield put(changeCountWidget(widgetId, data.count))
    if (isEqual(data.list, currentDatasource) && !isEmpty(currentDatasource)) {
        yield put(setModel(PREFIXES.datasource, modelId, null))
    }
    yield put(setModel(PREFIXES.datasource, modelId, data.list))
    if (isEmpty(data.list)) {
        yield put(setModel(PREFIXES.resolve, modelId, null))
    }
    if (data.page) {
        yield put(changePageWidget(widgetId, data.page))
    }
    if (data.metadata) {
        yield put(setWidgetMetadata(widgetState.pageId, widgetId, data.metadata))
        yield put(resetWidgetState(widgetId))
    }
    yield put(dataSuccessWidget(widgetId, data))
}

export function getWithoutSelectedId(
    options,
    location,
    selectedId,
    prevSelectedId,
) {
    if (!options || isEmpty(options)) {
        return null
    }
    if (
        !location.pathname.includes(selectedId) ||
        prevSelectedId === selectedId
    ) {
        return true
    }

    return options.withoutSelectedId
}

/**
 * @param {string} modelId
 * @param {string} widgetId
 * @param options
 * @param {Function} isQueryEqual
 * @param {string} prevSelectedId
 */
export function* handleFetch(modelId, widgetId, options, isQueryEqual, prevSelectedId) {
    if (!(yield isActive(widgetId))) {
        return
    }
    try {
        const {
            state,
            location,
            widgetState,
            routes,
            dataProvider,
            currentDatasource,
        } = yield call(prepareFetch, widgetId, modelId)

        if (!dataProvider?.url) {
            yield put(dataFailWidget(widgetId))

            return
        }

        const query = {
            page: get(options, 'page', widgetState.page),
            size: widgetState.size,
            sorting: widgetState.sorting,
        }
        const resolvedProvider = yield call(
            dataProviderResolver,
            state,
            dataProvider,
            query,
            options,
        )

        const withoutSelectedId = getWithoutSelectedId(
            options,
            location,
            widgetState.selectedId,
            prevSelectedId,
        )

        if (
            isQueryEqual(
                widgetId,
                resolvedProvider.basePath,
                resolvedProvider.baseQuery,
            ) &&
            !withoutSelectedId &&
            widgetState.selectedId
        ) {
            resolvedProvider.baseQuery.selectedId = widgetState.selectedId
        }

        if (routes && routes.queryMapping) {
            yield* routesQueryMapping(state, routes, location)
        }

        const response = yield doFetch(modelId, resolvedProvider)

        yield call(
            afterFetch,
            response,
            modelId,
            widgetId,
            widgetState,
            currentDatasource,
        )
    } catch (err) {
        // eslint-disable-next-line no-console
        console.warn(`JS Error: Widget(${widgetId}) fetch saga. ${err.message}`)
        yield put(
            dataFailWidget(
                widgetId,
                err,
                err.json?.meta ||
                    {
                        meta: generateErrorMeta({
                            id: id(),
                            text: 'Произошла внутренняя ошибка',
                            stacktrace: err.stack,
                            closeButton: true,
                        }),
                    },
            ),
        )
    }
}

// endregion fetch

export function* runResolve(action) {
    const { modelId, model } = action.payload

    try {
        yield put(setModel(PREFIXES.resolve, modelId, model))
        // eslint-disable-next-line no-empty
    } catch (err) {}
}

export function* clearForm(action) {
    yield put(reset(action.payload.key))
}

export function* clearOnDisable(action) {
    const { widgetId, modelId } = action.payload

    yield put(setModel(PREFIXES.datasource, modelId, null))
    yield put(changeCountWidget(widgetId, 0))
}

const pagesHash = []

function* clearFilters(action) {
    const { widgetId } = action.payload

    if (last(pagesHash) === widgetId) {
        return
    }

    if (pagesHash.includes(widgetId)) {
        const currentPageIndex = pagesHash.indexOf(widgetId)
        const filterResetIds = pagesHash.splice(currentPageIndex + 1)

        for (let index = 0; index < filterResetIds.length; index += 1) {
            const filterResetId = filterResetIds[index]

            yield put(removeModel(PREFIXES.filter, filterResetId))
        }
    } else {
        pagesHash.push(widgetId)
    }
}

/**
 * Сайд-эффекты для виджет редюсера
 * @ignore
 */
export default () => [
    takeEvery(dataRequestWidget, dataRequest),
    takeEvery(clearModel, clearForm),
    takeEvery(resolveWidget, runResolve),
    takeEvery(disableWidget, clearOnDisable),
    takeEvery(changePageWidget, clearFilters),
]
