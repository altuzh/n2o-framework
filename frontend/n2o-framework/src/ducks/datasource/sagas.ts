import {
    put,
    takeEvery,
    select,
    fork,
    cancel,
} from 'redux-saga/effects'
import type { Task } from 'redux-saga'

import { clearModel, copyModel, removeAllModel, removeModel, setModel } from '../models/store'
import type { State } from '../State'

import { dataRequest as query } from './sagas/query'
import { validate as validateSaga } from './sagas/validate'
import {
    changePage,
    changeSize,
    dataRequest,
    DATA_REQUEST,
    remove,
    setSorting,
    startValidate,
    submit,
} from './store'
import { watchDependencies } from './sagas/dependencies'
import type { ChangePageAction, DataRequestAction, RemoveAction } from './Actions'
import { submitSaga } from './sagas/submit'

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

    yield put(removeAllModel(id))
}

// Обёртка над dataRequestSaga для сохранения сылк на задачу, которую надо будет отменить в случае дестроя DS
export function* dataRequestWrapper(action: DataRequestAction) {
    const { id } = action.payload
    const task: Task = yield fork(query, action)

    activeTasks[id] = activeTasks[id] || []
    activeTasks[id].push(task)
    // фильтр завершенных задач, чтобы память не текла
    task.toPromise().finally(() => {
        activeTasks[id] = activeTasks[id].filter(activeTask => activeTask !== task)
    })
}

// Кеш предыдущего состояния для наблюдения за изменениями зависимостей
let prevState: State = {} as State

export default () => [
    takeEvery([setSorting, changePage, changeSize], runDataRequest),
    takeEvery(dataRequest, dataRequestWrapper),
    takeEvery(DATA_REQUEST, function* remapRequest({ payload }) {
        const { datasource, options } = payload

        yield put(dataRequest(datasource, options))
    }),
    takeEvery(startValidate, validateSaga),
    takeEvery(submit, submitSaga),
    takeEvery(remove, removeSaga),
    takeEvery([setModel, removeModel, removeAllModel, copyModel, clearModel], function* watcher(action) {
        yield watchDependencies(action, prevState)
        prevState = yield select()
    }),
    // @ts-ignore FIXME: проставить тип action
    takeEvery(action => action.meta?.refresh?.datasources, function* refreshSage({ meta }) {
        const { refresh } = meta
        const { datasources } = refresh

        for (const datasource of datasources) {
            yield put(dataRequest(datasource))
        }
    }),
]
