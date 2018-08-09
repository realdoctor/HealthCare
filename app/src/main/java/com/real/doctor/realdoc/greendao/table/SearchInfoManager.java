package com.real.doctor.realdoc.greendao.table;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.real.doctor.realdoc.application.RealDocApplication;
import com.real.doctor.realdoc.greendao.DaoMaster;
import com.real.doctor.realdoc.greendao.DaoSession;
import com.real.doctor.realdoc.greendao.SearchInfoBeanDao;
import com.real.doctor.realdoc.model.SearchInfoBean;
import com.real.doctor.realdoc.util.EmptyUtils;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

public class SearchInfoManager {

    public final static String dbName = "save_doc";
    private static SearchInfoManager mInstance;
    private DaoMaster.DevOpenHelper openHelper;
    private Context context;

    public SearchInfoManager(Context context) {
        this.context = context;
        openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
    }

    /**
     * 获取单例引用
     *
     * @param context
     * @return
     */
    public static SearchInfoManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (SearchInfoManager.class) {
                if (mInstance == null) {
                    mInstance = new SearchInfoManager(context);
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
    public List<SearchInfoBean> querySearchList(Context context) {
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        SearchInfoBeanDao searchBeanDao = daoSession.getSearchInfoBeanDao();
        QueryBuilder<SearchInfoBean> qb = searchBeanDao.queryBuilder();
        List<SearchInfoBean> list = qb.list();
        return list;
    }

    /**
     * 插入一条记录
     *
     * @param bean
     */
    public void insertSearch(Context context, SearchInfoBean bean) {
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        SearchInfoBeanDao searchBeanDao = daoSession.getSearchInfoBeanDao();
        searchBeanDao.insertOrReplace(bean);
    }

    /**
     * 插入视频list
     *
     * @param beanList
     */
    public void insertSearchList(Context context, List<SearchInfoBean> beanList) {
        if (EmptyUtils.isEmpty(beanList)) {
            return;
        }
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        SearchInfoBeanDao searchBeanDao = daoSession.getSearchInfoBeanDao();
        searchBeanDao.insertOrReplaceInTx(beanList);
    }

    /**
     * 查询视频list列表
     */
    public List<SearchInfoBean> querySearchWithValue(Context context, String value) {
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        SearchInfoBeanDao searchBeanDao = daoSession.getSearchInfoBeanDao();
        QueryBuilder<SearchInfoBean> qb = searchBeanDao.queryBuilder();
        List<SearchInfoBean> list = qb.where(SearchInfoBeanDao.Properties.Value.like("%" + value + "%")).list();
        return list;
    }


    /**
     * 删除一条记录
     */
    public void deleteSearchByValue(Context context, String value) {
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        SearchInfoBeanDao searchBeanDao = daoSession.getSearchInfoBeanDao();
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
            SearchInfoBeanDao searchBeanDao = daoSession.getSearchInfoBeanDao();
            searchBeanDao.deleteAll();
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }
}
