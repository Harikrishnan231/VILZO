package com.drugfreeindia.app.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.drugfreeindia.app.data.local.entities.SurveyQuestionEntity;
import com.drugfreeindia.app.data.local.entities.SurveyResponseEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
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
public final class SurveyDao_Impl implements SurveyDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<SurveyQuestionEntity> __insertionAdapterOfSurveyQuestionEntity;

  private final EntityInsertionAdapter<SurveyResponseEntity> __insertionAdapterOfSurveyResponseEntity;

  private final SharedSQLiteStatement __preparedStmtOfClearResponses;

  public SurveyDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfSurveyQuestionEntity = new EntityInsertionAdapter<SurveyQuestionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `survey_questions` (`id`,`category`,`questionText`,`optionsJson`) VALUES (?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SurveyQuestionEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getCategory());
        statement.bindString(3, entity.getQuestionText());
        statement.bindString(4, entity.getOptionsJson());
      }
    };
    this.__insertionAdapterOfSurveyResponseEntity = new EntityInsertionAdapter<SurveyResponseEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `survey_responses` (`id`,`monthKey`,`questionId`,`selectedAnswer`,`timestamp`) VALUES (nullif(?, 0),?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SurveyResponseEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getMonthKey());
        statement.bindString(3, entity.getQuestionId());
        statement.bindString(4, entity.getSelectedAnswer());
        statement.bindLong(5, entity.getTimestamp());
      }
    };
    this.__preparedStmtOfClearResponses = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM survey_responses";
        return _query;
      }
    };
  }

  @Override
  public Object insertQuestions(final List<SurveyQuestionEntity> questions,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfSurveyQuestionEntity.insert(questions);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object recordResponse(final SurveyResponseEntity response,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfSurveyResponseEntity.insert(response);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object clearResponses(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearResponses.acquire();
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
          __preparedStmtOfClearResponses.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<SurveyQuestionEntity>> getQuestions() {
    final String _sql = "SELECT * FROM survey_questions";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"survey_questions"}, new Callable<List<SurveyQuestionEntity>>() {
      @Override
      @NonNull
      public List<SurveyQuestionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfQuestionText = CursorUtil.getColumnIndexOrThrow(_cursor, "questionText");
          final int _cursorIndexOfOptionsJson = CursorUtil.getColumnIndexOrThrow(_cursor, "optionsJson");
          final List<SurveyQuestionEntity> _result = new ArrayList<SurveyQuestionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SurveyQuestionEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpQuestionText;
            _tmpQuestionText = _cursor.getString(_cursorIndexOfQuestionText);
            final String _tmpOptionsJson;
            _tmpOptionsJson = _cursor.getString(_cursorIndexOfOptionsJson);
            _item = new SurveyQuestionEntity(_tmpId,_tmpCategory,_tmpQuestionText,_tmpOptionsJson);
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
  public Flow<List<SurveyResponseEntity>> getAllResponses() {
    final String _sql = "SELECT * FROM survey_responses";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"survey_responses"}, new Callable<List<SurveyResponseEntity>>() {
      @Override
      @NonNull
      public List<SurveyResponseEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfMonthKey = CursorUtil.getColumnIndexOrThrow(_cursor, "monthKey");
          final int _cursorIndexOfQuestionId = CursorUtil.getColumnIndexOrThrow(_cursor, "questionId");
          final int _cursorIndexOfSelectedAnswer = CursorUtil.getColumnIndexOrThrow(_cursor, "selectedAnswer");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final List<SurveyResponseEntity> _result = new ArrayList<SurveyResponseEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SurveyResponseEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpMonthKey;
            _tmpMonthKey = _cursor.getString(_cursorIndexOfMonthKey);
            final String _tmpQuestionId;
            _tmpQuestionId = _cursor.getString(_cursorIndexOfQuestionId);
            final String _tmpSelectedAnswer;
            _tmpSelectedAnswer = _cursor.getString(_cursorIndexOfSelectedAnswer);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            _item = new SurveyResponseEntity(_tmpId,_tmpMonthKey,_tmpQuestionId,_tmpSelectedAnswer,_tmpTimestamp);
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
  public Object getQuestionCount(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM survey_questions";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
