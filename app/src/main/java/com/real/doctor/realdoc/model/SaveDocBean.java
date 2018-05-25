package com.real.doctor.realdoc.model;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.google.gson.annotations.SerializedName;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.JoinProperty;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;

import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

import com.real.doctor.realdoc.greendao.DaoSession;
import com.real.doctor.realdoc.greendao.VideoBeanDao;
import com.real.doctor.realdoc.greendao.RecordBeanDao;
import com.real.doctor.realdoc.greendao.ImageListBeanDao;
import com.real.doctor.realdoc.greendao.SaveDocBeanDao;

/**
 * Created by Administrator on 2018/4/24.
 */
@SuppressLint("ParcelCreator")
@Entity
public class SaveDocBean implements Parcelable{

    @Id
    @SerializedName("diagCode")
    private String id;
    //疾病
    @SerializedName("diagName")
    private String ill;
    //医院
    @SerializedName("visitOrgName")
    private String hospital;
    //医生
    @SerializedName("respDoctorName")
    private String doctor;
    //就诊时间
    @SerializedName("visitDtime")
    private String time;
    //存放病历图片的文件夹
    private String folder;
    //病历图片的名称
    private String imgs;
    @ToMany(joinProperties = {
            @JoinProperty(name = "id", referencedName = "recordId")
    })
    private List<ImageListBean> imageListBeans;
    @ToMany(joinProperties = {
            @JoinProperty(name = "id", referencedName = "recordId")
    })
    private List<RecordBean> audioList;
    @ToMany(joinProperties = {
            @JoinProperty(name = "id", referencedName = "recordId")
    })
    private List<VideoBean> videoList;
    //处方
    private String advice;
    //机构编码
    private String orgCode;
    //诊断信息id
    private String patientDiagId;
    //病人ID
    private String patientId;
    // 就诊科室名称
    private String visitDeptName;
    //就诊通道(1、门诊，2、住院)
    private String visitWay;
    //是否选中病历
    private boolean isSelect = false;
    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /**
     * Used for active entity operations.
     */
    @Generated(hash = 225895165)
    private transient SaveDocBeanDao myDao;

    public SaveDocBean() {
    }


@Generated(hash = 434754401)
public SaveDocBean(String id, String ill, String hospital, String doctor,
        String time, String folder, String imgs, String advice, String orgCode,
        String patientDiagId, String patientId, String visitDeptName,
        String visitWay, boolean isSelect) {
    this.id = id;
    this.ill = ill;
    this.hospital = hospital;
    this.doctor = doctor;
    this.time = time;
    this.folder = folder;
    this.imgs = imgs;
    this.advice = advice;
    this.orgCode = orgCode;
    this.patientDiagId = patientDiagId;
    this.patientId = patientId;
    this.visitDeptName = visitDeptName;
    this.visitWay = visitWay;
    this.isSelect = isSelect;
}


    protected SaveDocBean(Parcel in) {
        id = in.readString();
        ill = in.readString();
        hospital = in.readString();
        doctor = in.readString();
        time = in.readString();
        folder = in.readString();
        imgs = in.readString();
        audioList = in.createTypedArrayList(RecordBean.CREATOR);
        videoList = in.createTypedArrayList(VideoBean.CREATOR);
        advice = in.readString();
        orgCode = in.readString();
        patientDiagId = in.readString();
        patientId = in.readString();
        visitDeptName = in.readString();
        visitWay = in.readString();
        isSelect = in.readByte() != 0;
    }

    public static final Creator<SaveDocBean> CREATOR = new Creator<SaveDocBean>() {
        @Override
        public SaveDocBean createFromParcel(Parcel in) {
            return new SaveDocBean(in);
        }

        @Override
        public SaveDocBean[] newArray(int size) {
            return new SaveDocBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(ill);
        dest.writeString(hospital);
        dest.writeString(doctor);
        dest.writeString(time);
        dest.writeString(folder);
        dest.writeString(imgs);
        dest.writeTypedList(audioList);
        dest.writeTypedList(videoList);
        dest.writeString(advice);
        dest.writeString(orgCode);
        dest.writeString(patientDiagId);
        dest.writeString(patientId);
        dest.writeString(visitDeptName);
        dest.writeString(visitWay);
        dest.writeByte((byte) (isSelect ? 1 : 0));
    }


public String getId() {
    return this.id;
}


public void setId(String id) {
    this.id = id;
}


public String getIll() {
    return this.ill;
}


public void setIll(String ill) {
    this.ill = ill;
}


public String getHospital() {
    return this.hospital;
}


public void setHospital(String hospital) {
    this.hospital = hospital;
}


public String getDoctor() {
    return this.doctor;
}


public void setDoctor(String doctor) {
    this.doctor = doctor;
}


public String getTime() {
    return this.time;
}


public void setTime(String time) {
    this.time = time;
}


public String getFolder() {
    return this.folder;
}


public void setFolder(String folder) {
    this.folder = folder;
}


public String getImgs() {
    return this.imgs;
}


public void setImgs(String imgs) {
    this.imgs = imgs;
}


public String getAdvice() {
    return this.advice;
}


public void setAdvice(String advice) {
    this.advice = advice;
}


public String getOrgCode() {
    return this.orgCode;
}


public void setOrgCode(String orgCode) {
    this.orgCode = orgCode;
}


public String getPatientDiagId() {
    return this.patientDiagId;
}


public void setPatientDiagId(String patientDiagId) {
    this.patientDiagId = patientDiagId;
}


public String getPatientId() {
    return this.patientId;
}


public void setPatientId(String patientId) {
    this.patientId = patientId;
}


public String getVisitDeptName() {
    return this.visitDeptName;
}


public void setVisitDeptName(String visitDeptName) {
    this.visitDeptName = visitDeptName;
}


public String getVisitWay() {
    return this.visitWay;
}


public void setVisitWay(String visitWay) {
    this.visitWay = visitWay;
}


public boolean getIsSelect() {
    return this.isSelect;
}


public void setIsSelect(boolean isSelect) {
    this.isSelect = isSelect;
}


/**
 * To-many relationship, resolved on first access (and after reset).
 * Changes to to-many relations are not persisted, make changes to the target entity.
 */
@Generated(hash = 92669509)
public List<ImageListBean> getImageListBeans() {
    if (imageListBeans == null) {
        final DaoSession daoSession = this.daoSession;
        if (daoSession == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        ImageListBeanDao targetDao = daoSession.getImageListBeanDao();
        List<ImageListBean> imageListBeansNew = targetDao
                ._querySaveDocBean_ImageListBeans(id);
        synchronized (this) {
            if (imageListBeans == null) {
                imageListBeans = imageListBeansNew;
            }
        }
    }
    return imageListBeans;
}


/** Resets a to-many relationship, making the next get call to query for a fresh result. */
@Generated(hash = 640787854)
public synchronized void resetImageListBeans() {
    imageListBeans = null;
}


/**
 * To-many relationship, resolved on first access (and after reset).
 * Changes to to-many relations are not persisted, make changes to the target entity.
 */
@Generated(hash = 706159832)
public List<RecordBean> getAudioList() {
    if (audioList == null) {
        final DaoSession daoSession = this.daoSession;
        if (daoSession == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        RecordBeanDao targetDao = daoSession.getRecordBeanDao();
        List<RecordBean> audioListNew = targetDao
                ._querySaveDocBean_AudioList(id);
        synchronized (this) {
            if (audioList == null) {
                audioList = audioListNew;
            }
        }
    }
    return audioList;
}


/** Resets a to-many relationship, making the next get call to query for a fresh result. */
@Generated(hash = 1629719131)
public synchronized void resetAudioList() {
    audioList = null;
}


/**
 * To-many relationship, resolved on first access (and after reset).
 * Changes to to-many relations are not persisted, make changes to the target entity.
 */
@Generated(hash = 1048978764)
public List<VideoBean> getVideoList() {
    if (videoList == null) {
        final DaoSession daoSession = this.daoSession;
        if (daoSession == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        VideoBeanDao targetDao = daoSession.getVideoBeanDao();
        List<VideoBean> videoListNew = targetDao
                ._querySaveDocBean_VideoList(id);
        synchronized (this) {
            if (videoList == null) {
                videoList = videoListNew;
            }
        }
    }
    return videoList;
}


/** Resets a to-many relationship, making the next get call to query for a fresh result. */
@Generated(hash = 1186219891)
public synchronized void resetVideoList() {
    videoList = null;
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


/** called by internal mechanisms, do not call yourself. */
@Generated(hash = 748456124)
public void __setDaoSession(DaoSession daoSession) {
    this.daoSession = daoSession;
    myDao = daoSession != null ? daoSession.getSaveDocBeanDao() : null;
}
}
