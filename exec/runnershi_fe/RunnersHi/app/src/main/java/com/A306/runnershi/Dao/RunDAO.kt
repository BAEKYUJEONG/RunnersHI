package com.A306.runnershi.Dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.A306.runnershi.Model.Run

@Dao
interface RunDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRun(run:Run)

    @Delete
    suspend fun deleteRun(run:Run)

    // 필요에 따라 비슷하게 생성하면 됩니다.
    // 달린 날짜, 시간 순
    @Query("SELECT * FROM running_table ORDER BY timestamp DESC")
    fun getAllRunsSortedByDate(): LiveData<List<Run>>
}