package datagetter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.zhy.http.okhttp.https.HttpsUtils;
import com.zhy.http.okhttp.log.LoggerInterceptor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import entity.Doctor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DataGetter {
    // 这就是信任所有证书
    HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, null);
    private final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(10000L, TimeUnit.MILLISECONDS)
            .readTimeout(10000L, TimeUnit.MILLISECONDS)
            .addInterceptor(new LoggerInterceptor("TAG"))
            .hostnameVerifier((hostname, session) -> true)
            .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
            .build();

    public DataGetter() {

    }


    public String get(String url) {
        Request request = new Request.Builder().url(url).build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                String result = response.body().string();

                response.close();
//                Log.d("success", result.toString());
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("fail", "返回参数为空 ！");
        return null;
    }

    public List<Doctor> getDoctorList(String url) {
        String result = get(url);
        Log.i("info", "getDoctorList:" + result);
        List<Doctor> doctorList = new ArrayList<Doctor>();
        if (result != null && !result.contentEquals("")) {
            String[] parseRs = result.split(";");
            for (String parseR : parseRs) {
                String[] tem = parseR.split(",");
                Doctor doctor = new Doctor();
                doctor.setId(Integer.parseInt(tem[0]));
                doctor.setName(tem[1]);
                doctor.setAge(Integer.parseInt(tem[2]));
                doctor.setSex(tem[3]);
                doctor.setOffice(tem[4]);
                doctorList.add(doctor);
            }
        }
        return doctorList;
    }

    public Bitmap getDoctorIcon(int id) {
        String url = "https://10.0.2.2:8080/testBoot/getIconById/";
        Request request = new Request.Builder().url(url+id).build();
        Bitmap bitmap = null;
        try {
            Response response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                InputStream inputStream = response.body().byteStream();//得到图片的流
                bitmap = BitmapFactory.decodeStream(inputStream);
                response.close();
                if (bitmap != null)
                    Log.d("getIcon", "Datagetter/getDoctorIcon:bitmap is not null");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }
    public String getDoctorIntro(String url){
        return this.get(url);
    }

}

