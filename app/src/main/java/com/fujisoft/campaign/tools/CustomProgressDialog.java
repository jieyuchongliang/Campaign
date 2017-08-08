package com.fujisoft.campaign.tools;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.TextView;

import com.fujisoft.campaign.R;

/**
 * Created by Administrator on 2015/7/29.
 */
public  class CustomProgressDialog extends Dialog {
    private Context context = null;
    private int anim=0;
    private static CustomProgressDialog commProgressDialog = null;

    public CustomProgressDialog(Context context){
        super(context);
        this.context = context;
    }

    public CustomProgressDialog(Context context, int theme, int anim) {
        super(context, theme);
        this.anim=anim;
    }

    public static CustomProgressDialog createDialog(Context context,int anim){
        commProgressDialog = new CustomProgressDialog(context, R.style.CommProgressDialog ,anim);
        commProgressDialog.setContentView(R.layout.comm_progress_dialog);
        commProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;

        return commProgressDialog;
    }



    public void onWindowFocusChanged(boolean hasFocus){

        if (commProgressDialog == null){
            return;
        }

        ImageView imageView = (ImageView) commProgressDialog.findViewById(R.id.iv_loading);
        if(anim!=0) {
            imageView.setBackgroundResource(anim);
        }
        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getBackground();
        animationDrawable.start();
    }

    /**
     * 设置标题
     * @param strTitle
     * @return
     */
    public CustomProgressDialog setTitile(String strTitle){
        return commProgressDialog;
    }

    /**
     * 设置提示内容
     * @param strMessage
     * @return
     */
    public CustomProgressDialog setMessage(String strMessage){
        TextView tvMsg = (TextView)commProgressDialog.findViewById(R.id.tv_loading_msg);

        if (tvMsg != null){
            tvMsg.setText(strMessage);
        }

        return commProgressDialog;
    }

//    /**屏蔽返回键**/
//    @Override
//    public boolean onKeyDown(int keyCode,KeyEvent event){
//        if(keyCode==KeyEvent.KEYCODE_BACK)
//            return true;
//        return super.onKeyDown(keyCode, event);
//    }
}
