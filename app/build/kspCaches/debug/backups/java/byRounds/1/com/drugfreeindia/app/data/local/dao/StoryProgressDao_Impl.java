package com.drugfreeindia.app.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.drugfreeindia.app.data.local.entities.ScoreEventEntity;
import com.drugfreeindia.app.data.local.entities.StoryProgressEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class StoryProgressDao_Impl implements StoryProgressDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<StoryProgressEntity> __insertionAdapterOfStoryProgressEntity;

  private final EntityInsertionAdapter<ScoreEventEntity> __insertionAdapterOfScoreEventEntity;

  private final SharedSQLiteStatement __preparedStmtOfClearProgress;

  private final SharedSQLiteStatement __preparedStmtOfClearScoreEvents;

  public StoryProgressDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfStoryProgressEntity = new EntityInsertionAdapter<StoryProgressEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `story_progress` (`storyId`,`bestScore`,`completedAt`,`attempts`) VALUES (?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final StoryProgressEntity entity) {
        statement.bindString(1, entity.getStoryId());
        statement.bindLong(2, entity.getBestScore());
        statement.bindLong(3, entity.getCompletedAt());
        statement.bindLong(4, entity.getAttempts());
      }
    };
    this.__insertionAdapterOfScoreEventEntity = new EntityInsertionAdapter<ScoreEventEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `score_events` (`id`,`storyId`,`points`,`tag`,`timestamp`) VALUES (nullif(?, 0),?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ScoreEventEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getStoryId());
        statement.bindLong(3, entity.getPoints());
        statement.bindString(4, entity.getTag());
        statement.bindLong(5, entity.getTimestamp());
      }
    };
    this.__preparedStmtOfClearProgress = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM story_progress";
        return _query;
      }
    };
    this.__preparedStmtOfClearScoreEvents = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM score_events";
        return _query;
      }
    };
  }

  @Override
  public Object recordProgress(final StoryProgressEntity progress,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfStoryProgressEntity.insert(progress);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object recordScoreEvent(final ScoreEventEntity event,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfScoreEventEntity.insert(event);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object clearProgress(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearProgress.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfClearProgress.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object clearScoreEvents(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearScoreEvents.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfClearScoreEvents.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<StoryProgressEntity>> getAllProgress() {
    final String _sql = "SELECT * FROM story_progress";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"story_progress"}, new Callable<List<StoryProgressEntity>>() {
      @Override
      @NonNull
      public List<StoryProgressEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfStoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "storyId");
          final int _cursorIndexOfBestScore = CursorUtil.getColumnIndexOrThrow(_cursor, "bestScore");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final int _cursorIndexOfAttempts = CursorUtil.getColumnIndexOrThrow(_cursor, "attempts");
          final List<StoryProgressEntity> _result = new ArrayList<StoryProgressEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final StoryProgressEntity _item;
            final String _tmpStoryId;
            _tmpStoryId = _cursor.getString(_cursorIndexOfStoryId);
            final int _tmpBestScore;
            _tmpBestScore = _cursor.getInt(_cursorIndexOfBestScore);
            final long _tmpCompletedAt;
            _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            final int _tmpAttempts;
            _tmpAttempts = _cursor.getInt(_cursorIndexOfAttempts);
            _item = new StoryProgressEntity(_tmpStoryId,_tmpBestScore,_tmpCompletedAt,_tmpAttempts);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getProgressForStory(final String storyId,
      final Continuation<? super StoryProgressEntity> $completion) {
    final String _sql = "SELECT * FROM story_progress WHERE storyId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, storyId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<StoryProgressEntity>() {
      @Override
      @Nullable
      public StoryProgressEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfStoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "storyId");
          final int _cursorIndexOfBestScore = CursorUtil.getColumnIndexOrThrow(_cursor, "bestScore");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final int _cursorIndexOfAttempts = CursorUtil.getColumnIndexOrThrow(_cursor, "attempts");
          final StoryProgressEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpStoryId;
            _tmpStoryId = _cursor.getString(_cursorIndexOfStoryId);
            final int _tmpBestScore;
            _tmpBestScore = _cursor.getInt(_cursorIndexOfBestScore);
            final long _tmpCompletedAt;
            _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            final int _tmpAttempts;
            _tmpAttempts = _cursor.getInt(_cursorIndexOfAttempts);
            _result = new StoryProgressEntity(_tmpStoryId,_tmpBestScore,_tmpCompletedAt,_tmpAttempts);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<ScoreEventEntity>> getAllScoreEvents() {
    final String _sql = "SELECT * FROM score_events ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"score_events"}, new Callable<List<ScoreEventEntity>>() {
      @Override
      @NonNull
      public List<ScoreEventEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfStoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "storyId");
          final int _cursorIndexOfPoints = CursorUtil.getColumnIndexOrThrow(_cursor, "points");
          final int _cursorIndexOfTag = CursorUtil.getColumnIndexOrThrow(_cursor, "tag");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final List<ScoreEventEntity> _result = new ArrayList<ScoreEventEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ScoreEventEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpStoryId;
            _tmpStoryId = _cursor.getString(_cursorIndexOfStoryId);
            final int _tmpPoints;
            _tmpPoints = _cursor.getInt(_cursorIndexOfPoints);
            final String _tmpTag;
            _tmpTag = _cursor.getString(_cursorIndexOfTag);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            _item = new ScoreEventEntity(_tmpId,_tmpStoryId,_tmpPoints,_tmpTag,_tmpTimestamp);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
