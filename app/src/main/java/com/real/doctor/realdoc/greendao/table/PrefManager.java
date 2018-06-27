package com.real.doctor.realdoc.greendao.table;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.real.doctor.realdoc.application.RealDocApplication;
import com.real.doctor.realdoc.greendao.DaoMaster;
import com.real.doctor.realdoc.greendao.DaoSession;
import com.real.doctor.realdoc.greendao.PrefBeanDao;
import com.real.doctor.realdoc.greendao.UserBeanDao;
import com.real.doctor.realdoc.model.PrefBean;
import com.real.doctor.realdoc.util.EmptyUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PrefManager {
    public final static String dbName = "save_doc";
    private Context context;
    private static PrefManager mInstance;
    private DaoMaster.DevOpenHelper openHelper;
    protected Map<Key, Object> valueCache = new HashMap<Key, Object>();

    public PrefManager(Context context) {
        this.context = context;
        openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
    }

    /**
     * 获取单例引用
     *
     * @param context
     * @return
     */
    public static PrefManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (PrefManager.class) {
                if (mInstance == null) {
                    mInstance = new PrefManager(context);
                }
            }
        }
        return mInstance;
    }

    /**
     * 获取可读数据库
     */
    public SQLiteDatabase getReadableDatabase() {
        if (openHelper == null) {
            openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
        }
        SQLiteDatabase db = openHelper.getReadableDatabase();
        return db;
    }

    /**
     * 获取可写数据库
     */
    public SQLiteDatabase getWritableDatabase() {
        if (openHelper == null) {
            openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
        }
        SQLiteDatabase db = openHelper.getWritableDatabase();
        return db;
    }

    /**
     * 插入一条记录
     *
     * @param bean
     */
    public void insertPref(Context context, PrefBean bean) {
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        PrefBeanDao prefBeanDao = daoSession.getPrefBeanDao();
        prefBeanDao.insertOrReplace(bean);
    }

    /**
     * 插入用户list
     *
     * @param beanList
     */
    public void insertPrefList(Context context, List<PrefBean> beanList) {
        if (EmptyUtils.isEmpty(beanList)) {
            return;
        }
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        PrefBeanDao prefBeanDao = daoSession.getPrefBeanDao();
        prefBeanDao.insertOrReplaceInTx(beanList);
    }

    public List<String> getDisabledIds() {
        Object val = valueCache.get(Key.DisabledIds);
        if (val == null) {
            val = queryColumList(RealDocApplication.getDaoSession(RealDocApplication.getContext()), PrefBeanDao.Properties.Disabled_ids.columnName);
            valueCache.put(Key.DisabledIds, val);
        }

        //noinspection unchecked
        return (List<String>) val;
    }

    public List<String> getDisabledGroups() {
        Object val = valueCache.get(Key.DisabledGroups);

        if (val == null) {
            val = queryColumList(RealDocApplication.getDaoSession(RealDocApplication.getContext()), PrefBeanDao.Properties.Disabled_groups.columnName);
            valueCache.put(Key.DisabledGroups, val);
        }

        //noinspection unchecked
        return (List<String>) val;
    }

    /**
     * 查询字段
     */
    synchronized public static List<String> queryColumList(DaoSession session, String column) {
        ArrayList<String> result = new ArrayList<String>();
        String SQL_COLUMN_LIST = "SELECT DISTINCT " + column + " FROM " + PrefBeanDao.TABLENAME;
        Cursor c = session.getDatabase().rawQuery(SQL_COLUMN_LIST, null);
        try {
            if (c.moveToFirst()) {
                do {
                    result.add(c.getString(0));
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        return result;
    }

    enum Key {
        DisabledGroups,
        DisabledIds
    }
}
