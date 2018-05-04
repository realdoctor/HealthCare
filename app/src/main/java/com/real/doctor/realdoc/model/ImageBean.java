package com.real.doctor.realdoc.model;

/**
 * @author zhujiabin
 * @package com.real.doctor.rdsurvey.bean
 * @fileName ${Name}
 * @Date 2018-2-7 0007
 * @describe TODO
 * @email zhujiabindragon@163.com
 */

public class ImageBean {
    private String id;
    private int spareImage = 0; // 备用显示图片
    private String advice = "";
    private String imgUrl = "";

    public ImageBean() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getAdvice() {
        return advice;
    }

    public void setAdvice(String advice) {
        this.advice = advice;
    }

    public int getSpareImage() {
        return spareImage;
    }

    public void setSpareImage(int spareImage) {
        this.spareImage = spareImage;
    }
}
