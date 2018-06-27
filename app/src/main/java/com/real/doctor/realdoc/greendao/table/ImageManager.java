package com.real.doctor.realdoc.greendao.table;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.real.doctor.realdoc.application.RealDocApplication;
import com.real.doctor.realdoc.greendao.DaoMaster;
import com.real.doctor.realdoc.greendao.DaoSession;
import com.real.doctor.realdoc.greendao.ImageBeanDao;
import com.real.doctor.realdoc.greendao.ImageListBeanDao;
import com.real.doctor.realdoc.model.ImageBean;
import com.real.doctor.realdoc.model.ImageListBean;
import com.real.doctor.realdoc.util.EmptyUtils;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

public class ImageManager {

    public final static String dbName = "save_doc";
    private static ImageManager mInstance;
    private DaoMaster.DevOpenHelper openHelper;
    private Context context;

    public ImageManager(Context context) {
        this.context = context;
        openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
    }

    /**
     * 获取单例引用
     *
     * @param context
     * @return
     */
    public static ImageManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (ImageManager.class) {
                if (mInstance == null) {
                    mInstance = new ImageManager(context);
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
    public void insertImage(Context context, ImageBean bean) {
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        ImageBeanDao imageBeanDao = daoSession.getImageBeanDao();
        imageBeanDao.insertOrReplace(bean);
    }

    /**
     * 插入病历list
     *
     * @param beanList
     */
    public void insertImageList(Context context, List<ImageBean> beanList) {
        if (EmptyUtils.isEmpty(beanList)) {
            return;
        }
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        ImageBeanDao imageBeanDao = daoSession.getImageBeanDao();
        imageBeanDao.insertOrReplaceInTx(beanList);
    }

    public void insertPatientImageList(Context context, List<ImageBean> beanList,String time ,String folderName) {
        if (EmptyUtils.isEmpty(beanList)) {
            return;
        }
        DaoSession daoSession = RealDocApplication.getPatientDaoSession(context,time,folderName);
        ImageBeanDao imageBeanDao = daoSession.getImageBeanDao();
        imageBeanDao.insertOrReplaceInTx(beanList);
    }

    /**
     * 查询图片list列表
     */
    public List<ImageBean> queryImageList(Context context) {
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        ImageBeanDao saveDocDao = daoSession.getImageBeanDao();
        QueryBuilder<ImageBean> qb = saveDocDao.queryBuilder();
        List<ImageBean> list = qb.list();
        return list;
    }

    /**
     * 查询Imagelist列表
     */
    public List<ImageBean> queryImageByImageId(Context context, String imageListId) {
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        ImageBeanDao imageBeanDao = daoSession.getImageBeanDao();
        QueryBuilder<ImageBean> qb = imageBeanDao.queryBuilder();
        List<ImageBean> list = qb.where(ImageBeanDao.Properties.ImageId.eq(imageListId)).list();
        return list;
    }
    /**
     * 为每一个病人上传病历时查询Imagelist列表
     */
    public List<ImageBean> queryPatientImageByImageId(Context context, String imageListId, String time, String folderName) {
        DaoSession daoSession = RealDocApplication.getPatientDaoSession(context, time, folderName);
        ImageBeanDao imageBeanDao = daoSession.getImageBeanDao();
        QueryBuilder<ImageBean> qb = imageBeanDao.queryBuilder();
        List<ImageBean> list = qb.where(ImageBeanDao.Properties.ImageId.eq(imageListId)).list();
        return list;
    }

    private static final String SQL_IMAGE_URL_LIST = "SELECT DISTINCT " + ImageBeanDao.Properties.ImgUrl.columnName + " FROM " + ImageBeanDao.TABLENAME + " WHERE " + ImageBeanDao.Properties.ImageId.columnName + "=?";

    /**
     * 查询该份病历中Id列表
     */
    public static List<String> queryImageUrlList(DaoSession session, String imageId) {
        ArrayList<String> result = new ArrayList<String>();
        Cursor c = session.getDatabase().rawQuery(SQL_IMAGE_URL_LIST, new String[]{imageId});
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

    /**
     * 批量更新图片文件
     */
    public void updateImageUrlList(List<ImageBean> bean) throws Exception {
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        ImageBeanDao imageBeanDao = daoSession.getImageBeanDao();
        imageBeanDao.updateInTx(bean);
    }

    /**
     * 根据recordId删除数据
     */
    public void deleteImagesByImageId(String imageId) {
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        ImageBeanDao imageBeanDao = daoSession.getImageBeanDao();
        imageBeanDao.queryBuilder().where( ImageBeanDao.Properties.ImageId.eq(imageId)).buildDelete().executeDeleteWithoutDetachingEntities();
    }

    /**
     * 通过imageurl查询对应的医嘱
     */
    public List<ImageBean> queryAdviceByImageUrl(Context context, String imageUrl) {
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        ImageBeanDao imageBeanDao = daoSession.getImageBeanDao();
        QueryBuilder<ImageBean> qb = imageBeanDao.queryBuilder();
        List<ImageBean> list = qb.where(ImageBeanDao.Properties.ImgUrl.eq(imageUrl)).list();
        return list;
    }
    /**
     * 删除一条记录
     *
     * @param bean
     */
//    public void deleteRecordByName(Context context, String name) {
//        DaoSession daoSession = RealDocApplication.getDaoSession(context);
//        ImageBeanDao imageBeanDao = daoSession.getImageBeanDao();
//        imageBeanDao.deleteByKey(name);
//    }
}