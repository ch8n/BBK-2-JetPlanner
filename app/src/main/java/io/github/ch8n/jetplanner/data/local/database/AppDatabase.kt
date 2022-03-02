package io.github.ch8n.jetplanner.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import io.github.ch8n.jetplanner.data.model.Task
import io.github.ch8n.jetplanner.data.repository.TaskDao

@Database(entities = [Task::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao

    companion object {
        fun instance(applicationContext: Context): AppDatabase {
            return Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java, "jetPlannerDB"
            ).build()
        }
    }
}
