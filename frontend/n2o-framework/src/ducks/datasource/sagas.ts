import {
    put,
    takeEvery,
    fork,
    cancel,
    delay,
} from 'redux-saga/effects'
import type { Task } from 'redux-saga'

import { clearModel, removeAllModel, removeModel, setModel, updateMapModel, updateModel } from '../models/store'
import { EffectWrapper } from '../api/utils/effectWrapper'

import { dataRequest as query } from './sagas/query'
import { validate as validateSaga } from './sagas/validate'
import {
    changePage,
    changeSize,
    dataRequest,
    DATA_REQUEST,
    register,
    remove,
    setSorting,
    startValidate,
    submit,
} from './store'
import { applyOnInitDependencies, watchDependencies } from './sagas/dependencies'
import type { ChangePageAction, DataRequestAction, RemoveAction } from './Actions'
import { submitSaga } from './sagas/submit'
import { clear } from './Providers/Storage'

// Запуск запроса за данными при изменении мета-данных (фильтр, сортировка, страница)
export function* runDataRequest({ payload }: ChangePageAction) {
    const { id, page } = payload

    yield put(dataRequest(id, { page: page || 1 }))
}

/** Список активных задач dataRequest, которые надо отменить при дестрое */
const activeTasks: Record<string, Task[]> = {}

// Очистка данных и отмена активных задач при дестрое ds
export function* removeSaga({ payload }: RemoveAction) {
    const { id } = payload
    const tasks = activeTasks[id] || []

    for (const task of tasks) {
        yield cancel(task)
    }

    /**
     * При переходе со страницы, у которой есть несколько datsource, они удаляются поочерёдо
     * И, если делать удаление модели без задержки,
     * то может стригериться зависимость на удаляемую модуль у другого удаляемого в этот же момент datasource,
     * что в свою очередь может дёрнуть нежелаемый сабмит с пустой моделью
     * (из интерессного: упирается в именование датасурсов и какой id за каким следует в списке %) )
     *
     * Поэтому закидываем небольшую задержку, чтобы избежать этой гонки
     */
    yield delay(50)
    yield put(removeAllModel(id))
}

// Обёртка над dataRequestSaga для сохранения сылк на задачу, которую надо будет отменить в случае дестроя DS
export function* dataRequestWrapper(apiProvider: unknown, action: DataRequestAction) {
    const { id } = action.payload
    const task: Task = yield fork(EffectWrapper(query), action, apiProvider)

    activeTasks[id] = activeTasks[id] || []
    activeTasks[id].push(task)
    // фильтр завершенных задач, чтобы память не текла
    yield task.toPromise().finally(() => {
        activeTasks[id] = activeTasks[id].filter(activeTask => activeTask !== task)
    })
}

export default (apiProvider: unknown) => [
    takeEvery([setSorting, changePage, changeSize], runDataRequest),
    takeEvery(dataRequest, dataRequestWrapper, apiProvider),
    takeEvery(DATA_REQUEST, EffectWrapper(function* remapRequest({ payload }) {
        const { datasource, options } = payload

        yield put(dataRequest(datasource, options))
    })),
    takeEvery(startValidate, validateSaga),
    // @ts-ignore хер знает как затипизировать
    takeEvery(submit, EffectWrapper(submitSaga), apiProvider),
    takeEvery(remove, removeSaga),
    takeEvery([setModel, removeModel, removeAllModel, clearModel, updateModel, updateMapModel], watchDependencies),
    takeEvery(register, applyOnInitDependencies),
    // @ts-ignore FIXME: проставить тип action
    takeEvery(action => action.meta?.refresh?.datasources, function* refreshSaga({ meta }) {
        const { refresh } = meta
        const { datasources } = refresh

        for (const datasource of datasources) {
            yield put(dataRequest(datasource))
        }
    }),
    // @ts-ignore FIXME: проставить тип action
    takeEvery(action => action.meta?.clear, clear),
]
