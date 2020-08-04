package com.example.pdfview;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MyPage extends AppCompatActivity {
    private String[] spinnerItems = {"Spinner", "Android", "Apple", "Windows"};
    public String urlProjectName = "http://10.20.170.52/sample/downloadPDF.php";
    public String urlPDF = "http://10.20.170.52/sample/pdf/sampleProject1/lowcarbon05.pdf";
    public static ArrayList<String> projectName = new ArrayList<>();
    public static ArrayList<String> fileName = new ArrayList<>();
    public static ArrayList<String> path = new ArrayList<>();
    Spinner spinner;
    //public String uplode = "http://10.20.170.52/sample/uploadPDF.php";
    public Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);

        TextView textViewUser = (TextView)findViewById(R.id.user);
        textViewUser.setText("User： " + MainActivity.username);

        //spinnerの定義
        spinner  = findViewById(R.id.spinner);
        final Spinner spinnerFile = findViewById(R.id.spinnerFile);

        getPDFInfo gp = new getPDFInfo();
        gp.execute(urlProjectName);



        // リスナーを登録
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            //　アイテムが選択された時
            @Override
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int position, long id) {
                Spinner spinner = (Spinner)parent;
                String item = (String)spinner.getSelectedItem();
                // ArrayAdapter
                ArrayAdapter<String> adapterFile
                        = new ArrayAdapter<String>(MyPage.this,
                        android.R.layout.simple_spinner_item, Collections.singletonList(fileName.get(0)));

                adapterFile.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                // spinner に adapter をセット
                spinnerFile.setAdapter(adapterFile);

            }

            //　アイテムが選択されなかった
            public void onNothingSelected(AdapterView<?> parent) {
                //
            }
        });

        button = (Button) findViewById(R.id.upDate);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
                return;
            }
        }
        enable_button();

    }

    private void enable_button(){
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                new MaterialFilePicker()
                        .withActivity(MyPage.this)
                        .withRequestCode(10)
                        .start();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        if(requestCode == 100 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)){
            enable_button();
        }else{
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},100);
            }
        }
    }

    ProgressDialog progress;

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10 && resultCode == RESULT_OK) {

            progress = new ProgressDialog(MyPage.this);
            progress.setTitle("Uploading");
            progress.setMessage("Please wait...");
            progress.show();

            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {

                    File f = new File(data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH));
                    System.out.println(f);
                    String content_type = getMimeType(f.getPath());

                    String file_path = f.getAbsolutePath();
                    OkHttpClient client = new OkHttpClient();
                    RequestBody file_body = RequestBody.create(MediaType.parse(content_type), f);

                    RequestBody request_body = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("type", content_type)
                            .addFormDataPart("uploaded_file", file_path.substring(file_path.lastIndexOf("/") + 1), file_body)
                            .build();

                    Request request = new Request.Builder()
                            .url("http://10.20.170.52/sample/save_file.php")
                            .post(request_body)
                            .build();

                    try {
                        Response response = client.newCall(request).execute();

                        if (!response.isSuccessful()) {
                            throw new IOException("Error : " + response);
                        }

                        progress.dismiss();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            t.start();


        }
    }

    private String getMimeType(String path){
        String extension = MimeTypeMap.getFileExtensionFromUrl(path);

        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }


    private class getPDFInfo extends AsyncTask<String, Void, ArrayList<String>>{
        @Override
        public ArrayList<String> doInBackground(String... params) {
            String params0_url = params[0];
            HttpURLConnection con = null;
            //http接続のレスポンスデータとして取得するInputStreamオブジェクトを宣言（try外）
            InputStream is = null;
            //返却用の変数
            StringBuffer conResult = new StringBuffer();

            switch (params0_url) {
                //プロジェクト名
                case "http://10.20.170.52/sample/downloadPDF.php":
                    System.out.println("projectName");
                    //http接続を行うHttpURLConnectionオブジェクトを宣言
                    //finallyで解放するためにtry外で宣言
                    /*HttpURLConnection con = null;
                    //http接続のレスポンスデータとして取得するInputStreamオブジェクトを宣言（try外）
                    InputStream is = null;
                    //返却用の変数
                    StringBuffer conResult = new StringBuffer();
*/
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

                    //事前情報の取得
                    String regex_projectName = "\"projectName\":.+?\",";
                    String regex_fileName = "\"fileName\":.+?\",";
                    String regex_path = "\"pass\":.+?F\"";
                    Pattern p_projectName = Pattern.compile(regex_projectName);
                    checkProjectName(p_projectName, conResult.toString());
                    //System.out.println(projectName);
                    Pattern p_fileName = Pattern.compile(regex_fileName);
                    checkFileName(p_fileName, conResult.toString());
                    //System.out.println(fileName);
                    Pattern p_path = Pattern.compile(regex_path);
                    checkPath(p_path, conResult.toString());
                    //System.out.println(path);
                    //result = conResult.toString();
                    return projectName;


                //PDF
                case "http://10.20.170.52/sample/pdf/sampleProject1/lowcarbon05.pdf":
                    System.out.println("fileName");

                    //図面登録
                case "http://10.20.170.52/sample/uploadPDF.php":

                    //http接続
                    //try {
                        //URLオブジェクト作成
                        //URL url = new URL(params[0]);
                        //System.out.println(url);
                        File pdfFile = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/lowcarbon05.pdf");
                        //File pdfFile = new File(dir.getAbsolutePath()+"/lowcarbon05.pdf");
                        System.out.println(pdfFile);
                        //URLオブジェクトからHttpURLConnectionオブジェクトを取得
                        //con = (HttpURLConnection) url.openConnection();

                        //ここから編集
                        /*StringBuilder sb = new StringBuilder();
                        try{
                            //pdfをjpeg形式で
                        }


                        byte[] byteArray;
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG)

                        //HTTPメソッドを指定する
                        con.setRequestMethod("POST");
                        //POST出力可能に設定
                        con.setDoOutput(true);
                        con.setDoInput(true);
                        //con.setRequestProperty("Accept", "application/json");
                        //データがJSONであること、エンコードを指定する
                        //con.setRequestProperty("Content-Type", "application/JSON; charset=utf-8"); //
                        con.setRequestProperty("Content-Type", "application/pdf");
                        //con.setRequestProperty("Content-Length", String.valueOf(json.length()));
                        //リクエストbodyにJSON文字列を書き込む
                        //OutputStreamWriter os = new OutputStreamWriter(con.getOutputStream());//リクエスト文字列のバイト列を送信する
                        //OutputStream os = new FileOutputStream("test.pdf");

                        byte[] buf = new byte[8192];
                        //InputStream is2 = new FileInputStream(pdfFile);
                        int c = 0;
                        while ((c = is2.read(buf, 0, buf.length)) > 0) {
                            os.write(buf, 0, c);
                            os.flush();
                            is2.close();
                        }
                        //System.out.println(json);
                        //os.write(json);
                        //os.flush();
                        con.connect();

                        //HTTPレスポンスコード
                        final int httpStatus = con.getResponseCode();
                        System.out.println(httpStatus);
                        *//*if(httpStatus == HttpURLConnection.HTTP_OK){
                            is = con.getInputStream();
                            String encoding = con.getContentEncoding();
                            if(null == encoding){
                                encoding = "UTF-8";
                            }
                            final InputStreamReader inReader = new InputStreamReader(is, encoding);
                            final BufferedReader bufReader = new BufferedReader(inReader);
                            String line = bufReader.readLine();
                            //1行ずつ読み込み
                            while (line != null){
                                result.append(line);
                                line = bufReader.readLine();
                            }
                            bufReader.close();
                            inReader.close();
                            is.close();

                        }else {
                            //通信失敗時のレスポンスコード
                            System.out.println(httpStatus);
                        }*//*
                        //OutputStreamを開放する
                        os.close();
                    } catch (ProtocolException e) {
                        e.printStackTrace();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        //HttpURLConnectionがnullでないなら解放
                        if (con != null) {
                            con.disconnect();
                        }*/
                        //}

            }
            return null;
        }

        @Override
        public void onPostExecute(ArrayList<String> stringArrayList){
            if (stringArrayList == projectName) {
                // ArrayAdapter
                ArrayAdapter<String> adapter
                        = new ArrayAdapter<String>(MyPage.this,
                        android.R.layout.simple_spinner_item, stringArrayList);

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                // spinner に adapter をセット
                spinner.setAdapter(adapter);
            }

            else if(stringArrayList == null){
                System.out.println("upload");
            }

        }
    }

    //正規表現でJSON形式から配列に当てはめる
    private static void checkProjectName(Pattern p, String target){
        Matcher m = p.matcher(target);
        while(m.find()){
            String pName = m.group();
            projectName.add(pName.substring(16, pName.length() - 2));
            //System.out.println(m.group());
        }
    }
    private static void checkFileName(Pattern p, String target){
        Matcher m = p.matcher(target);
        while(m.find()){
            fileName.add(m.group().substring(13, m.group().length() - 2));
            //System.out.println(m.group());
        }
    }
    private static void checkPath(Pattern p, String target){
        Matcher m = p.matcher(target);
        while(m.find()){
            path.add(m.group().substring(9, m.group().length() - 1));
            //System.out.println(m.group());
        }
    }
    public void showPDF(View view){
        Intent intent = new Intent(this, showPDF.class);
        startActivity(intent);
    }

    }


