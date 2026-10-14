package com.drugfreeindia.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.drugfreeindia.app.data.local.dao.ActivityDao
import com.drugfreeindia.app.data.local.dao.BadgeDao
import com.drugfreeindia.app.data.local.dao.CentreDao
import com.drugfreeindia.app.data.local.dao.CollegeStatDao
import com.drugfreeindia.app.data.local.dao.EventDao
import com.drugfreeindia.app.data.local.dao.HelplineDao
import com.drugfreeindia.app.data.local.dao.MentorDao
import com.drugfreeindia.app.data.local.dao.StoryDao
import com.drugfreeindia.app.data.local.dao.StoryProgressDao
import com.drugfreeindia.app.data.local.dao.StreakDao
import com.drugfreeindia.app.data.local.dao.SurveyDao
import com.drugfreeindia.app.data.local.dao.TrustedContactDao
import com.drugfreeindia.app.data.local.dao.UserProfileDao
import com.drugfreeindia.app.data.local.entities.ActivityEntity
import com.drugfreeindia.app.data.local.entities.BadgeEntity
import com.drugfreeindia.app.data.local.entities.CentreEntity
import com.drugfreeindia.app.data.local.entities.CollegeStatEntity
import com.drugfreeindia.app.data.local.entities.EventEntity
import com.drugfreeindia.app.data.local.entities.HelplineEntity
import com.drugfreeindia.app.data.local.entities.MentorEntity
import com.drugfreeindia.app.data.local.entities.ScoreEventEntity
import com.drugfreeindia.app.data.local.entities.StoryEntity
import com.drugfreeindia.app.data.local.entities.StoryProgressEntity
import com.drugfreeindia.app.data.local.entities.StreakEntity
import com.drugfreeindia.app.data.local.entities.SurveyQuestionEntity
import com.drugfreeindia.app.data.local.entities.SurveyResponseEntity
import com.drugfreeindia.app.data.local.entities.TrustedContactEntity
import com.drugfreeindia.app.data.local.entities.UserProfileEntity

@Database(
    entities = [
        UserProfileEntity::class,
        StoryEntity::class,
        StoryProgressEntity::class,
        ScoreEventEntity::class,
        BadgeEntity::class,
        StreakEntity::class,
        TrustedContactEntity::class,
        CentreEntity::class,
        HelplineEntity::class,
        ActivityEntity::class,
        EventEntity::class,
        MentorEntity::class,
        SurveyQuestionEntity::class,
        SurveyResponseEntity::class,
        CollegeStatEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun storyDao(): StoryDao
    abstract fun storyProgressDao(): StoryProgressDao
    abstract fun badgeDao(): BadgeDao
    abstract fun streakDao(): StreakDao
    abstract fun trustedContactDao(): TrustedContactDao
    abstract fun centreDao(): CentreDao
    abstract fun helplineDao(): HelplineDao
    abstract fun activityDao(): ActivityDao
    abstract fun eventDao(): EventDao
    abstract fun mentorDao(): MentorDao
    abstract fun surveyDao(): SurveyDao
    abstract fun collegeStatDao(): CollegeStatDao
}
