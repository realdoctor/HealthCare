package com.real.doctor.realdoc.greendao;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.real.doctor.realdoc.model.SaveDocBean;

import com.real.doctor.realdoc.greendao.SaveDocBeanDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig saveDocBeanDaoConfig;

    private final SaveDocBeanDao saveDocBeanDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        saveDocBeanDaoConfig = daoConfigMap.get(SaveDocBeanDao.class).clone();
        saveDocBeanDaoConfig.initIdentityScope(type);

        saveDocBeanDao = new SaveDocBeanDao(saveDocBeanDaoConfig, this);

        registerDao(SaveDocBean.class, saveDocBeanDao);
    }
    
    public void clear() {
        saveDocBeanDaoConfig.clearIdentityScope();
    }

    public SaveDocBeanDao getSaveDocBeanDao() {
        return saveDocBeanDao;
    }

}
