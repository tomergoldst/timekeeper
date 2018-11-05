package com.tomergoldst.timekeeper.core;

import android.content.Context;

public final class TimeKeeper {

    private static Client sClient = null;

    /**
     * Gets the default instance.
     *
     * @return default instance
     */
    public static Client getInstance() {
        if (sClient == null){
            sClient = new Client();
        }
        return sClient;
    }

    /**
     * Initialize the SDK with the Android app context
     * Initializing is required before calling other methods.
     *
     * @param context context
     */
    public static void initialize(Context context) {
        getInstance().initialize(context);
    }

}
