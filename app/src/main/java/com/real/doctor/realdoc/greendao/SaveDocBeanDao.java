package com.real.doctor.realdoc.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.real.doctor.realdoc.model.SaveDocBean;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "SAVE_DOC_BEAN".
*/
public class SaveDocBeanDao extends AbstractDao<SaveDocBean, String> {

    public static final String TABLENAME = "SAVE_DOC_BEAN";

    /**
     * Properties of entity SaveDocBean.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, String.class, "id", true, "ID");
        public final static Property Ill = new Property(1, String.class, "ill", false, "ILL");
        public final static Property Hospital = new Property(2, String.class, "hospital", false, "HOSPITAL");
        public final static Property Doctor = new Property(3, String.class, "doctor", false, "DOCTOR");
        public final static Property Imgs = new Property(4, String.class, "imgs", false, "IMGS");
        public final static Property IsSelect = new Property(5, boolean.class, "isSelect", false, "IS_SELECT");
    }


    public SaveDocBeanDao(DaoConfig config) {
        super(config);
    }
    
    public SaveDocBeanDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"SAVE_DOC_BEAN\" (" + //
                "\"ID\" TEXT PRIMARY KEY NOT NULL ," + // 0: id
                "\"ILL\" TEXT," + // 1: ill
                "\"HOSPITAL\" TEXT," + // 2: hospital
                "\"DOCTOR\" TEXT," + // 3: doctor
                "\"IMGS\" TEXT," + // 4: imgs
                "\"IS_SELECT\" INTEGER NOT NULL );"); // 5: isSelect
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"SAVE_DOC_BEAN\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, SaveDocBean entity) {
        stmt.clearBindings();
 
        String id = entity.getId();
        if (id != null) {
            stmt.bindString(1, id);
        }
 
        String ill = entity.getIll();
        if (ill != null) {
            stmt.bindString(2, ill);
        }
 
        String hospital = entity.getHospital();
        if (hospital != null) {
            stmt.bindString(3, hospital);
        }
 
        String doctor = entity.getDoctor();
        if (doctor != null) {
            stmt.bindString(4, doctor);
        }
 
        String imgs = entity.getImgs();
        if (imgs != null) {
            stmt.bindString(5, imgs);
        }
        stmt.bindLong(6, entity.getIsSelect() ? 1L: 0L);
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, SaveDocBean entity) {
        stmt.clearBindings();
 
        String id = entity.getId();
        if (id != null) {
            stmt.bindString(1, id);
        }
 
        String ill = entity.getIll();
        if (ill != null) {
            stmt.bindString(2, ill);
        }
 
        String hospital = entity.getHospital();
        if (hospital != null) {
            stmt.bindString(3, hospital);
        }
 
        String doctor = entity.getDoctor();
        if (doctor != null) {
            stmt.bindString(4, doctor);
        }
 
        String imgs = entity.getImgs();
        if (imgs != null) {
            stmt.bindString(5, imgs);
        }
        stmt.bindLong(6, entity.getIsSelect() ? 1L: 0L);
    }

    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0);
    }    

    @Override
    public SaveDocBean readEntity(Cursor cursor, int offset) {
        SaveDocBean entity = new SaveDocBean( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // ill
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // hospital
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // doctor
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // imgs
            cursor.getShort(offset + 5) != 0 // isSelect
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, SaveDocBean entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setIll(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setHospital(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setDoctor(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setImgs(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setIsSelect(cursor.getShort(offset + 5) != 0);
     }
    
    @Override
    protected final String updateKeyAfterInsert(SaveDocBean entity, long rowId) {
        return entity.getId();
    }
    
    @Override
    public String getKey(SaveDocBean entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(SaveDocBean entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}