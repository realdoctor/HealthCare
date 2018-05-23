package com.real.doctor.realdoc.model;

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
public class ImageBean {
    @Id
    private String id;
    private String imgUrl = "";
    private int spareImage = 0; // 用于显示"0"无,"1"处方,"2"医嘱,"3"体征,"4"体检报告
    private String advice = "";
    private String imageId;
    @Generated(hash = 2059577659)
    public ImageBean(String id, String imgUrl, int spareImage, String advice,
            String imageId) {
        this.id = id;
        this.imgUrl = imgUrl;
        this.spareImage = spareImage;
        this.advice = advice;
        this.imageId = imageId;
    }
    @Generated(hash = 645668394)
    public ImageBean() {
    }
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


}
