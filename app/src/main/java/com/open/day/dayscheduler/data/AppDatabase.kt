package com.open.day.dayscheduler.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.open.day.dayscheduler.data.dao.TaskDao
import com.open.day.dayscheduler.data.dao.UserDao
import com.open.day.dayscheduler.data.entity.TaskEntity
import com.open.day.dayscheduler.data.entity.UserEntity

@Database(entities = [TaskEntity::class, UserEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, "schedule")
                .addCallback(
                    object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            val cursor = db.query("SELECT * FROM users WHERE is_local_user = 1 LIMIT 1;")

                            if (cursor.count == 0) {
                                val user = UserEntity(null, null, true)
                                db.execSQL("INSERT INTO users VALUES(null, null, 1, %s);".format(user.id))
                            }

                            cursor.close()
                        }
                    }
                )
                .build()
        }
    }
}