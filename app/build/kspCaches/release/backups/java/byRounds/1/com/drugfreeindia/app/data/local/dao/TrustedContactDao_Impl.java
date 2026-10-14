package com.drugfreeindia.app.data.local.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.drugfreeindia.app.data.local.entities.TrustedContactEntity;
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
public final class TrustedContactDao_Impl implements TrustedContactDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<TrustedContactEntity> __insertionAdapterOfTrustedContactEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteContact;

  private final SharedSQLiteStatement __preparedStmtOfClear;

  public TrustedContactDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfTrustedContactEntity = new EntityInsertionAdapter<TrustedContactEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `trusted_contacts` (`id`,`name`,`phone`,`relationship`,`isPrimary`) VALUES (?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final TrustedContactEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getPhone());
        statement.bindString(4, entity.getRelationship());
        final int _tmp = entity.isPrimary() ? 1 : 0;
        statement.bindLong(5, _tmp);
      }
    };
    this.__preparedStmtOfDeleteContact = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM trusted_contacts WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfClear = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM trusted_contacts";
        return _query;
      }
    };
  }

  @Override
  public Object insertContact(final TrustedContactEntity contact,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfTrustedContactEntity.insert(contact);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteContact(final String id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteContact.acquire();
        int _argIndex = 1;
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
          __preparedStmtOfDeleteContact.release(_stmt);
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
  public Flow<List<TrustedContactEntity>> getAllContacts() {
    final String _sql = "SELECT * FROM trusted_contacts";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"trusted_contacts"}, new Callable<List<TrustedContactEntity>>() {
      @Override
      @NonNull
      public List<TrustedContactEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfPhone = CursorUtil.getColumnIndexOrThrow(_cursor, "phone");
          final int _cursorIndexOfRelationship = CursorUtil.getColumnIndexOrThrow(_cursor, "relationship");
          final int _cursorIndexOfIsPrimary = CursorUtil.getColumnIndexOrThrow(_cursor, "isPrimary");
          final List<TrustedContactEntity> _result = new ArrayList<TrustedContactEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TrustedContactEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpPhone;
            _tmpPhone = _cursor.getString(_cursorIndexOfPhone);
            final String _tmpRelationship;
            _tmpRelationship = _cursor.getString(_cursorIndexOfRelationship);
            final boolean _tmpIsPrimary;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsPrimary);
            _tmpIsPrimary = _tmp != 0;
            _item = new TrustedContactEntity(_tmpId,_tmpName,_tmpPhone,_tmpRelationship,_tmpIsPrimary);
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
