package com.chen.fy.orthogonaltable.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.chen.fy.orthogonaltable.R;
import com.chen.fy.orthogonaltable.utils.GenerateOrthogonalUtil;
import com.chen.fy.orthogonaltable.utils.UiUtils;
import com.chen.fy.orthogonaltable.views.TableView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ShowActivity extends AppCompatActivity {

    private TableView mTable;
    private TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show);

        initView();
        initData();
    }

    private void initView() {
        mTable = findViewById(R.id.table);
        tvTitle = findViewById(R.id.tv_title);
    }

    private void initData() {

        String inputStr = null;

        if (getIntent() != null) {
            inputStr = getIntent().getStringExtra("inputStr");
        }

        if (inputStr != null) {
            String[] strings = inputStr.split("\n");
            List<String> inputList = new ArrayList<>(Arrays.asList(strings));

            if(inputList.isEmpty()){
                return;
            }
            List<List<String>> resultList = GenerateOrthogonalUtil.generate(inputList, this);
            if (resultList != null) {
                showTable(resultList);
            }else{
                tvTitle.setText("无匹配的正交表");
            }
        }
    }

    private void showTable(List<List<String>> resultList) {
        //获取表格顶部header的标题
        int columns = resultList.get(0).size();
        String[] titles = new String[columns];
        for (int i = 0; i < columns; i++) {
            titles[i] = resultList.get(0).get(i);
        }

        //设置表头
        mTable = mTable.clearTableContents().setHeader(titles);

        //获取每一行的数值
        String[][] infos = new String[resultList.size() - 1][columns];
        for (int i = 1; i < resultList.size(); i++) {
            for (int j = 0; j < columns; j++) {
                infos[i-1][j] = resultList.get(i).get(j);
            }
            mTable.addContent(infos[i-1]);
        }
        mTable.refreshTable();
    }
}
