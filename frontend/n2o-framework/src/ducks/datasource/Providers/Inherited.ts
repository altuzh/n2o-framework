import { put, select } from 'redux-saga/effects'
import { get, cloneDeep, set } from 'lodash'

import { dataSourceByIdSelector } from '../selectors'
import type { QueryOptions, InheritedProvider, InheritedSubmit } from '../Provider'
import type { DataSourceState } from '../DataSource'
import { makeGetModelByPrefixSelector } from '../../models/selectors'
import { setModel } from '../../models/store'
import { State } from '../../State'

import { applyFilter } from './storage/applyFilter'
import { applySorting } from './storage/applySorting'
import { applyPaging } from './storage/applyPaging'

export function* submit(id: string, {
    model: prefix,
    targetDs: tagetId,
    targetModel: targetPrefix,
    targetField,
}: InheritedSubmit) {
    const model: object = yield select(makeGetModelByPrefixSelector(prefix, id))
    let data

    if (targetField) {
        const targetModel: object | void = yield select(makeGetModelByPrefixSelector(targetPrefix, tagetId))

        data = cloneDeep(targetModel) || {}
        set(data, targetField, cloneDeep(model))
    } else {
        data = cloneDeep(model)
    }

    yield put(setModel(targetPrefix, tagetId, data))
}

export function* query(id: string, {
    sourceDs: datasourceId,
    sourceModel: prefix,
    sourceField,
    filters,
}: InheritedProvider, options: QueryOptions) {
    const datasource: DataSourceState = yield select(dataSourceByIdSelector(id))
    const { sorting, paging: { size, page } } = datasource

    const model: object | void = yield select(makeGetModelByPrefixSelector(prefix, datasourceId))
    const data = cloneDeep(sourceField ? get(model, sourceField) : model)

    if (!data) {
        return {
            list: [],
            paging: {
                page: 1,
                count: 0,
            },
        }
    }

    const state: State = yield select()
    const filtered: object[] = applyFilter(
        state,
        Array.isArray(data) ? data : [data],
        filters,
    )
    const sortered = applySorting(filtered, sorting)
    const { list, paging } = applyPaging(
        sortered,
        {
            size,
            page: typeof options.page === 'undefined' ? page : options.page,
        },
    )

    return {
        list,
        paging: {
            page: paging.page,
            count: filtered.length,
        },
    }
}
