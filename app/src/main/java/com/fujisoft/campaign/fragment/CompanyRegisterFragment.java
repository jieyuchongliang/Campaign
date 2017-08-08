package com.fujisoft.campaign.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.fujisoft.campaign.R;
import com.fujisoft.campaign.UserAgreementActivity;
import com.fujisoft.campaign.utils.Constants;
import com.fujisoft.campaign.utils.PhotoUtils;
import com.fujisoft.campaign.utils.Utils;
import com.fujisoft.campaign.view.MatrixImageView;

import java.io.File;

import static android.app.Activity.RESULT_OK;

/**
 * 企业注册用Fragment
 */
public class CompanyRegisterFragment extends Fragment implements View.OnClickListener {
    private String TAG = "campaign";
    private EditText edt_register_company_name;
    private EditText edt_company_user_name;
    private EditText edt_register_company_pwd;

    private EditText text_register_company_license;
    private ImageView btn_register_company_license;
    private ImageView img_register_company_license;

    private CheckBox checkBox_agree_company;
    private TextView agree_company;
    private Button btn_register_company_settle;

    private AlertDialog licenseDialog;

    private String basePath = Environment.getExternalStorageDirectory().getPath() + "/capture/";
    //为了解决小米机型拍照不能保存的问题，使用既存路径"/DCIM/Camera/"
    //private String basePath = Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera/";
    File fileTest = new File(basePath);
    private String compressedLicenseFilePath = basePath + "compressedLicense.jpeg";
    private File licenseFile = null;
    private String licenseFilePath;
    private Uri uri;

    private PopupWindow popupWindow;
    private onGetCompanyDataListener mCallback;

    public interface onGetCompanyDataListener {
        void onSetCompanyData(String companyName, String adminName, String adminPwd, File pic,String clientId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View mView = inflater.inflate(R.layout.fragment_company_view, container, false);
        if (!fileTest.exists()) {
            fileTest.mkdirs();
        }
        // 企业名称
        edt_register_company_name = (EditText) mView.findViewById(R.id.edt_register_company_name);

        // 用户名
        edt_company_user_name = (EditText) mView.findViewById(R.id.edt_company_user_name);

        // 密码
        edt_register_company_pwd = (EditText) mView.findViewById(R.id.edt_register_company_pwd);

        // 营业执照的相机
        btn_register_company_license = (ImageView) mView.findViewById(R.id.btn_register_company_license);
        btn_register_company_license.setOnClickListener(this);

        // 灰色的“营业执照”字体
        text_register_company_license = (EditText) mView.findViewById(R.id.text_register_company_license);
        text_register_company_license.setOnClickListener(this);

        // 显示营业执照的小图
        img_register_company_license = (ImageView) mView.findViewById(R.id.img_register_company_license);
        img_register_company_license.setOnClickListener(this);

        agree_company = (TextView) mView.findViewById(R.id.agree_company);
        agree_company.setOnClickListener(this);

        // 提交按钮
        btn_register_company_settle = (Button) mView.findViewById(R.id.btn_register_company_settle);
        btn_register_company_settle.setOnClickListener(this);

        // 用户协议的选项框
        checkBox_agree_company = (CheckBox) mView.findViewById(R.id.checkBox_agree_company);

        mView.setFocusable(true);
        mView.setFocusableInTouchMode(true);
        mView.setOnKeyListener(backListener);
        return mView;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (onGetCompanyDataListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onGetCompanyDataListener");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.agree_company:
                Intent intent = new Intent(getContext(), UserAgreementActivity.class);
                // getContext().startActivity(intent);
                intent.putExtra("from_fragment","CompanyRegisterFragment");
                startActivityForResult(intent, Activity.RESULT_FIRST_USER);
                break;
            case R.id.btn_register_company_license:

            case R.id.text_register_company_license:
                showLicenseChooseDialog();
                break;
            case R.id.img_register_company_license:
                if (getImageThumbnail(licenseFile.getPath(), 360, 60) != null) {
                    showPopupWindow(getActivity().findViewById(R.id.tool_bar_center_text_view));
                }
                break;
            case R.id.btn_register_company_settle:

                if(!checkBox_agree_company.isChecked()){
                    Toast.makeText(getActivity(), getResources().getString(R.string.toast_login_rule_company), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (Utils.isConnect(getContext())) {
                    if (checkEmpty()) {


                        SharedPreferences sp =getActivity().getSharedPreferences(Constants.SHARED_PREFERENCES_FILE_NAME_CID, Context.MODE_MULTI_PROCESS);
                        String clientId = sp.getString(Constants.EXTRA_USER_CID,"0");
                        mCallback.onSetCompanyData(edt_register_company_name.getText().toString().trim(), edt_company_user_name.getText().toString().trim(), edt_register_company_pwd.getText().toString().trim(), licenseFile,clientId);
                    }
                } else {
                    Utils.showToast(getContext(), R.string.netWrong);
                }
                break;
            case R.id.license_choose_takePhoto: // 拍照
                if (licenseDialog != null && licenseDialog.isShowing()) {
                    licenseDialog.dismiss();
                }
                Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                licenseFile = new File(basePath + "license.jpg");
                uri = Uri.fromFile(licenseFile);
                takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                startActivityForResult(takePhotoIntent, Constants.CODE_CHOOSE_LICENSE_FROM_TAKE);
                break;
            case R.id.license_choose_choosePhoto: // 从相册中选择
                if (licenseDialog != null && licenseDialog.isShowing()) {
                    licenseDialog.dismiss();
                }
                Intent choosePhotoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                choosePhotoIntent.addCategory(Intent.CATEGORY_OPENABLE);
                choosePhotoIntent.setType("image/*");
                startActivityForResult(choosePhotoIntent, Constants.CODE_CHOOSE_LICENSE_FROM_PHOTOS);
                break;
            default:
                break;
        }
    }

    private boolean checkEmpty() {
        if (edt_register_company_name.getText().toString().trim() == null || "".equals(edt_register_company_name.getText().toString().trim())) {
            showErrorDialog("企业名称不能为空，请重新输入！");

        } else if (edt_company_user_name.getText().toString().trim() == null || "".equals(edt_company_user_name.getText().toString().trim())) {
            showErrorDialog("用户名不能为空，请重新输入！");
        } else if (edt_company_user_name.getText().toString().trim().length()<6 ||edt_company_user_name.getText().toString().trim().length()>32) {
            showErrorDialog("用户名位数不正确！请输入6-32位用户名！");

        } else if (edt_register_company_pwd.getText().toString().trim() == null || "".equals(edt_register_company_pwd.getText().toString().trim())) {
            showErrorDialog("密码不能为空，请重新输入！");
        } else if (edt_register_company_pwd.getText().toString().trim().length()<6 ||edt_register_company_pwd.getText().toString().trim().length()>32) {
            showErrorDialog("密码位数不正确！请输入6-32位密码！");

        } else if (licenseFile == null || !licenseFile.exists()) {
            showErrorDialog("营业执照不能为空，请重新输入！");
        } else {
            return true;
        }
        return false;
    }

    private void showErrorDialog(String message) {
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("提示")
//                .setMessage(String.format(getString(R.string.register_person_dialog_input_empty), message))
                .setMessage(message)
                .setPositiveButton("确定", null).create();
        dialog.show();
    }

    /**
     * 点击相机按钮后，显示“从相册中选择”和“拍照”的窗口
     */
    private void showLicenseChooseDialog() {
        LinearLayout dialogLayout = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.license_choose_dialog, null);
        licenseDialog = new AlertDialog.Builder(getContext()).create();
        licenseDialog.show();

        Window window = licenseDialog.getWindow();
        window.setContentView(dialogLayout);
        window.setGravity(Gravity.BOTTOM);

        Button chooseFromPhotos = (Button) dialogLayout.findViewById(R.id.license_choose_takePhoto);
        Button chooseFromTake = (Button) dialogLayout.findViewById(R.id.license_choose_choosePhoto);
        chooseFromPhotos.setOnClickListener(this);
        chooseFromTake.setOnClickListener(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (data != null) {
                if (data.getExtras() != null) {
                    if (data.getExtras().getBoolean("USER_AGREEMENT_FLAG")) {
                        checkBox_agree_company.setChecked(true);
                        return;
                    }
                }
            }

            switch (requestCode) {
                case Constants.CODE_CHOOSE_LICENSE_FROM_TAKE:
                    String sdStatus = Environment.getExternalStorageState();
                    if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
                        return;
                    }
                    if (data != null) {
                        licenseFilePath = data.getData().toString();
                    } else {
                        licenseFilePath = uri.getPath();
                    }
                    break;
                case Constants.CODE_CHOOSE_LICENSE_FROM_PHOTOS:
                    if (data != null) {
                        licenseFilePath = PhotoUtils.getPath(getContext(), data.getData());
                    } else {
                        return;
                    }
                    break;
            }
            compressedLicenseFilePath = PhotoUtils.compressImage(licenseFilePath, compressedLicenseFilePath);
            if (compressedLicenseFilePath != null) {
                licenseFile = new File(compressedLicenseFilePath);
                text_register_company_license.setVisibility(View.GONE);
                img_register_company_license.setVisibility(View.VISIBLE);
//                img_register_company_license.setImageBitmap(getImageThumbnail(licenseFile.getPath(), 360, 60));


                Bitmap bmp= BitmapFactory.decodeFile(licenseFile.getPath());

                img_register_company_license.setImageBitmap(bmp);
            }

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != bitmap) bitmap.recycle();
    }

    Bitmap bitmap = null;

    private Bitmap getImageThumbnail(String imagePath, int width, int height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        options.inJustDecodeBounds = false;
        int h = options.outHeight;
        int w = options.outWidth;
        int beWidth = w / width;
        int beHeight = h / height;
        int be = 1;
        if (beWidth < beHeight) {
            be = beWidth;
        } else {
            be = beHeight;
        }
        if (be <= 0) {
            be = 1;
        }
        options.inSampleSize = be;
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

    /**
     * PopupWindow
     *
     * @param view
     */
    private void showPopupWindow(View view) {

        View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.pop_window, null);
        MatrixImageView captureBig = (MatrixImageView) contentView.findViewById(R.id.capture_big);
        popupWindow = new PopupWindow(contentView, 870, 1330);

        popupWindow.setFocusable(true);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);

        popupWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });

        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.transparent));
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        bitmap = BitmapFactory.decodeFile(compressedLicenseFilePath, options);
        captureBig.setImageBitmap(bitmap);

        popupWindow.showAsDropDown(view, 100, 0);
    }

    /**
     * 监听返回键
     */
    private View.OnKeyListener backListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View view, int i, KeyEvent keyEvent) {
            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                if ((i == KeyEvent.KEYCODE_BACK)) {
                    if (popupWindow != null && popupWindow.isShowing()) {
                        popupWindow.dismiss();
                        popupWindow = null;
                        return true;
                    } else {
                        return false;
                    }
                }
            }
            return false;
        }
    };
}