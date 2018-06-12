package com.real.doctor.realdoc.greendao.table;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.real.doctor.realdoc.application.RealDocApplication;
import com.real.doctor.realdoc.greendao.DaoMaster;
import com.real.doctor.realdoc.greendao.DaoSession;
import com.real.doctor.realdoc.greendao.NewFriendsMsgsDao;
import com.real.doctor.realdoc.model.NewFriendsMsgs;
import com.real.doctor.realdoc.util.EmptyUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewsFriendsManager {

    public final static String dbName = "save_doc";
    private Context context;
    private static NewsFriendsManager mInstance;
    private DaoMaster.DevOpenHelper openHelper;
    protected Map<PrefManager.Key, Object> valueCache = new HashMap<PrefManager.Key, Object>();

    public NewsFriendsManager(Context context) {
        this.context = context;
        openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
    }

    /**
     * 获取单例引用
     *
     * @param context
     * @return
     */
    public static NewsFriendsManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (NewsFriendsManager.class) {
                if (mInstance == null) {
                    mInstance = new NewsFriendsManager(context);
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
    public void insertNewFriendsMsgs(Context context, NewFriendsMsgs bean) {
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        NewFriendsMsgsDao newFriendsMsgsDao = daoSession.getNewFriendsMsgsDao();
        newFriendsMsgsDao.insertOrReplace(bean);
    }

    /**
     * 插入list
     *
     * @param beanList
     */
    public void insertNewFriendsMsgsList(Context context, List<NewFriendsMsgs> beanList) {
        if (EmptyUtils.isEmpty(beanList)) {
            return;
        }
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        NewFriendsMsgsDao newFriendsMsgsDao = daoSession.getNewFriendsMsgsDao();
        newFriendsMsgsDao.insertOrReplaceInTx(beanList);
    }

}
