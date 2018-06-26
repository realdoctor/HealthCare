package com.real.doctor.realdoc.model;

public class VideoListBean extends VideoBean {

    private String cover;

    public VideoListBean() {
        super();
    }

    public VideoListBean(String fileName, String cover, String filePath) {
        this.fileName = fileName;
        this.cover = cover;
        this.filePath = filePath;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }
}
