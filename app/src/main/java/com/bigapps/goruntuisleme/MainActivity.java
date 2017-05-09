package com.bigapps.goruntuisleme;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class MainActivity extends AppCompatActivity {
    private static int RESULT_LOAD_IMG = 1;
    ImageView  filtre_iv;
    Button galeri_btn;
    Bitmap bitmap;
    String imgDecodableString;

    private BaseLoaderCallback mOpenCVCallBack = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                            WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    Log.i("GoruntuIsleme", "OpenCV loaded successfully");

                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }else{
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
        setContentView(R.layout.activity_main);
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        galeri_btn = (Button) findViewById(R.id.galeri_btn);
        final Button gaussian_btn = (Button) findViewById(R.id.gaussian_btn);
        final Button ortalama_btn = (Button) findViewById(R.id.ortalama_btn);
        final ImageView bozuk_iv = (ImageView) findViewById(R.id.bozuk_iv);
        filtre_iv = (ImageView) findViewById(R.id.filtre_iv);
        //bozuk_iv.setDrawingCacheEnabled(true);

        Log.i("GoruntuIsleme", "Trying to load OpenCV library");
        if (!OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mOpenCVCallBack))
        {
            Log.e("GoruntuIsleme", "Cannot connect to OpenCV Manager");
        }

        galeri_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMG);
                filtre_iv.setImageDrawable(null);


            }
        });

        gaussian_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filtre_iv.setImageDrawable(null);
                
                bitmap = ((BitmapDrawable)bozuk_iv.getDrawable()).getBitmap();
                Bitmap filtreImg = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
                Mat matImg = new Mat();
                Utils.bitmapToMat(bitmap, matImg);
                Mat blurredImageMat = new Mat();
                Size size=new Size(7,7);
                Imgproc.GaussianBlur(matImg, blurredImageMat, size,0,0);
                Utils.matToBitmap(blurredImageMat,filtreImg);
                filtre_iv.setImageBitmap(filtreImg);
            }
        });

        ortalama_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filtre_iv.setImageDrawable(null);

                bitmap = ((BitmapDrawable)bozuk_iv.getDrawable()).getBitmap();
                Bitmap filtreImg = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
                Mat matImg = new Mat();
                Utils.bitmapToMat(bitmap, matImg);
                Mat blurredImageMat = new Mat();
                Size size=new Size(7,7);
                Imgproc.blur(matImg, blurredImageMat, size);
                Utils.matToBitmap(blurredImageMat,filtreImg);
                filtre_iv.setImageBitmap(filtreImg);

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            ImageView imageView = (ImageView) findViewById(R.id.bozuk_iv);
            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));

        }


    }


}
