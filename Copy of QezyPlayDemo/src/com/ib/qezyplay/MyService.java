package com.ib.qezyplay;



import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;



import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.StrictMode;

import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

@SuppressLint("NewApi")
public class MyService extends Service {
	private static final String TAG = "MyService";
	private static final String MainDirectory=Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/com.ideabytes.ytv/YTV";
	private static final String FilePath=MainDirectory+"/Status.txt";
	
	private static  String path;
	private static String action="updatestatus";
	private String datetime=null;
	private String imei="AJAY";//IB000006
	SimpleDateFormat sdf ;
	private static final int delay = 1000; // delay for 1 sec before first start
	private static final int period = 20000; // repeat check every 10 sec.
	private Timer timer;
	private static boolean ReadisServiceFound ;
	private static boolean ReadisConnected ;
	private static boolean isServiceFound ;
	private static boolean isStreamFound ;
	private static boolean isConnected ;
	private static String activityStatus=null;
	private static String InternetStatus=null;
	private static String InternetSpeed=null;
	public  Dialog dialog;
	static String StreamStatus;
	static String check;

	private static String ReadStreamStatus;
	public MyService() {
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO: Return the communication channel to the service.
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
    public void onCreate() {
        //Toast.makeText(this, "The new Service was Created", Toast.LENGTH_LONG).show();
		 Log.i(TAG, "new Service created");
		 StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

		 StrictMode.setThreadPolicy(policy); 
		 
		 }
	
  @SuppressLint("SimpleDateFormat")
	@Override
    public void onStart(Intent intent, int startId) {
	  StreamStatus=intent.getStringExtra("StreamStatus");
	  String activestatus=intent.getStringExtra("activityStatus");
	  String InternetSpeed=intent.getStringExtra("InternetSpeed");
	  boolean isServiceFound=intent.getExtras().getBoolean("isServiceFound");
    	 Log.d(TAG, "service on start");
    	//Getting activity forground info
    	 ActivityManager activityManager = (ActivityManager)this.getSystemService (Context.ACTIVITY_SERVICE); 
		    List<RunningTaskInfo> services = activityManager.getRunningTasks(Integer.MAX_VALUE); 
		    isServiceFound = false;
		    
		    isStreamFound=false;
		    
		    isConnected=isConnectingToInternet();
		    //Writing status activity and connection to file
		    Log.i("IMEI number service", imei);
	        CreateIBPlayerDirectoryIfRequired();
	        Log.d("Folder Path", ""+path);
			 WriteStatusFile(isServiceFound,StreamStatus);
			 try {
				LoadData();
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		
	//Auto R-Launch
    	
 //to  get current date time
    	Calendar c = Calendar.getInstance();
        System.out.println("Current time => "+c.getTime());
         sdf = new SimpleDateFormat("yyyy-MM-dd H:mm:ss");
        datetime = sdf.format(c.getTime());
        Log.i("Current Date Time", ""+datetime);
        
       
       //to get imei number of device 
      /* TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
     imei = telephonyManager.getDeviceId();
        Log.i("IMEI number", ""+telephonyManager.getDeviceId());*/
        String text = "";
        BufferedReader reader=null;
        activityStatus=activestatus;
        check=StreamStatus;
        System.out.println("yyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy"+check);
       // intent.putExtra("activityStatus", isServiceFound);
        Log.i("my serivce activityStatus ", ""+activityStatus);
        Log.i("my serivce Stream Status ", ""+StreamStatus);
      //network status connected or not 
     final boolean netStatus=isConnectingToInternet();
     Log.i("my serivce netStatus ", ""+netStatus);
     InternetStatus=String.valueOf(netStatus);
        // Send data
      try
      {
    	  String data = URLEncoder.encode("action", "UTF-8")
		             + "=" + URLEncoder.encode(action, "UTF-8");

          data += "&" + URLEncoder.encode("datetime", "UTF-8") + "="
                      + URLEncoder.encode(datetime, "UTF-8");
          data += "&" + URLEncoder.encode("imei", "UTF-8") + "="
                  + URLEncoder.encode(imei, "UTF-8");
          data += "&" + URLEncoder.encode("activityStatus", "UTF-8") + "="
                 + URLEncoder.encode(activityStatus, "UTF-8");
          data += "&" + URLEncoder.encode("InternetSpeed", "UTF-8") + "="
                  + URLEncoder.encode(InternetSpeed, "UTF-8");
          data += "&" + URLEncoder.encode("StreamStatus", "UTF-8") + "="
                  + URLEncoder.encode(StreamStatus, "UTF-8");
          // Defined URL  where to send data
          URL url = new URL("http://www.autotestscript.com/urdutvindia/mobile/updatesessionbyapp.php");
       // Send POST data request

        URLConnection conn = url.openConnection();
        conn.setDoOutput(true);
        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
        wr.write( data );
        wr.flush();
    
        // Get the server response
         
      reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
      StringBuilder sb = new StringBuilder();
      String line = null;
      
      // Read Server Response
      while((line = reader.readLine()) != null)
          {
                 // Append server response in string
                 sb.append(line + "\n");
          }
          
          
          text = sb.toString();
          Log.i("Server Connection", text);
          try
          {
          if(text!=null){
         // Toast.makeText(this, text, Toast.LENGTH_LONG).show();
          }
          
          }
          catch(Exception e)
          {
        	  Toast.makeText(this, "No Server Connection", Toast.LENGTH_LONG).show();    
          }
      }
      catch(Exception ex)
      {
         Log.e("error",""+ex.toString());  
      }
      finally
      {
          try
          {

              reader.close();
          }

          catch(Exception ex) {}
      }
      
    }
    @Override
    public void onDestroy() {
      //  Toast.makeText(this, "Service Stopped", Toast.LENGTH_LONG).show();
    	 Log.i(TAG, "service Stopped");
    }
    public static boolean WriteStatusFile(boolean Astatus,String Sstatus) {
    	try {
    		FileWriter outFile = new FileWriter(FilePath);			
    		PrintWriter out = new PrintWriter(outFile);
    		out.println("activityStatus="+Astatus);
    		out.println("StreamStatus="+Sstatus);
    		out.flush();
    		out.close();
    	} 
    	catch (Exception e) {
    		e.printStackTrace();
    		return false;
        }
    	return true;
    }
    public static boolean CreateIBPlayerDirectoryIfRequired() {
    	File directory = new File(MainDirectory);
    	path=directory.getAbsolutePath();
    	if (!directory.isDirectory()) {
    		if (!directory.mkdirs()) {
    			return false;
    		}
    	}
    	return true;
    }
    /**
     * Checking for all possible internet providers
     * **/
    public boolean isConnectingToInternet(){
        ConnectivityManager connectivity = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
          if (connectivity != null)
          {
              NetworkInfo[] info = connectivity.getAllNetworkInfo();
              if (info != null)
                  for (int i = 0; i < info.length; i++)
                      if (info[i].getState() == NetworkInfo.State.CONNECTED)
                      {
                          return true;
                      }

          }
          return false;
    }
	    public static void LoadData() throws FileNotFoundException {
	        String property = null;
			String value = null;
			
				BufferedReader input;
				
				input = new BufferedReader(new FileReader(FilePath));
				
				try {
				    String line = null; //not declared within while loop
				    while (( line = input.readLine()) != null) {
				    	String[] x = line.split("=");
				    	property = x[0];
				    	value = x[1];
				    	if (property.equals("activityStatus")) {
				    		ReadisServiceFound=Boolean.parseBoolean(value);
				    		Log.i("data read ReadisServiceFound=", ""+ReadisServiceFound);
				   		
				    	}
				    	else if (property.equals("StreamStatus")) {
				    		ReadStreamStatus=(value);
				    		 Log.i("data read StreamStatus=", ""+ReadStreamStatus);
				    				}
				    	}
				    	}
				    	catch (Exception e) {
							e.printStackTrace();
						}
	    		}
   	

	}
