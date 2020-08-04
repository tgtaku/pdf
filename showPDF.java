package com.example.pdfview;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.pdf.PdfRenderer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import static android.os.Environment.getExternalStorageDirectory;

public class showPDF extends AppCompatActivity {
    String url = "http://";
    public static ArrayList<Bitmap> bitmapPDF = new ArrayList<>();
    private static int pageNum;
    private static TextView pdfPage;
    private static ImageView view;
    private int i = 1;
    private static String pageText;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_p_d_f);

        pdfPage = findViewById(R.id.page);

        //File sdcard = Environment.getExternalStorageDirectory(DOWNLOAD_SERVICE);
        File path = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        //System.out.println(path);
        ParcelFileDescriptor fd = null;
        PdfRenderer renderer = null;
        PdfRenderer.Page page = null;
        //PdfRenderer.Page page = null;
        try {
            // SDカード直下からtest.pdfを読み込み、1ページ目を取得
            fd = ParcelFileDescriptor.open(new File(path, "lowcarbon05.pdf"), ParcelFileDescriptor.MODE_READ_ONLY);
            renderer = new PdfRenderer(fd);
            pageNum = renderer.getPageCount();
            page = renderer.openPage(0);


            view = (ImageView) findViewById(R.id.pdfImage);
            int viewWidth = view.getWidth();
            int viewHeight = view.getHeight();
            float pdfWidth = page.getWidth();
            float pdfHeight = page.getHeight();
            Log.i("test", "viewWidth=" + viewWidth + ", viewHeight=" + viewHeight
                    + ", pdfWidth=" + pdfWidth + ", pdfHeight=" + pdfHeight);

            // 縦横比合うように計算
           /* float wRatio = viewWidth / pdfWidth;
            float hRatio = viewHeight / pdfHeight;
            if (wRatio <= hRatio) {
                viewHeight = (int) Math.ceil(pdfHeight * wRatio);
            } else {
                viewWidth = (int) Math.ceil(pdfWidth * hRatio);
            }
            Log.i("test", "drawWidth=" + viewWidth + ", drawHeight=" + viewHeight);

            // Bitmap生成して描画
            Bitmap bitmap = Bitmap.createBitmap(viewWidth, viewHeight, Bitmap.Config.ARGB_8888);
            page.render(bitmap, new Rect(0, 0, viewWidth, viewHeight), null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
*/

           Bitmap bitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);
           bitmapPDF.add(Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888));
           page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
           view.setImageBitmap(bitmap);
            pageText = i + "ページ / " + pageNum +"ページ";
            pdfPage.setText(pageText);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fd != null) {
                    fd.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (page != null) {
                page.close();
            }
            if (renderer != null) {
                renderer.close();
            }

        }
        showPdf sp = new showPdf();
        sp.execute(url);
    }



    private class showPdf extends AsyncTask<String, Void, ArrayList<Bitmap>> {
        @Override
        public ArrayList<Bitmap> doInBackground(String... params){
            ArrayList<Bitmap> bitmapArrayList = new ArrayList<>();
            Bitmap bmp = null;
            return bitmapArrayList;

        }
        @Override
        public void onPostExecute(ArrayList<Bitmap> bitmapArrayList){

        }
    }

    public boolean onTouchEvent(MotionEvent event){
        //X軸の取得
        float pointX = event.getX();
        //Y軸の取得
        float pointY = event.getY();

        //取得した内容をログに表示
        Log.d("TouchEvent", "X:" + pointX + ",Y:" + pointY);
        return true;
    }


    /*
    NextPage
    @param myButton nextPage
    */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void onClickNextPage(View myButton){
        if(i != pageNum){
            File path = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
            System.out.println(path);
            ParcelFileDescriptor fd = null;
            PdfRenderer renderer = null;
            PdfRenderer.Page page = null;
            try {
                fd = ParcelFileDescriptor.open(new File(path, "lowcarbon05.pdf"), ParcelFileDescriptor.MODE_READ_ONLY);
                renderer = new PdfRenderer(fd);
                page = renderer.openPage(i);

                view = (ImageView) findViewById(R.id.pdfImage);
                int viewWidth = view.getWidth();
                int viewHeight = view.getHeight();
                float pdfWidth = page.getWidth();
                float pdfHeight = page.getHeight();
                Log.i("test", "viewWidth=" + viewWidth + ", viewHeight=" + viewHeight
                        + ", pdfWidth=" + pdfWidth + ", pdfHeight=" + pdfHeight);
                Bitmap bitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                view.setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fd != null) {
                        fd.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (page != null) {
                    page.close();
                }
                if (renderer != null) {
                    renderer.close();
                }
            }
            i++;
            pageText = i + "ページ / " + pageNum +"ページ";
            pdfPage.setText(pageText);
            //pdfPage.setText(i + "ページ");
        }
    }

    /*
    BackPage
    @param myButton nextPage
    */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void onClickBackPage(View myButton){
        if(i != 1){
            //String pathname = "http://10.20.170.52/sample/pdf/sampleProject1/lowcarbon05.pdf";
            //File path = new File()
            File path = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
            //System.out.println(path);
            ParcelFileDescriptor fd = null;
            PdfRenderer renderer = null;
            PdfRenderer.Page page = null;
            try {
                fd = ParcelFileDescriptor.open(new File(path,"lowcarbon05.pdf"), ParcelFileDescriptor.MODE_READ_ONLY);
                //fd = ParcelFileDescriptor.open(new File(pathname), ParcelFileDescriptor.MODE_READ_ONLY);
                renderer = new PdfRenderer(fd);
                pageNum = renderer.getPageCount();
                page = renderer.openPage(i-2);

                view = (ImageView) findViewById(R.id.pdfImage);
                int viewWidth = view.getWidth();
                int viewHeight = view.getHeight();
                float pdfWidth = page.getWidth();
                float pdfHeight = page.getHeight();
                Log.i("test", "viewWidth=" + viewWidth + ", viewHeight=" + viewHeight
                        + ", pdfWidth=" + pdfWidth + ", pdfHeight=" + pdfHeight);
                Bitmap bitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                view.setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fd != null) {
                        fd.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (page != null) {
                    page.close();
                }
                if (renderer != null) {
                    renderer.close();
                }
            }
            i--;
            pageText = i + "ページ / " + pageNum +"ページ";
            pdfPage.setText(pageText);
            //pdfPage.setText(i + "ページ /" + pageNum +" ページ");
        }
    }

    public void cameraClick(View view){

    }

    public void createReportClick(View view){
        dialogFragment dialog = new dialogFragment();
        dialog.show(getSupportFragmentManager(), "dialogFragment");
    }
}