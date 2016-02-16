package gt.research.dc.core.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import gt.research.dc.core.db.Apk;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "APK".
*/
public class ApkDao extends AbstractDao<Apk, String> {

    public static final String TABLENAME = "APK";

    /**
     * Properties of entity Apk.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, String.class, "id", true, "ID");
        public final static Property Version = new Property(1, String.class, "version", false, "VERSION");
        public final static Property Url = new Property(2, String.class, "url", false, "URL");
        public final static Property Latest = new Property(3, boolean.class, "latest", false, "LATEST");
        public final static Property PkgName = new Property(4, String.class, "pkgName", false, "PKG_NAME");
    };


    public ApkDao(DaoConfig config) {
        super(config);
    }
    
    public ApkDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"APK\" (" + //
                "\"ID\" TEXT PRIMARY KEY NOT NULL ," + // 0: id
                "\"VERSION\" TEXT NOT NULL ," + // 1: version
                "\"URL\" TEXT NOT NULL ," + // 2: url
                "\"LATEST\" INTEGER NOT NULL ," + // 3: latest
                "\"PKG_NAME\" TEXT);"); // 4: pkgName
        // Add Indexes
        db.execSQL("CREATE INDEX " + constraint + "IDX_APK_ID ON APK" +
                " (\"ID\");");
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"APK\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Apk entity) {
        stmt.clearBindings();
        stmt.bindString(1, entity.getId());
        stmt.bindString(2, entity.getVersion());
        stmt.bindString(3, entity.getUrl());
        stmt.bindLong(4, entity.getLatest() ? 1L: 0L);
 
        String pkgName = entity.getPkgName();
        if (pkgName != null) {
            stmt.bindString(5, pkgName);
        }
    }

    /** @inheritdoc */
    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.getString(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public Apk readEntity(Cursor cursor, int offset) {
        Apk entity = new Apk( //
            cursor.getString(offset + 0), // id
            cursor.getString(offset + 1), // version
            cursor.getString(offset + 2), // url
            cursor.getShort(offset + 3) != 0, // latest
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4) // pkgName
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Apk entity, int offset) {
        entity.setId(cursor.getString(offset + 0));
        entity.setVersion(cursor.getString(offset + 1));
        entity.setUrl(cursor.getString(offset + 2));
        entity.setLatest(cursor.getShort(offset + 3) != 0);
        entity.setPkgName(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
     }
    
    /** @inheritdoc */
    @Override
    protected String updateKeyAfterInsert(Apk entity, long rowId) {
        return entity.getId();
    }
    
    /** @inheritdoc */
    @Override
    public String getKey(Apk entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
