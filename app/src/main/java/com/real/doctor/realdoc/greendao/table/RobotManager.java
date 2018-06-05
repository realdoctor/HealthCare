package com.real.doctor.realdoc.greendao.table;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.hyphenate.util.HanziToPinyin;
import com.real.doctor.realdoc.application.RealDocApplication;
import com.real.doctor.realdoc.greendao.DaoMaster;
import com.real.doctor.realdoc.greendao.DaoSession;
import com.real.doctor.realdoc.greendao.RobotBeanDao;
import com.real.doctor.realdoc.model.RobotBean;
import com.real.doctor.realdoc.model.RobotUser;
import com.real.doctor.realdoc.util.EmptyUtils;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class RobotManager {

    public final static String dbName = "save_doc";
    private Context context;
    private static RobotManager mInstance;
    private DaoMaster.DevOpenHelper openHelper;


    public RobotManager(Context context) {
        this.context = context;
        openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
    }

    /**
     * 获取单例引用
     *
     * @param context
     * @return
     */
    public static RobotManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (RobotManager.class) {
                if (mInstance == null) {
                    mInstance = new RobotManager(context);
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
    public void insertRobot(Context context, RobotBean bean) {
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        RobotBeanDao robotBeanDao = daoSession.getRobotBeanDao();
        robotBeanDao.insertOrReplace(bean);
    }

    /**
     * 插入list
     *
     * @param beanList
     */
    public void insertRobotList(Context context, List<RobotBean> beanList) {
        if (EmptyUtils.isEmpty(beanList)) {
            return;
        }
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        RobotBeanDao robotBeanDao = daoSession.getRobotBeanDao();
        robotBeanDao.insertOrReplaceInTx(beanList);
    }


    /**
     * load robot list
     */
    synchronized public Map<String, RobotUser> getRobotList(DaoSession session) {
        Map<String, RobotUser> users = null;
        String SQL_LIST = "SELECT DISTINCT * FROM " + RobotBeanDao.TABLENAME;
        Cursor cursor = session.getDatabase().rawQuery(SQL_LIST, null);
        if (cursor.getCount() > 0) {
            users = new Hashtable<String, RobotUser>();
        }
        while (cursor.moveToNext()) {
            String username = cursor.getString(cursor.getColumnIndex( RobotBeanDao.Properties.Username.columnName));
            String nick = cursor.getString(cursor.getColumnIndex(RobotBeanDao.Properties.Nick.columnName));
            String avatar = cursor.getString(cursor.getColumnIndex(RobotBeanDao.Properties.Avatar.columnName));
            RobotUser user = new RobotUser(username);
            user.setNick(nick);
            user.setAvatar(avatar);
            String headerName = null;
            if (!TextUtils.isEmpty(user.getNick())) {
                headerName = user.getNick();
            } else {
                headerName = user.getUsername();
            }
            if (Character.isDigit(headerName.charAt(0))) {
                user.setInitialLetter("#");
            } else {
                user.setInitialLetter(HanziToPinyin.getInstance().get(headerName.substring(0, 1)).get(0).target
                        .substring(0, 1).toUpperCase());
                char header = user.getInitialLetter().toLowerCase().charAt(0);
                if (header < 'a' || header > 'z') {
                    user.setInitialLetter("#");
                }
            }

            try {
                users.put(username, user);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        return users;
    }
}
