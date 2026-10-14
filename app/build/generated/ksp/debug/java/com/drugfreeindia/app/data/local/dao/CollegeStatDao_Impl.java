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
import com.drugfreeindia.app.data.local.entities.CollegeStatEntity;
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
public final class CollegeStatDao_Impl implements CollegeStatDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<CollegeStatEntity> __insertionAdapterOfCollegeStatEntity;

  private final SharedSQLiteStatement __preparedStmtOfClear;

  public CollegeStatDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfCollegeStatEntity = new EntityInsertionAdapter<CollegeStatEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `college_stats` (`collegeCode`,`collegeName`,`points`,`participants`,`rank`) VALUES (?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final CollegeStatEntity entity) {
        statement.bindString(1, entity.getCollegeCode());
        statement.bindString(2, entity.getCollegeName());
        statement.bindLong(3, entity.getPoints());
        statement.bindLong(4, entity.getParticipants());
        statement.bindLong(5, entity.getRank());
      }
    };
    this.__preparedStmtOfClear = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM college_stats";
        return _query;
      }
    };
  }

  @Override
  public Object insertCollegeStats(final List<CollegeStatEntity> stats,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfCollegeStatEntity.insert(stats);
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
  public Flow<List<CollegeStatEntity>> getCollegeStats() {
    final String _sql = "SELECT * FROM college_stats ORDER BY points DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"college_stats"}, new Callable<List<CollegeStatEntity>>() {
      @Override
      @NonNull
      public List<CollegeStatEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfCollegeCode = CursorUtil.getColumnIndexOrThrow(_cursor, "collegeCode");
          final int _cursorIndexOfCollegeName = CursorUtil.getColumnIndexOrThrow(_cursor, "collegeName");
          final int _cursorIndexOfPoints = CursorUtil.getColumnIndexOrThrow(_cursor, "points");
          final int _cursorIndexOfParticipants = CursorUtil.getColumnIndexOrThrow(_cursor, "participants");
          final int _cursorIndexOfRank = CursorUtil.getColumnIndexOrThrow(_cursor, "rank");
          final List<CollegeStatEntity> _result = new ArrayList<CollegeStatEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final CollegeStatEntity _item;
            final String _tmpCollegeCode;
            _tmpCollegeCode = _cursor.getString(_cursorIndexOfCollegeCode);
            final String _tmpCollegeName;
            _tmpCollegeName = _cursor.getString(_cursorIndexOfCollegeName);
            final int _tmpPoints;
            _tmpPoints = _cursor.getInt(_cursorIndexOfPoints);
            final int _tmpParticipants;
            _tmpParticipants = _cursor.getInt(_cursorIndexOfParticipants);
            final int _tmpRank;
            _tmpRank = _cursor.getInt(_cursorIndexOfRank);
            _item = new CollegeStatEntity(_tmpCollegeCode,_tmpCollegeName,_tmpPoints,_tmpParticipants,_tmpRank);
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
  public Object getCount(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM college_stats";
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
