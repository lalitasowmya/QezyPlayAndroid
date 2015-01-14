package com.ib.qezyplay;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import octoshape.mp;
import octoshape.osa2.OctoshapeSystem;
import octoshape.osa2.Problem;
import octoshape.osa2.StreamPlayer;
import octoshape.osa2.listeners.ProblemListener;
import octoshape.osa2.listeners.StreamPlayerListener;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.IPackageDataObserver;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.StrictMode;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.view.WindowManager.BadTokenException;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ib.qezyplay.VideoControllerView;
import com.ib.coverflow.CoverFlow;
import com.ib.coverflow.ReflectingImageAdapter;
import com.ib.coverflow.ResourceImageAdapter;












import com.octoshape.android.client.MediaPlayerConstants;
import com.octoshape.android.client.OctoshapePortListener;
import com.octoshape.android.client.OctoshapeSystemCreator;


public class MiniAndroidPlayer extends Activity implements
OnVideoSizeChangedListener, OnPreparedListener,SurfaceHolder.Callback, VideoControllerView.MediaPlayerControl  {


	private static final String LOGTAG = "OctoAndroidPlayer";

	private static String OCTOLINK1=null;
	private static String OCTOLINK2=null;
	private static String OCTOLINK3=null;
	public  Dialog dialog;
	private SurfaceView surface;
	private SurfaceHolder holder;
	private OctoshapeSystem os;
	private MediaPlayer FullScreenMediaPlayer;
	private LinkedList<Uri> urlQueue = new LinkedList<Uri>();
	MediaPlayer player;
	private static String action="updatestatus";

	private static String datetime=null;
	private static String imei="YTV00IB0007";
	static SimpleDateFormat sdf;
	private static String activityStatus=null;
	private static String StreamStatus;
	private static String InternetSpeed=null;
	private static String active;

	ProgressDialog progressBar;
	private int progressBarStatus = 0;
	private Handler progressBarHandler = new Handler();


	private static final long CACHE_APP = Long.MAX_VALUE;

	private static final String TAG = null;
	private CachePackageDataObserver mClearCacheObserver;
	private TextView text;
	String value;
	private VideoControllerView controller;
	private FrameLayout FullScreenLayout;

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		Log.e(TAG,"ENTERED MINNI ANDROID PLAYER");
		Intent intent = getIntent();

		//Log.e(TAG,"ENTERED MINNI ANDROID PLAYER get string extra "+ intent.getStringExtra("OCTO"));
		OCTOLINK1 =  intent.getStringExtra("OCTO");
		//		if (extras != null) {
		//			value = extras.getString("OCTO");
		//			=value; 
		//		}
		//	OCTOLINK1="octoshape://streams.octoshape.net/ideabytes/live/ib-ch7/auto";
		Log.e(TAG,"ENTERED MINI ANDROID PLAYER octolink "+OCTOLINK1);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.video);


		FullScreenLayout = (FrameLayout)findViewById(R.id.videoSurfaceContainer);
		//   FullScreenLayout.setOnTouchListener(this);
		surface = (SurfaceView) findViewById(R.id.surface);                  
		holder = surface.getHolder();                  
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		FullScreenMediaPlayer = new MediaPlayer();
		controller = new VideoControllerView(this);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy); 	
		if(OCTOLINK1.startsWith("octoshape"))
		{
			initOctoshapeSystem() ; 
		}
		else if(OCTOLINK1.endsWith("m3u8"))
		{
			playHlsStreamsFullScreen(OCTOLINK1) ;
		}
		else 
		{
			playHlsStreamsFullScreen(OCTOLINK1) ;
		}
	}

	private void playHlsStreamsFullScreen(String OCTOLINK1) {
		// TODO Auto-generated method stub
		try {
			Log.e(LOGTAG, "octoliing "+OCTOLINK1);

			FullScreenMediaPlayer.setDataSource(this, Uri.parse(OCTOLINK1));
			
			FullScreenMediaPlayer.setOnPreparedListener(this);

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



		try {
			FullScreenMediaPlayer.prepare();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		FullScreenMediaPlayer.start();	

	}	


	public void initOctoshapeSystem() 
	{

		Log.e(TAG,"ENTERED initoctoshapesystem octolink "+OCTOLINK1);
		os = OctoshapeSystemCreator.create(this, problemListener,
				new OctoshapePortListener() {
			@Override
			public void onPortBound(int port) {
				Log.e(TAG,"ENTERED  1 ");
				setupStream(OCTOLINK1).requestPlay();

			}
		}, Locale.ENGLISH.getLanguage());
		Log.e(TAG,"ENTERED  2 ");
		os.setProblemMessageLanguage(Locale.ENGLISH.getLanguage());
		Log.e(TAG,"ENTERED  3 ");
		os.setProblemListener(problemListener);
		Log.e(TAG,"ENTERED  4 ");
		os.addPlayerNameAndVersion(MediaPlayerConstants.NATIVE_PLAYER, MediaPlayerConstants.NATIVE_PLAYER,
				"" + Build.VERSION.SDK_INT);
		Log.e(TAG,"ENTERED  5 ");
		os.open();
		Log.e(TAG,"ENTERED  6 ");
	}

	public StreamPlayer setupStream(final String stream) {

		Log.d(LOGTAG, "Setting up stream: " + stream);
		StreamPlayer sp = os.createStreamPlayer(stream);
		Log.e(TAG,"ENTERED  7 ");
		sp.setProblemListener(new ProblemListener() {

			public void gotProblem(Problem p) {
				Log.e(TAG,"ENTERED  777 ");
				StreamStatus="No Stream/Internet Problem";
				Log.e(TAG,"ENTERED  8 ");
				Log.e("Stream Status  ", "Not Ok");
				active="yellow";

				Log.e(LOGTAG, stream+": "+p.getMessage() + " " + p.toString());    
				Log.e(TAG,"ENTERED  9 ");
			}
		});
		sp.setListener(new StreamPlayerListener() {

			private String playerId;


			@Override
			public void gotUrl(String url, long seekOffset,
					boolean playAfterBuffer) {

				Log.e(LOGTAG, "gotUrl");
				if (playAfterBuffer)
				{
					Log.e(TAG,"ENTERED  11 ");
					urlQueue.add(Uri.parse(url));
				}
				else
				{Log.e(TAG,"ENTERED  12");
				playStream(Uri.parse(url), playerId);

				}
			}

			@Override
			public void gotNewOnDemandStreamDuration(long duration) {
				Log.e(TAG,"ENTERED  13 ");
			}

			@Override
			public void resolvedNativeSeek(boolean isLive, String playerId) {
				Log.i(LOGTAG, "resolvedNativeSeek");
				this.playerId = playerId;
			}

			@Override
			public void resolvedNoSeek(boolean isLive, String playerId) {
				Log.i(LOGTAG, "resolvedNoSeek");
				this.playerId = playerId;
			}

			@Override
			/**
			 * Called when stream support OsaSeek / DVR
			 */
			public void resolvedOsaSeek(boolean isLive, long duration,
					String playerId) {
				Log.i(LOGTAG, "resolvedOsaSeek");
				this.playerId = playerId;
			}
		});
		//sp.initialize(false);
		return sp;
	}
	protected void playStream(Uri mediaUrl, final String playerId) {

		Log.e(LOGTAG, playerId + " now plays: " + mediaUrl);

		try {
			controller.setMediaPlayer(this);
			controller.setAnchorView(FullScreenLayout);
			FullScreenMediaPlayer.setDisplay(holder);
			FullScreenMediaPlayer.setDataSource(this, mediaUrl);
			FullScreenMediaPlayer.setOnVideoSizeChangedListener(this);
			
			FullScreenMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					if (!urlQueue.isEmpty())
						playStream(urlQueue.removeFirst(), playerId);
				}
			});
			FullScreenMediaPlayer.setOnErrorListener(new OnErrorListener() {
				@Override
				public boolean onError(MediaPlayer mp, int what, int extra) {
					error("MediaPlayer Error: " + what + ":" + extra);
					return true;
				}
			});
			FullScreenMediaPlayer.prepare();
			FullScreenMediaPlayer.start();
		} catch (Exception e) {
			Log.e(LOGTAG, "Error preparing MediaPlayer", e);
			error("Error preparing MediaPlayer: " + e.getMessage());
		}
	}


	@Override
	public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {

		FrameLayout.LayoutParams vLayout = (FrameLayout.LayoutParams)surface
				.getLayoutParams();
		surface.setLayoutParams(vLayout);
		// dialog.dismiss();
		StreamStatus="Stream Ok";
		Log.i("Stream Stattus", "Stream Ok");

		active="true";
		long startTime = System.currentTimeMillis();
		System.out.println("dddddddddddddddddddddddddddddddddddddddddddddd"+startTime);


		Timer timer = new Timer();
		timer.schedule(new SayHello(), 50000, 60000);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if(keyCode == KeyEvent.KEYCODE_BACK)
		{

			Log.d(LOGTAG, "Back button pressed!");

			shutdown();
		}
		return super.onKeyDown(keyCode, event);
	}@SuppressLint("SimpleDateFormat")



	public void shutdown(){
		if (FullScreenMediaPlayer != null)
			FullScreenMediaPlayer.release();
		if (os != null) {
			os.close(new Runnable() {
				@Override
				public void run() {
					MiniAndroidPlayer.this.finish();
				}
			});
		}
	}

	@Override
	protected void onStop()
	{
		super.onStop();

		Log.d(LOGTAG, "MY onStop is called");
		shutdown();
	}
	ProblemListener problemListener = new ProblemListener() {
		@Override
		public void gotProblem(Problem p) {
			Log.e(LOGTAG, p.getMessage() + "\n" + p.toString());
			error(p.getMessage());
		}
	};

	protected void error(final String error) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {

				try {

					//  dialog.show();
					Log.i(LOGTAG, "problem dialog block");
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}
	class SayHello extends TimerTask {
		public void run() {
			System.out.println("Catche clear method scheduled");
			clearCache();
		}
	}
	void clearCache() 
	{
		Log.d(LOGTAG, "clearCache() clled");
		if (mClearCacheObserver == null) 
		{
			mClearCacheObserver=new CachePackageDataObserver();
		}

		PackageManager mPM=getPackageManager();

		@SuppressWarnings("rawtypes")
		final Class[] classes= { Long.TYPE, IPackageDataObserver.class };

		Long localLong=Long.valueOf(CACHE_APP);

		try 
		{
			Method localMethod=
					mPM.getClass().getMethod("freeStorageAndNotify", classes);


			try 
			{
				localMethod.invoke(mPM, localLong, mClearCacheObserver);
			}
			catch (IllegalArgumentException e) 
			{
				e.printStackTrace();
			}
			catch (IllegalAccessException e) 
			{
				e.printStackTrace();
			}
			catch (InvocationTargetException e)
			{
				e.printStackTrace();
			}

		}
		catch (NoSuchMethodException e1)
		{

			e1.printStackTrace();
		}
	}

	private class CachePackageDataObserver extends IPackageDataObserver.Stub 
	{
		public void onRemoveCompleted(String packageName, boolean succeeded) 
		{

		}
	}
	@Override
	public void onPrepared(MediaPlayer mp) {
		// TODO Auto-generated method stub
		
		controller.setMediaPlayer(this);
		controller.setAnchorView(FullScreenLayout);
		FullScreenMediaPlayer.setDisplay(holder);

		
		
		FullScreenMediaPlayer.start();


	}



	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub

		//FullScreenMediaPlayer.prepareAsync();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub

	}


	@Override
	public boolean canPause() {
		return true;
	}
	@Override
	public boolean canSeekBackward() {
		return true;
	}
	@Override
	public boolean canSeekForward() {
		return true;
	}

	@Override
	public int getBufferPercentage() {
		return 0;
	}

	@Override
	public int getCurrentPosition() {
		//Log.e(TAG, "GET CURRENT POSITION " +mMediaPlayer.getCurrentPosition());
		return FullScreenMediaPlayer.getCurrentPosition();
	}

	@Override
	public int getDuration() {
		return FullScreenMediaPlayer.getDuration();
	}

	@Override
	public boolean isPlaying() {
		return FullScreenMediaPlayer.isPlaying();
	}

	@Override
	public void pause() {
		FullScreenMediaPlayer.pause();
	}

	@Override
	public void seekTo(int i) {
		FullScreenMediaPlayer.seekTo(i);
	}
	@Override
	public void start() {
		FullScreenMediaPlayer.start();
	}
	@Override
	public boolean isFullScreen() {
		return false;
	}
	@Override
	public void toggleFullScreen() {

		//	controller.hide();
		//	FullScreenMediaPlayer.stop();
		//
		//	intent = new Intent(CoverFlowTestingActivity.this,
		//			MiniAndroidPlayer.class);
		//
		//	intent.putExtra("OCTO", OCTOLINK1);	
		//	startActivity(intent);
		//		
		// 	setContentView(R.layout.video);
		// 	 surface = (SurfaceView) findViewById(R.id.surface);   
		// 
		//	mHolder = mSurface.getHolder();                  
		//	mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		//		 setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); 
		//         DisplayMetrics displaymetrics = new DisplayMetrics();
		//           getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		//           int height = displaymetrics.heightPixels;
		//           int width = displaymetrics.widthPixels;
		//           android.widget.FrameLayout.LayoutParams params = (android.widget.FrameLayout.LayoutParams) surface.getLayoutParams();
		//           params.width = width;
		//           params.height=height;
		//           params.setMargins(0, 0, 0, 0);
		//		


	}
	// End VideoMediaController.MediaPlayerControl

	@Override
	public boolean onTouchEvent(MotionEvent event) {
 
	controller.show();
	return false;
		 
	}
	 
 










}
