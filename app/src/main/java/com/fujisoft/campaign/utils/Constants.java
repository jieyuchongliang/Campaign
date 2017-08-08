package com.fujisoft.campaign.utils;

public class Constants {

    /**
     * 开发测试使用
     */
    public static final String baseUrl = "http://192.168.42.34/App";

    /**
     * 开发测试使用图片加载路径
     */
    public static final String PICTURE_BASE_URL = "http://52.80.12.35";


    /**
     * 服务器版本更新用路径
     */
    public static final String BASE_URL_UPDATE = PICTURE_BASE_URL+"/VersionUpdate";

    /**
     * 手机版本更新用路径
     */
    public static final String BASE_URL_UPDATE_SD = "/VersionUpdate";

    /**
     * 首页载入
     */
    public static final String URL_INDEX = baseUrl + "/Index/index.action";

    /**
     * 首页搜索
     */
    public static final String URL_INDEX_SEARCH = baseUrl + "/Index/search.action";

    /**
     * 用户登录
     */
    public static final String URL_USER_LOGIN = baseUrl + "/User/login.action";

    /**
     * 用户注册
     */
    public static final String URL_USER_REGISTER = baseUrl + "/User/register.action";

    /**
     * 企业注册
     */
    public static final String URL_ENTERPRISE_REGISTER = baseUrl + "/Enterprise/register.action";

    /**
     * 兴趣爱好载入
     */
    public static final String URL_USER_HOBBY_LIST = baseUrl + "/User/hobbyList.action";

    /**
     * 兴趣爱好
     */
    public static final String URL_USER_HOBBY = baseUrl + "/User/hobby.action";

    /**
     * 任务大厅->载入
     * 任务大厅->搜索
     */
    public static final String TASK_INDEX = "Task/index.action";

    /**
     * 已完成任务，带搜索
     * 任务大厅->任务栏
     */
    public static final String TASK_FINISHED_LIST = "Task/taskBar.action";

    /**
     * 鲜花商城
     * 商城首页->载入
     * 商城首页->搜索
     */
    public static final String MARKET_LIST = "Mall/index.action";

    /**
     * 添加购物车
     */
    public static final String ADD_Cart = "Mall/addToCart.action";

    /**
     * 取得购物车数量
     */
    public static final String URL_GET_CART_NUM = baseUrl + "Mall/cartCount.action";


    /**
     * 购物车数
     */
    public static final String URL_CARTNUM = baseUrl + "/Mall/cartNum.action";

    /**
     * 我的
     */
    public static final String URL_USER_MY = baseUrl + "/User/my.action";

    /**
     * 密码修改
     */
    public static final String URL_USER_RESET_PWD = baseUrl + "/User/modifyPwd.action";

    /**
     * 头像载入
     */
    public static final String URL_USER_AVATAR = baseUrl + "/User/avatar.action";

    /**
     * 头像修改
     */
    public static final String URL_USER_MODIFY_AVATAR = baseUrl + "/User/modifyAvatar.action";

    /**
     * 地址载入
     */
    public static final String URL_USER_ADDRESS = baseUrl + "/User/address.action";

    /**
     * 添加地址
     */
    public static final String URL_USER_ADD_ADDRESS = baseUrl + "/User/addAdd.action";

    /**
     * 地址删除
     */
    public static final String URL_USER_DEL_ADDRESS = baseUrl + "/User/delAdd.action";

    /**
     * 设置默认地址
     */
    public static final String URL_USER_DEFAULT_ADDRESS = baseUrl + "/User/setDefault.action";
    /**
     * 改变默认地址
     */
    public static final String URL_USER_CHANGE_ADDRESS = baseUrl + "/User/reDefault.action";
    /**
     * 设置是否接受邀请
     */
    public static final String URL_USER_INVITE = baseUrl + "/User/acceptInvitation.action";

    /**
     * 发送验证码
     */
    public static final String URL_SEND_SMS = baseUrl + "/User/sendSMS.action";

    /**
     * 修改密码发送验证码
     */
    public static final String URL_PASS_SEND_SMS = baseUrl + "/User/forgetPWSendSMS.action";


    /**
     * 电话修改
     */
    public static final String URL_USER_MODIFY_PHONE = baseUrl + "/User/modifyPhone.action";
    /**
     * 忘记密码
     */
    public static final String URL_FORGET_PASSWORD = baseUrl + "/User/resetPassword.action";

    /**
     * 昵称修改
     */
    public static final String URL_USER_MODIFY_NICK = baseUrl + "/User/modifyNick.action";

    /**
     * 商品详情
     */
    public static final String GOODS_DETAIL = baseUrl + "/Mall/goodsDetail.action";

    /**
     * 购物车
     */
    public static final String URL_MALL_SHOP_CART = baseUrl + "/Mall/shopcart.action";
    /**
     * 购物车删除
     */
    public static final String URL_DEL_CART = baseUrl + "/Mall/delCart.action";

    /**
     * 购物车结算
     */
    public static final String URL_MALL_ORDER = baseUrl + "/Mall/order.action";

    /**
     * 查询订单状态
     */
    public static final String URL_MALL_CHECK_ORDER = baseUrl + "/Mall/checkOrder.action";

    /**
     * 支付取消
     */
    public static final String URL_MALL_RE_ORDER = baseUrl + "/Mall/reOrder.action";

    /**
     * 订单详情
     */
    public static final String URL_ORDER_DETAILS = baseUrl + "/Order/orderDetail.action";

    /**
     * 订单载入
     */
    public static final String URL_ORDER_INDEX = baseUrl + "/Order/index.action";

    /**
     * 已分享任务载入
     */
    public static final String URL_SHARED_TASK_LIST = baseUrl + "/Task/taskBar.action";

    /**
     * 已发布任务载入
     */
    public static final String URL_ENTERPRISE_TASK_LIST = baseUrl + "/Enterprise/taskList.action";

    /**
     * 任务详情
     */
    public static final String URL_ENTERPRISE_TASK_DETAILS = baseUrl + "/Enterprise/taskDetails.action";

    /**
     * 员工
     */
    public static final String URL_ENTERPRISE_ENLIST = baseUrl + "/Enterprise/enList.action";

    /**
     * 任务提醒
     */
    public static final String URL_ENTERPRISE_REMIND = baseUrl + "/Enterprise/remind.action";

    /**
     * 财务管理载入
     */
    public static final String URL_FINANCE_INDEX = baseUrl + "/Finance/index.action";

    /**
     * 元宝
     */
    public static final String URL_FINANCE_INTEGRAL = baseUrl + "/Finance/integral.action";

    /**
     * 充值载入
     */
    public static final String URL_FINANCE_RECHARGE_LOADING = baseUrl + "/Finance/recharge.action";

    /**
     * 充值记录载入
     */
    public static final String URL_FINANCE_RECHARGE_RECORD = baseUrl + "/Finance/rechargeRecord.action";

    /**
     * 申请发票载入
     */
    public static final String URL_FINANCE_APPLY_INVOICE = baseUrl + "/Finance/applyInvoice.action";

    /**
     * 申请发票
     */
    public static final String URL_FINANCE_RECHARGE = baseUrl + "/Finance/applyInvoiceSave.action";

    /**
     * 当日鲜花排行
     */
    public static final String URL_RANKING_FLOWER = baseUrl + "/User/flowerRankList.action";

    /**
     * 所有鲜花排行
     */
    public static final String URL_RANKING_ALLFLOWER = baseUrl + "/User/flowerAllRankList.action";

    /**
     * 当日元宝排行
     */
    public static final String URL_RANKING_GOLD = baseUrl + "/User/goldRankList.action";

    /**
     * 所有元宝排行
     */
    public static final String URL_RANKING_ALLGOLD = baseUrl + "/User/goldAllRankList.action";

    /**
     * 消息列表
     */
    public static final String URL_MESSAGE_LIST = baseUrl + "/User/message.action";

    /**
     * 发布任务载入
     */
    public static final String URL_RELEASE_LOAD = baseUrl + "/Enterprise/publishTask.action";

    /**
     * 发布任务
     */
    public static final String URL_RELEASE_TASK = baseUrl + "/Enterprise/publishSave.action";

    /**
     * 分享成功接口
     */
    public static final String URL_SHARE_SUCCESS = baseUrl + "/Task/shareSuccess.action";

    /**
     * 缴纳开户费
     * 企业注册成功缴纳开户费
     */
    public static final String URL_ENTERPRISE_PAY_FOR_ACCOUNT = baseUrl + "/Enterprise/payForAccount.action";

    /**
     * 查询开户费支付状态
     * 企业注册成功缴纳开户费同步回调
     */
    public static final String URL_ENTERPRISE_PAY_FOR_ACCOUNT_CALL_BACK = baseUrl + "/Enterprise/checkOrder.action";

    /**
     * 充值年费和金币
     * 财务->充值
     */
    public static final String URL_FINANCE_PAY = baseUrl + "/Finance/pay.action";

    /**
     * 查询充值年费和金币状态
     * 充值年费和金币同步回调
     */
    public static final String URL_FINANCE_PAY_CALL_BACK = baseUrl + "/Finance/checkOrder.action";

    /**
     * app启动-更新定位
     */
    public static final String URL_UPDATE_USER_LOCATION = baseUrl + "/User/updateUserLocation.action";



    /**
     * 微信支付APP_ID
     */
//    public static final String APP_ID = "wxd930ea5d5a258f4f";
    public static final String APP_ID = "wx551a26c96fffbf66";
    /**
     * KEY
     */
    public static final String KEY = "1234qazwsx";

    /**
     * intent传值的用户ID
     */
    public static final String EXTRA_USER_ID = "USER_ID";

    /**
     * 分享提示对话框
     */
    public static final String SHARE_DIALOG = "SHARE_DIALOG";

    /**
     * intent传值的订单ID
     */
    public static final String EXTRA_ORDER_ID = "ORDER_ID";

    /**
     * intent传值的用户ID
     */
    public static final String EXTRA_USER_TYPE = "USER_TYPE";

    /**
     * intent传值的用户CID
     */
    public static final String EXTRA_USER_CID = "USER_CID";

    /**
     * intent传值的用户激活状态(0:未激活 1：已激活)
     */
    public static final String EXTRA_USER_ACTIVE = "USER_ACTIVE";

    /**
     * intent传值的用户激活状态(0:未激活 1：已激活)
     */
    public static final String EXTRA_SHARED_SUCCESS = "SHARED_SUCCESS";

    /**
     * intent传值的任务ID
     */
    public static final String EXTRA_TASK_ID = "TASK_ID";

    /**
     * intent传值的商品ID
     */
    public static final String EXTRA_GOODS_ID = "GOODS_ID";

    /**
     * intent传值的前画面
     */
    public static final String EXTRA_PREVIOUS_SCREEN = "PREVIOUS_SCREEN";

    /**
     * 充值/支付金额值
     */
    public static final String EXTRA_AMOUNT = "AMOUNT";

    /**
     * 充值/支付金额值
     */
    public static final String EXTRA_RECHARGE_RESULT = "RECHARGE_RESULT";

    /**
     * 企业注册成功后，intent传递“交付开户费及年费”的值
     */
    public static final String EXTRA_COMPANY_REGISTER_AMOUNT = "COMPANY_REGISTER_AMOUNT";

    /**
     * 支付/充值类型的区分Type
     */
    public static final String EXTRA_PAY_RECHARGE_TYPE = "PAY_RECHARGE_TYPE";

    /**
     * 支付类型
     */
    public static final String EXTRA_PAY_TYPE = "PAY_TYPE";

    /**
     * 充值类型
     */
    public static final String EXTRA_RECHARGE_TYPE = "RECHARGE_TYPE";

    /**
     * 用户TYPE:员工
     */
    public static final String USER_TYPE_STAFF = "1";

    /**
     * 用户TYPE:个人
     */
    public static final String USER_TYPE_NORMAL = "2";

    /**
     * 用户TYPE:管理员
     */
    public static final String USER_TYPE_MANAGER = "3";

    /**
     * SharedPreferences的文件名
     * 它保存在"/data/data<package name>/shared_prefs"目录下
     */
    public static final String SHARED_PREFERENCES_FILE_NAME = "user_data";


    /**
     * SharedPreferences的文件名(CID保存用)
     * 它保存在"/data/data<package name>/shared_prefs"目录下
     */
    public static final String SHARED_PREFERENCES_FILE_NAME_CID = "user_data_cid";

    /**
     * 执行成功
     */
    public static final int CODE_EXECUTE_SUCCESS = 0x01;

    /**
     * 执行失败
     */
    public static final int CODE_EXECUTE_FAILURE = 0x02;

    /**
     * 执行发生异常
     */
    public static final int CODE_EXECUTE_EXCEPTION = 0x03;

    /**
     * 用户注册失败
     */
    public static final int CODE_REGISTER_PERSON_ERROR = 0x04;

    /**
     * 用户注册成功
     */
    public static final int CODE_REGISTER_PERSON_SUCCESS = 0x05;

    /**
     * 用户注册异常
     */
    public static final int CODE_REGISTER_PERSON_EXCEPTION = 0x06;

    /**
     * 企业注册失败
     */
    public static final int CODE_REGISTER_COMPANY_ERROR = 0x07;

    /**
     * 企业注册成功
     */
    public static final int CODE_REGISTER_COMPANY_SUCCESS = 0x08;

    /**
     * 企业注册异常
     */
    public static final int CODE_REGISTER_COMPANY_EXCEPTION = 0x09;

    /**
     * 从相册选取的request code
     */
    public static final int CODE_CHOOSE_LICENSE_FROM_PHOTOS = 0x10;

    /**
     * 拍照选取的request code
     */
    public static final int CODE_CHOOSE_LICENSE_FROM_TAKE = 0x11;

    /**
     * 任务详情获取成功
     */
    public static final int CODE_TASK_GET_SUCCESS = 0x12;

    /**
     * 下拉刷新
     */
    public static final int CODE_PULL_DOWN_REFRESH = 0x14;

    /**
     * 上拉加载
     */
    public static final int CODE_TOP_PULL_LOADING = 0x15;

    /**
     * 已加载全部
     */
    public static final int CODE_LOADED_ALL = 0x16;

    /**
     * 任务详情Request Code
     */
    public static final int CODE_REQUEST_TASK_DETAILS = 0x17;

    /**
     * 任务提醒失败
     */
    public static final int CODE_REMIND_STAFF_ERROR = 0x18;

    /**
     * 任务提醒成功
     */
    public static final int CODE_REMIND_STAFF_SUCCESS = 0x19;

    /**
     * 任务提醒请求异常
     */
    public static final int CODE_REMIND_STAFF_EXCEPTION = 0x20;

    /**
     * 修改用户头像成功
     */
    public static final int CODE_UPDATE_AVATAR_SUCCESS = 0x21;

    /**
     * 修改手机号成功
     */
    public static final int CODE_UPDATE_PHONE_SUCCESS = 0x22;

    /**
     * 申请发票成功
     */
    public static final int CODE_APPLY_INVOICE_SUCCESS = 0x23;

    /**
     * 发布任务成功
     */
    public static final int CODE_RELEASE_TASK_SUCCESS = 0x24;

    /**
     * 用户未激活状态
     */
    public static final int CODE_USER_UNACTIVE = 0x25;

    /**
     * 订单详情获取成功
     */
    public static final int CODE_ORDER_GET_SUCCESS = 0x26;

    /**
     * 订单详情获取异常
     */
    public static final int CODE_ORDER_GET_EXCEPTION = 0x27;

    /**
     * 加载页面无数据
     */
    public static final int CODE_INIT_VIEW_NULL = 0x28;

    /**
     * 地址管理改变
     */
    public static final int CODE_EXECUTE_SUCCESS_CHANGE = 0x29;

    /**
     * 支付、充值等支付后回调接口执行成功
     */
    public static final int CODE_EXECUTE_CALL_BACK_SUCCESS = 0x30;
    /**
     * 消息管理改变
     */
    public static final int CODE_MESSAGE_SUCCESS_CHANGE = 0x31;

}