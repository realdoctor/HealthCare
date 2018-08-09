package com.real.doctor.realdoc.greendao.table;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.real.doctor.realdoc.application.RealDocApplication;
import com.real.doctor.realdoc.greendao.DaoMaster;
import com.real.doctor.realdoc.greendao.DaoSession;
import com.real.doctor.realdoc.greendao.SearchProductBeanDao;
import com.real.doctor.realdoc.model.SearchProductBean;
import com.real.doctor.realdoc.util.EmptyUtils;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

public class SearchProductManager {

    public final static String dbName = "save_doc";
    private static SearchProductManager mInstance;
    private DaoMaster.DevOpenHelper openHelper;
    private Context context;

    public SearchProductManager(Context context) {
        this.context = context;
        openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
    }

    /**
     * 获取单例引用
     *
     * @param context
     * @return
     */
    public static SearchProductManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (SearchProductManager.class) {
                if (mInstance == null) {
                    mInstance = new SearchProductManager(context);
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
    public List<SearchProductBean> querySearchList(Context context) {
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        SearchProductBeanDao searchBeanDao = daoSession.getSearchProductBeanDao();
        QueryBuilder<SearchProductBean> qb = searchBeanDao.queryBuilder();
        List<SearchProductBean> list = qb.list();
        return list;
    }

    /**
     * 插入一条记录
     *
     * @param bean
     */
    public void insertSearch(Context context, SearchProductBean bean) {
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        SearchProductBeanDao searchBeanDao = daoSession.getSearchProductBeanDao();
        searchBeanDao.insertOrReplace(bean);
    }

    /**
     * 插入视频list
     *
     * @param beanList
     */
    public void insertSearchList(Context context, List<SearchProductBean> beanList) {
        if (EmptyUtils.isEmpty(beanList)) {
            return;
        }
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        SearchProductBeanDao searchBeanDao = daoSession.getSearchProductBeanDao();
        searchBeanDao.insertOrReplaceInTx(beanList);
    }

    /**
     * 查询视频list列表
     */
    public List<SearchProductBean> querySearchWithValue(Context context, String value) {
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        SearchProductBeanDao searchBeanDao = daoSession.getSearchProductBeanDao();
        QueryBuilder<SearchProductBean> qb = searchBeanDao.queryBuilder();
        List<SearchProductBean> list = qb.where(SearchProductBeanDao.Properties.Value.like("%" + value + "%")).list();
        return list;
    }


    /**
     * 删除一条记录
     */
    public void deleteSearchByValue(Context context, String value) {
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        SearchProductBeanDao searchBeanDao = daoSession.getSearchProductBeanDao();
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
            SearchProductBeanDao searchBeanDao = daoSession.getSearchProductBeanDao();
            searchBeanDao.deleteAll();
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }
}
