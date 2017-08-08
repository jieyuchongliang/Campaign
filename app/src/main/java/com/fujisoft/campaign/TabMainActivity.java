package com.fujisoft.campaign;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.fujisoft.campaign.adapter.TaskListRVAdapter;
import com.fujisoft.campaign.bean.NoticeBean;
import com.fujisoft.campaign.fragment.FlowerMarketFragment;
import com.fujisoft.campaign.fragment.IndexFragmentNew;
import com.fujisoft.campaign.fragment.MineFragment;
import com.fujisoft.campaign.fragment.TaskFragment;
import com.fujisoft.campaign.popup.TaskFinishPopWin;
import com.fujisoft.campaign.service.UpdateManager;
import com.fujisoft.campaign.utils.Constants;
import com.fujisoft.campaign.utils.Utils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.fujisoft.campaign.R.id.tool_bar_task_hall_search_edit_text;

/**
 * 4个Tab页开始的主页面
 */
public class TabMainActivity extends BaseActivity implements TabLayout.OnTabSelectedListener, View.OnClickListener, TaskFinishPopWin.ITaskFinished, TaskListRVAdapter.ITaskListExt, IndexFragmentNew.OnNoticeLoadCallback {

    private String TAG = "campaign";
    public final static String GO_WHERE = "go_where";
    //toolbar
    private View toolbarView;
    private LinearLayout toolbarIndexLayout;
    private RelativeLayout shoppingCartView;

    private TextView shoppingCartNum;
    private LinearLayout toolbarTaskHallLayout;
    private TextView titleText;
    private TextView search_button;
    private EditText toolbarIndexSearchEditText;
    private ImageButton backIB;
    ImageButton toolBarTaskHallListButton;
    ImageButton tool_bar_task_button;
    public int mCurrentTab;
    String keyword;

    //传递用参数
    private String userId = null;
    private String userType = null;

    private String cartNum = null;

    //Index画面控件
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private FragmentPageAdapter mFragmentPageAdapter;

    private int[] tabUnselectedImages = {R.mipmap.tab_unselected_homepage, R.mipmap.tab_unselected_taskhall, R.mipmap.tab_unselected_flowermarket, R.mipmap.tab_unselected_min};
    private int[] tabSelectedImages = {R.mipmap.tab_selected_homepage, R.mipmap.tab_selected_taskhall, R.mipmap.tab_selected_flowermarket, R.mipmap.tab_selected_min};
    private String[] tabTxts = {"首页", "任务大厅", "鲜花商城", "我的"};

    // TaskFragment(任务大厅画面)
    private TaskFragment mTaskFragment;
    private ViewFlipper mAutoTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "=== TabMainActivity# onCreate() Start ===");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_main);

        UpdateManager manager = new UpdateManager(TabMainActivity.this);
        // 检查软件更新
        manager.checkUpdate();

        initViews();
        Log.d(TAG, "=== TabMainActivity# onCreate() End ===");
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "=== TabMainActivity# onResume() Start ===");
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_FILE_NAME, MODE_PRIVATE);
        userId = sharedPreferences.getString(Constants.EXTRA_USER_ID, "");
        userType = sharedPreferences.getString(Constants.EXTRA_USER_TYPE, "");

        toolbarIndexSearchEditText.clearFocus();
        Log.d(TAG, "=== TabMainActivity# onResume() 从SP中取到的userId = " + userId);
        Log.d(TAG, "=== TabMainActivity# onResume() 从SP中取到的userType = " + userType);

        getCartNum();
        Log.d(TAG, "=== TabMainActivity# onResume() End ===");
    }

    @Override
    public void onCreateCustomToolBar(Toolbar toolbar) {
        super.onCreateCustomToolBar(toolbar);
        toolbar.showOverflowMenu();
        toolbarView = getLayoutInflater().inflate(R.layout.toolbar_button, toolbar);

        //首页
        toolbarIndexLayout = (LinearLayout) toolbarView.findViewById(R.id.tool_bar_horn_layout);
        toolbarIndexLayout.setVisibility(View.VISIBLE);
        // 实例化任务大厅Toolbar领域控件
        toolbarTaskHallLayout = (LinearLayout) toolbarView.findViewById(R.id.tool_bar_task_hall_layout);
        toolBarTaskHallListButton = (ImageButton) toolbarTaskHallLayout.findViewById(R.id.tool_bar_task_hall_list_button);
        tool_bar_task_button = (ImageButton) toolbarTaskHallLayout.findViewById(R.id.tool_bar_task_button);

        toolBarTaskHallListButton.setOnClickListener(this);
        // 实例化Toolbar中间TextView控件
        titleText = (TextView) toolbarView.findViewById(R.id.tool_bar_center_text_view);
        // 实例化鲜花商城Toolbar、右侧购物车按钮
        shoppingCartView = (RelativeLayout) toolbarView.findViewById(R.id.tool_bar_shopping_car_button);
        shoppingCartNum = (TextView) toolbarView.findViewById(R.id.tool_bar_shopping_car_num);
        // Toolbar左侧Back键
        backIB = (ImageButton) findViewById(R.id.tool_bar_back_button);

        search_button = (TextView) toolbarView.findViewById(R.id.tool_bar_task_hall_search_button);

        ImageView horn = (ImageView) toolbarIndexLayout.findViewById(R.id.horn);
        mAutoTextView = (ViewFlipper) toolbarIndexLayout.findViewById(R.id.id_main_switcher);

        horn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != userId && !"".equals(userId)) {
                    Intent searchIntent = new Intent(TabMainActivity.this, IndexSearchActivity.class);
                    searchIntent.putExtra(Constants.EXTRA_USER_ID, userId);
                    startActivity(searchIntent);
                } else {
                    toLogin();
                }
            }
        });
        toolbarIndexSearchEditText = (EditText) toolbarView.findViewById(tool_bar_task_hall_search_edit_text);
        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Utils.hideKeyBoard(TabMainActivity.this);

                if (null != userId && !"".equals(userId)) {
                    keyword = toolbarIndexSearchEditText.getText().toString();
                    Log.d("liuyq", "keyword: " + keyword);
                    if (null != keyword && !"".equals(keyword)) {
                        mTaskFragment.select(keyword);
                        toolBarTaskHallListButton.setVisibility(View.GONE);
                        tool_bar_task_button.setVisibility(View.VISIBLE);
                        tool_bar_task_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //onBackPressed();
                                // initViews();
                                toolbarIndexSearchEditText.setText("");
                                keyword = toolbarIndexSearchEditText.getText().toString();
                                mTaskFragment.select(keyword);
                                tool_bar_task_button.setVisibility(View.GONE);
                                toolBarTaskHallListButton.setVisibility(View.VISIBLE);
                            }
                        });
                    } else {
                        tool_bar_task_button.setVisibility(View.GONE);
                        toolBarTaskHallListButton.setVisibility(View.VISIBLE);
                        mTaskFragment.select(keyword);

                    }
                } else {
                    toolBarTaskHallListButton.setVisibility(View.VISIBLE);
                    tool_bar_task_button.setVisibility(View.GONE);
                    toLogin();
                }
            }
        });

    }

    @Override
    public void onDataCallback(List<NoticeBean> data) {
        int afficheListSize = data.size();
        android.view.View[] afficheViews = new View[afficheListSize];
        for (int i = 0; i < afficheListSize; i++) {
            NoticeBean noticeBean = data.get(i);
            String affiche = noticeBean.getTitle();
            afficheViews[i] = getAfficheView(affiche);
        }
        bindAfficheViews(afficheViews);
    }

    public void bindAfficheViews(View... views) {
        for (View view : views) {
            mAutoTextView.addView(view);
        }
    }

    public View getAfficheView(final String affiche) {
        TextView afficheView = new TextView(this);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        afficheView.setTextColor(ContextCompat.getColor(this, R.color.tab_main_color));
        afficheView.setGravity(Gravity.CENTER_VERTICAL);
        afficheView.setText(affiche);
        afficheView.setLayoutParams(lp);
        afficheView.setMaxLines(1);
        afficheView.setEllipsize(TextUtils.TruncateAt.valueOf("END"));
        afficheView.setFocusable(true);
        afficheView.setFocusableInTouchMode(true);


        afficheView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != userId && !"".equals(userId)) {
                    Intent searchIntent = new Intent(TabMainActivity.this, IndexSearchActivity.class);
                    searchIntent.putExtra(Constants.EXTRA_USER_ID, userId);
                    startActivity(searchIntent);
                } else {
                    toLogin();
                }
            }
        });
        return afficheView;
    }

    private void initViews() {
        Log.d(TAG, "=== TabMainActivity# initViews() Start ===");
        mViewPager = (ViewPager) findViewById(R.id.index_vp);
        mTabLayout = (TabLayout) findViewById(R.id.index_tl);
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        mFragmentPageAdapter = new FragmentPageAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mFragmentPageAdapter);
        //预加载的页面数量,当前view的左右两边的预加载的页面的个数
        mViewPager.setOffscreenPageLimit(3);
        mTabLayout.setupWithViewPager(mViewPager);
        for (int i = 0; i < mTabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = mTabLayout.getTabAt(i);
            if (tab != null) {
                tab.setCustomView(mFragmentPageAdapter.getTabView(i));
            }
        }
        mTabLayout.addOnTabSelectedListener(this);
        Log.d(TAG, "=== TabMainActivity# initViews() End ===");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String tag = intent.getStringExtra(GO_WHERE);
        if (!TextUtils.isEmpty(tag) && tag.equals("2")) {
            if (mViewPager.getChildCount() > 2)
                mViewPager.setCurrentItem(2);
        }
    }

    /**
     * 页面底部导航栏选择时被调用
     *
     * @param tab ： 选择的导航栏的位置值
     */
    @Override
    public void onTabSelected(TabLayout.Tab tab) {

        ((ImageView) tab.getCustomView().findViewById(R.id.tab_img)).setImageResource(tabSelectedImages[tab.getPosition()]);
        ((TextView) tab.getCustomView().findViewById(R.id.tab_txt)).setTextColor(getResources().getColor(R.color.common_color));

        mCurrentTab = tab.getPosition();

        switch (tab.getPosition()) {
            case 0:
                mViewPager.setCurrentItem(0);
                break;
            case 1:
                mViewPager.setCurrentItem(1);
                break;
            case 2:
                mViewPager.setCurrentItem(2);
                break;
            case 3:
                mViewPager.setCurrentItem(3);
                break;
        }
        updateToolbar();
    }

    /**
     * 页面底部导航栏选择时被调用
     *
     * @param tab ： 选择前的导航栏的位置值
     */
    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        ((ImageView) tab.getCustomView().findViewById(R.id.tab_img)).setImageResource(tabUnselectedImages[tab.getPosition()]);
        ((TextView) tab.getCustomView().findViewById(R.id.tab_txt)).setTextColor(getResources().getColor(R.color.tab_unselected));
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
    }

    /**
     * 点击事件
     *
     * @param view
     */
    public void onButtonClick(View view) {
        if (view == null) {
            return;
        }
        Intent intent = null;
        switch (view.getId()) {
            case R.id.flower_market_fab: // 鲜花抽奖
                if (null != userId && !"".equals(userId)) {
                    intent = new Intent();
                    intent.setClass(this, LuckDrawActivity.class);
                } else {
                    toLogin();
                    return;
                }
            case R.id.mine_run_to_flower_market_btn: // 进入鲜花商城兑换和抽奖
            case R.id.mine_run_to_flower_market_layout: // 进入鲜花商城兑换和抽奖
            case R.id.mine_run_to_flower_num_text: // 进入鲜花商城兑换和抽奖
            case R.id.mine_flower_icon_img: // 进入鲜花商城兑换和抽奖
            case R.id.mine_run_to_gold_num_text: // 进入鲜花商城兑换和抽奖
            case R.id.mine_run_to_gold_icon_img: // 进入鲜花商城兑换和抽奖
            case R.id.mine_run_to_gold_text: // 进入鲜花商城兑换和抽奖
                //  case R.id.mine_run_to_gold_text_2: // 进入鲜花商城兑换和抽奖
            case R.id.mine_run_to_strawberry_num_text: // 进入鲜花商城兑换和抽奖
            case R.id.mine_strawberry_icon_img: // 进入鲜花商城兑换和抽奖
                mViewPager.setCurrentItem(2);
                break;
            case R.id.mine_my_ranking_view: // 我的排名-进入排行榜页面
                intent = new Intent();
                intent.setClass(this, RankingFlowerActivity.class);
                break;
            case R.id.mine_my_finances_button: // 我的财务
            case R.id.mine_my_finances_button_img: // 我的财务
                intent = new Intent();
                intent.setClass(this, FinanceActivity.class);
                break;
            case R.id.mine_my_tasks_sent_button: // 已发任务
            case R.id.mine_my_tasks_sent_button_img: // 已发任务
                intent = new Intent();
                intent.setClass(this, PublishedTasksActivity.class);
                break;
            case R.id.mine_info_btn: // 个人信息
                intent = new Intent();
                intent.setClass(this, PersonalInformationActivity.class);
                break;
            case R.id.mine_publish_task_btn:// 发布任务
                intent = new Intent();
                intent.setClass(this, ReleaseTaskActivity.class);
                break;
            case R.id.mine_shared_task_btn: // 已分享任务
                intent = new Intent();
                intent.setClass(this, SharedTasksActivity.class);
                break;
            case R.id.mine_staff_btn: // 员工
                intent = new Intent();
                intent.setClass(this, StaffListActivity.class);
                break;
            case R.id.mine_my_orders_list_btn: // 订单列表
                intent = new Intent();
                intent.setClass(this, OrderListActivity.class);
                break;
            case R.id.mine_my_message_list_btn: // 消息列表
                intent = new Intent();
                intent.setClass(this, MessageListActivity.class);
                break;
            case R.id.mine_change_photo_user_icon: // 修改头像
                intent = new Intent();
                intent.setClass(this, ChangePhotoActivity.class);
                break;
            case R.id.tool_bar_shopping_car:       // 购物车
                if (null != userId && !"".equals(userId)) {
                    intent = new Intent();
                    intent.setClass(this, ShoppingCartActivity.class);
                    intent.putExtra(Constants.EXTRA_USER_ID, userId);
                } else {
                    toLogin();
                    return;
                }
                break;
            default:
                break;
        }
        if (intent != null) {
            startActivity(intent);
        }
    }

    /**
     * 更新工具栏
     */
    private void updateToolbar() {
        if (mTabLayout != null) {
            //在更新工具栏之前先将动画停止
            mAutoTextView.stopFlipping();
            if (mTabLayout.getSelectedTabPosition() == 0) {
                //首页被选中时的时候，开启动画
                mAutoTextView.startFlipping();
                // 首页被选中时
                toolbarIndexLayout.setVisibility(View.VISIBLE);
                // 隐藏任务大厅Toolbar领域控件
                toolbarTaskHallLayout.setVisibility(View.GONE);
                // 隐藏鲜花商城Toolbar领域控件
                titleText.setVisibility(View.GONE);
                shoppingCartView.setVisibility(View.GONE);
            } else if (mTabLayout.getSelectedTabPosition() == 1) {
                // 任务大厅被选中时
                // 隐藏首页Toolbar领域控件
                toolbarIndexLayout.setVisibility(View.GONE);
                // 隐藏鲜花商城Toolbar领域控件
                titleText.setVisibility(View.GONE);
                shoppingCartView.setVisibility(View.GONE);
                // 显示任务大厅Toolbar领域控件
                toolbarTaskHallLayout.setVisibility(View.VISIBLE);
            } else if (mTabLayout.getSelectedTabPosition() == 2) {
                // 鲜花商城被选中时
                // 隐藏首页Toolbar领域控件
                toolbarIndexLayout.setVisibility(View.GONE);
                // 隐藏任务大厅Toolbar领域控件
                toolbarTaskHallLayout.setVisibility(View.GONE);
                // 隐藏Toolbar的返回键控件
                backIB.setVisibility(View.GONE);

                // 显示鲜花商城Toolbar领域控件
                titleText.setText(R.string.flower_market_title);
                titleText.setVisibility(View.VISIBLE);
                shoppingCartView.setVisibility(View.VISIBLE);

            } else if (mTabLayout.getSelectedTabPosition() == 3) {
                // 我的被选中时
                // 隐藏首页Toolbar领域控件
                toolbarIndexLayout.setVisibility(View.GONE);
                // 隐藏任务大厅Toolbar领域控件
                toolbarTaskHallLayout.setVisibility(View.GONE);
                // 隐藏鲜花商城Toolbar领域控件
                shoppingCartView.setVisibility(View.GONE);

                TextView shoppingCarTxt = (TextView) findViewById(R.id.tool_bar_center_text_view);
                shoppingCarTxt.setVisibility(View.VISIBLE);
                shoppingCarTxt.setText(R.string.mine_title);
            }
        }
        toolbarView.invalidate();
    }

    /**
     * 任务大厅页面左上角图标点击事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {

        if (null != userId && !"".equals(userId)) {
            mTaskFragment.showPopWin();
        } else {
            toLogin();
            return;
        }
    }

    /**
     * viewPager的Adapter
     */
    class FragmentPageAdapter extends FragmentStatePagerAdapter {

        public FragmentPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Log.d(TAG, "=== TabMainActivity# FragmentPageAdapter#getItem() position = " + position);
            switch (position) {
                case 0:
                    // 加载首页画面
                    Log.d(TAG, "=== TabMainActivity# FragmentPageAdapter#getItem() 加载首页画面 ");
                    return IndexFragmentNew.newInstance();
                case 1:
                    // 加载任务大厅画面
                    Log.d(TAG, "=== TabMainActivity# FragmentPageAdapter#getItem() 加载任务大厅画面 ");
                    Log.d(TAG, "=== TabMainActivity# FragmentPageAdapter#getItem() mTaskFragment = " + mTaskFragment);
                    if (mTaskFragment != null) {
                        return mTaskFragment;
                    } else {
                        mTaskFragment = TaskFragment.newInstance();
                        return mTaskFragment;
                    }
                case 2:
                    // 加载鲜花商城画面
                    Log.d(TAG, "=== TabMainActivity# FragmentPageAdapter#getItem() 加载鲜花商城画面 ");
                    return FlowerMarketFragment.newInstance();
                case 3:
                    // 加载我的画面
                    Log.d(TAG, "=== TabMainActivity# FragmentPageAdapter#getItem() 加载我的画面 ");
                    return MineFragment.newInstance();
                default:
                    Log.d(TAG, "=== TabMainActivity# FragmentPageAdapter#getItem() default加载首页画面 ");
                    return IndexFragmentNew.newInstance();
            }
        }

        @Override
        public int getCount() {
            return 4;
        }

        public int getItemPosition(Object object) {
            Log.d(TAG, "=== TabMainActivity# FragmentPageAdapter#getItemPosition() object = " + object);
            if (object instanceof IndexFragmentNew) {
                ((IndexFragmentNew) object).updateDate();
            }
            return super.getItemPosition(object);
        }

        public void destroyItem(ViewGroup container, int position, Object object) {
            Log.d(TAG, "=== TabMainActivity# FragmentPageAdapter#destroyItem() position = " + position);
            super.destroyItem(container, position, object);
        }

        public View getTabView(int position) {
            View v = LayoutInflater.from(TabMainActivity.this).inflate(R.layout.tablayout_tab, null);
            TextView tv = (TextView) v.findViewById(R.id.tab_txt);
            ImageView img = (ImageView) v.findViewById(R.id.tab_img);
            if (position == mTabLayout.getSelectedTabPosition()) {
                tv.setText(tabTxts[position]);
                tv.setTextColor(getResources().getColor(R.color.common_color));
                img.setImageResource(tabSelectedImages[position]);
            } else {
                tv.setText(tabTxts[position]);
                tv.setTextColor(getResources().getColor(R.color.tab_unselected));
                img.setImageResource(tabUnselectedImages[position]);
                mTabLayout.getTabAt(position).setCustomView(v);
            }
            return v;
        }
    }

    /**
     * 显示Error Dialog
     *
     * @param msgId
     */
    public void showErrorDialog(int msgId) {
        String dialogMessage = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(TabMainActivity.this);
        builder.setTitle(getString(R.string.login_dialog_hint))
                .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        switch (msgId) {
            case Constants.CODE_EXECUTE_FAILURE:
                dialogMessage = getString(R.string.login_dialog_error);
                break;
        }
        builder.setMessage(dialogMessage);
        builder.create().show();
    }

    /**
     * 获取屏幕的高度
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getHeight();
    }

    @Override
    public void showTaskFinishedActivity() {
        Intent intent = new Intent();
        intent.putExtra(Constants.EXTRA_USER_ID, userId);
        intent.setClass(this, TaskFinishedListActivity.class);
        this.startActivity(intent);
        this.overridePendingTransition(R.anim.pop_push_up_in, R.anim.pop_push_down_out);
    }

    /**
     * 根据点击的taskId，进入相应的任务详情页面
     *
     * @param taskId
     */
    @Override
    public void toTaskDetails(String taskId) {
        Intent intent = new Intent(this, TaskDetailsActivity.class);
        intent.putExtra(Constants.EXTRA_USER_ID, userId);
        intent.putExtra(Constants.EXTRA_TASK_ID, taskId);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mViewPager != null) mViewPager.removeAllViews();
        if (mTabLayout != null) mTabLayout.removeAllViews();
    }

    //退出时的时间
    private long mExitTime;

    /**
     * 对返回键进行监听
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            exit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void exit() {
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            Toast.makeText(TabMainActivity.this, "再按一次退出点灿", Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
//            dismissProgressDialog();
            switch (msg.what) {
                case Constants.CODE_EXECUTE_FAILURE:
                    break;
                case Constants.CODE_EXECUTE_SUCCESS:
                    if (cartNum != null && !"".equals(cartNum) && !"null".equals(cartNum)) {
                        shoppingCartNum.setText(cartNum);
                        shoppingCartNum.setVisibility(View.VISIBLE);
                    } else {
                        shoppingCartNum.setVisibility(View.GONE);
                    }
                    break;
                case Constants.CODE_EXECUTE_EXCEPTION:
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 获取购物车右上角的数量值
     */
    public void getCartNum() {
//        showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    List<NameValuePair> params = new ArrayList<>();
                    params.add(new BasicNameValuePair("id", userId));
                    // 获取响应的结果信息
                    String json = getPostData(Constants.URL_CARTNUM, params);
                    Log.d(TAG, "=== TabMainActivity#getCartNum() json = " + json);

                    //JSON的解析过程
                    if (json != null) {
                        JSONObject jsonObject = new JSONObject(json);
                        boolean result = (boolean) jsonObject.get("success");
                        if (result) {
                            JSONObject dataJsonObject = (JSONObject) jsonObject.get("data");
                            if (dataJsonObject != null) {
                                cartNum = dataJsonObject.getString("cartNum");
                            }
                            //成功后的处理
                            mHandler.sendEmptyMessage(Constants.CODE_EXECUTE_SUCCESS);
                        } else {
                            mHandler.sendEmptyMessage(Constants.CODE_EXECUTE_FAILURE);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    //异常后的处理
                    mHandler.sendEmptyMessage(Constants.CODE_EXECUTE_EXCEPTION);
                }
            }
        }).start();
    }
}