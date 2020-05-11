package com.chen.fy.orthogonaltable.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.chen.fy.orthogonaltable.R;
import com.chen.fy.orthogonaltable.utils.UiUtils;

public class UseInfoActivity extends AppCompatActivity {

    private TextView tvContent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.use_info);

        UiUtils.changeStatusBarTextImgColor(this, false);
        initView();
        initData();
    }

    private void initView() {
        tvContent = findViewById(R.id.tv_use_content);
    }

    private void initData() {
        StringBuilder sb = new StringBuilder();
        sb.append("此APP可以通过用户给定的数据\n自动地生成一个正交表");
        sb.append("\n即用最少的实验覆盖最多的操作");

        tvContent.setText(sb.toString());
    }
}
