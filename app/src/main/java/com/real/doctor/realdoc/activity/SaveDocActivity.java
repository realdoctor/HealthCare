package com.real.doctor.realdoc.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.adapter.GridAdapter;
import com.real.doctor.realdoc.application.RealDocApplication;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.greendao.table.SaveDocManager;
import com.real.doctor.realdoc.model.ImageBean;
import com.real.doctor.realdoc.model.LabelBean;
import com.real.doctor.realdoc.model.SaveDocBean;
import com.real.doctor.realdoc.photopicker.PhotoPicker;
import com.real.doctor.realdoc.photopicker.PhotoPreview;
import com.real.doctor.realdoc.util.DateUtil;
import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.util.FileProvider7;
import com.real.doctor.realdoc.util.SDCardUtils;
import com.real.doctor.realdoc.util.ToastUtil;
import com.real.doctor.realdoc.view.DocGridView;
import com.real.doctor.realdoc.view.LabelsView;
import com.real.doctor.realdoc.view.SaveDialogActivity;
import com.real.doctor.realdoc.view.SelectPopupWindow;

import java.io.File;
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

public class SaveDocActivity extends BaseActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    @BindView(R.id.ill)
    EditText ill;
    @BindView(R.id.hospital)
    EditText hospital;
    @BindView(R.id.doctor)
    EditText doctor;
    @BindView(R.id.doc_grid_view)
    DocGridView docGridView;
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
    private List<ImageBean> imageList;
    private GridAdapter adapter;
    //底部弹出菜单
    private SelectPopupWindow mPopup;
    private String mCurrentPhotoPath = null;
    private String mAdvice = null;
    private SaveDialogActivity dialog;
    private boolean flag = false;
    //拍照
    private static final int REQUEST_CODE_TAKE_PHOTO = 0x110;
    //疾病标签
    ArrayList<LabelBean> labelList = new ArrayList<>();
    //医院标签
    ArrayList<LabelBean> hospitalList = new ArrayList<>();

    @Override
    public int getLayoutId() {
        return R.layout.activity_save_doc;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
    }

    @Override
    public void initData() {
        initLable();
        imageList = new ArrayList<>();
        adapter = new GridAdapter(this, imageList);
        docGridView.setAdapter(adapter);
        mPopup = new SelectPopupWindow(SaveDocActivity.this, itemsOnClick);
        ImageBean bean = new ImageBean();
        bean.setSpareImage(R.mipmap.add);
        imageList.add(bean);
        rightTitle.setText("查看");
        rightTitle.setVisibility(View.VISIBLE);
    }

    private void initLable() {
        labelList.add(new LabelBean("心脏病", 1));
        labelList.add(new LabelBean("呼吸系统疾病", 2));
        illLabels.setLabels(labelList, new LabelsView.LabelTextProvider<LabelBean>() {
            @Override
            public CharSequence getLabelText(TextView label, int position, LabelBean data) {
                return data.getName();
            }
        });
        hospitalList.add(new LabelBean("杭州仁和医院", 1));
        hospitalList.add(new LabelBean("杭州第一人民医院", 2));
        hospitalLabels.setLabels(hospitalList, new LabelsView.LabelTextProvider<LabelBean>() {
            @Override
            public CharSequence getLabelText(TextView label, int position, LabelBean data) {
                return data.getName();
            }
        });
    }

    @Override
    public void initEvent() {
        docGridView.setOnItemClickListener(this);
        docGridView.setOnItemLongClickListener(this);
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (EmptyUtils.isNotEmpty(mCurrentPhotoPath) && flag) {
            ImageBean bean = new ImageBean();
            bean.setSpareImage(R.mipmap.add);
            bean.setImgUrl("");
            imageList.add(bean);
            flag = false;
            mCurrentPhotoPath = null;
        }
    }

    @Override
    @OnClick({R.id.button_save_doc, R.id.right_title, R.id.finish_back, R.id.more_ill, R.id.more_hospital})
    public void widgetClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.button_save_doc:
                //TODO 判断是否有数据库的存在
                SaveDocBean bean = new SaveDocBean();
                bean.setId(String.valueOf(Math.random()));
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
                StringBuilder sb = new StringBuilder();
                StringBuilder ad = new StringBuilder();
                String time = null;
                String folder = null;
                //将数据存储到数据库中
                //将存储到sdcard中
                if (SDCardUtils.isSDCardEnable()) {
                    imageList.remove(imageList.size() - 1); // 现将加号移除（有加号才能显示此按钮）
                    time = DateUtil.timeStamp();
                    folder = String.valueOf(time);
                    for (ImageBean image : imageList) {
                        String img = image.getImgUrl();
                        SDCardUtils.saveToSdCard(img, folder);
                        String fileName = img.substring(img.lastIndexOf("/") + 1, img.length());
                        sb.append(fileName);
                        sb.append(";");
                        ad.append(image.getAdvice());
                        ad.append(";");
                    }
                } else {
                    ToastUtil.showLong(RealDocApplication.getContext(), "病历数据保存失败!");
                    return;
                }
                bean.setFolder(folder);
                bean.setTime(time);
                bean.setImgs(sb.toString());
                bean.setAdvice(ad.toString());
                SaveDocManager instance = SaveDocManager.getInstance(SaveDocActivity.this);
                if (EmptyUtils.isNotEmpty(instance)) {
                    instance.insertSaveDoc(SaveDocActivity.this, bean);
                    ToastUtil.showLong(RealDocApplication.getContext(), "病历数据保存成功!");
                    finish();
                } else {
                    ToastUtil.showLong(RealDocApplication.getContext(), "病历数据保存失败!");
                    return;
                }
                break;
            case R.id.right_title:
                actionStart(SaveDocActivity.this, RecordListActivity.class);
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
        }
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // 获取点击的对象
        ImageBean imageBean = imageList.get(position);
        String url = imageBean.getImgUrl();
        int spare = imageBean.getSpareImage();
        ImageView imageView = view.findViewById(R.id.delete_icon);
        if (url.equals("") && spare != 0) {
            //显示窗口
            mPopup.showAtLocation(saveDocLayout, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
            backgroundAlpha(0.5f);
            mPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    //改变显示的按钮图片为正常状态
                    backgroundAlpha(1);
                }
            });
        } else if (imageView.getVisibility() == View.VISIBLE) {
            imageList.remove(position);
            adapter.notifyDataSetChanged();
            imageView.setVisibility(View.GONE);
        }
    }

    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            imageList.remove(imageList.size() - 1); // 现将加号移除（有加号才能显示此按钮）
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
                        .setPhotoCount(1)
                        .setShowCamera(false)
                        .setShowGif(false)
                        .setPreviewEnabled(true)//是否可以预览
                        .start(SaveDocActivity.this, PhotoPicker.REQUEST_CODE);
                break;
            case 0x0002:
                takePhotoCompress();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK &&
                (requestCode == PhotoPicker.REQUEST_CODE || requestCode == PhotoPreview.REQUEST_CODE)) {
            List<String> photos = null;
            if (data != null) {
                photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
            }
            mCurrentPhotoPath = photos.get(0);
            Intent intent = new Intent(this, SaveDialogActivity.class);
            intent.putExtra("mCurrentPhotoPath", mCurrentPhotoPath);
            startActivityForResult(intent, 0x111);
        } else if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_TAKE_PHOTO) {
            Intent intent = new Intent(this, SaveDialogActivity.class);
            intent.putExtra("mCurrentPhotoPath", mCurrentPhotoPath);
            startActivityForResult(intent, 0x111);
        } else if (resultCode == RESULT_OK && requestCode == 0x111) {
            flag = true;
            //弹出完善信息界面
            ImageBean imageBean = new ImageBean();
            String advice = data.getStringExtra("advice");
            String path = data.getStringExtra("path");
            imageBean.setAdvice(advice);
            imageBean.setImgUrl(path);
            imageList.add(imageBean);
            adapter.notifyDataSetChanged();
        } else if (resultCode == 0 && !flag && requestCode != 100 && requestCode != 110) {
            ImageBean imageBean = new ImageBean();
            imageBean.setSpareImage(R.mipmap.add);
            imageBean.setImgUrl("");
            imageList.add(imageBean);
            adapter.notifyDataSetChanged();
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

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        // 获取点击的对象
        ImageBean imageBean = imageList.get(position);
        String url = imageBean.getImgUrl();
        int spare = imageBean.getSpareImage();
        if (!url.equals("") && spare == 0) {
            ImageView imageView = view.findViewById(R.id.delete_icon);
            imageView.setVisibility(View.VISIBLE);
        }
        return false;
    }
}
