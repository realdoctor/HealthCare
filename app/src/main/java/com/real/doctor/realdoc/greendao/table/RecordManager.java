package com.real.doctor.realdoc.greendao.table;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.real.doctor.realdoc.application.RealDocApplication;
import com.real.doctor.realdoc.greendao.DaoMaster;
import com.real.doctor.realdoc.greendao.DaoSession;
import com.real.doctor.realdoc.greendao.RecordBeanDao;
import com.real.doctor.realdoc.model.RecordBean;
import com.real.doctor.realdoc.util.EmptyUtils;
import org.greenrobot.greendao.query.QueryBuilder;
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
     * 查询音频list列表
     */
    public List<RecordBean> queryRecordList(Context context) {
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        RecordBeanDao recordBeanDao = daoSession.getRecordBeanDao();
        QueryBuilder<RecordBean> qb = recordBeanDao.queryBuilder();
        List<RecordBean> list = qb.where(RecordBeanDao.Properties.IsPatient.isNull()).list();
        return list;
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
     * 插入音频list
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
     * 插入音频list
     *
     * @param beanList
     */
    public void insertGlobedRecordList(Context context, List<RecordBean> beanList) {
        if (EmptyUtils.isEmpty(beanList)) {
            return;
        }
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        daoSession.deleteAll(RecordBean.class);
        RecordBeanDao recordBeanDao = daoSession.getRecordBeanDao();
        recordBeanDao.insertOrReplaceInTx(beanList);
    }

    /**
     * 插入每一个病人上传病历时的音频list
     *
     * @param beanList
     */
    public void insertPatientRecordList(Context context, List<RecordBean> beanList, String time, String folderName) {
        if (EmptyUtils.isEmpty(beanList)) {
            return;
        }
        DaoSession daoSession = RealDocApplication.getPatientDaoSession(context, time, folderName);
        RecordBeanDao recordBeanDao = daoSession.getRecordBeanDao();
        recordBeanDao.insertOrReplaceInTx(beanList);
    }

    /**
     * 插入音频list
     *
     * @param beanList
     */
    public void insertGlobeRecordList(Context context, List<RecordBean> beanList, String mobile, String folderName) {
        if (EmptyUtils.isEmpty(beanList)) {
            return;
        }
        DaoSession daoSession = RealDocApplication.getGlobeDaoSession(context, mobile, folderName);
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
        List<RecordBean> list = null;
        if (EmptyUtils.isNotEmpty(folder)) {
            list = qb.where(RecordBeanDao.Properties.Folder.eq(folder),RecordBeanDao.Properties.IsPatient.isNull()).orderDesc(RecordBeanDao.Properties.ElapsedMillis).list();
        }
        return list;
    }

    public List<RecordBean> queryRecordWithRecordId(Context context, String recordId) {
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        RecordBeanDao recordBeanDao = daoSession.getRecordBeanDao();
        QueryBuilder<RecordBean> qb = recordBeanDao.queryBuilder();
        List<RecordBean> list = qb.where(RecordBeanDao.Properties.RecordId.eq(recordId),RecordBeanDao.Properties.IsPatient.isNull()).orderDesc(RecordBeanDao.Properties.ElapsedMillis).list();
        return list;
    }

    /**
     * 为每一个病人上传病历时查询音频list列表
     */
    public List<RecordBean> queryPatientRecordWithRecordId(Context context, String recordId, String
            time, String folderName) {
        DaoSession daoSession = RealDocApplication.getPatientDaoSession(context, time, folderName);
        RecordBeanDao recordBeanDao = daoSession.getRecordBeanDao();
        QueryBuilder<RecordBean> qb = recordBeanDao.queryBuilder();
        List<RecordBean> list = qb.where(RecordBeanDao.Properties.RecordId.eq(recordId), RecordBeanDao.Properties.IsPatient.isNull()).orderDesc(RecordBeanDao.Properties.ElapsedMillis).list();
        return list;
    }

    /**
     * 查询音频list列表
     */
    public List<RecordBean> queryGlobeRecord(Context context, String
            mobile, String folderName) {
        DaoSession daoSession = RealDocApplication.getGlobeDaoSession(context, mobile, folderName);
        RecordBeanDao recordBeanDao = daoSession.getRecordBeanDao();
        QueryBuilder<RecordBean> qb = recordBeanDao.queryBuilder();
        List<RecordBean> list = qb.where(RecordBeanDao.Properties.IsPatient.isNull()).list();
        return list;
    }

    /**
     * 删除一条记录
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

