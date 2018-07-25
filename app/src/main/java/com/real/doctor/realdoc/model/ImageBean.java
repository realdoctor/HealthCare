package com.real.doctor.realdoc.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @author zhujiabin
 * @package com.real.doctor.rdsurvey.bean
 * @fileName ${Name}
 * @Date 2018-2-7 0007
 * @describe TODO
 * @email zhujiabindragon@163.com
 */
@Entity
public class ImageBean implements Parcelable {
    @Id
    private String id;
    private String imgUrl = "";
    private int spareImage = 0; // 用于显示"0"无,"1"处方,"2"医嘱,"3"体征,"4"体检报告
    private String advice = "";
    private String imageId;
    //如果该字段是空,则该病历是本地的病历，否则如果是"1",就是患者传给医生的病历
    private String isPatient;

    @Generated(hash = 967313543)
    public ImageBean(String id, String imgUrl, int spareImage, String advice,
                     String imageId, String isPatient) {
        this.id = id;
        this.imgUrl = imgUrl;
        this.spareImage = spareImage;
        this.advice = advice;
        this.imageId = imageId;
        this.isPatient = isPatient;
    }

    @Generated(hash = 645668394)
    public ImageBean() {
    }

    protected ImageBean(Parcel in) {
        id = in.readString();
        imgUrl = in.readString();
        spareImage = in.readInt();
        advice = in.readString();
        imageId = in.readString();
        isPatient = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(imgUrl);
        dest.writeInt(spareImage);
        dest.writeString(advice);
        dest.writeString(imageId);
        dest.writeString(isPatient);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ImageBean> CREATOR = new Creator<ImageBean>() {
        @Override
        public ImageBean createFromParcel(Parcel in) {
            return new ImageBean(in);
        }

        @Override
        public ImageBean[] newArray(int size) {
            return new ImageBean[size];
        }
    };

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImgUrl() {
        return this.imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public int getSpareImage() {
        return this.spareImage;
    }

    public void setSpareImage(int spareImage) {
        this.spareImage = spareImage;
    }

    public String getAdvice() {
        return this.advice;
    }

    public void setAdvice(String advice) {
        this.advice = advice;
    }

    public String getImageId() {
        return this.imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getIsPatient() {
        return this.isPatient;
    }

    public void setIsPatient(String isPatient) {
        this.isPatient = isPatient;
    }
}
