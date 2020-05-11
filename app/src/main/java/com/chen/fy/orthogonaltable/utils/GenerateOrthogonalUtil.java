package com.chen.fy.orthogonaltable.utils;

import android.app.Activity;
import android.content.Intent;

import com.chen.fy.orthogonaltable.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class GenerateOrthogonalUtil {

    //正交表生成
    public static List<List<String>> generate(List<String> list, Activity activity) {

        String[][] OriginalTable = new String[list.size()][]; //正交表  储存着我们要测试的用例
        boolean isEqual = true;
        int factor = 0;  //因素数
        int levels = 0;  //水平数

        for (int n = 0; n < list.size(); n++) {
            String temp = "";
            temp = list.get(n).replace("，", ",");
            temp = temp.replace("：", ":");
            OriginalTable[n] = temp.split(":|,");
        }

        factor = OriginalTable.length;          //有几列
        levels = OriginalTable[0].length - 1;   //去掉：前的一项


        //判断各个因素的水平数是否相同
        for (int i = 0; i < OriginalTable.length; i++) {
            if (levels != OriginalTable[i].length - 1) {
                isEqual = false;
            }
        }

        if (isEqual) {  //第一种情况 水平数相等
            List<List<String>> tableList = getTable(factor, levels,activity);

            if (tableList != null) {
                List<List<String>> completedTable = new ArrayList<>();
                List<String> tableLine = new ArrayList<>();
                for (int n = 0; n < factor; n++) {
                    tableLine.add(OriginalTable[n][0]);  //操作系统等等
                }
                completedTable.add(tableLine);
                for (int n = 0; n < tableList.size(); n++) {
                    tableLine = new ArrayList<>();
                    for (int m = 0; m < factor; m++) {
                        if(OriginalTable[m].length == Integer.parseInt(tableList.get(n).get(m)) + 1){
                            return null;
                        }
                        tableLine.add(OriginalTable[m][Integer.parseInt(tableList.get(n).get(m)) + 1]);
                    }
                    completedTable.add(tableLine);
                }
                return completedTable;
            }
        } else {  //第二种情况 水平数不相等
            OriginalTable = sort(OriginalTable);
            int[] count = new int[99];
            for (int i = 0; i < OriginalTable.length; i++) {
                count[OriginalTable[i].length - 1]++;
            }
            List<List<String>> tableTist = getTable(count,activity);
            if (tableTist != null) {
                List<List<String>> completedTable = new ArrayList<List<String>>();
                List<String> tableLine = new ArrayList<String>();
                for (int n = 0; n < factor; n++) {
                    tableLine.add(OriginalTable[n][0]);
                }
                completedTable.add(tableLine);
                for (int n = 0; n < tableTist.size(); n++) {
                    tableLine = new ArrayList<String>();
                    for (int m = 0; m < tableTist.get(0).size(); m++) {
                        tableLine.add(OriginalTable[m][Integer.parseInt(tableTist.get(n).get(m)) + 1]);
                    }
                    completedTable.add(tableLine);
                }
                return completedTable;
            }
        }
        return null;
    }

    /**
     * 根据给定的匹配条件在正交表中进行匹配
     * @param identificationCode    匹配条件
     * @return  返回匹配完成后的List
     */
    private static List<List<String>> searchTable(String identificationCode, Activity activity) {
        try {

            //读取文件，并匹配关键字，存入List中
            InputStream inputStream = activity.getResources().openRawResource(R.raw.orthogonal_table);
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(inputStream));
            String lineText = null;
            boolean tableStart = false;
            List<String> table = new ArrayList<>();
            while ((lineText = br.readLine()) != null) { // 数据以逗号分隔
                if (lineText.contains(identificationCode)) {
                    tableStart = true;
                }
                if (tableStart) {
                    if ("".equals(lineText)) //只读取相关部分
                        break;
                    else
                        table.add(lineText);
                }
            }
            br.close();

            if (!table.isEmpty()) {
                String[][] OriginalTable = new String[table.size() - 1][];
                for (int n = 0; n < table.size() - 1; n++) {
                    OriginalTable[n] = table.get(n + 1).split(" ");// 按空格拆分
                }

                List<List<String>> tableList = new ArrayList<>();
                for (int n = 0; n < OriginalTable.length; n++) {
                    List<String> columnList = new ArrayList<>();
                    for (int m = 0; m < OriginalTable[n].length; m++) {
                        if (OriginalTable[n][m].length() >= 3 | OriginalTable[n][m].length() == 2 && m == 0) {
                            char[] str1 = OriginalTable[n][m].toCharArray();
                            for (int i = 0; i < str1.length; i++) {
                                columnList.add(String.valueOf(str1[i]));
                            }
                        } else {
                            columnList.add(OriginalTable[n][m]);
                        }
                    }
                    tableList.add(columnList);
                }
                return tableList;
            } else
                return null;
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据给定的因素数和水平数匹配符合的正交表
     * @param factor 因素数
     * @param level 水平数
     * @return  返回匹配完成的List
     */
    private static List<List<String>> getTable(int factor, int level,Activity activity) {
        String identificationCode = "";
        List<List<String>> list = null;
        identificationCode = identificationCode + level + "^" + factor + "     n";
        list = searchTable(identificationCode,activity);
        if (list == null) { // 若不存在刚好水平数和因素数符合的正交表 则取水平数相同，
                            // 则获取要素数最接近的一个正交表 但是不能超过太多，控制在4以内
            for (int n = 1; n <= 4; n++) {
                identificationCode = level + "^" + (factor + n) + "     n";
                list = searchTable(identificationCode,activity);
                if (list != null) { // 成功获取到要素数最接近的一个正交表
                    break;
                }
            }
        }
        return list;
    }

    private static List<List<String>> getTable(int[] levelsFactor,Activity activity) {
        String identificationCode = "";
        for (int i = 0; i < levelsFactor.length; i++)
            if (levelsFactor[i] != 0)
                identificationCode = identificationCode + i + "^" + levelsFactor[i] + " ";
        identificationCode = identificationCode + "    n";
        return searchTable(identificationCode,activity);
    }

    private static String[][] sort(String[][] a) {
        if (a.length == 0) return a;
        System.out.println("");
        int size = a.length;
        for (int i = 0; i < size - 1; i++) {
            for (int j = i + 1; j < a.length; j++) {
                if (a[i].length > a[j].length) {
                    String[] temp = a[i];
                    a[i] = a[j];
                    a[j] = temp;
                }
            }
        }
        return a;
    }
}
