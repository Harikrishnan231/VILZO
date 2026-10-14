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
import com.drugfreeindia.app.data.local.entities.ActivityEntity;
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
public final class ActivityDao_Impl implements ActivityDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ActivityEntity> __insertionAdapterOfActivityEntity;

  private final SharedSQLiteStatement __preparedStmtOfToggleJoin;

  private final SharedSQLiteStatement __preparedStmtOfClear;

  public ActivityDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfActivityEntity = new EntityInsertionAdapter<ActivityEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `activities` (`id`,`title`,`category`,`organizer`,`location`,`schedule`,`description`,`whatToBring`,`isVerified`,`isJoined`) VALUES (?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ActivityEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getTitle());
        statement.bindString(3, entity.getCategory());
        statement.bindString(4, entity.getOrganizer());
        statement.bindString(5, entity.getLocation());
        statement.bindString(6, entity.getSchedule());
        statement.bindString(7, entity.getDescription());
        statement.bindString(8, entity.getWhatToBring());
        final int _tmp = entity.isVerified() ? 1 : 0;
        statement.bindLong(9, _tmp);
        final int _tmp_1 = entity.isJoined() ? 1 : 0;
        statement.bindLong(10, _tmp_1);
      }
    };
    this.__preparedStmtOfToggleJoin = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE activities SET isJoined = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfClear = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM activities";
        return _query;
      }
    };
  }

  @Override
  public Object insertActivities(final List<ActivityEntity> activities,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfActivityEntity.insert(activities);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object toggleJoin(final String id, final boolean isJoined,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfToggleJoin.acquire();
        int _argIndex = 1;
        final int _tmp = isJoined ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
        _argIndex = 2;
        _stmt.bindString(_argIndex, id);
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
          __preparedStmtOfToggleJoin.release(_stmt);
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
  public Flow<List<ActivityEntity>> getAllActivities() {
    final String _sql = "SELECT * FROM activities";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"activities"}, new Callable<List<ActivityEntity>>() {
      @Override
      @NonNull
      public List<ActivityEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfOrganizer = CursorUtil.getColumnIndexOrThrow(_cursor, "organizer");
          final int _cursorIndexOfLocation = CursorUtil.getColumnIndexOrThrow(_cursor, "location");
          final int _cursorIndexOfSchedule = CursorUtil.getColumnIndexOrThrow(_cursor, "schedule");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfWhatToBring = CursorUtil.getColumnIndexOrThrow(_cursor, "whatToBring");
          final int _cursorIndexOfIsVerified = CursorUtil.getColumnIndexOrThrow(_cursor, "isVerified");
          final int _cursorIndexOfIsJoined = CursorUtil.getColumnIndexOrThrow(_cursor, "isJoined");
          final List<ActivityEntity> _result = new ArrayList<ActivityEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ActivityEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpOrganizer;
            _tmpOrganizer = _cursor.getString(_cursorIndexOfOrganizer);
            final String _tmpLocation;
            _tmpLocation = _cursor.getString(_cursorIndexOfLocation);
            final String _tmpSchedule;
            _tmpSchedule = _cursor.getString(_cursorIndexOfSchedule);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final String _tmpWhatToBring;
            _tmpWhatToBring = _cursor.getString(_cursorIndexOfWhatToBring);
            final boolean _tmpIsVerified;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsVerified);
            _tmpIsVerified = _tmp != 0;
            final boolean _tmpIsJoined;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsJoined);
            _tmpIsJoined = _tmp_1 != 0;
            _item = new ActivityEntity(_tmpId,_tmpTitle,_tmpCategory,_tmpOrganizer,_tmpLocation,_tmpSchedule,_tmpDescription,_tmpWhatToBring,_tmpIsVerified,_tmpIsJoined);
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
  public Object getActivityById(final String id,
      final Continuation<? super ActivityEntity> $completion) {
    final String _sql = "SELECT * FROM activities WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<ActivityEntity>() {
      @Override
      @Nullable
      public ActivityEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfOrganizer = CursorUtil.getColumnIndexOrThrow(_cursor, "organizer");
          final int _cursorIndexOfLocation = CursorUtil.getColumnIndexOrThrow(_cursor, "location");
          final int _cursorIndexOfSchedule = CursorUtil.getColumnIndexOrThrow(_cursor, "schedule");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfWhatToBring = CursorUtil.getColumnIndexOrThrow(_cursor, "whatToBring");
          final int _cursorIndexOfIsVerified = CursorUtil.getColumnIndexOrThrow(_cursor, "isVerified");
          final int _cursorIndexOfIsJoined = CursorUtil.getColumnIndexOrThrow(_cursor, "isJoined");
          final ActivityEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpOrganizer;
            _tmpOrganizer = _cursor.getString(_cursorIndexOfOrganizer);
            final String _tmpLocation;
            _tmpLocation = _cursor.getString(_cursorIndexOfLocation);
            final String _tmpSchedule;
            _tmpSchedule = _cursor.getString(_cursorIndexOfSchedule);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final String _tmpWhatToBring;
            _tmpWhatToBring = _cursor.getString(_cursorIndexOfWhatToBring);
            final boolean _tmpIsVerified;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsVerified);
            _tmpIsVerified = _tmp != 0;
            final boolean _tmpIsJoined;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsJoined);
            _tmpIsJoined = _tmp_1 != 0;
            _result = new ActivityEntity(_tmpId,_tmpTitle,_tmpCategory,_tmpOrganizer,_tmpLocation,_tmpSchedule,_tmpDescription,_tmpWhatToBring,_tmpIsVerified,_tmpIsJoined);
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
  public Object getCount(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM activities";
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
