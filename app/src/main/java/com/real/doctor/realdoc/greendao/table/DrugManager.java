package com.real.doctor.realdoc.greendao.table;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.real.doctor.realdoc.application.RealDocApplication;
import com.real.doctor.realdoc.greendao.DaoMaster;
import com.real.doctor.realdoc.greendao.DaoSession;
import com.real.doctor.realdoc.greendao.DrugBeanDao;
import com.real.doctor.realdoc.model.DrugBean;
import com.real.doctor.realdoc.util.EmptyUtils;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

public class DrugManager {
    public final static String dbName = "save_doc";
    private static DrugManager mInstance;
    private DaoMaster.DevOpenHelper openHelper;
    private Context context;

    public DrugManager(Context context) {
        this.context = context;
        openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
    }

    /**
     * 获取单例引用
     *
     * @param context
     * @return
     */
    public static DrugManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (DrugManager.class) {
                if (mInstance == null) {
                    mInstance = new DrugManager(context);
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
    public void insertDrug(Context context, DrugBean bean) {
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        DrugBeanDao drugBeanDao = daoSession.getDrugBeanDao();
        drugBeanDao.insertOrReplace(bean);
    }

    /**
     * 插入病历list
     *
     * @param beanList
     */
    public void insertDrug(Context context, List<DrugBean> beanList) {
        if (EmptyUtils.isEmpty(beanList)) {
            return;
        }
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        DrugBeanDao drugBeanDao = daoSession.getDrugBeanDao();
        drugBeanDao.insertOrReplaceInTx(beanList);
    }

    /**
     * 查询处方list列表
     */
    public List<DrugBean> queryDrugList(Context context) {
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        DrugBeanDao drugBeanDao = daoSession.getDrugBeanDao();
        QueryBuilder<DrugBean> qb = drugBeanDao.queryBuilder();
        List<DrugBean> list = qb.list();
        return list;
    }

    /**
     * 根据recordId查询处方list列表
     */
    public List<DrugBean> queryDrugList(Context context, String recordId) {
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        DrugBeanDao drugBeanDao = daoSession.getDrugBeanDao();
        QueryBuilder<DrugBean> qb = drugBeanDao.queryBuilder();
        List<DrugBean> list = qb.where(DrugBeanDao.Properties.RecordId.eq(recordId)).list();
        return list;
    }
}
