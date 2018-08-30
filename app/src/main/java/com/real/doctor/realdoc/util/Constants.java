package com.real.doctor.realdoc.util;

import java.io.File;

/**
 * Created by Administrator on 2017/8/27.
 */

public class Constants {
    //本地sp存储用户对应的key
    public static final String USER_KEY = "user_id";
    public static final String ROLE_ID = "role_id";
    public static final String ROLE_CHANGE_ID = "role_change_id";
    public static final String VERIFYFLAG = "verifyFlag";
    public static final String TOKEN = "token";
    public static final String MOBILE = "mobile";
    public static final String REALNAME = "realName";
    public static final String URL = "url";
    public static final String ORIGINALIMAGEURL = "originalImageUrl";
    public static final String COMMENT = "comment";
    public static final String COMMENT_TIME = "comment_time";
    public static final String COMMENT_FROM_MOBILE = "comment_from_mobile";
    public static final String COMMENT_MOBILE = "comment_mobile";
    public static final String COMMENT_USER_ID = "comment_user_id";
    public static final String CONNECT = "connect";
    public static final String CONNECT_TIME = "connect_time";
    public static final String CONNECT_TAG_ID = "connect_tag_id";
    //微信登录
    public static final String WX_APP_ID = "wxdcd80da641faa3ae";
    public static final String GetCodeRequest = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
    // 获取用户个人信息
    public static final String GetUserInfo = "https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID";
    public static final String WX_APP_SECRET = "";
    //QQ登录
    public static final String QQ_APP_ID = "";
    //新浪登录
    public static final String XINLANG_APP_KEY = "";
    public static final String XINLANG_APP_SECRET = "";
    public static final String Value = "com.real.doctor";

    /**
     * 当前 DEMO 应用的回调页，第三方应用可以使用自己的回调页。
     * <p>
     * <p>
     * 注：关于授权回调页对移动客户端应用来说对用户是不可见的，所以定义为何种形式都将不影响，
     * 但是没有定义将无法使用 SDK 认证登录。
     * 建议使用默认回调页：https://api.weibo.com/oauth2/default.html
     * </p>
     */
    public static final String REDIRECT_URL = "http://www.sina.com";

    /**
     * Scope 是 OAuth2.0 授权机制中 authorize 接口的一个参数。通过 Scope，平台将开放更多的微博
     * 核心功能给开发者，同时也加强用户隐私保护，提升了用户体验，用户在新 OAuth2.0 授权页中有权利
     * 选择赋予应用的功能。
     * <p>
     * 我们通过新浪微博开放平台-->管理中心-->我的应用-->接口管理处，能看到我们目前已有哪些接口的
     * 使用权限，高级权限需要进行申请。
     * <p>
     * 目前 Scope 支持传入多个 Scope 权限，用逗号分隔。
     * <p>
     * 有关哪些 OpenAPI 需要权限申请，请查看：http://open.weibo.com/wiki/%E5%BE%AE%E5%8D%9AAPI
     * 关于 Scope 概念及注意事项，请查看：http://open.weibo.com/wiki/Scope
     */
    public static final String SCOPE =
            "email,direct_messages_read,direct_messages_write,"
                    + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,";
}
