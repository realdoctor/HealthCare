package com.real.doctor.realdoc.util;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.application.RealDocApplication;
import com.real.doctor.realdoc.widget.progress.processutil.ProcessManager;
import com.real.doctor.realdoc.widget.progress.processutil.models.AndroidAppProcess;

import org.json.JSONObject;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * @author zhujiabin
 * @package com.real.doctor.lib.utils
 * @fileName ${Name}
 * @Date 2018-1-16 0016
 * @describe TODO
 * @email zhujiabindragon@163.com
 */

public class DocUtils {
    // 两次点击按钮之间的点击间隔不能少于1000毫秒
    private static final int MIN_CLICK_DELAY_TIME = 1000;
    private static long lastClickTime;

    /**
     * <p/>
     * private static boolean canRelease = false;
     * <p/>
     * public static void setReleaseable(boolean release) {
     * canRelease = release;
     * }
     * <p/>
     * public static boolean getRealeaseable() {
     * return canRelease;
     * }
     */
    public static boolean hasValue(JSONObject object, String tag) {
        boolean exist = false;
        if (object != null && object.has(tag)) {
            try {
                Object object2 = object.get(tag);
                // LOG.D(TAG + " object2 = "+object2 + " tag = "+tag);
                if (object2 != null && !object2.equals("")
                        && !object.isNull(tag)) {
                    exist = true;
                }
            } catch (Exception e) {
                // TODO: handle exception
            }

        }
        return exist;
    }

    //判断email格式是否正确
    public static boolean isEmail(String email) {
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);

        return m.matches();
    }

    //判断是否全是数字
    public boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    /**
     * Rxjava 参数上传
     */
    public static RequestBody toRequestBodyOfText(String value) {
        RequestBody body = RequestBody.create(MediaType.parse("text/plain"), value);
        return body;
    }

    public static RequestBody toRequestBodyOfImage(File pFile) {
        RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), pFile);
        return fileBody;
    }

    public static RequestBody toRequestBodyOfVideo(File pFile) {
        RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), pFile);
        return fileBody;
    }


    // 封装请求体，可以看到这里和OkHttp的请求体封装基本上是一样的
    @NonNull
    public static MultipartBody.Part prepareFilePart(String path) {
        File file = new File(path);
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), file);
        return MultipartBody.Part.createFormData("file", file.getName(), requestFile);
    }

    public static boolean isFastClick() {
        boolean flag = false;
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
            flag = true;
        }
        lastClickTime = curClickTime;
        return flag;
    }

    public static Bitmap getVideoThumbnail(String videoPath) {
        MediaMetadataRetriever media = new MediaMetadataRetriever();
        media.setDataSource(videoPath);
        Bitmap bitmap = media.getFrameAtTime();
        return bitmap;
    }

    /**
     * 获取dimens定义的大小
     *
     * @param dimensionId
     * @return
     */
    public static int getPixelById(int dimensionId) {
        return RealDocApplication.getContext().getResources().getDimensionPixelSize(dimensionId);
    }

    /**
     * 使用文件管理器打开指定文件夹，浏览里面的内容
     *
     * @return
     */
    public static void openAssignFolder(Context context, String path) {
        File file = new File(path);
        if (null == file || !file.exists()) {
            return;
        }
        //获取父目录
        File parentFlie = new File(file.getParent());
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setDataAndType(Uri.fromFile(parentFlie), "*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        context.startActivity(intent);
    }

    /**
     * 进度条对话框
     *
     * @param context
     * @return
     */
    public static Dialog getProgressDialog(Context context, String str) {
        LayoutInflater inflater = LayoutInflater.from(context);
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.dialog_progress, null);
        TextView textView = layout.findViewById(R.id.progress_txt);
        textView.setText(str);
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setCancelable(false)
                .setView(layout)
                .create();
        //此处设置位置窗体大小
        dialog.getWindow().setLayout(SizeUtils.dip2px(context, 300), LinearLayout.LayoutParams.WRAP_CONTENT);
        return dialog;
    }

    public static boolean getLinuxCoreInfo(Context context, String packageName) {

        List<AndroidAppProcess> processes = ProcessManager.getRunningForegroundApps(context);
        for (AndroidAppProcess appProcess : processes) {
            if (appProcess.getPackageName().equals(packageName) && appProcess.foreground) {
                return true;
            }
        }
        return false;
    }
}
