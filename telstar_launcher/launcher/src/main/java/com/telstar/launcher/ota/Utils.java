package com.telstar.launcher.ota;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StatFs;
import android.os.SystemProperties;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public final class Utils {
	private static final String TAG="update";

	public static final int DOWNLOAD_STATUS_NOSTART=0;
	public static final int DOWNLOAD_STATUS_START=1;
	public static final int DOWNLOAD_STATUS_PAUSE=2;
	

	public static final int HANDLER_CHECK_VERSION=50;
	public static final int HANDLER_DISPLAY_VIEW=51;
	
	public static final int HANDLER_DOWNLOAD_REFRESH=60;
	
	public static final int SERVICE_HANDLER_STORAGE_NOENOUGH=79;
	public static final int SERVICE_HANDLER_CHECK_VERSION=80;
	public static final int SERVICE_HANDLER_SHOW_UPDATEUI=81;
	public static final int SERVICE_HANDLER_DOWNLOAD=82;
	public static final int SERVICE_HANDLER_RECHECK_VERSION=83;
	public static final int SERVICE_HANDLER_NETWORK_ERROR=88;
	public static final int SERVICE_HANDLER_NO_NEW_VERSION=89;
	public static final int SERVICE_HANDLER_UNKOWN_ERROR=90;
	public static final int SERVICE_HANDLER_UPDATE_ISRUNING=91;
	
	public static final int UPDATE_HANDLER_CHECKFILE=84;
	public static final int CHECK_UPDATE_FILE=87;
	public static final int UPDATE_HANDLER_CHECKFILE_SUCCESS=85;
	public static final int UPDATE_HANDLER_CHECKFILE_FAILED=86;

	public static final String CHECK_VERSION="com.android.settings.ota.CHECK_VERSION";
	public static final String CHECK_VERSION_RETURN="com.android.settings.ota.CHECK_VERSION_RETURN";
	public static final String CHECK_VERSION_ERROR="com.android.settings.ota.CHECK_VERSION_ERROR";
	
	public static final String CANCEL_TASK="com.android.settings.ota.CANCEL_TASK";
	public static final String START_TASK="com.android.settings.ota.START_TASK";
	public static final String CHECK_NOW="com.android.settings.ota.CHECK_NOW";
	public static final String CHECK_END="com.android.settings.ota.CHECK_END";
	
	public static final String START_DOWNLOAD="com.android.settings.ota.START_DOWNLOAD";
	public static final String CANCEL_DOWNLOAD="com.android.settings.ota.CANCEL_DOWNLOAD";
	
	public static final String DOWNLOAD_DONE="com.android.settings.ota.DOWNLOAD_DONE";
	public static final String DOWNLOAD_ERROR="com.android.settings.ota.DOWNLOAD_ERROR";
	
	public static final String NETWORK_UNAVAILABLE="com.android.settings.ota.NETWORK_UNAVAILABLE";
	public static final String STORAGE_NOENOUGH="com.android.settings.ota.STORAGE_NOENOUGH";
	
	public static final String UPDATE_SYSTEM_NOW="com.android.settings.ota.UPDATE_SYSTEM_NOW";
	public static final String URL_PMP="http://yf.prestigio.com/YfOTA/default.asmx?op=ClientRequestUpgrade";
	public static final String URL_DATA="http://ota.mediacomeurope.it/YfOTA/default.asmx";

    public static final String URL_MidQuery="http://ota.telstar.net.cn/Entrance.asmx?op=MidQuery";
    public static final String URL_ReportDownloadRequest="http://ota.inhuasoft.cn/OTA_Query_WS/Entrance.asmx?op=ReportDownloadRequest";
    public static final String URL_ReportDownloadEnd="http://ota.inhuasoft.cn/OTA_Query_WS/Entrance.asmx?op=ReportDownloadEnd";
    public static final String URL_EntranceMethod="http://ota.inhuasoft.cn/OTA_Query_WS/Entrance.asmx?op=EntranceMethod";
   // public static final String URL_IP="http://218.17.160.137:2013/Entrance.asmx?op=MidQuery";
	public static final String NAMESPACE="http://tempuri.org/";
	public static final String METHOD="ClientRequestUpgrade";
	public static final String SOAPACTION="http://tempuri.org/ClientRequestUpgrade";
	
	private static final String flash_path="/mnt/sdcard";
	private static final String sdcard_path="/mnt/external_sd";
	public static final int STORAGE_FLASH=0;//flash
	public static final int STORAGE_SDCARD=1;//sdcard
	public static final String STORAGE_TYPE="flash";
	
	public static final int PLATFORM=1;//rk2818
	//public static final int PLATFORM=2;//Sangsumg
	
	public static File getStorageType(String type)
	{
		File file=null;
		if(type.equals("sdcard"))
		{
			//file=Environment.getExternalStorageDirectory();
			file=new File(sdcard_path);
		}
		else
		{
			file=new File(flash_path);
			//file=Environment.getFlashStorageDirectory();
			//file=Environment.getExternalStorageDirectory();
		}
		return file;
	}

	public static File getFileExist(String version,String type)
    {
		  File filef=getStorageType(type);
		  
		 // File filef=new File("/mnt/sdcard");
		 
	    File fd=new File(filef.getAbsolutePath(),"update_"+version+".zip");
	    return fd;
    }

	public static String makeTimeString(Context context, long pos,long flen)
	{

		return "";
	}
	public static String makeTimeString(Context context, int pos)
	{

		return "";
	}
	
	public static void sendBroadCastToFreshMedia(Context context,String type)
    {
       Intent intent = new Intent(Intent.ACTION_MEDIA_MOUNTED);
       //intent.setData(Uri.parse("file://" + "/flash"));
       if(type.equals("sdcard")){
    	   intent.setData(Uri.parse("file://" + "/mnt/external_sd"));
       }else{
    	   intent.setData(Uri.parse("file://" + "/mnt/sdcard"));
       }
       //intent.putExtra("read-only", false);            
       context.sendBroadcast(intent);
    }
	

	public static boolean isNetworkOpen(Context context)
	{
		boolean NetResult;
		try {
			ConnectivityManager connectivity = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = connectivity.getActiveNetworkInfo();
			NetResult = ((info != null) && (info.isConnected()));
		} catch (Exception e) {
			Log.d(TAG, "isNetworkOpen():"+e.toString());
			NetResult = false;
		}
		Log.d(TAG, "isNetworkOpen():"+NetResult);
		return NetResult;
	}
	
	private static final int DISK_SIZE_LIMIT = 150;
    
    public static boolean getSDCardStatus(String type)
    {
    	File filef=null;
    	if(type.equals("sdcard")){
    		filef=new File(sdcard_path);
    	}else
    	{
    		filef=new File(flash_path);
    		//filef=Environment.getExternalStorageDirectory();
    	}
    	if(filef.exists()){
    		StatFs fs = new StatFs(filef.getAbsolutePath());
			long blocks = fs.getAvailableBlocks();
	        long blockSize = fs.getBlockSize();
	        Log.d(TAG, "-------------->getSDCardStatus:"+(blocks*blockSize)/(1024*1024));
	        if(blocks*blockSize>=1024*1024*DISK_SIZE_LIMIT)
	        {
	        	return true;
	        }
    	}
	    return false;
    }
    
    public static String getVersion()
    {
		return SystemProperties.get("ro.product.version","undefine");
    }
    
    
    public static String getAndroidVersion()
    {
    	
    	return Build.VERSION.RELEASE;
    }
    
    
    public static String getDevice()
    {
    	return Build.DEVICE;
    }
    public static String getHWINFO()
    {
    	return SystemProperties.get("ro.hwinfo","hw1.0.0");
    }
    public static String getClinetCode()
    {
     return SystemProperties.get("ro.ota.clientname","undefine");
    }
    
     public static String getTypeName()
    {
      return SystemProperties.get("ro.ota.modelname","undefine");
    }
     
     public static String getDeviceNo()
    {
      return SystemProperties.get("ro.serialno","undefined");
    }
    
     

 	
 	public static String GetModleName() {
		return Build.MODEL;
	}
    public static String getValByTagName(Document doc, String tagName) {
		NodeList list = doc.getElementsByTagName(tagName);
		if (list.getLength() > 0) {
			   Node node = list.item(0);
			   Node valNode = node.getFirstChild();
			   if (valNode != null) {
			   String val = valNode.getNodeValue();
			   return val;
			  }
		 }
		 return null;   
	}
	

    public static Bundle getResponseString(String deviceNo,String clientCode ,String typeName, String versionCode,String androidVersion,String hardwareInfo, String ext )
	{
		String envelope="<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
		  "<soap12:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">" +
		  "<soap12:Body>" +
		  "<MidQuery xmlns=\"http://zjt.query.org/\">" +
		  "<DeviceNo>"+deviceNo+"</DeviceNo>" +
		  "<ClientCode>"+clientCode+"</ClientCode>" +
			"<TypeName>"+typeName+"</TypeName>" +
			"<VersionCode>"+versionCode+"</VersionCode>" +
			"<AndroidVersion>"+androidVersion+"</AndroidVersion>" +
			"<HardwareInfo>"+hardwareInfo+"</HardwareInfo>" +
			"<Ext>"+ext+"</Ext>" +
		  "</MidQuery>" +
		  "</soap12:Body>" +
		  "</soap12:Envelope>";
		HttpURLConnection httpConnection=null;
		OutputStream output=null;
		InputStream input=null;
		Bundle bundle=new Bundle();
		try{
			  URL url= new URL(Utils.URL_MidQuery); 
			 /* try {
					//InetAddress ip = InetAddress.getByName("www.inhuasoft.cn");
					//ip.getHostAddress();
					//Toast.makeText(getApplicationContext(),ip.getHostAddress(),Toast.LENGTH_LONG).show();
					//ILog.d(TAG,"-------www.inhuasoft.cn can access,ip is  -------------"+ip.getHostAddress());
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
				  ILog.d(TAG,"-------www.inhuasoft.cn can't access  to  use 218.17.160.137  -------------");
				  //url = new URL(Utils.URL_IP); 
				}*/
		    Log.d(TAG,"-------URL-------------"+url.toString());
		    
		    httpConnection = (HttpURLConnection)url.openConnection();
		   /* httpConnection.setConnectTimeout(6*1000);
		    if (httpConnection.getResponseCode() != 200)
		    {
		    	url = new URL(Utils.URL_IP);
		    	ILog.d(TAG, "---------------------IP IS  218.17.160.137");
		    }*/
		    httpConnection.setRequestMethod("POST");
		    httpConnection.setRequestProperty( "Content-Length",String.valueOf( envelope.length() ) );
		    httpConnection.setRequestProperty("Content-Type","text/xml; charset=utf-8");
		    httpConnection.setDoOutput(true);
		    httpConnection.setDoInput(true);
		    output=httpConnection.getOutputStream();
		    output.write(envelope.getBytes());
		    output.flush();
		    
		    input=httpConnection.getInputStream();
		 
		    DocumentBuilderFactory factory =  DocumentBuilderFactory.newInstance(); 
		    DocumentBuilder builder = factory.newDocumentBuilder();   
		    Document dom = builder.parse(input);
		    
		    String status=getValByTagName(dom,"RespCode");
		    String md5=null;
		    String durl=null;
		    String nversion=null;
		    String versionid=null;
		    if(status!=null)
		    {
		    	if(status.equals("1"))
		    	{
		    		md5=getValByTagName(dom,"RespFileMD5");
		    		durl=getValByTagName(dom,"RespDownloadUrl");
		    		versionid=getValByTagName(dom,"RespVersionID");
		    		nversion=getValByTagName(dom,"RespNewVerCode");
		    		
		    	}else
		    	{
		    		nversion="";
		    		versionid="";
            md5="";
            durl="";
		    	}
		    }else{
		    		nversion="";
            md5="";
            versionid="";
            durl="";
		    }

		    //System.out.println(durl);
		    //String ddurl=durl.replace('#', '&');
		    Log.d(TAG, "---------------------status:"+status);
		    Log.d(TAG, "---------------------md5:"+md5);
		    Log.d(TAG, "---------------------durl:"+durl);
		    Log.d(TAG, "---------------------nversion:"+nversion);
		    Log.d(TAG, "---------------------versionid:"+versionid);
		    bundle.putString("status", status);
		    bundle.putString("md5", md5);
		    bundle.putString("url", durl);
		    bundle.putString("version", nversion);
		    bundle.putString("versionid", versionid);
		    Log.d(TAG, "---------------------dd");
		    
		    return bundle;
		}catch(Exception ex)
		{
			Log.d(TAG, "-->getResponseString:catch"+ex.getMessage());
			return null;
		}finally
		{
			try{
				output.close();
				input.close();
				httpConnection.disconnect();
			}catch(Exception e)
			{
				Log.d(TAG, "-->getResponseString:finally"+e.getMessage());
			}
		}
	}
    
    
    
    
    public static Bundle getReportDownloadRequest(String deviceNo,int VersionID)
   	{
   		String envelope="<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
   		  "<soap12:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">" +
   		  "<soap12:Body>" +
   		  "<ReportDownloadRequest xmlns=\"http://zjt.query.org/\">"+
   	      "<DeviceNo>"+deviceNo+"</DeviceNo>"+
   	      "<VersionID>"+VersionID+"</VersionID>"+
   	      "</ReportDownloadRequest>"+
   		  "</soap12:Body>" +
   		  "</soap12:Envelope>";
   		HttpURLConnection httpConnection=null;
   		OutputStream output=null;
   		InputStream input=null;
   		Bundle bundle=new Bundle();
   		try{
   			  URL url= new URL(Utils.URL_ReportDownloadRequest); 
   			 /* try {
   					//InetAddress ip = InetAddress.getByName("www.inhuasoft.cn");
   					//ip.getHostAddress();
   					//Toast.makeText(getApplicationContext(),ip.getHostAddress(),Toast.LENGTH_LONG).show();
   					//ILog.d(TAG,"-------www.inhuasoft.cn can access,ip is  -------------"+ip.getHostAddress());
   					
   				} catch (Exception e) {
   					// TODO Auto-generated catch block
   				  ILog.d(TAG,"-------www.inhuasoft.cn can't access  to  use 218.17.160.137  -------------");
   				  //url = new URL(Utils.URL_IP); 
   				}*/
   		    Log.d(TAG,"---URL_ReportDownloadRequest----------------"+url.toString());
   		    
   		    httpConnection = (HttpURLConnection)url.openConnection();
   		   /* httpConnection.setConnectTimeout(6*1000);
   		    if (httpConnection.getResponseCode() != 200)
   		    {
   		    	url = new URL(Utils.URL_IP);
   		    	ILog.d(TAG, "---------------------IP IS  218.17.160.137");
   		    }*/
   		    httpConnection.setRequestMethod("POST");
   		    httpConnection.setRequestProperty( "Content-Length",String.valueOf( envelope.length() ) );
   		    httpConnection.setRequestProperty("Content-Type","text/xml; charset=utf-8");
   		    httpConnection.setDoOutput(true);
   		    httpConnection.setDoInput(true);
   		    output=httpConnection.getOutputStream();
   		    output.write(envelope.getBytes());
   		    output.flush();
   		    
   		    input=httpConnection.getInputStream();
   		 
   		    DocumentBuilderFactory factory =  DocumentBuilderFactory.newInstance(); 
   		    DocumentBuilder builder = factory.newDocumentBuilder();   
   		    Document dom = builder.parse(input);
   		    
   		    String result=getValByTagName(dom,"ReportDownloadRequestResult");
   		    
   		    Log.d(TAG, "---URL_ReportDownloadRequest------------------result:"+result);
   		    bundle.putString("result", result);
   		    return bundle;
   		}catch(Exception ex)
   		{
   			Log.d(TAG, "-->getResponseString:catch"+ex.getMessage());
   			return null;
   		}finally
   		{
   			try{
   				output.close();
   				input.close();
   				httpConnection.disconnect();
   			}catch(Exception e)
   			{
   				Log.d(TAG, "-->getResponseString:finally"+e.getMessage());
   			}
   		}
   	}
    
    
    
    
    public static void getReportDownloadEnd(int DownloadId,String LastSize ,int DownStatus)
   	{
   		String envelope="<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
   		  "<soap12:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">" +
   		  "<soap12:Body>" +
   		  "<ReportDownloadEnd xmlns=\"http://zjt.query.org/\">"+
   	      "<DownID>"+DownloadId+"</DownID>"+
   	      "<LastSize>"+LastSize+"</LastSize>"+
   	      "<DownStatus>"+DownStatus+"</DownStatus>"+
   	      "</ReportDownloadEnd>"+
   		  "</soap12:Body>" +
   		  "</soap12:Envelope>";
   		HttpURLConnection httpConnection=null;
   		OutputStream output=null;
   		InputStream input=null;
   		//Bundle bundle=new Bundle();
   		try{
   			  URL url= new URL(Utils.URL_ReportDownloadEnd); 
   			 /* try {
   					//InetAddress ip = InetAddress.getByName("www.inhuasoft.cn");
   					//ip.getHostAddress();
   					//Toast.makeText(getApplicationContext(),ip.getHostAddress(),Toast.LENGTH_LONG).show();
   					//ILog.d(TAG,"-------www.inhuasoft.cn can access,ip is  -------------"+ip.getHostAddress());
   					
   				} catch (Exception e) {
   					// TODO Auto-generated catch block
   				  ILog.d(TAG,"-------www.inhuasoft.cn can't access  to  use 218.17.160.137  -------------");
   				  //url = new URL(Utils.URL_IP); 
   				}*/
   		    Log.d(TAG,"---URL_getReportDownloadEnd----------------"+url.toString());
   		    
   		    httpConnection = (HttpURLConnection)url.openConnection();
   		   /* httpConnection.setConnectTimeout(6*1000);
   		    if (httpConnection.getResponseCode() != 200)
   		    {
   		    	url = new URL(Utils.URL_IP);
   		    	ILog.d(TAG, "---------------------IP IS  218.17.160.137");
   		    }*/
   		    httpConnection.setRequestMethod("POST");
   		    httpConnection.setRequestProperty( "Content-Length",String.valueOf( envelope.length() ) );
   		    httpConnection.setRequestProperty("Content-Type","text/xml; charset=utf-8");
   		    httpConnection.setDoOutput(true);
   		    httpConnection.setDoInput(true);
   		    output=httpConnection.getOutputStream();
   		    output.write(envelope.getBytes());
   		    output.flush();
   		    
   		    input=httpConnection.getInputStream();
   		 
   		    DocumentBuilderFactory factory =  DocumentBuilderFactory.newInstance(); 
   		    DocumentBuilder builder = factory.newDocumentBuilder();   
   		    Document dom = builder.parse(input);
   		    
   		   /* String result=getValByTagName(dom,"ReportDownloadRequestResult");
   		    
   		    ILog.d(TAG, "---URL_ReportDownloadRequest------------------result:"+result);
   		    bundle.putString("result", result);
   		    return bundle;*/
   		}catch(Exception ex)
   		{
   			Log.d(TAG, "-->getReportDownloadEnd:catch  "+ ex);
   		}finally
   		{
   			try{
   				output.close();
   				input.close();
   				httpConnection.disconnect();
   			}catch(Exception e)
   			{
   				Log.d(TAG, "-->getReportDownloadEnd:finally    "+e );
   			}
   		}
   	}
    
	
    private static boolean isDATAM8XModel()
    {
    	String input = Build.MODEL;
		String reg ="(DATAM8)|(DATAM7)\\w";
		Pattern pattern = Pattern.compile(reg);
		Matcher  matcher = pattern.matcher(input);
		boolean bresult =matcher.find();
		return bresult;
    }
    
	public static String getSN() {
        String sn=null;
        try {
            BufferedReader reader = new BufferedReader(new FileReader("/sys/class/boardinfo/sn"), 256);
            try {
                sn = reader.readLine();
            } finally {
                reader.close();
            }
          } catch (IOException e) {
            Log.e(TAG,
                "IO Exception when getting kernel version for Device Info screen",
                e);

            return null;
        }
     return sn;
}
}
