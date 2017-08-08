package com.fujisoft.campaign;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.fujisoft.campaign.utils.Constants;
import com.fujisoft.campaign.utils.MultipartEntityEx;
import com.fujisoft.campaign.utils.Utils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 头像修改页面
 */
public class ChangePhotoActivity extends BaseActivity implements View.OnClickListener {
    private String TAG = "campaign";
    private SimpleDraweeView userPictureView;
    private String capturePathFromServer;
    private String userId = null;

    private static final int PHOTO_REQUEST_GALLERY = 1;
    private static final int PHOTO_REQUEST_TAKE_PHOTO = 2;
    private static final int PHOTO_REQUEST_CUT = 3;

    public static final String APP_CAPTURE_FOLDER_STORAGE_PATH = Environment.getExternalStorageDirectory().getPath() + "/com.fujisoft.campaign/" + "capture/";
    File localPictureFile = null;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            dismissProgressDialog();
            switch (msg.what) {
                case Constants.CODE_EXECUTE_FAILURE:
                    Toast.makeText(ChangePhotoActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
                case Constants.CODE_EXECUTE_SUCCESS:
                    initView();
                    break;
                case Constants.CODE_EXECUTE_EXCEPTION:
                    Toast.makeText(ChangePhotoActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
                case Constants.CODE_UPDATE_AVATAR_SUCCESS:
                    Toast.makeText(ChangePhotoActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_photo);

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        userId = sharedPreferences.getString(Constants.EXTRA_USER_ID, "");
        Log.d(TAG, "=== ChangePhotoActivity#onCreate() 从SharedPreferences中取到的userId = " + userId);

        Button galleryButton = (Button) findViewById(R.id.button_gallery);
        galleryButton.setOnClickListener(this);
        Button takePhotoButton = (Button) findViewById(R.id.button_take_photo);
        takePhotoButton.setOnClickListener(this);

        if (!new File(APP_CAPTURE_FOLDER_STORAGE_PATH).exists()) {
            new File(APP_CAPTURE_FOLDER_STORAGE_PATH).mkdirs();
        }
        localPictureFile = new File(APP_CAPTURE_FOLDER_STORAGE_PATH, getPhotoFileName());
        Log.d(TAG, "=== ChangePhotoActivity#onCreate() localPictureFile = " + localPictureFile);

        showProgressDialog();
        // 判断网络是否连接，请求服务器，接收页面数据
        if (Utils.isConnect(this)) {
            // 从服务器端获取数据
            getAvatar();
        } else {
            Utils.showToast(this, R.string.netWrong);
        }
    }

    @Override
    public void onCreateCustomToolBar(Toolbar toolbar) {
        super.onCreateCustomToolBar(toolbar);
        toolbar.showOverflowMenu();
        getLayoutInflater().inflate(R.layout.toolbar_button, toolbar);

        ImageButton tool_bar_back_button = (ImageButton) findViewById(R.id.tool_bar_back_button);
        tool_bar_back_button.setVisibility(View.VISIBLE);
        tool_bar_back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        TextView tool_bar_center_text_view = (TextView) findViewById(R.id.tool_bar_center_text_view);
        tool_bar_center_text_view.setVisibility(View.VISIBLE);
        tool_bar_center_text_view.setText(getString(R.string.modify_person_image));
    }

    public void initView() {
        userPictureView = (SimpleDraweeView) findViewById(R.id.user_picture_view);
        if (capturePathFromServer != null && !"".equals(capturePathFromServer) && !"null".equals(capturePathFromServer)) {
            userPictureView.setImageURI(Constants.PICTURE_BASE_URL + capturePathFromServer);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_gallery:
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, null);
                galleryIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(galleryIntent, PHOTO_REQUEST_GALLERY);
                break;
            case R.id.button_take_photo:
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Log.d(TAG, "=== ChangePhotoActivity#onClick() localPictureFile = " + localPictureFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(localPictureFile));
                startActivityForResult(cameraIntent, PHOTO_REQUEST_TAKE_PHOTO);
                break;
        }
    }

    private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        return dateFormat.format(date) + ".jpg";
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "=== ChangePhotoActivity#onActivityResult() requestCode = " + requestCode);
        switch (requestCode) {
            case PHOTO_REQUEST_GALLERY:
                if (data != null) {
                    Log.d(TAG, "=== ChangePhotoActivity#onActivityResult() data.getData(): " + data.getData());
                    startPhotoZoom(data.getData(), 100);
                }
                break;
            case PHOTO_REQUEST_TAKE_PHOTO:
                Log.d(TAG, "PHOTO_REQUEST_TAKE_PHOTO localPictureFile = " + localPictureFile.toString());
                Log.d(TAG, "PHOTO_REQUEST_TAKE_PHOTO Uri.fromFile(localPictureFile): " + Uri.fromFile(localPictureFile));
                startPhotoZoom(Uri.fromFile(localPictureFile), 100);
                break;
            case PHOTO_REQUEST_CUT:
                Log.d(TAG, "PHOTO_REQUEST_CUT data: " + data);
                if (data != null) {
                    setPicToView(data);
                }
                break;
        }
    }

    /**
     * 取得图像文件，截取返还
     *
     * @param uri  : 画像文件的uri
     * @param size : 画像文件的尺寸
     */
    private void startPhotoZoom(Uri uri, int size) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", size);
        intent.putExtra("outputY", size);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", true);

        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

    Bitmap bitmap = null;

    /**
     * 把截取图像文件设置为头像
     * 把图像保存到服务器
     *
     * @param picData
     */
    private void setPicToView(Intent picData) {
        Bundle bundle = picData.getExtras();
        FileOutputStream fileOutputStream = null;
        if (bundle != null) {
            bitmap = bundle.getParcelable("data");
            userPictureView.setImageBitmap(bitmap);
            try {
                localPictureFile.createNewFile();
                fileOutputStream = new FileOutputStream(localPictureFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                updateAvatar();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != bitmap) bitmap.recycle();
    }

    /**
     * 头像修改页面初始化
     */
    private void getAvatar() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msg = "Exception";
                Message message = new Message();
                try {
                    List<NameValuePair> params = new ArrayList<>();
                    params.add(new BasicNameValuePair("id", userId));
                    // 获取响应的结果信息
                    String json = getPostData(Constants.URL_USER_AVATAR, params);
                    Log.d(TAG, "=== ChangePhotoActivity#getAvatar() json = " + json);
                    // JSON的解析过程
                    if (json != null) {
                        JSONObject jsonObject = new JSONObject(json);//转换为JSONObject
                        boolean result = jsonObject.getBoolean("success");
                        msg = jsonObject.getString("msg");
                        if (result) {
                            JSONObject dataObject = jsonObject.getJSONObject("data");
                            capturePathFromServer = dataObject.getString("headPicUrl");// 用户头像url
                            Log.d(TAG, "=== ChangePhotoActivity#getAvatar() capturePathFromServer = " + capturePathFromServer);

                            // 获取数据成功后的处理
                            message.obj = msg;
                            message.what = Constants.CODE_EXECUTE_SUCCESS;
                            mHandler.sendMessage(message);
                        } else {
                            // 获取数据失败后的处理
                            message.obj = msg;
                            message.what = Constants.CODE_EXECUTE_FAILURE;
                            mHandler.sendMessage(message);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    // 获取数据异常后的处理
                    message.obj = msg;
                    message.what = Constants.CODE_EXECUTE_EXCEPTION;
                    mHandler.sendMessage(message);
                }
            }
        }).start();
    }

    /**
     * 修改头像
     */
    private void updateAvatar() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msg = "Exception";
                Message message = new Message();
                try {
                    //Post发送请求数据
                    MultipartEntityEx multiPartEntity = new MultipartEntityEx();
                    multiPartEntity.addPart("id", userId);
                    multiPartEntity.addPart("headPicUrl", localPictureFile);
                    //获取响应的结果信息
                    String json = getPostData(Constants.URL_USER_MODIFY_AVATAR, multiPartEntity);
                    Log.d(TAG, "=== ChangePhotoActivity#updateAvatar() json2 = " + json);
                    //JSON的解析
                    boolean result = false;
                    if (json != null) {
                        JSONObject jsonObject = new JSONObject(json);
                        result = (boolean) jsonObject.get("success");
                        msg = jsonObject.get("msg").toString();
                    }

                    if (!result) {
                        //修改失败后的处理
                        message.obj = msg;
                        message.what = Constants.CODE_EXECUTE_FAILURE;
                        mHandler.sendMessage(message);
                    } else {
                        //修改成功后的处理
                        message.obj = msg;
                        message.what = Constants.CODE_UPDATE_AVATAR_SUCCESS;
                        mHandler.sendMessage(message);
                    }

                } catch (Exception e) {
                    //修改异常后的处理
                    message.obj = msg;
                    message.what = Constants.CODE_EXECUTE_EXCEPTION;
                    mHandler.sendMessage(message);
                }
            }
        }).start();
    }
}