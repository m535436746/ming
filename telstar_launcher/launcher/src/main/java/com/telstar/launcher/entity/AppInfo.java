package com.telstar.launcher.entity;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

public class AppInfo {
    /**
     * 此属性用于数据库操作
     */
    public int id;
    /**
     * The application name.
     */
    public CharSequence Title;

    /**
     * A bitmap of the application's text in the bubble.
     */
    public Bitmap titleBitmap;
    /**
     * The intent used to start the application.
     */
    public Intent intent;

    /**
     * A bitmap version of the application icon.
     */
    public Bitmap iconBitmap;
    /**
     * The application icon.
     */
    public Drawable icon;

    /**
     * When set to true, indicates that the icon has been resized.
     */
    public boolean filtered;

    public ComponentName componentName;

    public boolean fromNetwork ;

    public String apkDownloadUrl ;

    public String apk_md5sum ;
    /**
     * Creates the application intent based on a component name and various launch flags.
     *
     * @param className   the class name of the component representing the intent
     * @param launchFlags the launch flags
     */
    public final void setActivity(ComponentName className, int launchFlags) {
        intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setComponent(className);
        intent.setFlags(launchFlags);
        componentName = className;
    }

    public String title;
    public String imageUrl;
    public int num;
    public int getNum() {
        return num;
    }
    public void setNum(int num) {
        this.num = num;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public  boolean isFromNetwork ()
    {
        return  fromNetwork ;
    }

    public  void setFromNetwork( boolean _fromNetwork)
    {
        fromNetwork = _fromNetwork;
    }

    public  String getApkDownloadUrl()
    {
        return  apkDownloadUrl;
    }

    public  void setApkDownloadUrl(String downloadUrl)
    {
        apkDownloadUrl = downloadUrl ;
    }

    public  String getApk_md5sum()
    {
        return apk_md5sum;
    }

    public void setApk_md5sum(String md5sum)
    {
        apk_md5sum = md5sum ;
    }

}
