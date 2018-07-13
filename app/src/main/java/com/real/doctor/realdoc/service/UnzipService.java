package com.real.doctor.realdoc.service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;

import com.real.doctor.realdoc.activity.CaseListActivity;
import com.real.doctor.realdoc.greendao.table.ImageManager;
import com.real.doctor.realdoc.greendao.table.ImageRecycleManager;
import com.real.doctor.realdoc.greendao.table.RecordManager;
import com.real.doctor.realdoc.greendao.table.SaveDocManager;
import com.real.doctor.realdoc.greendao.table.VideoManager;
import com.real.doctor.realdoc.model.ImageBean;
import com.real.doctor.realdoc.model.ImageListBean;
import com.real.doctor.realdoc.model.PatientBean;
import com.real.doctor.realdoc.model.RecordBean;
import com.real.doctor.realdoc.model.SaveDocBean;
import com.real.doctor.realdoc.model.VideoBean;
import com.real.doctor.realdoc.rxjavaretrofit.down.DownCallBack;
import com.real.doctor.realdoc.rxjavaretrofit.http.HttpRequestClient;
import com.real.doctor.realdoc.util.DataFilterUtil;
import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.util.FileUtils;
import com.real.doctor.realdoc.util.NetworkUtil;
import com.real.doctor.realdoc.util.SDCardUtils;
import com.real.doctor.realdoc.util.ToastUtil;
import com.real.doctor.realdoc.util.ZipUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class UnzipService extends JobService {

    public static final String TAG = UnzipService.class.getSimpleName();
    private PatientBean patientBean;
    private String srcUrl;
    //从数据库中获取数据
    private ImageManager imageInstance;
    private ImageRecycleManager imageRecycleInstance;
    //数据库处理
    private SaveDocManager instance;
    private RecordManager recordInstance;
    private VideoManager videoInstance;
    private List<SaveDocBean> list;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            srcUrl = patientBean.getSrc();
            if (EmptyUtils.isNotEmpty(srcUrl)) {
                //下载zip
                downLoadData();
            } else {
                //通知页面刷新数据
                Intent intent = new Intent(CaseListActivity.GET_LIST);
                LocalBroadcastManager.getInstance(UnzipService.this).sendBroadcast(intent);
            }
            return true;
        }
    });

    private void downLoadData() {
        if (NetworkUtil.isNetworkAvailable(UnzipService.this)) {
            File file = new File(srcUrl);
            if (file.exists()) {
                return;
            } else {
                String dirPath = SDCardUtils.getGlobalDir() + srcUrl.substring(srcUrl.lastIndexOf("/") + 1, srcUrl.length());
                HttpRequestClient.getInstance(UnzipService.this).createBaseApi().download(srcUrl, dirPath, new DownCallBack() {
                    @Override
                    public void onProgress(long fileSizeDownloaded) {
                        super.onProgress(fileSizeDownloaded);
                        //显示下载进度
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.showLong(UnzipService.this, "文件下载失败,请返回列表重新进入病历列表!");
                    }

                    @Override
                    public void onSucess(String filePath, String fileName, long fileSize) {
                        //zip包解压
                        try {
                            boolean zipFile = ZipUtils.unzipFile(filePath, SDCardUtils.getGlobalDir());
                            //插入数据库
                            if (zipFile) {
                                //解压完成,读取数据库文件,展示列表
                                String name = fileName.substring(0, fileName.lastIndexOf(".zip"));
                                String path = SDCardUtils.getGlobalDir() + name + File.separator + "datebases" + File.separator;
                                //过滤.db文件类型列表
                                //存储所有符合条件的文件名
                                List<String> names = new ArrayList<String>();
                                String[] fileTypes = {".db"};
                                DataFilterUtil dbfilter = null;
                                File pathFolder = new File(path);
                                for (int i = 0; i < fileTypes.length; i++) {
                                    dbfilter = new DataFilterUtil(fileTypes[i]);
                                    String[] filenames = pathFolder.list(dbfilter);
                                    for (int j = 0; j < filenames.length; j++) {
                                        names.add(filenames[j]);
                                    }
                                }
                                //截取.db前patient后字符串
                                String str = names.get(0);
                                str = str.substring(7, str.lastIndexOf(".db"));
                                String folderName = SDCardUtils.getGlobalDir() + name + File.separator;
                                //将子数据库数据导入到本地数据库文件中,然后删除子数据文件
                                list = instance.queryPatientSaveDocList(UnzipService.this, str, folderName);
                                System.out.print(list.size());
                                for (int i = 0; i < list.size(); i++) {
                                    String id = list.get(i).getId();
                                    //通过id查询item数据
                                    List<ImageListBean> imageListBeans = imageRecycleInstance.queryPatientImageListById(UnzipService.this, id, str, folderName);
                                    imageRecycleInstance.insertImageListList(UnzipService.this, imageListBeans);
                                    int imageListBeanLength = imageListBeans.size();
                                    for (int j = 0; j < imageListBeanLength; j++) {
                                        String imageId = imageListBeans.get(j).getId();
                                        List<ImageBean> images = imageInstance.queryPatientImageByImageId(UnzipService.this, imageId, str, folderName);
                                        imageInstance.insertImageList(UnzipService.this, images);
                                    }
                                    String folderStr = list.get(i).getFolder();
                                    if (EmptyUtils.isNotEmpty(folderStr)) {
                                        //通过folder查询视频数据
                                        List<VideoBean> videoList = videoInstance.queryPatientVideoWithFolder(UnzipService.this, folderStr, str, folderName);
                                        videoInstance.insertVideoList(UnzipService.this, videoList);
                                        //通过folder查询音频频数据
                                        List<RecordBean> recordList = recordInstance.queryPatientRecordWithFolder(UnzipService.this, folderStr, str, folderName);
                                        recordInstance.insertRecordList(UnzipService.this, recordList);
                                    }
                                }
                                instance.insertSaveDoc(UnzipService.this, list);
                                //删除数据库
                                FileUtils.deleteDir(path);
                            } else {
                                ToastUtil.showLong(UnzipService.this, "解压文件失败,请返回列表重新进入病历列表!");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        //通知页面刷新数据
                        Intent intent = new Intent(CaseListActivity.GET_LIST);
                        intent.putParcelableArrayListExtra("list", (ArrayList<? extends Parcelable>) list);
                        LocalBroadcastManager.getInstance(UnzipService.this).sendBroadcast(intent);
                    }
                });
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        instance = SaveDocManager.getInstance(UnzipService.this);
        imageInstance = ImageManager.getInstance(UnzipService.this);
        imageRecycleInstance = ImageRecycleManager.getInstance(UnzipService.this);
        recordInstance = RecordManager.getInstance(UnzipService.this);
        videoInstance = VideoManager.getInstance(UnzipService.this);
        if (intent != null) {
            patientBean = intent.getParcelableExtra("patientBean");
        }
        Message m = Message.obtain();
        handler.sendMessage(m);
        return START_STICKY;
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
