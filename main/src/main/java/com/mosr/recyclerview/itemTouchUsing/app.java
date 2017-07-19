package com.mosr.recyclerview.itemTouchUsing;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

/**
 * Synopsis     ${SYNOPSIS}
 * Author		Mosr
 * version		${VERSION}
 * Create 	    2017/3/10 15:40
 * Email  		intimatestranger@sina.cn
 */
public class app extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);
    }
}
