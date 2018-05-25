package com.real.doctor.realdoc.greendao.table;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.real.doctor.realdoc.application.RealDocApplication;
import com.real.doctor.realdoc.greendao.DaoMaster;
import com.real.doctor.realdoc.greendao.DaoSession;
import com.real.doctor.realdoc.greendao.RecordBeanDao;
import com.real.doctor.realdoc.greendao.SaveDocBeanDao;
import com.real.doctor.realdoc.model.RecordBean;
import com.real.doctor.realdoc.model.SaveDocBean;
import com.real.doctor.realdoc.util.DateUtil;
import com.real.doctor.realdoc.util.EmptyUtils;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/4/24.
 */

public class RecordManager {
    public final static String dbName = "save_doc";
    private static RecordManager mInstance;
    private DaoMaster.DevOpenHelper openHelper;
    private Context context;

    public RecordManager(Context context) {
        this.context = context;
        openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
    }

    /**
     * 获取单例引用
     *
     * @param context
     * @return
     */
    public static RecordManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (RecordManager.class) {
                if (mInstance == null) {
                    mInstance = new RecordManager(context);
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
    public void insertRecord(Context context, RecordBean bean) {
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        RecordBeanDao recordBeanDao = daoSession.getRecordBeanDao();
        recordBeanDao.insertOrReplace(bean);
    }

    /**
     * 插入病历list
     *
     * @param beanList
     */
    public void insertRecordList(Context context, List<RecordBean> beanList) {
        if (EmptyUtils.isEmpty(beanList)) {
            return;
        }
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        RecordBeanDao recordBeanDao = daoSession.getRecordBeanDao();
        recordBeanDao.insertOrReplaceInTx(beanList);
    }

    /**
     * 查询音频list列表
     */
    public List<RecordBean> queryRecordWithFolder(Context context, String folder) {
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        RecordBeanDao recordBeanDao = daoSession.getRecordBeanDao();
        QueryBuilder<RecordBean> qb = recordBeanDao.queryBuilder();
        List<RecordBean> list = qb.where(RecordBeanDao.Properties.Folder.eq(folder)).orderDesc(RecordBeanDao.Properties.ElapsedMillis).list();
        return list;
    }

    /**
     * 删除一条记录
     *
     * @param bean
     */
    public void deleteRecordByName(Context context, String name) {
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        RecordBeanDao recordBeanDao = daoSession.getRecordBeanDao();
        recordBeanDao.deleteByKey(name);
    }

    /**
     * 批量更新音频文件
     */
    public void updateRecord(List<RecordBean> bean) throws Exception {
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        RecordBeanDao recordBeanDao = daoSession.getRecordBeanDao();
        recordBeanDao.updateInTx(bean);
    }

    /**
     * 根据recordId删除数据
     */
    public void deleteRecordsByRecordId(String recordId) {
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        RecordBeanDao recordBeanDao = daoSession.getRecordBeanDao();
        recordBeanDao.queryBuilder().where(RecordBeanDao.Properties.RecordId.eq(recordId)).buildDelete().executeDeleteWithoutDetachingEntities();
    }
}

