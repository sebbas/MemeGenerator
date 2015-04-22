package org.sebbas.android.memegenerator;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;

public class JSONHandler {

    private String mUrlString = null;

    private ArrayList<Integer> mViewCounts = new ArrayList<>();
    private ArrayList<String> mImageUrls = new ArrayList<>();
    private ArrayList<String> mImageIds = new ArrayList<>();

    public volatile boolean mParsingComplete = false;
    public volatile boolean mParsingSuccessful = false;

    public JSONHandler(String url) {
        mUrlString = url;
    }

    public ArrayList<Integer> getViewCounts() {
        return mViewCounts;
    }

    public ArrayList<String> getImageUrls() {
        return mImageUrls;
    }

    public ArrayList<String> getImageIds() {
        return mImageIds;
    }

    @SuppressLint("NewApi")
    private void readAndParseJSON(String in) {
        try {
            JSONObject reader = new JSONObject(in);

            // Only start loading json if success field is true
            if (isValid(reader)) {
                JSONArray data = reader.getJSONArray("data");
                System.out.println(data);
                for (int i = 0; i < data.length(); i++) {
                    JSONObject image = data.getJSONObject(i);

                    // Only look at json objects that represent images
                    if (!isAlbum(image)) {
                        int views = image.getInt("views");
                        String imageUrl = image.getString("link");
                        String imageId = image.getString("id");

                        mViewCounts.add(views);
                        mImageUrls.add(imageUrl);
                        mImageIds.add(imageId);
                    }
                }
                mParsingSuccessful = true;
            } else {
                mParsingSuccessful = false;
            }
            mParsingComplete = true;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isAlbum(JSONObject image) throws JSONException {
        boolean isAlbum = false;
        try {
            isAlbum = image.getBoolean("is_album");
        } finally {
            return isAlbum;
        }
    }

    private boolean isValid(JSONObject reader) throws JSONException {
        return reader.getBoolean("success");
    }

    public void fetchJSON() {
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    /*OkHttpClient okHttpClient = new OkHttpClient();
                    okHttpClient.setConnectTimeout(15, TimeUnit.SECONDS); // connect timeout
                    okHttpClient.setReadTimeout(15, TimeUnit.SECONDS);    // socket timeout

                    Request request = new Request.Builder()
                            .url(mUrlString)
                            .build();

                    Response response = okHttpClient.newCall(request).execute();
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                    InputStream stream = response.body().byteStream();
                    String data = convertStreamToString(stream);

                    readAndParseJSON(data);*/

                    URL url = new URL(mUrlString);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    //conn.setReadTimeout(10000 /* milliseconds */);
                    //conn.setConnectTimeout(15000 /* milliseconds */);
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Authorization", "Client-ID " + Utils.getImgurClientId());
                    conn.setDoInput(true);
                    // Starts the query
                    conn.connect();
                    InputStream stream = conn.getInputStream();

                    String data = convertStreamToString(stream);

                    readAndParseJSON(data);
                    stream.close();

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
