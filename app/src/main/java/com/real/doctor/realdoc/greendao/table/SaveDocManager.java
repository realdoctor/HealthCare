package com.real.doctor.realdoc.greendao.table;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.real.doctor.realdoc.greendao.DaoMaster;
import com.real.doctor.realdoc.greendao.DaoSession;
import com.real.doctor.realdoc.greendao.SaveDocBeanDao;
import com.real.doctor.realdoc.model.SaveDocBean;
import com.real.doctor.realdoc.util.EmptyUtils;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

/**
 * Created by Administrator on 2018/4/24.
 */

public class SaveDocManager {
    private final static String dbName = "save_doc";
    private static SaveDocManager mInstance;
    private DaoMaster.DevOpenHelper openHelper;
    private Context context;

    public SaveDocManager(Context context) {
        this.context = context;
        openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
    }

    /**
     * 获取单例引用
     *
     * @param context
     * @return
     */
    public static SaveDocManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (SaveDocManager.class) {
                if (mInstance == null) {
                    mInstance = new SaveDocManager(context);
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
    public void insertSaveDoc(Context context, SaveDocBean bean) {
        SaveDocManager manager = SaveDocManager.getInstance(context);
        DaoMaster daoMaster = new DaoMaster(manager.getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        SaveDocBeanDao saveDocDao = daoSession.getSaveDocBeanDao();
        saveDocDao.insertOrReplace(bean);
    }

    /**
     * 插入Playlist
     *
     * @param beanList
     */
    public void insertSaveDoc(Context context, List<SaveDocBean> beanList) {
        if (EmptyUtils.isEmpty(beanList)) {
            return;
        }
        SaveDocManager manager = SaveDocManager.getInstance(context);
        DaoMaster daoMaster = new DaoMaster(manager.getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        daoSession.deleteAll(SaveDocBean.class);
        SaveDocBeanDao saveDocDao = daoSession.getSaveDocBeanDao();
        saveDocDao.insertOrReplaceInTx(beanList);
    }

    /**
     * 查询Playlist列表
     */
    public List<SaveDocBean> querySaveDocList(Context context) {
        SaveDocManager manager = SaveDocManager.getInstance(context);
        DaoMaster daoMaster = new DaoMaster(manager.getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        SaveDocBeanDao saveDocDao = daoSession.getSaveDocBeanDao();
        QueryBuilder<SaveDocBean> qb = saveDocDao.queryBuilder();
        List<SaveDocBean> list = qb.list();
        return list;
    }

    /**
     * 删除一条记录
     *
     * @param bean
     */
    public void deleteSaveDocList(SaveDocBean bean) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        SaveDocBeanDao saveDocDao = daoSession.getSaveDocBeanDao();
        saveDocDao.delete(bean);
    }
}

