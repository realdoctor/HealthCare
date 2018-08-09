package com.real.doctor.realdoc.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.adapter.InfoSearchAdapter;
import com.real.doctor.realdoc.adapter.ProductSearchAdapter;
import com.real.doctor.realdoc.adapter.QueryItemAdapter;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.fragment.ArticleShowFragment;
import com.real.doctor.realdoc.fragment.ShoppintMallFragment;
import com.real.doctor.realdoc.greendao.table.SearchInfoManager;
import com.real.doctor.realdoc.greendao.table.SearchManager;
import com.real.doctor.realdoc.greendao.table.SearchProductManager;
import com.real.doctor.realdoc.model.SearchBean;
import com.real.doctor.realdoc.model.SearchInfoBean;
import com.real.doctor.realdoc.model.SearchProductBean;
import com.real.doctor.realdoc.rxjavaretrofit.entity.BaseObserver;
import com.real.doctor.realdoc.rxjavaretrofit.http.HttpNetUtil;
import com.real.doctor.realdoc.rxjavaretrofit.http.HttpRequestClient;
import com.real.doctor.realdoc.util.DocUtils;
import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.util.ScreenUtil;
import com.real.doctor.realdoc.util.ToastUtil;
import com.real.doctor.realdoc.view.CustomListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;

public class SearchHistoryListActivity extends BaseActivity {

    @BindView(R.id.et_search)
    EditText searchEt;
    @BindView(R.id.tv_tip)
    TextView tipTv;
    @BindView(R.id.listView)
    CustomListView listView;
    @BindView(R.id.tv_clear)
    TextView clearTv;
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.search_top)
    LinearLayout search_top;
    private QueryItemAdapter adapter;
    private ProductSearchAdapter productAdapter;
    private InfoSearchAdapter infoAdapter;
    private int requestCode;
    public String categoryId;
    public ArrayList<SearchBean> list = new ArrayList<SearchBean>();
    public ArrayList<SearchProductBean> productList = new ArrayList<SearchProductBean>();
    public ArrayList<SearchInfoBean> infoList = new ArrayList<SearchInfoBean>();
    public Map<String, Object> map = new HashMap<String, Object>();
    public String type;
    private SearchManager searchInstance;
    private SearchInfoManager searchInfoInstance;
    private SearchProductManager searchProductInstance;

    @Override
    public int getLayoutId() {
        return R.layout.activity_search_history;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
    }

    @Override
    public void initData() {
        //加上沉浸式状态栏高度
        int statusHeight = ScreenUtil.getStatusHeight(SearchHistoryListActivity.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) search_top.getLayoutParams();
            lp.topMargin = statusHeight;
            search_top.setLayoutParams(lp);
        }
        Intent intent = getIntent();
        requestCode = intent.getIntExtra("requestCode", 0);
        categoryId = intent.getStringExtra("categoryId");
        if (requestCode == ArticleShowFragment.INFO_SEARCH) {
            type = "zx";
            searchInfoInstance = SearchInfoManager.getInstance(SearchHistoryListActivity.this);
        } else if (requestCode == ShoppintMallFragment.SHOPPING_EVENT_REQUEST_CODE) {
            type = "sp";
            searchProductInstance = SearchProductManager.getInstance(SearchHistoryListActivity.this);
        } else if (requestCode == RegistrationsActivity.REGISTRATION_EVENT_REQUEST_CODE) {
            type = "gh";
            searchInstance = SearchManager.getInstance(SearchHistoryListActivity.this);
        }
        // 搜索框的键盘搜索键点击回调
        searchEt.setOnKeyListener(new View.OnKeyListener() {// 输入完后按键盘上的搜索键
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {// 修改回车键功能
                    // 先隐藏键盘
                    ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                            getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    // 按完搜索键后将当前查询的关键字保存起来,如果该关键字已经存在就不执行保存
                    String search = searchEt.getText().toString().trim();
                    if (EmptyUtils.isNotEmpty(search)) {
                        //从map中得到相同value的数据
                        String cate = (String) map.get(search);
                        if (EmptyUtils.isNotEmpty(cate)) {
                            SearchBean bean = new SearchBean();
                            bean.setCate(cate);
                            bean.setValue(search);
                            insertData(bean);
                            setBackValue(search, cate);
                        } else {
                            SearchBean bean = new SearchBean();
                            bean.setCate("");
                            bean.setValue(search);
                            insertData(bean);
                            setBackValue(search, "");
                        }
                    } else {
                        ToastUtil.showLong(SearchHistoryListActivity.this, "搜索框为空,请输入您要搜索的信息!");
                    }
                }
                return false;
            }
        });

        // 搜索框的文本变化实时监听
        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().length() == 0) {
                    tipTv.setText("搜索历史");
                } else {
                    tipTv.setText("搜索结果");
                }
                String value = searchEt.getText().toString();
                // 根据value去模糊查询数据库中有没有数据
                queryData(value);
                list.clear();
                getQuery(value);
            }
        });

        // 设置适配器
        if (type.equals("sp")) {
            // 第一次进入查询所有的历史记录
            List<SearchProductBean> searchBeanList = queryProductAllData();
            productAdapter = new ProductSearchAdapter(SearchHistoryListActivity.this, searchBeanList);
            listView.setAdapter(productAdapter);
        } else if (type.equals("zx")) {
            // 第一次进入查询所有的历史记录
            List<SearchInfoBean> searchBeanList = queryInfoAllData();
            infoAdapter = new InfoSearchAdapter(SearchHistoryListActivity.this, searchBeanList);
            listView.setAdapter(infoAdapter);
        } else if (type.equals("gh")) {
            // 第一次进入查询所有的历史记录
            List<SearchBean> searchBeanList = queryAllData();
            adapter = new QueryItemAdapter(SearchHistoryListActivity.this, searchBeanList);
            listView.setAdapter(adapter);
        }
    }

    private List<SearchBean> queryAllData() {
        if (type.equals("gh")) {
            return searchInstance.querySearchList(SearchHistoryListActivity.this);
        }
        return null;
    }

    private List<SearchInfoBean> queryInfoAllData() {
        if (type.equals("zx")) {
            return searchInfoInstance.querySearchList(SearchHistoryListActivity.this);
        }
        return null;
    }

    private List<SearchProductBean> queryProductAllData() {
        if (type.equals("sp")) {
            return searchProductInstance.querySearchList(SearchHistoryListActivity.this);
        }
        return null;
    }

    @Override
    public void initEvent() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SearchBean bean = null;
                SearchInfoBean infoBean = null;
                SearchProductBean productBean = null;
                if (type.equals("sp")) {
                    productBean = (SearchProductBean) parent.getAdapter().getItem(position);
                    //插入数据库
                    insertProductData(productBean);
                    searchEt.setText(productBean.getValue());
                    searchEt.setSelection(productBean.getValue().length());
                    setBackValue(productBean.getValue(), productBean.getCate());
                } else if (type.equals("zx")) {
                    infoBean = (SearchInfoBean) parent.getAdapter().getItem(position);
                    //插入数据库
                    insertInfoData(infoBean);
                    searchEt.setText(infoBean.getValue());
                    searchEt.setSelection(infoBean.getValue().length());
                    setBackValue(infoBean.getValue(), infoBean.getCate());
                } else if (type.equals("gh")) {
                    bean = (SearchBean) parent.getAdapter().getItem(position);
                    //插入数据库
                    insertData(bean);
                    searchEt.setText(bean.getValue());
                    searchEt.setSelection(bean.getValue().length());
                    setBackValue(bean.getValue(), bean.getCate());
                }
            }
        });
    }

    @Override
    @OnClick({R.id.back, R.id.tv_clear})
    public void widgetClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                goBackBtn();
                break;
            case R.id.tv_clear:
                list.clear();
                deleteData();
                // 设置适配器
                if (type.equals("sp")) {
                    productAdapter = new ProductSearchAdapter(SearchHistoryListActivity.this, productList);
                    listView.setAdapter(productAdapter);
                } else if (type.equals("zx")) {
                    infoAdapter = new InfoSearchAdapter(SearchHistoryListActivity.this, infoList);
                    listView.setAdapter(infoAdapter);
                } else if (type.equals("gh")) {
                    adapter = new QueryItemAdapter(SearchHistoryListActivity.this, list);
                    listView.setAdapter(adapter);
                }
                break;
        }
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    public void setBackValue(String value, String cate) {
        if (EmptyUtils.isEmpty(value)) {
            ToastUtil.showLong(SearchHistoryListActivity.this, "搜索关键字不能为空!");
            return;
        }
        if (requestCode == RegistrationsActivity.REGISTRATION_EVENT_REQUEST_CODE) {
            Intent intent = new Intent(SearchHistoryListActivity.this, SearchResultListActivity.class);
            intent.putExtra("searchKey", value);
            intent.putExtra("cate", cate);
            startActivity(intent);
            SearchHistoryListActivity.this.finish();
        } else if (requestCode == ShoppintMallFragment.SHOPPING_EVENT_REQUEST_CODE) {
            Intent resultIntent = new Intent(SearchHistoryListActivity.this, SearchProductResultActivity.class);
            resultIntent.putExtra("searchKey", value);
            resultIntent.putExtra("categoryId", categoryId);
            startActivity(resultIntent);
            finish();
        } else if (requestCode == ArticleShowFragment.INFO_SEARCH) {
            Intent resultIntent = new Intent(SearchHistoryListActivity.this, SearchInfoResultActivity.class);
            resultIntent.putExtra("searchKey", value);
            startActivity(resultIntent);
            finish();
        }
    }

    /**
     * 插入数据
     */
    private void insertData(SearchBean bean) {
        if (type.equals("gh")) {
            searchInstance.insertSearch(this, bean);
        }
    }

    /**
     * 插入数据
     */
    private void insertProductData(SearchProductBean bean) {
        if (type.equals("sp")) {
            searchProductInstance.insertSearch(this, bean);
        }
    }

    /**
     * 插入数据
     */
    private void insertInfoData(SearchInfoBean bean) {
        if (type.equals("zx")) {
            searchInfoInstance.insertSearch(this, bean);
        }
    }

    /**
     * 模糊查询数据
     */
    private void queryData(String value) {
        if (type.equals("sp")) {
            searchProductInstance.querySearchWithValue(this, value);
        } else if (type.equals("zx")) {
            searchInfoInstance.querySearchWithValue(this, value);
        } else if (type.equals("gh")) {
            searchInstance.querySearchWithValue(this, value);
        }
    }

    /**
     * 清空数据
     */
    private void deleteData() {
        if (type.equals("sp")) {
            searchProductInstance.deleteAllSearch(this);
        } else if (type.equals("zx")) {
            searchInfoInstance.deleteAllSearch(this);
        } else if (type.equals("gh")) {
            searchInstance.deleteAllSearch(this);
        }
    }

    public void getQuery(final String queryStr) {
        HashMap<String, String> param = new HashMap<String, String>();
        param.put("query", queryStr);
        param.put("type", type);
        HttpRequestClient client = HttpRequestClient.getInstance(SearchHistoryListActivity.this, HttpNetUtil.SEARCH_URL);
        client.createBaseApi().get("autoComplete"
                , param, new BaseObserver<ResponseBody>(SearchHistoryListActivity.this) {
                    protected Disposable disposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.showLong(SearchHistoryListActivity.this, "查询失败!");
                        if (disposable != null && !disposable.isDisposed()) {
                            disposable.dispose();
                        }
                    }

                    @Override
                    public void onComplete() {
                        if (disposable != null && !disposable.isDisposed()) {
                            disposable.dispose();
                        }
                    }

                    @Override
                    protected void onHandleSuccess(ResponseBody responseBody) {
                        String data = null;
                        String msg = null;
                        String code = null;
                        try {
                            data = responseBody.string().toString();
                            try {
                                JSONObject object = new JSONObject(data);
                                if (DocUtils.hasValue(object, "msg")) {
                                    msg = object.getString("msg");
                                }
                                if (DocUtils.hasValue(object, "code")) {
                                    code = object.getString("code");
                                }
                                if (msg.equals("ok") && code.equals("0")) {
                                    JSONArray array = object.getJSONArray("data");
                                    for (int i = 0; i < array.length(); i++) {
                                        SearchBean bean = null;
                                        SearchInfoBean infoBean = null;
                                        SearchProductBean productBean = null;
                                        if (type.equals("sp")) {
                                            productBean = new SearchProductBean();
                                        } else if (type.equals("zx")) {
                                            infoBean = new SearchInfoBean();
                                        } else if (type.equals("gh")) {
                                            bean = new SearchBean();
                                        }
                                        JSONObject jsonObj = (JSONObject) array.get(i);
                                        String value = "";
                                        String cate = "";
                                        if (DocUtils.hasValue(jsonObj, "value")) {
                                            value = jsonObj.getString("value");
                                            if (type.equals("sp")) {
                                                productBean.setValue(value);
                                            } else if (type.equals("zx")) {
                                                infoBean.setValue(value);
                                            } else if (type.equals("gh")) {
                                                bean.setValue(value);
                                            }
                                        }
                                        if (DocUtils.hasValue(jsonObj, "cate")) {
                                            cate = jsonObj.getString("cate");
                                            if (type.equals("sp")) {
                                                productBean.setCate(cate);
                                            } else if (type.equals("zx")) {
                                                infoBean.setCate(cate);
                                            } else if (type.equals("gh")) {
                                                bean.setCate(cate);
                                            }
                                        }
                                        map.put(value, cate);
                                        if (type.equals("sp")) {
                                            productList.add(productBean);
                                        } else if (type.equals("zx")) {
                                            infoList.add(infoBean);
                                        } else if (type.equals("gh")) {
                                            list.add(bean);
                                        }
                                    }
                                    // 设置适配器
                                    if (type.equals("sp")) {
                                        productAdapter = new ProductSearchAdapter(SearchHistoryListActivity.this, productList);
                                        listView.setAdapter(productAdapter);
                                    } else if (type.equals("zx")) {
                                        infoAdapter = new InfoSearchAdapter(SearchHistoryListActivity.this, infoList);
                                        listView.setAdapter(infoAdapter);
                                    } else if (type.equals("gh")) {
                                        adapter = new QueryItemAdapter(SearchHistoryListActivity.this, list);
                                        listView.setAdapter(adapter);
                                    }
                                    queryData(queryStr);
                                } else {
                                    ToastUtil.showLong(SearchHistoryListActivity.this, "查询失败!");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            goBackBtn();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void goBackBtn() {
        //将地址还给baseUrl
        HttpRequestClient client = HttpRequestClient.getNotInstance(SearchHistoryListActivity.this, HttpNetUtil.BASE_URL, null);
        if (EmptyUtils.isNotEmpty(client)) {
            SearchHistoryListActivity.this.finish();
        }
    }
}
