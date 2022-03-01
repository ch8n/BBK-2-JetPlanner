package io.github.ch8n.jetplanner.data.repository

import android.content.Context
import androidx.room.*
import io.github.ch8n.jetplanner.data.model.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext


@Database(entities = [Task::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao

    companion object {
        fun instance(applicationContext: Context): AppDatabase {
            return Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java, "database-name"
            ).build()
        }
    }
}

@Dao
interface TaskDao {

    @Query("SELECT * FROM Task ORDER BY startTime ASC")
    fun getAll(): Flow<List<Task>>

    @Query("SELECT * FROM task WHERE id IN (:taskIds)")
    fun fromIds(taskIds: IntArray): List<Task>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg users: Task)

    @Delete
    fun delete(task: Task)
}


class TaskRepository(private val taskDao: TaskDao) {

    suspend fun getTasks(): Flow<List<Task>> = withContext(Dispatchers.IO) {
        taskDao.getAll()
    }

    suspend fun addUpdateTask(task: Task): Unit = withContext(Dispatchers.IO) {
        taskDao.insertAll(task)
    }

    suspend fun removeTask(task: Task): Unit = withContext(Dispatchers.IO) {
        taskDao.delete(task)
    }

}
