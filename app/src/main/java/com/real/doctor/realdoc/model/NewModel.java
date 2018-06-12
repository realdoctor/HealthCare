package com.real.doctor.realdoc.model;

import java.io.Serializable;

/**
 * Created by ZFT on 2018/5/29.
 * "article": null,
 "authorDept": "胸外科",
 "authorHos": "主任医师  |  \n\t\t\t\t\t\t\t北京协和医院",
 "authorProfer": null,
 "commend": 0,
 "createDate": null,
 "newsAuthor": "李泽坚",
 "newsId": 1141,
 "newsName": "怎样进行饮食护理？",
 "newsType": "家庭护理",
 "newsTypeId": 7,
 "photoAddress": null,
 "putOn": null,
 "viewedTime": null
 */

public class NewModel implements Serializable {
    public String article;
    public String authorDept;
    public String authorHos;
    public String authorProfer;
    public String commend;
    public String createDate;
    public String newsAuthor;
    public String newsId;
    public String newsName;
    public String newsType;
    public String newsTypeId;
    public String photoAddress="https://ss0.bdstatic.com/94oJfD_bAAcT8t7mm9GUKT-xh_/timg?image&quality=100&size=b4000_4000&sec=1526975425&di=74dfd10b301a448bbaf8a318a0f04d71&src=http://pic.58pic.com/58pic/15/26/31/29y58PIChz7_1024.jpg";
    public String putOn;
    public String viewedTime;
}
