package com.real.doctor.realdoc.service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;

import com.real.doctor.realdoc.application.RealDocApplication;
import com.real.doctor.realdoc.greendao.table.ImageManager;
import com.real.doctor.realdoc.greendao.table.ImageRecycleManager;
import com.real.doctor.realdoc.greendao.table.RecordManager;
import com.real.doctor.realdoc.greendao.table.SaveDocManager;
import com.real.doctor.realdoc.greendao.table.VideoManager;
import com.real.doctor.realdoc.model.ImageBean;
import com.real.doctor.realdoc.model.ImageListBean;
import com.real.doctor.realdoc.model.RecordBean;
import com.real.doctor.realdoc.model.SaveDocBean;
import com.real.doctor.realdoc.model.VideoBean;
import com.real.doctor.realdoc.util.DataFilterUtil;
import com.real.doctor.realdoc.util.FileUtils;
import com.real.doctor.realdoc.util.SDCardUtils;
import com.real.doctor.realdoc.util.ZipUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class PatientListService extends JobService {

    //从数据库中获取数据
    private ImageManager imageInstance;
    private ImageRecycleManager imageRecycleInstance;
    //数据库处理
    private SaveDocManager instance;
    private RecordManager recordInstance;
    private VideoManager videoInstance;


    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            //解压文件夹中的文件
//        String patientPath = SDCardUtils.getGlobalDir()+"patient"+ File.separator;
            String patientPath = SDCardUtils.getGlobalDir();
            //要进行过滤的文件目录
            File folder = new File(patientPath);
            //存储所有符合条件的文件名
            List<String> fileNames = new ArrayList<String>();
            List<File> files = new ArrayList<File>();
            //过滤文件类型列表
            String[] Filetypes = {".zip"};
            DataFilterUtil filter = null;
            for (int i = 0; i < Filetypes.length; i++) {
                filter = new DataFilterUtil(Filetypes[i]);
                String[] filenames = folder.list(filter);
                for (int j = 0; j < filenames.length; j++) {
                    File file = new File(patientPath + filenames[j]);
                    fileNames.add(filenames[j]);
                    files.add(file);
                }
            }
            try {
                ZipUtils.unzipFiles(files, patientPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //获得每个解压文件中的数据库文件，合并到总数据库中
            for (int k = 0; k < fileNames.size(); k++) {
                String name = fileNames.get(k).substring(0, fileNames.get(k).lastIndexOf(".zip"));
                String path = patientPath + name + File.separator + "datebases" + File.separator;
                String folderName = patientPath + name + File.separator;
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
                //将子数据库数据导入到本地数据库文件中,然后删除子数据文件
                List<SaveDocBean> list = instance.queryPatientSaveDocList(PatientListService.this, str, folderName);
                System.out.print(list.size());
                for (int i = 0; i < list.size(); i++) {
                    String id = list.get(i).getId();
                    //通过id查询item数据
                    List<ImageListBean> imageListBeans = imageRecycleInstance.queryPatientImageListById(PatientListService.this, id, str, folderName);
                    imageRecycleInstance.insertImageListList(PatientListService.this, imageListBeans);
                    int imageListBeanLength = imageListBeans.size();
                    for (int j = 0; j < imageListBeanLength; j++) {
                        String imageId = imageListBeans.get(j).getId();
                        List<ImageBean> images = imageInstance.queryPatientImageByImageId(PatientListService.this, imageId, str, folderName);
                        imageInstance.insertImageList(PatientListService.this, images);
                    }
                    String folderStr = list.get(i).getFolder();
                    //通过folder查询视频数据
                    List<VideoBean> videoList = videoInstance.queryPatientVideoWithFolder(PatientListService.this, folderStr, str, folderName);
                    videoInstance.insertVideoList(PatientListService.this, videoList);
                    //通过folder查询音频频数据
                    List<RecordBean> recordList = recordInstance.queryPatientRecordWithFolder(PatientListService.this, folderStr, str, folderName);
                    recordInstance.insertRecordList(PatientListService.this, recordList);
                }
                instance.insertSaveDoc(PatientListService.this, list);
                //删除数据库
                FileUtils.deleteDir(path);
            }
            //动态注册广播
            Intent intent = new Intent(RealDocApplication.HAVE_PATIENT_LIST);
            LocalBroadcastManager.getInstance(PatientListService.this).sendBroadcast(intent);
            return true;
        }
    });


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        instance = SaveDocManager.getInstance(PatientListService.this);
        imageInstance = ImageManager.getInstance(PatientListService.this);
        imageRecycleInstance = ImageRecycleManager.getInstance(PatientListService.this);
        recordInstance = RecordManager.getInstance(PatientListService.this);
        videoInstance = VideoManager.getInstance(PatientListService.this);
        Message m = Message.obtain();
        handler.sendMessage(m);
        return START_STICKY;
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        jobFinished(params, false);//任务执行完后记得调用jobFinsih通知系统释放相关资源
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        handler.removeCallbacksAndMessages(null);
        return false;
    }
}
