package com.A306.runnershi.Repository

import com.A306.runnershi.Dao.RunDAO
import com.A306.runnershi.Model.Run
import javax.inject.Inject

class SingleRunRepository @Inject constructor(
    val runDao: RunDAO
){
    suspend fun insertRun(run: Run) = runDao.insertRun(run)

    suspend fun deleteRun(run: Run) = runDao.deleteRun(run)

    fun getAllRunsSortedByDate() = runDao.getAllRunsSortedByDate()
}