package com.example.pdfview;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    public static String loginResult = "";
    EditText UsernameEt, PasswordEt, HostEt;
    public static String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        HostEt = (EditText)findViewById(R.id.editHost);
        UsernameEt = (EditText)findViewById(R.id.editUserName);
        PasswordEt = (EditText)findViewById(R.id.editPass);
    }

    public void loginClick(View view){
        username = UsernameEt.getText().toString();
        String password = PasswordEt.getText().toString();
        String host = HostEt.getText().toString();
        String type = "login";
        BackgroundWorker backgroundWorker = new BackgroundWorker(this);
        backgroundWorker.execute(host, username, password);

    }

    public class BackgroundWorker extends AsyncTask<String, Void, String> {
        Context context;
        AlertDialog alertDialog;

        BackgroundWorker(Context ctx) {
            context = ctx;
        }

        @Override
        protected String doInBackground(String... params) {
            loginResult = "";
            String localHostUrl = params[0] + "login.php";
            HttpURLConnection httpURLConnection = null;
            try {
                String user_name = params[1];
                String pass_word = params[2];
                URL url = new URL(localHostUrl);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("user_name", "UTF-8") + "=" + URLEncoder.encode(user_name, "UTF-8") + "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(pass_word, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    loginResult += line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(loginResult);
            return loginResult;
        }

        @Override
        protected void onPostExecute(String result){
            Toast.makeText(MainActivity.this, result, Toast.LENGTH_LONG).show();
            //alertDialog.setMessage("result");
            //alertDialog.show();
            if(result.equals("login success!")) {
                Intent intent = new Intent(MainActivity.this, MyPage.class);
                startActivity(intent);
            }
            result = "";
        }
    }


}


