package com.example.pdfview;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class dialogFragmentDate extends DialogFragment {
    public static String user = "user1";
    public static ArrayList<String> date = new ArrayList<>();
    private String resultForGetDate;
    private String allUserUrl = "http://10.20.170.52/sample/sample1.php";
    private String userUrl = "http://10.20.170.52/sample/sample1_selectUser.php";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        String accessUrl = allUserUrl;
        if (dialogFragment.createReportVer == 1) {
            accessUrl = userUrl;
        }
        setDate st = new setDate();
        st.execute(accessUrl);

        final ArrayList<String> array = new ArrayList<>();
        array.add("sample1");
        array.add("sample1");
        array.add("sample1");
        String[] arrays = date.toArray(new String[array.size()]);
        //final String[] items2 = {array.get(0), array.get(1), array.get(2)};
        final String[] items = {"item_0", "item_1", "item_2"};
        final ArrayList<Integer> checkedItems = new ArrayList<Integer>();
        return new AlertDialog.Builder(getActivity())
                .setTitle("Selector")
                .setMultiChoiceItems(arrays, null, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (isChecked) checkedItems.add(which);
                        else checkedItems.remove((Integer) which);
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (Integer i : checkedItems) {
                            // item_i checked
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }


    private static void checkDate(Pattern p, String target) {
        Matcher m = p.matcher(target);
        while (m.find()) {
            date.add(m.group().substring(9, m.group().length() - 2));
            //System.out.println(m.group());
        }
    }

    private class setDate extends AsyncTask<String, Void, String> {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public String doInBackground(String... params) {
            //http接続を行うHttpURLConnectionオブジェクトを宣言
            //finallyで解放するためにtry外で宣言
            HttpURLConnection con = null;
            //http接続のレスポンスデータとして取得するInputStreamオブジェクトを宣言（try外）
            InputStream is = null;
            //返却用の変数
            StringBuffer conResult = new StringBuffer();
            //http接続
            try {
                //URLオブジェクト作成
                URL url = new URL(params[0]);
                //System.out.println(url);
                //URLオブジェクトからHttpURLConnectionオブジェクトを取得
                con = (HttpURLConnection) url.openConnection();
                //HTTP接続メソッドを設定
                con.setRequestMethod("GET");
                //接続
                con.connect();
                final int httpStatus = con.getResponseCode();
                System.out.println(httpStatus);
                //HttpURLConnectionオブジェクトからレスポンスデータの取得
                is = con.getInputStream();
                String encoding = con.getContentEncoding();
                if (null == encoding) {
                    encoding = "UTF-8";
                }
                final InputStreamReader inReader = new InputStreamReader(is, encoding);
                final BufferedReader bufReader = new BufferedReader(inReader);
                String line = bufReader.readLine();
                //1行ずつ読み込み
                while (line != null) {
                    conResult.append(line);
                    line = bufReader.readLine();
                }
                bufReader.close();
                inReader.close();
                is.close();
            } catch (MalformedURLException ex) {
            } catch (IOException ex) {
            } finally {
                //HttpURLConnectionオブジェクトがnull出ないならば解散
                if (con != null) {
                    con.disconnect();
                }
                //InputStreamオブジェクトがnull出ないならば解散
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException ex) {
                    }
                }
            }
            return conResult.toString();
        }

        @Override
        public void onPostExecute(String res) {
            //事前情報の取得
            String regex_date = "\"date\":.+?\",";
            Pattern p_date = Pattern.compile(regex_date);
            checkDate(p_date, resultForGetDate.toString());


        }
    }
}
