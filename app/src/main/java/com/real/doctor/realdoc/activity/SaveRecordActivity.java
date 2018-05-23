package com.real.doctor.realdoc.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.adapter.AudioAdapter;
import com.real.doctor.realdoc.adapter.ImageCardAdapter;
import com.real.doctor.realdoc.adapter.VideoAdapter;
import com.real.doctor.realdoc.application.RealDocApplication;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.greendao.table.ImageManager;
import com.real.doctor.realdoc.greendao.table.ImageRecycleManager;
import com.real.doctor.realdoc.greendao.table.RecordManager;
import com.real.doctor.realdoc.greendao.table.SaveDocManager;
import com.real.doctor.realdoc.greendao.table.VideoManager;
import com.real.doctor.realdoc.model.AddLabelBean;
import com.real.doctor.realdoc.model.ImageBean;
import com.real.doctor.realdoc.model.ImageListBean;
import com.real.doctor.realdoc.model.LabelBean;
import com.real.doctor.realdoc.model.RecordBean;
import com.real.doctor.realdoc.model.SaveDocBean;
import com.real.doctor.realdoc.model.VideoBean;
import com.real.doctor.realdoc.photopicker.PhotoPicker;
import com.real.doctor.realdoc.photopicker.PhotoPreview;
import com.real.doctor.realdoc.util.DateUtil;
import com.real.doctor.realdoc.util.DocUtils;
import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.util.FileProvider7;
import com.real.doctor.realdoc.util.KeyBoardUtils;
import com.real.doctor.realdoc.util.SDCardUtils;
import com.real.doctor.realdoc.util.StringUtils;
import com.real.doctor.realdoc.util.ToastUtil;
import com.real.doctor.realdoc.view.LabelsView;
import com.real.doctor.realdoc.view.SelectPopupWindow;

import java.io.File;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/4/23.
 */

public class SaveRecordActivity extends BaseActivity {

    @BindView(R.id.ill)
    EditText ill;
    @BindView(R.id.hospital)
    EditText hospital;
    @BindView(R.id.doctor)
    EditText doctor;
    @BindView(R.id.save_doc_layout)
    LinearLayout saveDocLayout;
    @BindView(R.id.button_save_doc)
    Button saveDoc;
    @BindView(R.id.right_title)
    TextView rightTitle;
    @BindView(R.id.finish_back)
    ImageView finishBack;
    @BindView(R.id.ill_labels)
    LabelsView illLabels;
    @BindView(R.id.hospital_labels)
    LabelsView hospitalLabels;
    @BindView(R.id.more_ill)
    TextView moreIll;
    @BindView(R.id.more_hospital)
    TextView moreHospital;
    @BindView(R.id.ill_linear)
    LinearLayout illLinear;
    @BindView(R.id.hospital_linear)
    LinearLayout hospitalLinear;
    @BindView(R.id.add_audio_linear)
    LinearLayout addAudioLinear;
    @BindView(R.id.add_video_linear)
    LinearLayout addVideoLinear;
    @BindView(R.id.audio_recycle_view)
    RecyclerView audioRecycleView;
    @BindView(R.id.video_recycle_view)
    RecyclerView videoRecycleView;
    @BindView(R.id.grid_recycle_view)
    RecyclerView gridRecycleView;
    @BindView(R.id.add_icon)
    ImageView addIcon;
    @BindView(R.id.record_doc_relative)
    RelativeLayout recordDocRelative;
    //随机数,做为病历的Id
    private String mRecordId;
    //底部弹出菜单
    private SelectPopupWindow mPopup;
    private String mCurrentPhotoPath;
    private SaveDocManager instance;
    private RecordManager recordInstance;
    private VideoManager videoInstance;
    private ImageManager imageInstance;
    private ImageRecycleManager imageRecycleInstance;
    private List<String> diseasesList;
    private List<String> hospitalsList;
    private String folder;
    private ImageCardAdapter imageCardAdapter;
    private AudioAdapter audioAdapter;
    private VideoAdapter videoAdapter;
    private List<RecordBean> audioList;
    private List<VideoBean> videoList;
    private List<ImageListBean> imageBeanList;
    //拍照
    private static final int REQUEST_CODE_TAKE_PHOTO = 0x110;
    //疾病标签
    ArrayList<LabelBean> labelList = new ArrayList<>();
    //医院标签
    ArrayList<LabelBean> hospitalList = new ArrayList<>();

    public static String RECORD_IMAGES_TEXT = "android.intent.action.record.images.text";

    @Override
    public int getLayoutId() {
        return R.layout.activity_record_doc;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
    }

    @Override
    public void initData() {
        //初始化存储文件夹
        String folderTime = DateUtil.timeStamp();
        folder = String.valueOf(folderTime);
        instance = SaveDocManager.getInstance(SaveRecordActivity.this);
        recordInstance = RecordManager.getInstance(SaveRecordActivity.this);
        videoInstance = VideoManager.getInstance(SaveRecordActivity.this);
        imageInstance = ImageManager.getInstance(SaveRecordActivity.this);
        imageRecycleInstance = ImageRecycleManager.getInstance(SaveRecordActivity.this);
        audioList = new ArrayList<>();
        audioAdapter = new AudioAdapter(R.layout.audio_item, audioList);
        audioRecycleView.setAdapter(audioAdapter);
        videoList = new ArrayList<>();
        videoAdapter = new VideoAdapter(R.layout.video_item, videoList);
        videoRecycleView.setAdapter(videoAdapter);
        imageBeanList = new ArrayList<>();
        initLable();
        mPopup = new SelectPopupWindow(SaveRecordActivity.this, itemsOnClick);
        rightTitle.setText("");
        rightTitle.setVisibility(View.GONE);
        localBroadcast();
    }

    private void localBroadcast() {
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(RECORD_IMAGES_TEXT);
        BroadcastReceiver mItemViewListClickReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                List<String> pictures = intent.getStringArrayListExtra("imgs");
                String advice = intent.getStringExtra("advice");
                AddLabelBean label = intent.getParcelableExtra("label");
                String name = null;
                int spare = 0;
                if (EmptyUtils.isNotEmpty(label)) {
                    name = label.getName();
                }
                if (StringUtils.equals(name, "处方标签")) {
                    spare = 1;
                } else if (StringUtils.equals(name, "医嘱标签")) {
                    spare = 2;
                } else if (StringUtils.equals(name, "体征标签")) {
                    spare = 3;
                } else if (StringUtils.equals(name, "报告检查标签")) {
                    spare = 4;
                } else {
                    spare = 0;
                }
                ImageListBean bean = new ImageListBean();
                bean.setContent(advice);
                List<ImageBean> list = new ArrayList<>();
                ImageBean imageBean = null;
                for (int i = 0; i < pictures.size(); i++) {
                    imageBean = new ImageBean();
                    // 用于显示"0"无,"1"处方,"2"医嘱,"3"体征,"4"体检报告
                    imageBean.setSpareImage(spare);
                    imageBean.setImgUrl(pictures.get(i));
                    list.add(imageBean);
                }
                bean.setmImgUrlList(list);
                bean.setDate(DateUtil.timeStamp());
                imageBeanList.add(bean);
                //添加进recycleview，并将"+"按钮放在recycleview下面
                gridRecycleView.setLayoutManager(new LinearLayoutManager(SaveRecordActivity.this, LinearLayoutManager.VERTICAL, false));
                imageCardAdapter = new ImageCardAdapter(SaveRecordActivity.this, R.layout.image_card_item, imageBeanList);
                //给RecyclerView设置适配器
                gridRecycleView.setAdapter(imageCardAdapter);
            }
        };
        broadcastManager.registerReceiver(mItemViewListClickReceiver, intentFilter);
    }

    private void initLable() {
        //疾病列表
        diseasesList = instance.queryDiseaseList(RealDocApplication.getDaoSession(SaveRecordActivity.this));
        switch (diseasesList.size()) {
            case 0:
                illLinear.setVisibility(View.GONE);
                break;
            case 1:
                labelList.add(new LabelBean(diseasesList.get(0), 1));
                illLabels.setLabels(labelList, new LabelsView.LabelTextProvider<LabelBean>() {
                    @Override
                    public CharSequence getLabelText(TextView label, int position, LabelBean data) {
                        return data.getName();
                    }
                });
                moreIll.setVisibility(View.GONE);
                break;
            case 2:
                labelList.add(new LabelBean(diseasesList.get(0), 1));
                labelList.add(new LabelBean(diseasesList.get(1), 2));
                illLabels.setLabels(labelList, new LabelsView.LabelTextProvider<LabelBean>() {
                    @Override
                    public CharSequence getLabelText(TextView label, int position, LabelBean data) {
                        return data.getName();
                    }
                });
                moreIll.setVisibility(View.GONE);
                break;
            default:
                illLinear.setVisibility(View.VISIBLE);
                moreIll.setVisibility(View.VISIBLE);
                labelList.add(new LabelBean(diseasesList.get(0), 1));
                labelList.add(new LabelBean(diseasesList.get(1), 2));
                illLabels.setLabels(labelList, new LabelsView.LabelTextProvider<LabelBean>() {
                    @Override
                    public CharSequence getLabelText(TextView label, int position, LabelBean data) {
                        return data.getName();
                    }
                });
                break;
        }
        hospitalsList = instance.queryHospitalList(RealDocApplication.getDaoSession(SaveRecordActivity.this));
        switch (hospitalsList.size()) {
            case 0:
                hospitalLinear.setVisibility(View.GONE);
                break;
            case 1:
                hospitalLinear.setVisibility(View.VISIBLE);
                hospitalList.add(new LabelBean(hospitalsList.get(0), 1));
                hospitalLabels.setLabels(hospitalList, new LabelsView.LabelTextProvider<LabelBean>() {
                    @Override
                    public CharSequence getLabelText(TextView label, int position, LabelBean data) {
                        return data.getName();
                    }
                });
                moreHospital.setVisibility(View.GONE);
                break;
            case 2:
                hospitalLinear.setVisibility(View.VISIBLE);
                hospitalList.add(new LabelBean(hospitalsList.get(0), 1));
                hospitalList.add(new LabelBean(hospitalsList.get(1), 2));
                hospitalLabels.setLabels(hospitalList, new LabelsView.LabelTextProvider<LabelBean>() {
                    @Override
                    public CharSequence getLabelText(TextView label, int position, LabelBean data) {
                        return data.getName();
                    }
                });
                moreHospital.setVisibility(View.GONE);
                break;
            default:
                hospitalLinear.setVisibility(View.VISIBLE);
                moreHospital.setVisibility(View.VISIBLE);
                hospitalList.add(new LabelBean(hospitalsList.get(0), 1));
                hospitalList.add(new LabelBean(hospitalsList.get(1), 2));
                hospitalLabels.setLabels(hospitalList, new LabelsView.LabelTextProvider<LabelBean>() {
                    @Override
                    public CharSequence getLabelText(TextView label, int position, LabelBean data) {
                        return data.getName();
                    }
                });
                break;
        }
    }

    @Override
    public void initEvent() {
        hospitalLabels.setOnLabelClickListener(new LabelsView.OnLabelClickListener() {
            @Override
            public void onLabelClick(TextView label, Object data, int position) {
                LabelBean hospitalObject = (LabelBean) data;
                String hospitalLabel = hospitalObject.getName();
                hospital.setText(hospitalLabel);
                hospital.setSelection(hospital.getText().length());
            }
        });
        illLabels.setOnLabelClickListener(new LabelsView.OnLabelClickListener() {
            @Override
            public void onLabelClick(TextView label, Object data, int position) {
                LabelBean illObject = (LabelBean) data;
                String illLabel = illObject.getName();
                ill.setText(illLabel);
                ill.setSelection(ill.getText().length());
            }
        });
        audioAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, final int position) {
                //长按事件,弹出删除框
                AlertDialog dialog = new AlertDialog.Builder(SaveRecordActivity.this)
                        .setMessage("是否删除该录音？")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //从数据库中删除该录音
                                if (EmptyUtils.isNotEmpty(recordInstance)) {
                                    recordInstance.deleteRecordByName(SaveRecordActivity.this, audioList.get(position).getFileName());
                                }
                                if (audioList.size() > 1) {
                                    //从文件夹中删除该录音，如果录音只有一条，删除该文件夹
                                    SDCardUtils.deleteFile(SDCardUtils.getSDCardPath() + "RealDoc" + File.separator + folder + File.separator + "music" + File.separator + audioList.get(position).getFileName());
                                } else {
                                    SDCardUtils.deleteFile(SDCardUtils.getSDCardPath() + "RealDoc" + File.separator + folder + File.separator + "music");
                                }
                                audioList.remove(position);
                                //notifyDataSetChanged()
                                if (EmptyUtils.isEmpty(audioAdapter)) {
                                    audioAdapter = new AudioAdapter(R.layout.audio_item, audioList);
                                    audioRecycleView.setAdapter(audioAdapter);
                                } else {
                                    audioAdapter.notifyDataSetChanged();
                                }
                            }
                        })
                        .create();
                dialog.show();

                // 在dialog执行show之后设置样式
                TextView tvMsg = (TextView) dialog.findViewById(android.R.id.message);
                tvMsg.setTextSize(16);
                tvMsg.setTextColor(Color.parseColor("#4E4E4E"));

                dialog.getButton(dialog.BUTTON_NEGATIVE).setTextSize(16);
                dialog.getButton(dialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#8C8C8C"));
                dialog.getButton(dialog.BUTTON_POSITIVE).setTextSize(16);
                dialog.getButton(dialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#1DA6DD"));

                try {
                    Field mAlert = AlertDialog.class.getDeclaredField("mAlert");
                    mAlert.setAccessible(true);
                    Object alertController = mAlert.get(dialog);

                    Field mTitleView = alertController.getClass().getDeclaredField("mTitleView");
                    mTitleView.setAccessible(true);

                    TextView tvTitle = (TextView) mTitleView.get(alertController);
                    if (null != tvTitle) {
                        tvTitle.setTextSize(16);
                        tvTitle.setTextColor(Color.parseColor("#000000"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        });
        videoAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, final int position) {
                //长按事件,弹出删除框
                AlertDialog dialog = new AlertDialog.Builder(SaveRecordActivity.this)
                        .setMessage("是否删除该录像？")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //从数据库中删除该录音
                                if (EmptyUtils.isNotEmpty(videoInstance)) {
                                    videoInstance.deleteVideoByName(SaveRecordActivity.this, videoList.get(position).getFileName());
                                }
                                if (videoList.size() > 1) {
                                    //从文件夹中删除该录音，如果录音只有一条，删除该文件夹
                                    SDCardUtils.deleteFile(SDCardUtils.getSDCardPath() + "RealDoc" + File.separator + folder + File.separator + "video" + File.separator + videoList.get(position).getFileName());
                                } else {
                                    SDCardUtils.deleteFile(SDCardUtils.getSDCardPath() + "RealDoc" + File.separator + folder + File.separator + "video");
                                }
                                videoList.remove(position);
                                //notifyDataSetChanged()
                                if (EmptyUtils.isEmpty(videoAdapter)) {
                                    videoAdapter = new VideoAdapter(R.layout.video_item, videoList);
                                    videoRecycleView.setAdapter(videoAdapter);
                                } else {
                                    videoAdapter.notifyDataSetChanged();
                                }
                            }
                        })
                        .create();
                dialog.show();

                // 在dialog执行show之后设置样式
                TextView tvMsg = (TextView) dialog.findViewById(android.R.id.message);
                tvMsg.setTextSize(16);
                tvMsg.setTextColor(Color.parseColor("#4E4E4E"));

                dialog.getButton(dialog.BUTTON_NEGATIVE).setTextSize(16);
                dialog.getButton(dialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#8C8C8C"));
                dialog.getButton(dialog.BUTTON_POSITIVE).setTextSize(16);
                dialog.getButton(dialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#1DA6DD"));

                try {
                    Field mAlert = AlertDialog.class.getDeclaredField("mAlert");
                    mAlert.setAccessible(true);
                    Object alertController = mAlert.get(dialog);

                    Field mTitleView = alertController.getClass().getDeclaredField("mTitleView");
                    mTitleView.setAccessible(true);

                    TextView tvTitle = (TextView) mTitleView.get(alertController);
                    if (null != tvTitle) {
                        tvTitle.setTextSize(16);
                        tvTitle.setTextColor(Color.parseColor("#000000"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    @OnClick({R.id.button_save_doc, R.id.right_title, R.id.finish_back, R.id.more_ill, R.id.more_hospital, R.id.add_audio_linear, R.id.add_video_linear, R.id.add_icon})
    public void widgetClick(View v) {
        Intent intent = null;
        Bundle bundle = null;
        switch (v.getId()) {
            case R.id.button_save_doc:
                if (DocUtils.isFastClick()) {
                    //保存文字,图片，录音，录像信息
                    SaveDocBean bean = new SaveDocBean();
                    mRecordId = String.valueOf(Math.random());
                    bean.setId(mRecordId);
                    int videoLength = videoList.size();
                    for (int i = 0; i < videoLength; i++) {
                        videoList.get(i).setRecordId(mRecordId);
                    }
                    videoInstance.insertVideoList(this, videoList);
                    int audioLength = audioList.size();
                    for (int j = 0; j < audioLength; j++) {
                        audioList.get(j).setRecordId(mRecordId);
                    }
                    recordInstance.insertRecordList(this, audioList);
                    int imageListLength = imageBeanList.size();
                    for (int k = 0; k < imageListLength; k++) {
                        imageBeanList.get(k).setRecordId(mRecordId);
                        String random = String.valueOf(Math.random());
                        imageBeanList.get(k).setId(random);
                        List<ImageBean> imageBean = imageBeanList.get(k).getmImgUrlList();
                        int imageBeanLength = imageBean.size();
                        for (int x = 0; x < imageBeanLength; x++) {
                            imageBean.get(x).setImageId(random);
                            imageBean.get(x).setId(String.valueOf(Math.random()));
                        }
                        imageInstance.insertImageList(this, imageBean);
                    }
                    imageRecycleInstance.insertImageListList(this, imageBeanList);
                    String illness = ill.getText().toString().trim();
                    bean.setIll(illness);
                    String hospitaName = hospital.getText().toString().trim();
                    if (EmptyUtils.isEmpty(hospitaName)) {
                        ToastUtil.showLong(this, "请填写就诊医院名称!");
                        return;
                    }
                    bean.setHospital(hospitaName);
                    String doctorName = doctor.getText().toString().trim();
                    if (EmptyUtils.isEmpty(doctorName)) {
                        ToastUtil.showLong(this, "请填写就诊医生!");
                        return;
                    }
                    bean.setDoctor(doctorName);
                    bean.setFolder(folder);
                    bean.setTime(DateUtil.timeStamp());
                    //保存到病历表中
                    instance.insertSaveDoc(this, bean);
                    ToastUtil.showLong(this, "数据保存成功");
                    //广播通知刷新列表
                    intent = new Intent(RecordListActivity.RECORD_LIST_TEXT);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                    finish();
                }
                break;
            case R.id.right_title:
                actionStart(SaveRecordActivity.this, RecordListActivity.class);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case R.id.finish_back:
                finish();
                break;
            case R.id.more_ill:
                //进入疾病标签页
                intent = new Intent(this, IllLabelActivity.class);
                startActivityForResult(intent, 100);
                break;
            case R.id.more_hospital:
                //进入医院标签页
                intent = new Intent(this, HospitalLabelActivity.class);
                startActivityForResult(intent, 110);
                break;
            case R.id.add_audio_linear:
                if (DocUtils.isFastClick()) {
                    intent = new Intent(this, RecordActivity.class);
                    bundle = new Bundle();
                    bundle.putString("folder", folder);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, 111);
                }
                break;
            case R.id.add_video_linear:
                if (DocUtils.isFastClick()) {
                    intent = new Intent(this, VideoActivity.class);
                    bundle = new Bundle();
                    bundle.putString("folder", folder);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, 112);
                }
                break;
            case R.id.add_icon:
                mPopup.showAtLocation(recordDocRelative, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
                backgroundAlpha(0.5f);
                mPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        //改变显示的按钮图片为正常状态
                        backgroundAlpha(1);
                    }
                });
                break;
        }
    }

    @Override
    public void doBusiness(Context mContext) {

    }


    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            backgroundAlpha(1);
            mPopup.dismiss();
            int i = v.getId();
            if (i == R.id.photo_upload) {//拍照上传
                requestPermission(new String[]{Manifest.permission.CAMERA}, 0x0002);
            } else if (i == R.id.select_photo) {//相册选择
                /**
                 * 读写SD卡
                 */
                requestPermission(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0x0001);
            }
        }
    };

    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        // 类型
        lp.alpha = bgAlpha; //0.0-1.0
        getWindow().setAttributes(lp);
    }

    /**
     * 权限成功回调函数
     *
     * @param requestCode
     */
    @Override
    public void permissionSuccess(int requestCode) {
        super.permissionSuccess(requestCode);
        switch (requestCode) {
            case 0x0001:
                PhotoPicker.builder()
                        .setPhotoCount(PhotoPicker.DEFAULT_MAX_COUNT)
                        .setShowCamera(false)
                        .setShowGif(false)
                        .setPreviewEnabled(true)//是否可以预览
                        .start(SaveRecordActivity.this, PhotoPicker.REQUEST_CODE);
                break;
            case 0x0002:
                takePhotoCompress();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<String> photos = new ArrayList<>();
        if (resultCode == RESULT_OK &&
                (requestCode == PhotoPicker.REQUEST_CODE || requestCode == PhotoPreview.REQUEST_CODE)) {
            photos = null;
            if (data != null) {
                photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
            }
            RecordImagesActivity.startRecordActivity(SaveRecordActivity.this,
                    (ArrayList<String>) photos);
        } else if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_TAKE_PHOTO) {
            photos.add(mCurrentPhotoPath);
            RecordImagesActivity.startRecordActivity(SaveRecordActivity.this,
                    (ArrayList<String>) photos);
        } else if (resultCode == RESULT_OK && requestCode == 100) {
            illLabels.clearAllSelect();
            String disease = data.getStringExtra("disease");
            ill.setText(disease);
            ill.setSelection(ill.getText().length());
        } else if (resultCode == RESULT_OK && requestCode == 110) {
            hospitalLabels.clearAllSelect();
            String hospitalLabel = data.getStringExtra("hospital");
            hospital.setText(hospitalLabel);
            hospital.setSelection(hospital.getText().length());
        } else if (resultCode == RESULT_OK && requestCode == 111) {
            // 从数据库中取出音频列表,并展示
            audioList = recordInstance.queryRecordWithFolder(SaveRecordActivity.this, folder);
            if (EmptyUtils.isNotEmpty(audioList) && audioList.size() > 0) {
                audioRecycleView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
                //notifyDataSetChanged()
                audioAdapter = new AudioAdapter(R.layout.audio_item, audioList);
                audioRecycleView.setAdapter(audioAdapter);
                initEvent();
            }
        } else if (resultCode == RESULT_OK && requestCode == 112) {
            // 从数据库中取出视频列表,并展示
            videoList = videoInstance.queryVideoWithFolder(SaveRecordActivity.this, folder);
            if (EmptyUtils.isNotEmpty(videoList) && videoList.size() > 0) {
                videoRecycleView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
                //notifyDataSetChanged()
                videoAdapter = new VideoAdapter(R.layout.video_item, videoList);
                videoRecycleView.setAdapter(videoAdapter);
                initEvent();
            }
        } else if (resultCode == RESULT_OK && requestCode == 0x1000) {
            String pos = data.getStringExtra("pos");
            String advice = data.getStringExtra("advice");
            imageCardAdapter.updateGridView(Integer.valueOf(pos), advice);
        } else if (resultCode == RESULT_OK && requestCode == ImageCardAdapter.REQUEST_CODE_REPLACE_IMAGE) {
            //更新图片
            photos = null;
            String groupPos = null;
            String pos = null;
            if (data != null) {
                photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                groupPos = data.getStringExtra("groupPos");
                pos = data.getStringExtra("pos");
            }
            imageCardAdapter.replaceImg(true);
            imageBeanList.get(Integer.valueOf(groupPos)).getmImgUrlList().get(Integer.valueOf(pos)).setImgUrl(photos.get(0));
            imageCardAdapter.notifyDataSetChanged();
        } else if (resultCode == RESULT_OK && requestCode == ImageCardAdapter.REQUEST_CODE_CHANGE_ADVICE) {
            //更改嘱咐
            String groupPos = data.getStringExtra("pos");
            String advice = data.getStringExtra("advice");
            imageBeanList.get(Integer.valueOf(groupPos)).setContent(advice);
            imageCardAdapter.notifyDataSetChanged();
        }
    }

    public void takePhotoCompress() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            String filename = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.CHINA)
                    .format(new Date()) + ".png";
            File file = new File(SDCardUtils.getSDCardPath(), filename);
            mCurrentPhotoPath = file.getAbsolutePath();
            // 仅需改变这一行
            Uri fileUri = FileProvider7.getUriForFile(this, file);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            startActivityForResult(takePictureIntent, REQUEST_CODE_TAKE_PHOTO);
        }
    }
}
