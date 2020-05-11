package com.chen.fy.orthogonaltable.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.chen.fy.orthogonaltable.R;
import com.chen.fy.orthogonaltable.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mEtInput;
    private TextView tvTestContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        applyPermission();
        initView();
        initData();
    }

    private void initView() {
        mEtInput = findViewById(R.id.et_input);
        tvTestContent = findViewById(R.id.tv_test_content);

        findViewById(R.id.btn_generate).setOnClickListener(this);
        findViewById(R.id.btn_file_generate).setOnClickListener(this);

        findViewById(R.id.tv_use_info).setOnClickListener(this);
    }

    private void initData() {
        StringBuilder sb = new StringBuilder();
        sb.append("此APP可以通过用户给定的数据\n自动地生成一个正交表");
        sb.append("\n即用最少的实验覆盖最多的操作");
        sb.append("\n");
        sb.append("\n测试实例如下：");
        sb.append("\n");

        sb = new StringBuilder();
        sb.append("操作系统:2000,XP,2003\n" +
                "浏览器:IE6.0,IE7.0,TT\n" +
                "杀毒软件:卡巴,金山,诺顿");
        tvTestContent.setText(sb.toString());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_generate:  //手动输入生成
                //进行正交表查询
                String inputStr = mEtInput.getText().toString();
                if (!inputStr.isEmpty()) {
                    Intent intent1 = new Intent(MainActivity.this, ShowActivity.class);
                    intent1.putExtra("inputStr", inputStr);
                    startActivity(intent1);
                } else {
                    Toast.makeText(this, "请先输入...", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.btn_file_generate:  //文件生成
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("file/*");//设置类型，任意后缀的可以这样写。
                startActivityForResult(intent, 1);
                break;
            case R.id.tv_use_info:
                Intent intent2 = new Intent(MainActivity.this, UseInfoActivity.class);
                startActivity(intent2);
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {
                Uri uri = data.getData();

                assert uri != null;
                File file = FileUtils.uriToFile(uri, this);

                //进行正交表查询
                assert file != null;
                String testStr = FileUtils.getFileContent(file);
                Intent intent = new Intent(MainActivity.this, ShowActivity.class);
                intent.putExtra("inputStr", testStr);
                startActivity(intent);
            }
        }
    }

    /**
     * 动态申请危险权限
     */
    private void applyPermission() {
        //权限集合
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.
                WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.
                READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[0]);
            ActivityCompat.requestPermissions(this, permissions, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0) {
                for (int result : grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "必须同意所有权限才可以使用本程序!", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }
                }
            } else {
                Toast.makeText(this, "发生未知错误", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
