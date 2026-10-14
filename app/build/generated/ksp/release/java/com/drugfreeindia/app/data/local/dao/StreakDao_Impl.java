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
import com.drugfreeindia.app.data.local.entities.StreakEntity;
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
public final class StreakDao_Impl implements StreakDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<StreakEntity> __insertionAdapterOfStreakEntity;

  private final SharedSQLiteStatement __preparedStmtOfClear;

  public StreakDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfStreakEntity = new EntityInsertionAdapter<StreakEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `streak` (`id`,`current`,`longest`,`lastActiveDate`) VALUES (?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final StreakEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getCurrent());
        statement.bindLong(3, entity.getLongest());
        statement.bindString(4, entity.getLastActiveDate());
      }
    };
    this.__preparedStmtOfClear = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM streak";
        return _query;
      }
    };
  }

  @Override
  public Object updateStreak(final StreakEntity streak,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfStreakEntity.insert(streak);
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
  public Flow<StreakEntity> getStreak() {
    final String _sql = "SELECT * FROM streak WHERE id = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"streak"}, new Callable<StreakEntity>() {
      @Override
      @Nullable
      public StreakEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCurrent = CursorUtil.getColumnIndexOrThrow(_cursor, "current");
          final int _cursorIndexOfLongest = CursorUtil.getColumnIndexOrThrow(_cursor, "longest");
          final int _cursorIndexOfLastActiveDate = CursorUtil.getColumnIndexOrThrow(_cursor, "lastActiveDate");
          final StreakEntity _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final int _tmpCurrent;
            _tmpCurrent = _cursor.getInt(_cursorIndexOfCurrent);
            final int _tmpLongest;
            _tmpLongest = _cursor.getInt(_cursorIndexOfLongest);
            final String _tmpLastActiveDate;
            _tmpLastActiveDate = _cursor.getString(_cursorIndexOfLastActiveDate);
            _result = new StreakEntity(_tmpId,_tmpCurrent,_tmpLongest,_tmpLastActiveDate);
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
  public Object getStreakOnce(final Continuation<? super StreakEntity> $completion) {
    final String _sql = "SELECT * FROM streak WHERE id = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<StreakEntity>() {
      @Override
      @Nullable
      public StreakEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCurrent = CursorUtil.getColumnIndexOrThrow(_cursor, "current");
          final int _cursorIndexOfLongest = CursorUtil.getColumnIndexOrThrow(_cursor, "longest");
          final int _cursorIndexOfLastActiveDate = CursorUtil.getColumnIndexOrThrow(_cursor, "lastActiveDate");
          final StreakEntity _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final int _tmpCurrent;
            _tmpCurrent = _cursor.getInt(_cursorIndexOfCurrent);
            final int _tmpLongest;
            _tmpLongest = _cursor.getInt(_cursorIndexOfLongest);
            final String _tmpLastActiveDate;
            _tmpLastActiveDate = _cursor.getString(_cursorIndexOfLastActiveDate);
            _result = new StreakEntity(_tmpId,_tmpCurrent,_tmpLongest,_tmpLastActiveDate);
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
