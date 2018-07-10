package com.real.doctor.realdoc.greendao.table;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.real.doctor.realdoc.application.RealDocApplication;
import com.real.doctor.realdoc.greendao.DaoMaster;
import com.real.doctor.realdoc.greendao.DaoSession;
import com.real.doctor.realdoc.greendao.InqueryBeanDao;
import com.real.doctor.realdoc.model.InqueryBean;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

public class InqueryManager {

    public final static String dbName = "save_doc";
    private static InqueryManager mInstance;
    private DaoMaster.DevOpenHelper openHelper;
    private Context context;

    public InqueryManager(Context context) {
        this.context = context;
        openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
    }

    /**
     * 获取单例引用
     *
     * @param context
     * @return
     */
    public static InqueryManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (InqueryManager.class) {
                if (mInstance == null) {
                    mInstance = new InqueryManager(context);
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
    public void insertInquery(Context context, InqueryBean bean) {
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        InqueryBeanDao inqueryBeanDao = daoSession.getInqueryBeanDao();
        inqueryBeanDao.insertOrReplace(bean);
    }

    /**
     * 为每一个病人上传病历时插入一条咨询
     *
     * @param bean
     */
    public void insertPatientInquery(Context context, InqueryBean bean, String time, String folderName) {
        DaoSession daoSession = RealDocApplication.getPatientDaoSession(context, time, folderName);
        InqueryBeanDao inqueryBeanDao = daoSession.getInqueryBeanDao();
        inqueryBeanDao.insertOrReplace(bean);
    }

    /**
     * 查询list列表
     */
    public List<InqueryBean> queryInqueryList(Context context) {
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        InqueryBeanDao inqueryBeanDao = daoSession.getInqueryBeanDao();
        QueryBuilder<InqueryBean> qb = inqueryBeanDao.queryBuilder();
        List<InqueryBean> list = qb.list();
        return list;
    }

    /**
     * 为每一个病人上传病历时查询咨询list列表
     */
    public List<InqueryBean> queryPatientInqueryList(Context context, String time, String folderName) {
        DaoSession daoSession = RealDocApplication.getPatientDaoSession(context, time, folderName);
        InqueryBeanDao inqueryBeanDao = daoSession.getInqueryBeanDao();
        QueryBuilder<InqueryBean> qb = inqueryBeanDao.queryBuilder();
        List<InqueryBean> list = qb.list();
        return list;
    }


    /**
     * 根据recordId删除数据
     */
//    public void deleteImagesByImageId(String imageId) {
//        DaoSession daoSession = RealDocApplication.getDaoSession(context);
//        ImageBeanDao imageBeanDao = daoSession.getImageBeanDao();
//        imageBeanDao.queryBuilder().where(ImageBeanDao.Properties.ImageId.eq(imageId)).buildDelete().executeDeleteWithoutDetachingEntities();
//    }


}
