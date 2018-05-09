package com.telstar.launcher.ota;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemProperties;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.telstar.launcher.R;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import reco.frame.tv.TvHttp;
import reco.frame.tv.http.HttpHandler;
import reco.frame.tv.view.TvProgressBar;

/**
 * Created by bill on 2016/5/31.
 */
public class SystemUpdateActivity extends Activity  implements View.OnClickListener {

    private static final String TAG="update";
    ProgressBar progressBar ;
    TextView txtUpdateInfo ;
    IntentFilter filter;
    String downloadUrl ;
    String md5sum ;
    Button btnDownload ;
    TextView txtVersion ;
    TvProgressBar downloadProgressBar ;
    private HttpHandler<File> handler;
    private TvHttp tvHttp;
    Callback.Cancelable  mCancelable ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.system_update_layout);
        txtVersion = (TextView)findViewById(R.id.txtversion);
        String version =  SystemProperties.get("ro.product.version","");
        txtVersion.setText(version);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        txtUpdateInfo = (TextView) findViewById(R.id.txtUpdateInfo);
        btnDownload = (Button) findViewById(R.id.btn_download);
        downloadProgressBar = (TvProgressBar)findViewById(R.id.download_progressBar);
        downloadProgressBar.setVisibility(View.GONE);
        tvHttp = new TvHttp(getApplicationContext());
        Intent intent = new Intent(Utils.CHECK_NOW);
        progressBar.setVisibility(View.VISIBLE);
        txtUpdateInfo.setVisibility(View.GONE);
        btnDownload.setVisibility(View.GONE);
        btnDownload.setOnClickListener(this);
        filter=new IntentFilter();
        filter.addAction(Utils.CHECK_END);
        sendBroadcast(intent);

    }

    private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();
            if(action.equals(Utils.CHECK_END))
            {
                progressBar.setVisibility(View.GONE);
                txtUpdateInfo.setVisibility(View.VISIBLE);
               String status =   intent.getStringExtra("status");
                if("1".equals(status))
                {
                    btnDownload.setVisibility(View.VISIBLE);
                    String version = intent.getStringExtra("version");
                    md5sum = intent.getStringExtra("md5");
                    downloadUrl = intent.getStringExtra("url");
                    String str = context.getString(R.string.new_version);
                     str = String.format(str,version );
                    txtUpdateInfo.setText(str);
                }
                else if("2".equals(status))
                {
                    txtUpdateInfo.setText(R.string.ota_up_to_date);
                }
                else
                {
                    txtUpdateInfo.setText(R.string.unkown_error);
                }
            }
        }
    };

    @Override
    protected void onResume() {
        registerReceiver(mIntentReceiver, filter);
        super.onResume();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(mIntentReceiver);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btn_download)
        {
           /* if(btnDownload.getText().equals("Download"))
            {
                btnDownload.setText("Pause");
            }else
            {
                btnDownload.setText("Download");
            }*/
            StartDownload();
           // writeFileToSD();
         /*   if (handler == null) {
                StartDownload();
            } else {
                Log.e("t=", handler.isStop() + "");
                if (!handler.isStop()) {
                    handler.stop();
                } else {
                    StartDownload();
                }
            }*/
        }
    }

    private void writeFileToSD() {
        String sdStatus = Environment.getExternalStorageState();
        if(!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
            Log.d("TestFile", "SD card is not avaiable/writeable right now.");
            return;
        }
        try {
            String pathName="/sdcard/";
            String fileName="file.txt";
            File path = new File(pathName);
            File file = new File(pathName + fileName);
            if( !path.exists()) {
                Log.d("TestFile", "Create the path:" + pathName);
                path.mkdir();
            }
            if( !file.exists()) {
                Log.d("TestFile", "Create the file:" + fileName);
                file.createNewFile();
            }
            FileOutputStream stream = new FileOutputStream(file);
            String s = "this is a test string writing to file.";
            byte[] buf = s.getBytes();
            stream.write(buf);
            stream.close();

        } catch(Exception e) {
            Log.e("TestFile", "Error on writeFilToSD.");
            e.printStackTrace();
        }
    }

    private boolean copyFile(File src, File dst) {
        long inSize = src.length();
        long outSize = 0;
        int progress = 0;
        //listener.onCopyProgress(progress);
        try {
            if (dst.exists()) {
                dst.delete();
                dst.createNewFile();
            }
            FileInputStream in = new FileInputStream(src);
            FileOutputStream out = new FileOutputStream(dst);
            int length = -1;
            byte[] buf = new byte[1024];
            while ((length = in.read(buf)) != -1) {
                out.write(buf, 0, length);
                outSize += length;
                int temp = (int) (((float) outSize) / inSize * 100);
                if (temp != progress) {
                    progress = temp;
                    //listener.onCopyProgress(progress);
                }
              /*  if (mFocusStop) {
                    listener.onStopProgress(FAIL_STOP_FORCE);
                    out.flush();
                    in.close();
                    out.close();
                    return false;
                }*/
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
           /* if(outSize!=in.available()|| MD5.checkMd5Files(src,dst)){
                listener.onStopProgress(FAIL_STOP_COPYERROR);
            }*/
            out.flush();
            in.close();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private boolean copyFile(String src, String dst) {
        return copyFile(new File(src), new File(dst));
    }


    /**
     * 文件断点续示例 *先通过GET方法得到 文件下载地址
     */
    private void StartDownload() {

         downloadProgressBar.setVisibility(View.VISIBLE);
         //btnDownload.setVisibility(View.GONE);

        //final String savePath = getFilesDir()+ "/" + "update.zip";
        String savePath = "/sdcard/update.zip";
        Log.d(TAG,"savePath:"+savePath);
      /* handler = tvHttp.download(downloadUrl, savePath, false,
                new AjaxCallBack<File>() {

                    @Override
                    public void onLoading(long count, long current) {
                        int progress = (int) (current * 100 / count);
                        downloadProgressBar.setProgress(progress);
                        txtUpdateInfo.setText(progress+"%");
                        super.onLoading(count, current);
                    }

                    @Override
                    public void onSuccess(File t) {
                        downloadProgressBar.setVisibility(View.GONE);
                        txtUpdateInfo.setText("download sucess ");
                        //Toast.makeText(getApplicationContext(), "下载成功!!!", 1)
                        //        .show();
                        //copyFile(savePath,"/cache/update.zip");
                        super.onSuccess(t);
                    }

                    @Override
                    public void onFailure(Throwable t, int errorNo,
                                          String strMsg) {
                        //Toast.makeText(getApplicationContext(), strMsg, 1)
                        //        .show();
                        txtUpdateInfo.setText("download fail ");
                        btnDownload.setVisibility(View.VISIBLE);
                        downloadProgressBar.setVisibility(View.GONE);
                        super.onFailure(t, errorNo, strMsg);
                    }
                });
                */

        String fileSavePath = new File(savePath).getAbsolutePath();

        //String url="http://telstar.oss-cn-shenzhen.aliyuncs.com/test.zip";
        String url="http://ota.telstar.net.cn:81/1.zip";
        //文件保存在本地的路径
        String filepath="/sdcard/update.zip";
        File file = new File(filepath);
        if(file.exists())
        {
            file.delete();
        }
        if(mCancelable != null && !mCancelable.isCancelled() )
        {
            mCancelable.cancel();
            return;
        }
        RequestParams params = new RequestParams(url);
        params.setAutoResume(true);
        params.setSaveFilePath(filepath);
       mCancelable =  x.http().get(params, new Callback.ProgressCallback<File>() {
            @Override
            public void onWaiting() {

            }

            @Override
            public void onStarted() {

            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
                int progress = (int) (current * 100 / total);
                downloadProgressBar.setProgress(progress);
                txtUpdateInfo.setText(progress+"%");
            }

            @Override
            public void onSuccess(File result) {
                downloadProgressBar.setVisibility(View.GONE);
                txtUpdateInfo.setText("download sucess ");
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                txtUpdateInfo.setText("download fail ");
                btnDownload.setVisibility(View.VISIBLE);
                downloadProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });




    }


    public static String getSDPath() {
        File sdDir = null;
        try {
            boolean sdCardExist = Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED);
            // 判断sd卡是否存在
            if (sdCardExist) {
                sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
                return sdDir.toString();
            } else {
                return "/data/data/";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "/data/";

    }
}
