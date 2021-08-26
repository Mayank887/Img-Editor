package com.example.imgeditor;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;



import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {

    int IMAGE_REQUEST_CODE=45;
    int CAMERA_REQUEST_CODE=14;
    Button edit,camera,save;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        edit=findViewById(R.id.button);
        camera=findViewById(R.id.button2);
        imageView=findViewById(R.id.img);
        save=findViewById(R.id.button4);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,IMAGE_REQUEST_CODE);
            }
        });
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(MainActivity.this,new String[] {Manifest.permission.CAMERA},32);
                }
                else {
                    Intent cameraintent =new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraintent,CAMERA_REQUEST_CODE);
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==IMAGE_REQUEST_CODE){
            if(data.getData()!=null){
                Toast.makeText(this, data.getData().toString(), Toast.LENGTH_SHORT).show();
                imageView.setImageURI(data.getData());
                ColorMatrix matrix=new ColorMatrix();
                matrix.setSaturation(0);
                imageView.setColorFilter(new ColorMatrixColorFilter(matrix));
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        saveimg();

                    }
                });
            }
        }
        if(requestCode==CAMERA_REQUEST_CODE){
            Bitmap photo=(Bitmap)data.getExtras().get("data");
            Uri uri=getImageUri(photo);
            imageView.setImageURI(uri);
            ColorMatrix matrix=new ColorMatrix();
            matrix.setSaturation(0);
            imageView.setColorFilter(new ColorMatrixColorFilter(matrix));
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveimg();

                }
            });

        }
    }

    private void saveimg() {
        imageView.setDrawingCacheEnabled(true);
        Bitmap b=imageView.getDrawingCache();
        MediaStore.Images.Media.insertImage(getContentResolver(),b,"a","b");
        Toast.makeText(this, "Saved to Gallery", Toast.LENGTH_SHORT).show();
    }

    public Uri getImageUri(Bitmap bitmap){
        ByteArrayOutputStream arrayOutputStream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,arrayOutputStream);
        String path=MediaStore.Images.Media.insertImage(getContentResolver(),bitmap,"Title",null);
        return Uri.parse(path);

    }
}