package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.model.*

@Database(
    entities = [
        UserEntity::class,
        RepositoryEntity::class,
        RepositoryAnalysisEntity::class,
        TaskEntity::class,
        SubTaskEntity::class,
        FocusSessionEntity::class,
        AIConversationEntity::class,
        AIMessageEntity::class,
        DailyPlanEntity::class,
        DeveloperActivityEntity::class,
        EngineeringRiskEntity::class,
        ProjectGoalEntity::class,
        MilestoneEntity::class,
        OrganizationWorkspaceEntity::class,
        TeamMemberEntity::class,
        NotificationEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class DevPilotDatabase : RoomDatabase() {

    abstract fun devPilotDao(): DevPilotDao

    companion object {
        @Volatile
        private var INSTANCE: DevPilotDatabase? = null

        fun getDatabase(context: Context): DevPilotDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DevPilotDatabase::class.java,
                    "devpilot_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
