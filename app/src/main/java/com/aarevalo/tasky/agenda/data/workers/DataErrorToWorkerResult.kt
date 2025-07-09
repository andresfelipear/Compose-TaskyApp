package com.aarevalo.tasky.agenda.data.workers

import androidx.work.ListenableWorker
import com.aarevalo.tasky.core.domain.util.DataError


fun DataError.toWorkerResult(): ListenableWorker.Result {
    return when(this) {
        DataError.Local.DISK_FULL -> ListenableWorker.Result.failure()
        DataError.Local.BAD_DATA -> ListenableWorker.Result.failure()
        DataError.Local.UNKNOWN -> ListenableWorker.Result.failure()
        DataError.Network.REQUEST_TIMEOUT -> ListenableWorker.Result.retry()
        DataError.Network.UNAUTHORIZED -> ListenableWorker.Result.retry()
        DataError.Network.CONFLICT -> ListenableWorker.Result.retry()
        DataError.Network.TOO_MANY_REQUESTS -> ListenableWorker.Result.retry()
        DataError.Network.NO_INTERNET -> ListenableWorker.Result.retry()
        DataError.Network.PAYLOAD_TOO_LARGE -> ListenableWorker.Result.failure()
        DataError.Network.SERVER_ERROR -> ListenableWorker.Result.retry()
        DataError.Network.SERIALIZATION -> ListenableWorker.Result.failure()
        DataError.Network.UNKNOWN -> ListenableWorker.Result.failure()
        DataError.Network.NOT_FOUND -> ListenableWorker.Result.failure()
        DataError.Network.BAD_REQUEST -> ListenableWorker.Result.failure()
    }
}