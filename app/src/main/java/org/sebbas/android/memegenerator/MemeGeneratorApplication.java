package org.sebbas.android.memegenerator;

import android.app.Application;
import android.content.Context;

public class MemeGeneratorApplication extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();

        MemeGeneratorApplication.mContext = getApplicationContext();
    }

    public static Context getAppContext() {
        return MemeGeneratorApplication.mContext;
    }
}
