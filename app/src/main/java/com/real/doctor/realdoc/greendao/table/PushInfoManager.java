package com.real.doctor.realdoc.greendao.table;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.real.doctor.realdoc.application.RealDocApplication;
import com.real.doctor.realdoc.greendao.DaoMaster;
import com.real.doctor.realdoc.greendao.DaoSession;
import com.real.doctor.realdoc.greendao.PushInfoBeanDao;
import com.real.doctor.realdoc.model.PushInfoBean;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

public class PushInfoManager {
    public final static String dbName = "save_doc";
    private static PushInfoManager mInstance;
    private DaoMaster.DevOpenHelper openHelper;
    private Context context;

    public PushInfoManager(Context context) {
        this.context = context;
        openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
    }

    /**
     * 获取单例引用
     *
     * @param context
     * @return
     */
    public static PushInfoManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (PushInfoManager.class) {
                if (mInstance == null) {
                    mInstance = new PushInfoManager(context);
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
     * 查询消息list列表
     */
    public List<PushInfoBean> queryPushInfoList(Context context) {
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        PushInfoBeanDao pushInfoBeanDao = daoSession.getPushInfoBeanDao();
        QueryBuilder<PushInfoBean> qb = pushInfoBeanDao.queryBuilder();
        List<PushInfoBean> list = qb.list();
        return list;
    }

    /**
     * 插入一条消息记录
     *
     * @param bean
     */
    public void insertPushInfo(Context context, PushInfoBean bean) {
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        PushInfoBeanDao pushInfoBeanDao = daoSession.getPushInfoBeanDao();
        pushInfoBeanDao.insertOrReplace(bean);
    }

    /**
     * 删除所有记录
     *
     * @return
     */
    public boolean deleteAllPushInfo(Context context) {
        boolean flag = false;
        try {
            DaoSession daoSession = RealDocApplication.getDaoSession(context);
            PushInfoBeanDao pushInfoBeanDao = daoSession.getPushInfoBeanDao();
            pushInfoBeanDao.deleteAll();
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }
}
