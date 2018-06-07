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
    public String photoAddress="https://gss1.bdstatic.com/-vo3dSag_xI4khGkpoWK1HF6hhy/baike/c0%3Dbaike116%2C5%2C5%2C116%2C38/sign=1300500efffaaf5190ee89eded3dff8b/cdbf6c81800a19d861257c2d31fa828ba61e46f0.jpg";
    public String putOn;
    public String viewedTime;
}
