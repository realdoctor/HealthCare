package com.real.doctor.realdoc.greendao.table;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.real.doctor.realdoc.application.RealDocApplication;
import com.real.doctor.realdoc.greendao.DaoMaster;
import com.real.doctor.realdoc.greendao.DaoSession;
import com.real.doctor.realdoc.greendao.NewFriendsMsgsDao;
import com.real.doctor.realdoc.model.NewFriendsMsgs;
import com.real.doctor.realdoc.util.EmptyUtils;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
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
     * 查询list列表
     */
    public List<NewFriendsMsgs> queryNewFriendsMsgsList(Context context) {
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        NewFriendsMsgsDao newFriendsMsgsDao = daoSession.getNewFriendsMsgsDao();
        QueryBuilder<NewFriendsMsgs> qb = newFriendsMsgsDao.queryBuilder();
        List<NewFriendsMsgs> list = qb.list();
        return list;
    }

    /**
     * 按时间查询list列表
     */
    public List<NewFriendsMsgs> queryNewFriendsMsgsListByTime(Context context) {
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        NewFriendsMsgsDao newFriendsMsgsDao = daoSession.getNewFriendsMsgsDao();
        QueryBuilder<NewFriendsMsgs> qb = newFriendsMsgsDao.queryBuilder();
        List<NewFriendsMsgs> list = qb.orderDesc(NewFriendsMsgsDao.Properties.Time).list();
        return list;
    }

    /**
     * 按时间查询list列表
     */
    public List<NewFriendsMsgs> queryNewFriendsMsgsListById(Context context, String id) {
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        NewFriendsMsgsDao newFriendsMsgsDao = daoSession.getNewFriendsMsgsDao();
        QueryBuilder<NewFriendsMsgs> qb = newFriendsMsgsDao.queryBuilder();
        List<NewFriendsMsgs> list = qb.where(NewFriendsMsgsDao.Properties.Id.eq(id)).list();
        return list;
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

    /**
     * delete invitation message
     *
     * @param groupId
     */
    synchronized public void deleteGroupMessage(String groupId) {
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        NewFriendsMsgsDao newFriendsMsgsDao = daoSession.getNewFriendsMsgsDao();
        newFriendsMsgsDao.queryBuilder().where(NewFriendsMsgsDao.Properties.GroupId.eq(groupId)).buildDelete().executeDeleteWithoutDetachingEntities();
    }

    synchronized public void setUnreadNotifyCount(Context context, int count) {
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        NewFriendsMsgsDao newFriendsMsgsDao = daoSession.getNewFriendsMsgsDao();
        NewFriendsMsgs bean = new NewFriendsMsgs();
        bean.setUnreadMsgCount(String.valueOf(count));
        List<NewFriendsMsgs> list = queryNewFriendsMsgsList(context);
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setUnreadMsgCount(String.valueOf(count));
            newFriendsMsgsDao.update(list.get(i));
        }
    }

    /**
     * delete invitation message
     *
     * @param from
     */
    synchronized public void deleteMessage(Context context,String from) {
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        NewFriendsMsgsDao newFriendsMsgsDao = daoSession.getNewFriendsMsgsDao();
        newFriendsMsgsDao.queryBuilder().where(NewFriendsMsgsDao.Properties.UserName.eq(from)).buildDelete().executeDeleteWithoutDetachingEntities();
    }

    /**
     * get messges
     *
     * @return
     */
    synchronized public List<NewFriendsMsgs> getMessagesList(Context context) {
        List<NewFriendsMsgs> msgs = queryNewFriendsMsgsListByTime(context);
        return msgs;
    }

    /**
     * update message
     *
     * @param msgId
     * @param values
     */
    synchronized public void updateMessage(Context context, String msgId, String status) {
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        NewFriendsMsgsDao newFriendsMsgsDao = daoSession.getNewFriendsMsgsDao();
        List<NewFriendsMsgs> list = queryNewFriendsMsgsListById(context, msgId);
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setStatus(status);
            newFriendsMsgsDao.update(list.get(i));
        }
    }

    /**
     * delete invitation message
     *
     * @param groupId
     */
    synchronized public void deleteGroupMessage(Context context, String groupId, String from) {
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        NewFriendsMsgsDao newFriendsMsgsDao = daoSession.getNewFriendsMsgsDao();
        newFriendsMsgsDao.queryBuilder().where(NewFriendsMsgsDao.Properties.GroupId.eq(groupId), NewFriendsMsgsDao.Properties.UserName.eq(from)).buildDelete().executeDeleteWithoutDetachingEntities();
    }
}
