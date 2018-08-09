package com.real.doctor.realdoc.greendao.table;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.real.doctor.realdoc.application.RealDocApplication;
import com.real.doctor.realdoc.greendao.DaoMaster;
import com.real.doctor.realdoc.greendao.DaoSession;
import com.real.doctor.realdoc.greendao.SearchBeanDao;
import com.real.doctor.realdoc.model.SearchBean;
import com.real.doctor.realdoc.util.EmptyUtils;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

public class SearchManager {

    public final static String dbName = "save_doc";
    private static SearchManager mInstance;
    private DaoMaster.DevOpenHelper openHelper;
    private Context context;

    public SearchManager(Context context) {
        this.context = context;
        openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
    }

    /**
     * 获取单例引用
     *
     * @param context
     * @return
     */
    public static SearchManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (SearchManager.class) {
                if (mInstance == null) {
                    mInstance = new SearchManager(context);
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
     * 查询查询历史list列表
     */
    public List<SearchBean> querySearchList(Context context) {
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        SearchBeanDao searchBeanDao = daoSession.getSearchBeanDao();
        QueryBuilder<SearchBean> qb = searchBeanDao.queryBuilder();
        List<SearchBean> list = qb.list();
        return list;
    }

    /**
     * 插入一条记录
     *
     * @param bean
     */
    public void insertSearch(Context context, SearchBean bean) {
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        SearchBeanDao searchBeanDao = daoSession.getSearchBeanDao();
        searchBeanDao.insertOrReplace(bean);
    }

    /**
     * 插入视频list
     *
     * @param beanList
     */
    public void insertSearchList(Context context, List<SearchBean> beanList) {
        if (EmptyUtils.isEmpty(beanList)) {
            return;
        }
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        SearchBeanDao searchBeanDao = daoSession.getSearchBeanDao();
        searchBeanDao.insertOrReplaceInTx(beanList);
    }

    /**
     * 查询视频list列表
     */
    public List<SearchBean> querySearchWithValue(Context context, String value) {
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        SearchBeanDao searchBeanDao = daoSession.getSearchBeanDao();
        QueryBuilder<SearchBean> qb = searchBeanDao.queryBuilder();
        List<SearchBean> list = qb.where(SearchBeanDao.Properties.Value.like("%" + value + "%")).list();
        return list;
    }


    /**
     * 删除一条记录
     */
    public void deleteSearchByValue(Context context, String value) {
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        SearchBeanDao searchBeanDao = daoSession.getSearchBeanDao();
        searchBeanDao.deleteByKey(value);
    }

    /**
     * 删除所有记录
     *
     * @return
     */
    public boolean deleteAllSearch(Context context) {
        boolean flag = false;
        try {
            DaoSession daoSession = RealDocApplication.getDaoSession(context);
            SearchBeanDao searchBeanDao = daoSession.getSearchBeanDao();
            searchBeanDao.deleteAll();
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }
}
