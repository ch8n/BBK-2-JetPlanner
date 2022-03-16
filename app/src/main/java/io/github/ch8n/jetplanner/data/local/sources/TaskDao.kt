package io.github.ch8n.jetplanner.data.local.sources

import androidx.room.*
import io.github.ch8n.jetplanner.data.model.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("SELECT * FROM Task ORDER BY startTime ASC")
    fun getAll(): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg users: Task)

    @Delete
    fun delete(task: Task)
}
