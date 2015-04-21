package org.sebbas.android.memegenerator;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class JSONHandler {

    private String mUrlString = null;

    private ArrayList<String> mImageUrls = new ArrayList<String>();
    private ArrayList<String> mDisplayNames = new ArrayList<String>();

    public volatile boolean mParsingComplete = false;
    public volatile boolean mParsingSuccessful = false;

    public JSONHandler(String url) {
        mUrlString = url;
    }

    public ArrayList<String> getImageUrls() {
        return mImageUrls;
    }
    public ArrayList<String> getDisplayNames() {
        return mDisplayNames;
    }

    @SuppressLint("NewApi")
    private void readAndParseJSON(String in) {
        try {
            JSONObject reader = new JSONObject(in);

            JSONArray data  = reader.getJSONArray("result");

            for (int i = 0; i < data.length(); i++) {
                JSONObject jsonObject = data.getJSONObject(i);

                String displayName = jsonObject.getString("displayName");
                String imageUrl = jsonObject.getString("imageUrl");

                mDisplayNames.add(displayName);
                mImageUrls.add(imageUrl);
            }

            mParsingComplete = true;
            mParsingSuccessful = true;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void fetchJSON() {
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    OkHttpClient okHttpClient = new OkHttpClient();
                    okHttpClient.setConnectTimeout(15, TimeUnit.SECONDS); // connect timeout
                    okHttpClient.setReadTimeout(15, TimeUnit.SECONDS);    // socket timeout

                    Request request = new Request.Builder()
                            .url(mUrlString)
                            .build();

                    Response response = okHttpClient.newCall(request).execute();
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                    InputStream stream = response.body().byteStream();
                    String data = convertStreamToString(stream);

                    readAndParseJSON(data);

                } catch (Exception e) {
                    e.printStackTrace();
                    mParsingComplete = true;
                    mParsingSuccessful = false;
                }
            }
        });

        thread.start();
    }

    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
