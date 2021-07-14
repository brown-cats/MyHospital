package com.example.myhospital;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import datagetter.DataGetter;
import entity.Doctor;
import utils.Utils;

public class InfoActivity extends AppCompatActivity {
    private DataGetter dataGetter = new DataGetter();
    private int Id ;
    private String intro;
    ImageView imageView = null;
    TextView info_introView = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        // 首先获取到意图对象
        Intent intent = getIntent();

        // 获取到传递过来的姓名
        String doctorId = intent.getStringExtra("doctorId");
        Id = Integer.parseInt(doctorId);
        String name = intent.getStringExtra("name");
        String age = intent.getStringExtra("age");
        String sex = intent.getStringExtra("sex");
        String office = intent.getStringExtra("office");
        String intro = intent.getStringExtra("intro");
//        Bitmap bitmap = Utils.BytesToBitmap(intent.getByteArrayExtra("icon"));
        imageView = findViewById(R.id.info_iconView);
        InfoActivity.AsyncUpdateIconTask updateDatasTask = new AsyncUpdateIconTask();
        updateDatasTask.execute();

        TextView info_nameView = findViewById(R.id.info_name);
        TextView info_ageView = findViewById(R.id.info_ageText);
        TextView info_sexView = findViewById(R.id.info_sexText);
        TextView info_officeView = findViewById(R.id.info_office);
        info_introView = findViewById(R.id.info_longText);

        info_nameView.setText(name);
        info_ageView.setText(age);
        info_sexView.setText(sex);
        info_officeView.setText(office);
        ImageButton returnButton = findViewById(R.id.returnButton);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(
                        InfoActivity.this,
                        MainActivity.class);
                startActivity(intent);
            }
        });
    }

    class AsyncUpdateIconTask  extends AsyncTask<Void, Void, Bitmap> {
        DataGetter dataGetter = new DataGetter();
        @Override
        protected Bitmap doInBackground(Void... params) {
            // TODO Auto-generated method stub
            Bitmap bitmap = dataGetter.getDoctorIcon(Id);
            intro = dataGetter.getDoctorIntro("https://10.0.2.2:8080/testBoot/getTextById/"+(Id));

            return bitmap;
        }
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            // TODO Auto-generated method stub
            super.onPostExecute(bitmap);
            imageView.setImageBitmap(bitmap);
            info_introView.setText(intro);
            System.out.println("end update--------------");
        }
    }
}
