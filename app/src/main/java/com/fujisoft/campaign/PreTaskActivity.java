package com.fujisoft.campaign;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 任务预览画面
 *
 */
public class PreTaskActivity extends BaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_task);
        TextView title1 = (TextView) findViewById(R.id.title1);
        TextView title2 = (TextView) findViewById(R.id.title2);
        TextView text = (TextView) findViewById(R.id.text);
        ImageView picUrl = (ImageView) findViewById(R.id.picUrl);

        String name = String.valueOf(getIntent().getStringExtra("name"));
        String content = String.valueOf(getIntent().getStringExtra("content"));
        String file = String.valueOf(getIntent().getStringExtra("file"));
        Bitmap bmp= BitmapFactory.decodeFile(file);

        title1.setText(name);
        picUrl.setImageBitmap(bmp);

        title2.setText(name);
        text.setText(content);
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
        tool_bar_center_text_view.setText(R.string.pre_task_title);
    }
}