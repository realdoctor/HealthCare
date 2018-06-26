package com.real.doctor.realdoc.greendao.table;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.real.doctor.realdoc.application.RealDocApplication;
import com.real.doctor.realdoc.greendao.DaoMaster;
import com.real.doctor.realdoc.greendao.DaoSession;
import com.real.doctor.realdoc.greendao.RecordBeanDao;
import com.real.doctor.realdoc.greendao.VideoBeanDao;
import com.real.doctor.realdoc.model.RecordBean;
import com.real.doctor.realdoc.model.VideoBean;
import com.real.doctor.realdoc.util.EmptyUtils;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

public class VideoManager {
    public final static String dbName = "save_doc";
    private static VideoManager mInstance;
    private DaoMaster.DevOpenHelper openHelper;
    private Context context;

    public VideoManager(Context context) {
        this.context = context;
        openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
    }

    /**
     * 获取单例引用
     *
     * @param context
     * @return
     */
    public static VideoManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (VideoManager.class) {
                if (mInstance == null) {
                    mInstance = new VideoManager(context);
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
    public void insertVideo(Context context, VideoBean bean) {
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        VideoBeanDao videoBeanDao = daoSession.getVideoBeanDao();
        videoBeanDao.insertOrReplace(bean);
    }

    /**
     * 插入视频list
     *
     * @param beanList
     */
    public void insertVideoList(Context context, List<VideoBean> beanList) {
        if (EmptyUtils.isEmpty(beanList)) {
            return;
        }
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        VideoBeanDao videoBeanDao = daoSession.getVideoBeanDao();
        videoBeanDao.insertOrReplaceInTx(beanList);
    }

    /**
     * 插入每一个病人上传病历时的视频list
     *
     * @param beanList
     */
    public void insertPatientVideoList(Context context, List<VideoBean> beanList, String time, String folderName) {
        if (EmptyUtils.isEmpty(beanList)) {
            return;
        }
        DaoSession daoSession = RealDocApplication.getPatientDaoSession(context, time, folderName);
        VideoBeanDao videoBeanDao = daoSession.getVideoBeanDao();
        videoBeanDao.insertOrReplaceInTx(beanList);
    }

    /**
     * 查询视频list列表
     */
    public List<VideoBean> queryVideoWithFolder(Context context, String folder) {
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        VideoBeanDao videoBeanDao = daoSession.getVideoBeanDao();
        QueryBuilder<VideoBean> qb = videoBeanDao.queryBuilder();
        List<VideoBean> list = qb.where(VideoBeanDao.Properties.Folder.eq(folder)).orderDesc(VideoBeanDao.Properties.ElapsedMillis).list();
        return list;
    }

    /**
     * 为每一个病人上传病历时查询视频list列表
     */
    public List<VideoBean> queryPatientVideoWithFolder(Context context, String folder, String time, String folderName) {
        DaoSession daoSession = RealDocApplication.getPatientDaoSession(context, time, folderName);
        VideoBeanDao videoBeanDao = daoSession.getVideoBeanDao();
        QueryBuilder<VideoBean> qb = videoBeanDao.queryBuilder();
        List<VideoBean> list = qb.where(VideoBeanDao.Properties.Folder.eq(folder)).orderDesc(VideoBeanDao.Properties.ElapsedMillis).list();
        return list;
    }

    /**
     * 删除一条记录
     */
    public void deleteVideoByName(Context context, String name) {
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        VideoBeanDao videoBeanDao = daoSession.getVideoBeanDao();
        videoBeanDao.deleteByKey(name);
    }

    /**
     * 根据recordId删除数据
     */
    public void deleteVideosByRecordId(String recordId) {
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        VideoBeanDao videoBeanDao = daoSession.getVideoBeanDao();
        videoBeanDao.queryBuilder().where(VideoBeanDao.Properties.RecordId.eq(recordId)).buildDelete().executeDeleteWithoutDetachingEntities();
    }

    /**
     * 批量更新视频文件
     */
    public void updateVideo(List<VideoBean> bean) throws Exception {
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        VideoBeanDao videoBeanDao = daoSession.getVideoBeanDao();
        videoBeanDao.updateInTx(bean);
    }
}
