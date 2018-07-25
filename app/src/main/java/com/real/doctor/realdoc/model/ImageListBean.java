package com.real.doctor.realdoc.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.JoinProperty;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;

import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

import com.real.doctor.realdoc.greendao.DaoSession;
import com.real.doctor.realdoc.greendao.ImageBeanDao;
import com.real.doctor.realdoc.greendao.ImageListBeanDao;

@Entity
public class ImageListBean implements Parcelable {
    //item生成日期(在数据库中作为外键)
    @Id
    private String id;
    private String date;
    private String content;
    private String recordId;
    @ToMany(joinProperties = {
            @JoinProperty(name = "id", referencedName = "imageId")
    })
    private List<ImageBean> mImgUrlList;
    //如果该字段是空,则该病历是本地的病历，否则如果是"1",就是患者传给医生的病历
    private String isPatient;
    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /**
     * Used for active entity operations.
     */
    @Generated(hash = 873861295)
    private transient ImageListBeanDao myDao;

    @Generated(hash = 1030138088)
    public ImageListBean(String id, String date, String content, String recordId,
                         String isPatient) {
        this.id = id;
        this.date = date;
        this.content = content;
        this.recordId = recordId;
        this.isPatient = isPatient;
    }

    @Generated(hash = 1158683224)
    public ImageListBean() {
    }

    protected ImageListBean(Parcel in) {
        id = in.readString();
        date = in.readString();
        content = in.readString();
        recordId = in.readString();
        mImgUrlList = in.createTypedArrayList(ImageBean.CREATOR);
        isPatient = in.readString();
    }

    public static final Creator<ImageListBean> CREATOR = new Creator<ImageListBean>() {
        @Override
        public ImageListBean createFromParcel(Parcel in) {
            return new ImageListBean(in);
        }

        @Override
        public ImageListBean[] newArray(int size) {
            return new ImageListBean[size];
        }
    };

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRecordId() {
        return this.recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getIsPatient() {
        return this.isPatient;
    }

    public void setIsPatient(String isPatient) {
        this.isPatient = isPatient;
    }

    public List<ImageBean> getmImgUrlList() {
        return mImgUrlList;
    }

    public void setmImgUrlList(List<ImageBean> mImgUrlList) {
        this.mImgUrlList = mImgUrlList;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 624123320)
    public List<ImageBean> getMImgUrlList() {
        if (mImgUrlList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ImageBeanDao targetDao = daoSession.getImageBeanDao();
            List<ImageBean> mImgUrlListNew = targetDao
                    ._queryImageListBean_MImgUrlList(id);
            synchronized (this) {
                if (mImgUrlList == null) {
                    mImgUrlList = mImgUrlListNew;
                }
            }
        }
        return mImgUrlList;
    }

    /**
     * Resets a to-many relationship, making the next get call to query for a fresh result.
     */
    @Generated(hash = 2083827047)
    public synchronized void resetMImgUrlList() {
        mImgUrlList = null;
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /**
     * called by internal mechanisms, do not call yourself.
     */
    @Generated(hash = 1250701509)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getImageListBeanDao() : null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(date);
        dest.writeString(content);
        dest.writeString(recordId);
        dest.writeTypedList(mImgUrlList);
        dest.writeString(isPatient);
    }
}
