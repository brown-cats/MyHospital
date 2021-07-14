package com.example.myhospital;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import datagetter.DataGetter;
import entity.Doctor;
import utils.Utils;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    protected List<Doctor> doctorList = new ArrayList<Doctor>();
    private DataGetter dataGetter = new DataGetter();
    private ArrayList<ImageView> imageViewArrayList = new ArrayList<>();
    private ArrayList<Bitmap>  bitmapArrayList = new ArrayList<>();
    private BaseAdapter listadpter = new BaseAdapter() {

        @Override
        public int getCount() {
            return doctorList.size();
        }
        @Override
        public Object getItem(int position) {
            return doctorList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return doctorList.get(position).getId();
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = null;
            if(convertView == null){
                Log.i("info" ,"没有缓存，重新生成"+position);
                LayoutInflater layoutInflater = MainActivity.this.getLayoutInflater();
                view = layoutInflater.inflate(R.layout.label , null);
            }
            else{
                Log.i("info" , "有缓存，不需要重新生成"+position);
                view = convertView;
            }
            Doctor doctor = doctorList.get(position);
            // read the data
            ImageView imageView = view.findViewById(R.id.imageView);
            imageViewArrayList.add(imageView);
            imageView.setImageBitmap(bitmapArrayList.get(doctor.getId()-1));
            // image Listener for change the icon
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

//            imageView.setImageDrawable(getResources().getDrawable(R.drawable.icon1));

            TextView nameView = view.findViewById(R.id.name_text);
            nameView.setText(doctor.getName());
            TextView sexView = view.findViewById(R.id.sex_text);
            sexView.setText(doctor.getSex());
            TextView ageView =view.findViewById(R.id.age_text);
            ageView.setText(doctor.getAge().toString());
            TextView officeView = view.findViewById(R.id.office_text);
            officeView.setText(doctor.getOffice());
            return view;
        }
        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
        }

    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    String result = dataGetter.get("https://10.0.2.2:8080/testBoot/getAll");
//                    Log.i("info" , "dataGetter:"+result);
//                    if(result!=null&&!result.contentEquals("")){
//                        String[] parseRs = result.split(";");
//                        if(parseRs!=null)
//                            for(int i = 0 ; i < parseRs.length;i++){
//                                String[] tem = parseRs[i].split(",");
//                                Doctor doctor = new Doctor();
//                                doctor.setId(Integer.parseInt(tem[0]));
//                                doctor.setName(tem[1]);
//                                doctor.setAge(Integer.parseInt(tem[2]));
//                                doctor.setSex(tem[3]);
//                                doctor.setOffice(tem[4]);
//                                doctorList.add(doctor);
//                            }
//                    }
//                } catch (Exception e) {
//                    Log.i("failure", "run: " + e.toString());
//                }
//            }
//        }).start();
        AsyncUpdateDatasTask updateDatasTask = new AsyncUpdateDatasTask();
        updateDatasTask.execute();

        // get the listView
        listView = (ListView)findViewById(R.id.listView1);
        listView.setAdapter(listadpter);

        // set the Listener to jump the doctor infomation page
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Doctor doctor =(Doctor) listadpter.getItem(position);

                Log.i("click message:" , "doctorId:"+ doctor.getId());
                Log.i("click message:" , "doctor:"+ doctor.toString());
                Intent intent = new Intent();
                intent.setClass(
                        MainActivity.this,
                        InfoActivity.class);
                // pass the data
                intent.putExtra("doctorId", ""+doctor.getId());
                intent.putExtra("name" , ""+doctor.getName());
                intent.putExtra("age" , ""+doctor.getAge());
                intent.putExtra("sex" , ""+doctor.getSex());
                intent.putExtra("office" , ""+doctor.getOffice());
//                intent.putExtra("icon", Utils.Bitmap2Bytes(bitmapArrayList.get(doctor.getId()+1)));
//                intent.putExtra("intro", intro);
                startActivity(intent);
            }
        });
        listView.refreshDrawableState();
    }

    class AsyncUpdateDatasTask  extends AsyncTask<Void, Void, List<Doctor>> {
        DataGetter dataGetter = new DataGetter();
        @Override
        protected List<Doctor> doInBackground(Void... params) {
            // TODO Auto-generated method stub
            List<Doctor> list = new ArrayList<Doctor>();
            list = dataGetter.getDoctorList("https://10.0.2.2:8080/testBoot/getAll");
            Log.i("info", "doInBackground: list size: " + list.size());
            Bitmap bitmap;

            for (int i = 1; i <= list.size(); i++) {
                bitmap = dataGetter.getDoctorIcon(list.get(i-1).getId());
                if (bitmap != null) {
                    bitmapArrayList.add(bitmap);
                }
                else
                    Log.i("error", "doInBackground bitmap is null");
            }
            Log.i("info:", "doInBackground bitmapArrayList size:"+bitmapArrayList.size());
            return list;
        }

        @Override
        protected void onPostExecute(List<Doctor> result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            doctorList.addAll(result);
            Log.i("info" , "onPostExecute: size: "+result.size());
            listadpter.notifyDataSetChanged();
            int i = 0 ;
            System.out.println("imageViewList size:"+imageViewArrayList.size());
            if(imageViewArrayList.size()!=0){
                for(ImageView imageView : imageViewArrayList){
                    Bitmap bitmap = bitmapArrayList.get(i);
                    imageView.setImageBitmap(bitmap);

                    imageView.invalidate();
                    Log.i("info" , "onPostExecute: invalidate imageView: "+ i);
                    i++;
                }
                System.out.println("end update--------------");
//                return ;
            }

        }

    }

}

