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
import com.drugfreeindia.app.data.local.entities.UserProfileEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class UserProfileDao_Impl implements UserProfileDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<UserProfileEntity> __insertionAdapterOfUserProfileEntity;

  private final SharedSQLiteStatement __preparedStmtOfClear;

  public UserProfileDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfUserProfileEntity = new EntityInsertionAdapter<UserProfileEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `user_profile` (`anonId`,`nickname`,`collegeCode`,`language`,`createdAt`) VALUES (?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final UserProfileEntity entity) {
        statement.bindString(1, entity.getAnonId());
        statement.bindString(2, entity.getNickname());
        if (entity.getCollegeCode() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getCollegeCode());
        }
        statement.bindString(4, entity.getLanguage());
        statement.bindLong(5, entity.getCreatedAt());
      }
    };
    this.__preparedStmtOfClear = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM user_profile";
        return _query;
      }
    };
  }

  @Override
  public Object insertOrUpdate(final UserProfileEntity profile,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfUserProfileEntity.insert(profile);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object clear(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClear.acquire();
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
          __preparedStmtOfClear.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<UserProfileEntity> getUserProfile() {
    final String _sql = "SELECT * FROM user_profile LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"user_profile"}, new Callable<UserProfileEntity>() {
      @Override
      @Nullable
      public UserProfileEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfAnonId = CursorUtil.getColumnIndexOrThrow(_cursor, "anonId");
          final int _cursorIndexOfNickname = CursorUtil.getColumnIndexOrThrow(_cursor, "nickname");
          final int _cursorIndexOfCollegeCode = CursorUtil.getColumnIndexOrThrow(_cursor, "collegeCode");
          final int _cursorIndexOfLanguage = CursorUtil.getColumnIndexOrThrow(_cursor, "language");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final UserProfileEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpAnonId;
            _tmpAnonId = _cursor.getString(_cursorIndexOfAnonId);
            final String _tmpNickname;
            _tmpNickname = _cursor.getString(_cursorIndexOfNickname);
            final String _tmpCollegeCode;
            if (_cursor.isNull(_cursorIndexOfCollegeCode)) {
              _tmpCollegeCode = null;
            } else {
              _tmpCollegeCode = _cursor.getString(_cursorIndexOfCollegeCode);
            }
            final String _tmpLanguage;
            _tmpLanguage = _cursor.getString(_cursorIndexOfLanguage);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _result = new UserProfileEntity(_tmpAnonId,_tmpNickname,_tmpCollegeCode,_tmpLanguage,_tmpCreatedAt);
          } else {
            _result = null;
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
  public Object getUserProfileOnce(final Continuation<? super UserProfileEntity> $completion) {
    final String _sql = "SELECT * FROM user_profile LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<UserProfileEntity>() {
      @Override
      @Nullable
      public UserProfileEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfAnonId = CursorUtil.getColumnIndexOrThrow(_cursor, "anonId");
          final int _cursorIndexOfNickname = CursorUtil.getColumnIndexOrThrow(_cursor, "nickname");
          final int _cursorIndexOfCollegeCode = CursorUtil.getColumnIndexOrThrow(_cursor, "collegeCode");
          final int _cursorIndexOfLanguage = CursorUtil.getColumnIndexOrThrow(_cursor, "language");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final UserProfileEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpAnonId;
            _tmpAnonId = _cursor.getString(_cursorIndexOfAnonId);
            final String _tmpNickname;
            _tmpNickname = _cursor.getString(_cursorIndexOfNickname);
            final String _tmpCollegeCode;
            if (_cursor.isNull(_cursorIndexOfCollegeCode)) {
              _tmpCollegeCode = null;
            } else {
              _tmpCollegeCode = _cursor.getString(_cursorIndexOfCollegeCode);
            }
            final String _tmpLanguage;
            _tmpLanguage = _cursor.getString(_cursorIndexOfLanguage);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _result = new UserProfileEntity(_tmpAnonId,_tmpNickname,_tmpCollegeCode,_tmpLanguage,_tmpCreatedAt);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
