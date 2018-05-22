package com.real.doctor.realdoc.greendao.table;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.real.doctor.realdoc.application.RealDocApplication;
import com.real.doctor.realdoc.greendao.DaoMaster;
import com.real.doctor.realdoc.greendao.DaoSession;
import com.real.doctor.realdoc.greendao.ImageListBeanDao;
import com.real.doctor.realdoc.greendao.SaveDocBeanDao;
import com.real.doctor.realdoc.model.ImageListBean;
import com.real.doctor.realdoc.util.EmptyUtils;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

public class ImageRecycleManager {

    public final static String dbName = "save_doc";
    private static ImageRecycleManager mInstance;
    private DaoMaster.DevOpenHelper openHelper;
    private Context context;

    public ImageRecycleManager(Context context) {
        this.context = context;
        openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
    }

    /**
     * 获取单例引用
     *
     * @param context
     * @return
     */
    public static ImageRecycleManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (ImageRecycleManager.class) {
                if (mInstance == null) {
                    mInstance = new ImageRecycleManager(context);
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
    public void insertImageList(Context context, ImageListBean bean) {
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        ImageListBeanDao imageBeanListDao = daoSession.getImageListBeanDao();
        imageBeanListDao.insertOrReplace(bean);
    }

    /**
     * 插入病历list
     *
     * @param beanList
     */
    public void insertImageListList(Context context, List<ImageListBean> beanList) {
        if (EmptyUtils.isEmpty(beanList)) {
            return;
        }
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        ImageListBeanDao imageBeanListDao = daoSession.getImageListBeanDao();
        imageBeanListDao.insertOrReplaceInTx(beanList);
    }

    /**
     * 通过id查询列表list
     */
    public List<ImageListBean> queryImageListById(Context context, String recordId) {
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        ImageListBeanDao imageBeanListDao = daoSession.getImageListBeanDao();
        QueryBuilder<ImageListBean> qb = imageBeanListDao.queryBuilder();
        List<ImageListBean> list = qb.where(ImageListBeanDao.Properties.RecordId.eq(recordId)).list();
        return list;
    }


    private static final String SQL_ID_LIST = "SELECT DISTINCT " + ImageListBeanDao.Properties.Id.columnName + " FROM " + ImageListBeanDao.TABLENAME + " WHERE " + ImageListBeanDao.Properties.RecordId.columnName + "=?";

    /**
     * 查询该份病历中Id列表
     */
    public static List<String> queryIdList(DaoSession session, String recordId) {
        ArrayList<String> result = new ArrayList<String>();
        Cursor c = session.getDatabase().rawQuery(SQL_ID_LIST, new String[]{recordId});
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
}