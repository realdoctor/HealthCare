package com.real.doctor.realdoc.greendao.table;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.real.doctor.realdoc.application.RealDocApplication;
import com.real.doctor.realdoc.greendao.DaoMaster;
import com.real.doctor.realdoc.greendao.DaoSession;
import com.real.doctor.realdoc.greendao.RecieverAddressListBeanDao;
import com.real.doctor.realdoc.greendao.UserBeanDao;
import com.real.doctor.realdoc.model.RecieverAddressListBean;
import com.real.doctor.realdoc.model.UserBean;
import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.util.HuanXinPreferenceManager;
import com.real.doctor.realdoc.widget.Constant;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class RecieverAddressListManager {

    public final static String dbName = "save_doc";
    private Context context;
    private static RecieverAddressListManager mInstance;
    private DaoMaster.DevOpenHelper openHelper;
    public static final String COLUMN_NAME_DISABLED_IDS = "disabled_ids";

    public RecieverAddressListManager(Context context) {
        this.context = context;
        openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
    }

    /**
     * 获取单例引用
     *
     * @param context
     * @return
     */
    public static RecieverAddressListManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (RecieverAddressListManager.class) {
                if (mInstance == null) {
                    mInstance = new RecieverAddressListManager(context);
                    HuanXinPreferenceManager.init(context);
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
     * 查询地址list列表
     */
    public List<RecieverAddressListBean> queryBeanList(Context context) {
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        RecieverAddressListBeanDao userBeanDao = daoSession.getRecieverAddressListBeanDao();
        QueryBuilder<RecieverAddressListBean> qb = userBeanDao.queryBuilder();
        List<RecieverAddressListBean> list = qb.list();
        return list;
    }

    /**
     * 插入一条记录
     *
     * @param bean
     */
    public void insertBean(Context context, RecieverAddressListBean bean) {
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        RecieverAddressListBeanDao userBeanDao = daoSession.getRecieverAddressListBeanDao();
        userBeanDao.insertOrReplace(bean);
    }
    /**
     * 修改一条记录
     *
     * @param bean
     */
    public void updateBean(Context context, RecieverAddressListBean bean) {
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        RecieverAddressListBeanDao userBeanDao = daoSession.getRecieverAddressListBeanDao();
        userBeanDao.update(bean);
    }


    /**
     * delete a contact
     *
     */
    public void deleteBean(Long id) {
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        RecieverAddressListBeanDao userBeanDao = daoSession.getRecieverAddressListBeanDao();
        userBeanDao.deleteByKey(id);
    }

}
