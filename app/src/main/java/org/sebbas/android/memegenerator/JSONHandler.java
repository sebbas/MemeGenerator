package org.sebbas.android.memegenerator;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;

public class JSONHandler {

    private String urlString = null;

    private ArrayList<String> mImageUrls = new ArrayList<String>();
    private ArrayList<String> mDisplayNames = new ArrayList<String>();

    public volatile boolean parsingComplete = true;

    public JSONHandler(String url) {
        this.urlString = url;
    }

    public ArrayList<String> getImageUrls() {
        return mImageUrls;
    }
    public ArrayList<String> getDisplayNames() {
        return mDisplayNames;
    }

    @SuppressLint("NewApi")
    public void readAndParseJSON(String in) {
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

            parsingComplete = false;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void fetchJSON() {
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    URL url = new URL(urlString);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(10000 /* milliseconds */);
                    conn.setConnectTimeout(15000 /* milliseconds */);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    // Starts the query
                    conn.connect();
                    InputStream stream = conn.getInputStream();

                    String data = convertStreamToString(stream);

                    readAndParseJSON(data);
                    stream.close();

                } catch (Exception e) {
                    e.printStackTrace();
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
