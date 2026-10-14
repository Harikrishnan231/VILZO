package com.drugfreeindia.app.data.local;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.drugfreeindia.app.data.local.dao.ActivityDao;
import com.drugfreeindia.app.data.local.dao.ActivityDao_Impl;
import com.drugfreeindia.app.data.local.dao.BadgeDao;
import com.drugfreeindia.app.data.local.dao.BadgeDao_Impl;
import com.drugfreeindia.app.data.local.dao.CentreDao;
import com.drugfreeindia.app.data.local.dao.CentreDao_Impl;
import com.drugfreeindia.app.data.local.dao.CollegeStatDao;
import com.drugfreeindia.app.data.local.dao.CollegeStatDao_Impl;
import com.drugfreeindia.app.data.local.dao.EventDao;
import com.drugfreeindia.app.data.local.dao.EventDao_Impl;
import com.drugfreeindia.app.data.local.dao.HelplineDao;
import com.drugfreeindia.app.data.local.dao.HelplineDao_Impl;
import com.drugfreeindia.app.data.local.dao.MentorDao;
import com.drugfreeindia.app.data.local.dao.MentorDao_Impl;
import com.drugfreeindia.app.data.local.dao.StoryDao;
import com.drugfreeindia.app.data.local.dao.StoryDao_Impl;
import com.drugfreeindia.app.data.local.dao.StoryProgressDao;
import com.drugfreeindia.app.data.local.dao.StoryProgressDao_Impl;
import com.drugfreeindia.app.data.local.dao.StreakDao;
import com.drugfreeindia.app.data.local.dao.StreakDao_Impl;
import com.drugfreeindia.app.data.local.dao.SurveyDao;
import com.drugfreeindia.app.data.local.dao.SurveyDao_Impl;
import com.drugfreeindia.app.data.local.dao.TrustedContactDao;
import com.drugfreeindia.app.data.local.dao.TrustedContactDao_Impl;
import com.drugfreeindia.app.data.local.dao.UserProfileDao;
import com.drugfreeindia.app.data.local.dao.UserProfileDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile UserProfileDao _userProfileDao;

  private volatile StoryDao _storyDao;

  private volatile StoryProgressDao _storyProgressDao;

  private volatile BadgeDao _badgeDao;

  private volatile StreakDao _streakDao;

  private volatile TrustedContactDao _trustedContactDao;

  private volatile CentreDao _centreDao;

  private volatile HelplineDao _helplineDao;

  private volatile ActivityDao _activityDao;

  private volatile EventDao _eventDao;

  private volatile MentorDao _mentorDao;

  private volatile SurveyDao _surveyDao;

  private volatile CollegeStatDao _collegeStatDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `user_profile` (`anonId` TEXT NOT NULL, `nickname` TEXT NOT NULL, `collegeCode` TEXT, `language` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`anonId`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `stories` (`id` TEXT NOT NULL, `title` TEXT NOT NULL, `pack` TEXT NOT NULL, `estimatedSeconds` INTEGER NOT NULL, `scenesJson` TEXT NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `story_progress` (`storyId` TEXT NOT NULL, `bestScore` INTEGER NOT NULL, `completedAt` INTEGER NOT NULL, `attempts` INTEGER NOT NULL, PRIMARY KEY(`storyId`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `score_events` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `storyId` TEXT NOT NULL, `points` INTEGER NOT NULL, `tag` TEXT NOT NULL, `timestamp` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `badges` (`id` TEXT NOT NULL, `title` TEXT NOT NULL, `description` TEXT NOT NULL, `iconName` TEXT NOT NULL, `isUnlocked` INTEGER NOT NULL, `unlockedAt` INTEGER, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `streak` (`id` INTEGER NOT NULL, `current` INTEGER NOT NULL, `longest` INTEGER NOT NULL, `lastActiveDate` TEXT NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `trusted_contacts` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `phone` TEXT NOT NULL, `relationship` TEXT NOT NULL, `isPrimary` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `centres` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `type` TEXT NOT NULL, `address` TEXT NOT NULL, `city` TEXT NOT NULL, `state` TEXT NOT NULL, `pincode` TEXT NOT NULL, `phone` TEXT NOT NULL, `hours` TEXT NOT NULL, `latitude` REAL NOT NULL, `longitude` REAL NOT NULL, `isVerified` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `helplines` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `number` TEXT NOT NULL, `hours` TEXT NOT NULL, `description` TEXT NOT NULL, `category` TEXT NOT NULL, `lastVerified` TEXT NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `activities` (`id` TEXT NOT NULL, `title` TEXT NOT NULL, `category` TEXT NOT NULL, `organizer` TEXT NOT NULL, `location` TEXT NOT NULL, `schedule` TEXT NOT NULL, `description` TEXT NOT NULL, `whatToBring` TEXT NOT NULL, `isVerified` INTEGER NOT NULL, `isJoined` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `events` (`id` TEXT NOT NULL, `title` TEXT NOT NULL, `category` TEXT NOT NULL, `date` TEXT NOT NULL, `time` TEXT NOT NULL, `location` TEXT NOT NULL, `organizer` TEXT NOT NULL, `description` TEXT NOT NULL, `isReminderSet` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `mentors` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `role` TEXT NOT NULL, `organization` TEXT NOT NULL, `bio` TEXT NOT NULL, `isAvailable` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `survey_questions` (`id` TEXT NOT NULL, `category` TEXT NOT NULL, `questionText` TEXT NOT NULL, `optionsJson` TEXT NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `survey_responses` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `monthKey` TEXT NOT NULL, `questionId` TEXT NOT NULL, `selectedAnswer` TEXT NOT NULL, `timestamp` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `college_stats` (`collegeCode` TEXT NOT NULL, `collegeName` TEXT NOT NULL, `points` INTEGER NOT NULL, `participants` INTEGER NOT NULL, `rank` INTEGER NOT NULL, PRIMARY KEY(`collegeCode`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '8be0283c063ddbff80415a100901951e')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `user_profile`");
        db.execSQL("DROP TABLE IF EXISTS `stories`");
        db.execSQL("DROP TABLE IF EXISTS `story_progress`");
        db.execSQL("DROP TABLE IF EXISTS `score_events`");
        db.execSQL("DROP TABLE IF EXISTS `badges`");
        db.execSQL("DROP TABLE IF EXISTS `streak`");
        db.execSQL("DROP TABLE IF EXISTS `trusted_contacts`");
        db.execSQL("DROP TABLE IF EXISTS `centres`");
        db.execSQL("DROP TABLE IF EXISTS `helplines`");
        db.execSQL("DROP TABLE IF EXISTS `activities`");
        db.execSQL("DROP TABLE IF EXISTS `events`");
        db.execSQL("DROP TABLE IF EXISTS `mentors`");
        db.execSQL("DROP TABLE IF EXISTS `survey_questions`");
        db.execSQL("DROP TABLE IF EXISTS `survey_responses`");
        db.execSQL("DROP TABLE IF EXISTS `college_stats`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsUserProfile = new HashMap<String, TableInfo.Column>(5);
        _columnsUserProfile.put("anonId", new TableInfo.Column("anonId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserProfile.put("nickname", new TableInfo.Column("nickname", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserProfile.put("collegeCode", new TableInfo.Column("collegeCode", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserProfile.put("language", new TableInfo.Column("language", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserProfile.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysUserProfile = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesUserProfile = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoUserProfile = new TableInfo("user_profile", _columnsUserProfile, _foreignKeysUserProfile, _indicesUserProfile);
        final TableInfo _existingUserProfile = TableInfo.read(db, "user_profile");
        if (!_infoUserProfile.equals(_existingUserProfile)) {
          return new RoomOpenHelper.ValidationResult(false, "user_profile(com.drugfreeindia.app.data.local.entities.UserProfileEntity).\n"
                  + " Expected:\n" + _infoUserProfile + "\n"
                  + " Found:\n" + _existingUserProfile);
        }
        final HashMap<String, TableInfo.Column> _columnsStories = new HashMap<String, TableInfo.Column>(5);
        _columnsStories.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStories.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStories.put("pack", new TableInfo.Column("pack", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStories.put("estimatedSeconds", new TableInfo.Column("estimatedSeconds", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStories.put("scenesJson", new TableInfo.Column("scenesJson", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysStories = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesStories = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoStories = new TableInfo("stories", _columnsStories, _foreignKeysStories, _indicesStories);
        final TableInfo _existingStories = TableInfo.read(db, "stories");
        if (!_infoStories.equals(_existingStories)) {
          return new RoomOpenHelper.ValidationResult(false, "stories(com.drugfreeindia.app.data.local.entities.StoryEntity).\n"
                  + " Expected:\n" + _infoStories + "\n"
                  + " Found:\n" + _existingStories);
        }
        final HashMap<String, TableInfo.Column> _columnsStoryProgress = new HashMap<String, TableInfo.Column>(4);
        _columnsStoryProgress.put("storyId", new TableInfo.Column("storyId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStoryProgress.put("bestScore", new TableInfo.Column("bestScore", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStoryProgress.put("completedAt", new TableInfo.Column("completedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStoryProgress.put("attempts", new TableInfo.Column("attempts", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysStoryProgress = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesStoryProgress = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoStoryProgress = new TableInfo("story_progress", _columnsStoryProgress, _foreignKeysStoryProgress, _indicesStoryProgress);
        final TableInfo _existingStoryProgress = TableInfo.read(db, "story_progress");
        if (!_infoStoryProgress.equals(_existingStoryProgress)) {
          return new RoomOpenHelper.ValidationResult(false, "story_progress(com.drugfreeindia.app.data.local.entities.StoryProgressEntity).\n"
                  + " Expected:\n" + _infoStoryProgress + "\n"
                  + " Found:\n" + _existingStoryProgress);
        }
        final HashMap<String, TableInfo.Column> _columnsScoreEvents = new HashMap<String, TableInfo.Column>(5);
        _columnsScoreEvents.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScoreEvents.put("storyId", new TableInfo.Column("storyId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScoreEvents.put("points", new TableInfo.Column("points", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScoreEvents.put("tag", new TableInfo.Column("tag", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScoreEvents.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysScoreEvents = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesScoreEvents = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoScoreEvents = new TableInfo("score_events", _columnsScoreEvents, _foreignKeysScoreEvents, _indicesScoreEvents);
        final TableInfo _existingScoreEvents = TableInfo.read(db, "score_events");
        if (!_infoScoreEvents.equals(_existingScoreEvents)) {
          return new RoomOpenHelper.ValidationResult(false, "score_events(com.drugfreeindia.app.data.local.entities.ScoreEventEntity).\n"
                  + " Expected:\n" + _infoScoreEvents + "\n"
                  + " Found:\n" + _existingScoreEvents);
        }
        final HashMap<String, TableInfo.Column> _columnsBadges = new HashMap<String, TableInfo.Column>(6);
        _columnsBadges.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBadges.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBadges.put("description", new TableInfo.Column("description", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBadges.put("iconName", new TableInfo.Column("iconName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBadges.put("isUnlocked", new TableInfo.Column("isUnlocked", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBadges.put("unlockedAt", new TableInfo.Column("unlockedAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysBadges = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesBadges = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoBadges = new TableInfo("badges", _columnsBadges, _foreignKeysBadges, _indicesBadges);
        final TableInfo _existingBadges = TableInfo.read(db, "badges");
        if (!_infoBadges.equals(_existingBadges)) {
          return new RoomOpenHelper.ValidationResult(false, "badges(com.drugfreeindia.app.data.local.entities.BadgeEntity).\n"
                  + " Expected:\n" + _infoBadges + "\n"
                  + " Found:\n" + _existingBadges);
        }
        final HashMap<String, TableInfo.Column> _columnsStreak = new HashMap<String, TableInfo.Column>(4);
        _columnsStreak.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStreak.put("current", new TableInfo.Column("current", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStreak.put("longest", new TableInfo.Column("longest", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStreak.put("lastActiveDate", new TableInfo.Column("lastActiveDate", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysStreak = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesStreak = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoStreak = new TableInfo("streak", _columnsStreak, _foreignKeysStreak, _indicesStreak);
        final TableInfo _existingStreak = TableInfo.read(db, "streak");
        if (!_infoStreak.equals(_existingStreak)) {
          return new RoomOpenHelper.ValidationResult(false, "streak(com.drugfreeindia.app.data.local.entities.StreakEntity).\n"
                  + " Expected:\n" + _infoStreak + "\n"
                  + " Found:\n" + _existingStreak);
        }
        final HashMap<String, TableInfo.Column> _columnsTrustedContacts = new HashMap<String, TableInfo.Column>(5);
        _columnsTrustedContacts.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTrustedContacts.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTrustedContacts.put("phone", new TableInfo.Column("phone", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTrustedContacts.put("relationship", new TableInfo.Column("relationship", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTrustedContacts.put("isPrimary", new TableInfo.Column("isPrimary", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysTrustedContacts = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesTrustedContacts = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoTrustedContacts = new TableInfo("trusted_contacts", _columnsTrustedContacts, _foreignKeysTrustedContacts, _indicesTrustedContacts);
        final TableInfo _existingTrustedContacts = TableInfo.read(db, "trusted_contacts");
        if (!_infoTrustedContacts.equals(_existingTrustedContacts)) {
          return new RoomOpenHelper.ValidationResult(false, "trusted_contacts(com.drugfreeindia.app.data.local.entities.TrustedContactEntity).\n"
                  + " Expected:\n" + _infoTrustedContacts + "\n"
                  + " Found:\n" + _existingTrustedContacts);
        }
        final HashMap<String, TableInfo.Column> _columnsCentres = new HashMap<String, TableInfo.Column>(12);
        _columnsCentres.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCentres.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCentres.put("type", new TableInfo.Column("type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCentres.put("address", new TableInfo.Column("address", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCentres.put("city", new TableInfo.Column("city", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCentres.put("state", new TableInfo.Column("state", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCentres.put("pincode", new TableInfo.Column("pincode", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCentres.put("phone", new TableInfo.Column("phone", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCentres.put("hours", new TableInfo.Column("hours", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCentres.put("latitude", new TableInfo.Column("latitude", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCentres.put("longitude", new TableInfo.Column("longitude", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCentres.put("isVerified", new TableInfo.Column("isVerified", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysCentres = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesCentres = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoCentres = new TableInfo("centres", _columnsCentres, _foreignKeysCentres, _indicesCentres);
        final TableInfo _existingCentres = TableInfo.read(db, "centres");
        if (!_infoCentres.equals(_existingCentres)) {
          return new RoomOpenHelper.ValidationResult(false, "centres(com.drugfreeindia.app.data.local.entities.CentreEntity).\n"
                  + " Expected:\n" + _infoCentres + "\n"
                  + " Found:\n" + _existingCentres);
        }
        final HashMap<String, TableInfo.Column> _columnsHelplines = new HashMap<String, TableInfo.Column>(7);
        _columnsHelplines.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHelplines.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHelplines.put("number", new TableInfo.Column("number", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHelplines.put("hours", new TableInfo.Column("hours", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHelplines.put("description", new TableInfo.Column("description", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHelplines.put("category", new TableInfo.Column("category", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHelplines.put("lastVerified", new TableInfo.Column("lastVerified", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysHelplines = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesHelplines = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoHelplines = new TableInfo("helplines", _columnsHelplines, _foreignKeysHelplines, _indicesHelplines);
        final TableInfo _existingHelplines = TableInfo.read(db, "helplines");
        if (!_infoHelplines.equals(_existingHelplines)) {
          return new RoomOpenHelper.ValidationResult(false, "helplines(com.drugfreeindia.app.data.local.entities.HelplineEntity).\n"
                  + " Expected:\n" + _infoHelplines + "\n"
                  + " Found:\n" + _existingHelplines);
        }
        final HashMap<String, TableInfo.Column> _columnsActivities = new HashMap<String, TableInfo.Column>(10);
        _columnsActivities.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsActivities.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsActivities.put("category", new TableInfo.Column("category", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsActivities.put("organizer", new TableInfo.Column("organizer", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsActivities.put("location", new TableInfo.Column("location", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsActivities.put("schedule", new TableInfo.Column("schedule", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsActivities.put("description", new TableInfo.Column("description", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsActivities.put("whatToBring", new TableInfo.Column("whatToBring", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsActivities.put("isVerified", new TableInfo.Column("isVerified", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsActivities.put("isJoined", new TableInfo.Column("isJoined", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysActivities = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesActivities = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoActivities = new TableInfo("activities", _columnsActivities, _foreignKeysActivities, _indicesActivities);
        final TableInfo _existingActivities = TableInfo.read(db, "activities");
        if (!_infoActivities.equals(_existingActivities)) {
          return new RoomOpenHelper.ValidationResult(false, "activities(com.drugfreeindia.app.data.local.entities.ActivityEntity).\n"
                  + " Expected:\n" + _infoActivities + "\n"
                  + " Found:\n" + _existingActivities);
        }
        final HashMap<String, TableInfo.Column> _columnsEvents = new HashMap<String, TableInfo.Column>(9);
        _columnsEvents.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEvents.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEvents.put("category", new TableInfo.Column("category", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEvents.put("date", new TableInfo.Column("date", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEvents.put("time", new TableInfo.Column("time", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEvents.put("location", new TableInfo.Column("location", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEvents.put("organizer", new TableInfo.Column("organizer", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEvents.put("description", new TableInfo.Column("description", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEvents.put("isReminderSet", new TableInfo.Column("isReminderSet", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysEvents = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesEvents = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoEvents = new TableInfo("events", _columnsEvents, _foreignKeysEvents, _indicesEvents);
        final TableInfo _existingEvents = TableInfo.read(db, "events");
        if (!_infoEvents.equals(_existingEvents)) {
          return new RoomOpenHelper.ValidationResult(false, "events(com.drugfreeindia.app.data.local.entities.EventEntity).\n"
                  + " Expected:\n" + _infoEvents + "\n"
                  + " Found:\n" + _existingEvents);
        }
        final HashMap<String, TableInfo.Column> _columnsMentors = new HashMap<String, TableInfo.Column>(6);
        _columnsMentors.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMentors.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMentors.put("role", new TableInfo.Column("role", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMentors.put("organization", new TableInfo.Column("organization", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMentors.put("bio", new TableInfo.Column("bio", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMentors.put("isAvailable", new TableInfo.Column("isAvailable", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysMentors = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesMentors = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoMentors = new TableInfo("mentors", _columnsMentors, _foreignKeysMentors, _indicesMentors);
        final TableInfo _existingMentors = TableInfo.read(db, "mentors");
        if (!_infoMentors.equals(_existingMentors)) {
          return new RoomOpenHelper.ValidationResult(false, "mentors(com.drugfreeindia.app.data.local.entities.MentorEntity).\n"
                  + " Expected:\n" + _infoMentors + "\n"
                  + " Found:\n" + _existingMentors);
        }
        final HashMap<String, TableInfo.Column> _columnsSurveyQuestions = new HashMap<String, TableInfo.Column>(4);
        _columnsSurveyQuestions.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSurveyQuestions.put("category", new TableInfo.Column("category", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSurveyQuestions.put("questionText", new TableInfo.Column("questionText", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSurveyQuestions.put("optionsJson", new TableInfo.Column("optionsJson", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSurveyQuestions = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesSurveyQuestions = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoSurveyQuestions = new TableInfo("survey_questions", _columnsSurveyQuestions, _foreignKeysSurveyQuestions, _indicesSurveyQuestions);
        final TableInfo _existingSurveyQuestions = TableInfo.read(db, "survey_questions");
        if (!_infoSurveyQuestions.equals(_existingSurveyQuestions)) {
          return new RoomOpenHelper.ValidationResult(false, "survey_questions(com.drugfreeindia.app.data.local.entities.SurveyQuestionEntity).\n"
                  + " Expected:\n" + _infoSurveyQuestions + "\n"
                  + " Found:\n" + _existingSurveyQuestions);
        }
        final HashMap<String, TableInfo.Column> _columnsSurveyResponses = new HashMap<String, TableInfo.Column>(5);
        _columnsSurveyResponses.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSurveyResponses.put("monthKey", new TableInfo.Column("monthKey", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSurveyResponses.put("questionId", new TableInfo.Column("questionId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSurveyResponses.put("selectedAnswer", new TableInfo.Column("selectedAnswer", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSurveyResponses.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSurveyResponses = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesSurveyResponses = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoSurveyResponses = new TableInfo("survey_responses", _columnsSurveyResponses, _foreignKeysSurveyResponses, _indicesSurveyResponses);
        final TableInfo _existingSurveyResponses = TableInfo.read(db, "survey_responses");
        if (!_infoSurveyResponses.equals(_existingSurveyResponses)) {
          return new RoomOpenHelper.ValidationResult(false, "survey_responses(com.drugfreeindia.app.data.local.entities.SurveyResponseEntity).\n"
                  + " Expected:\n" + _infoSurveyResponses + "\n"
                  + " Found:\n" + _existingSurveyResponses);
        }
        final HashMap<String, TableInfo.Column> _columnsCollegeStats = new HashMap<String, TableInfo.Column>(5);
        _columnsCollegeStats.put("collegeCode", new TableInfo.Column("collegeCode", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCollegeStats.put("collegeName", new TableInfo.Column("collegeName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCollegeStats.put("points", new TableInfo.Column("points", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCollegeStats.put("participants", new TableInfo.Column("participants", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCollegeStats.put("rank", new TableInfo.Column("rank", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysCollegeStats = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesCollegeStats = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoCollegeStats = new TableInfo("college_stats", _columnsCollegeStats, _foreignKeysCollegeStats, _indicesCollegeStats);
        final TableInfo _existingCollegeStats = TableInfo.read(db, "college_stats");
        if (!_infoCollegeStats.equals(_existingCollegeStats)) {
          return new RoomOpenHelper.ValidationResult(false, "college_stats(com.drugfreeindia.app.data.local.entities.CollegeStatEntity).\n"
                  + " Expected:\n" + _infoCollegeStats + "\n"
                  + " Found:\n" + _existingCollegeStats);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "8be0283c063ddbff80415a100901951e", "f2472086e5e37e6c7678c1d310dc5b5d");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "user_profile","stories","story_progress","score_events","badges","streak","trusted_contacts","centres","helplines","activities","events","mentors","survey_questions","survey_responses","college_stats");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `user_profile`");
      _db.execSQL("DELETE FROM `stories`");
      _db.execSQL("DELETE FROM `story_progress`");
      _db.execSQL("DELETE FROM `score_events`");
      _db.execSQL("DELETE FROM `badges`");
      _db.execSQL("DELETE FROM `streak`");
      _db.execSQL("DELETE FROM `trusted_contacts`");
      _db.execSQL("DELETE FROM `centres`");
      _db.execSQL("DELETE FROM `helplines`");
      _db.execSQL("DELETE FROM `activities`");
      _db.execSQL("DELETE FROM `events`");
      _db.execSQL("DELETE FROM `mentors`");
      _db.execSQL("DELETE FROM `survey_questions`");
      _db.execSQL("DELETE FROM `survey_responses`");
      _db.execSQL("DELETE FROM `college_stats`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(UserProfileDao.class, UserProfileDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(StoryDao.class, StoryDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(StoryProgressDao.class, StoryProgressDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(BadgeDao.class, BadgeDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(StreakDao.class, StreakDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(TrustedContactDao.class, TrustedContactDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(CentreDao.class, CentreDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(HelplineDao.class, HelplineDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ActivityDao.class, ActivityDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(EventDao.class, EventDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(MentorDao.class, MentorDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(SurveyDao.class, SurveyDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(CollegeStatDao.class, CollegeStatDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public UserProfileDao userProfileDao() {
    if (_userProfileDao != null) {
      return _userProfileDao;
    } else {
      synchronized(this) {
        if(_userProfileDao == null) {
          _userProfileDao = new UserProfileDao_Impl(this);
        }
        return _userProfileDao;
      }
    }
  }

  @Override
  public StoryDao storyDao() {
    if (_storyDao != null) {
      return _storyDao;
    } else {
      synchronized(this) {
        if(_storyDao == null) {
          _storyDao = new StoryDao_Impl(this);
        }
        return _storyDao;
      }
    }
  }

  @Override
  public StoryProgressDao storyProgressDao() {
    if (_storyProgressDao != null) {
      return _storyProgressDao;
    } else {
      synchronized(this) {
        if(_storyProgressDao == null) {
          _storyProgressDao = new StoryProgressDao_Impl(this);
        }
        return _storyProgressDao;
      }
    }
  }

  @Override
  public BadgeDao badgeDao() {
    if (_badgeDao != null) {
      return _badgeDao;
    } else {
      synchronized(this) {
        if(_badgeDao == null) {
          _badgeDao = new BadgeDao_Impl(this);
        }
        return _badgeDao;
      }
    }
  }

  @Override
  public StreakDao streakDao() {
    if (_streakDao != null) {
      return _streakDao;
    } else {
      synchronized(this) {
        if(_streakDao == null) {
          _streakDao = new StreakDao_Impl(this);
        }
        return _streakDao;
      }
    }
  }

  @Override
  public TrustedContactDao trustedContactDao() {
    if (_trustedContactDao != null) {
      return _trustedContactDao;
    } else {
      synchronized(this) {
        if(_trustedContactDao == null) {
          _trustedContactDao = new TrustedContactDao_Impl(this);
        }
        return _trustedContactDao;
      }
    }
  }

  @Override
  public CentreDao centreDao() {
    if (_centreDao != null) {
      return _centreDao;
    } else {
      synchronized(this) {
        if(_centreDao == null) {
          _centreDao = new CentreDao_Impl(this);
        }
        return _centreDao;
      }
    }
  }

  @Override
  public HelplineDao helplineDao() {
    if (_helplineDao != null) {
      return _helplineDao;
    } else {
      synchronized(this) {
        if(_helplineDao == null) {
          _helplineDao = new HelplineDao_Impl(this);
        }
        return _helplineDao;
      }
    }
  }

  @Override
  public ActivityDao activityDao() {
    if (_activityDao != null) {
      return _activityDao;
    } else {
      synchronized(this) {
        if(_activityDao == null) {
          _activityDao = new ActivityDao_Impl(this);
        }
        return _activityDao;
      }
    }
  }

  @Override
  public EventDao eventDao() {
    if (_eventDao != null) {
      return _eventDao;
    } else {
      synchronized(this) {
        if(_eventDao == null) {
          _eventDao = new EventDao_Impl(this);
        }
        return _eventDao;
      }
    }
  }

  @Override
  public MentorDao mentorDao() {
    if (_mentorDao != null) {
      return _mentorDao;
    } else {
      synchronized(this) {
        if(_mentorDao == null) {
          _mentorDao = new MentorDao_Impl(this);
        }
        return _mentorDao;
      }
    }
  }

  @Override
  public SurveyDao surveyDao() {
    if (_surveyDao != null) {
      return _surveyDao;
    } else {
      synchronized(this) {
        if(_surveyDao == null) {
          _surveyDao = new SurveyDao_Impl(this);
        }
        return _surveyDao;
      }
    }
  }

  @Override
  public CollegeStatDao collegeStatDao() {
    if (_collegeStatDao != null) {
      return _collegeStatDao;
    } else {
      synchronized(this) {
        if(_collegeStatDao == null) {
          _collegeStatDao = new CollegeStatDao_Impl(this);
        }
        return _collegeStatDao;
      }
    }
  }
}
