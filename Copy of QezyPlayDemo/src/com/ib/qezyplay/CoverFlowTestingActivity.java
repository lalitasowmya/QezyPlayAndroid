package com.ib.qezyplay;

 
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.android.youtube.player.YouTubePlayer.PlayerStyle;
import com.ib.qezyplay.VideoControllerView;
import com.ib.coverflow.CoverFlow;
import com.ib.coverflow.ReflectingImageAdapter;
import com.ib.coverflow.ResourceImageAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException; 
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method; 
import java.text.SimpleDateFormat; 
import java.util.LinkedList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask; 

import octoshape.osa2.OctoshapeSystem;
import octoshape.osa2.Problem;
import octoshape.osa2.StreamPlayer;
import octoshape.osa2.listeners.ProblemListener;
import octoshape.osa2.listeners.StreamPlayerListener;
import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint; 
import android.app.Dialog;
import android.app.ProgressDialog; 
import android.content.pm.IPackageDataObserver;
import android.content.pm.PackageManager; 
import android.net.Uri; 
import android.os.Build; 
import android.os.Handler;
import android.os.StrictMode; 
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener; 
import android.util.Log;
import android.view.KeyEvent; 
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView; 
import android.view.View.OnTouchListener; 
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.WindowManager;

import com.octoshape.android.client.MediaPlayerConstants;
import com.octoshape.android.client.OctoshapePortListener;
import com.octoshape.android.client.OctoshapeSystemCreator;


/****
 * The Class CoverFlowTestingActivity.
 */
public class CoverFlowTestingActivity extends    YouTubeBaseActivity implements
YouTubePlayer.OnInitializedListener,  OnVideoSizeChangedListener,  SurfaceHolder.Callback, OnPreparedListener, VideoControllerView.MediaPlayerControl, OnTouchListener, android.view.animation.Animation.AnimationListener 
{

	private static final String LOGTAG = "OctoAndroidPlayer";
	public  static String OCTOLINK1=null;
	private static String OCTOLINK2=null;
	private static String OCTOLINK3=null;
	public  Dialog dialog;
	private SurfaceView mSurface,surface;
	private SurfaceHolder mHolder;
	private OctoshapeSystem os;
	private MediaPlayer mMediaPlayer;
	private LinkedList<Uri> urlQueue = new LinkedList<Uri>();

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
	FrameLayout PreviewFrame;
	ImageView PreviewFrameImage;
	VideoControllerView controller;
	private TextView textView;
	Intent intent;
	  private AnimatorSet animations;
	 
	private static final int RECOVERY_DIALOG_REQUEST = 1; 
	// YouTube player view
	public   static   YouTubePlayerView youTubeView;

	YouTubePlayer player;
	 Animation  FlipAnimation;
	 ObjectAnimator anim;


	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.main);



		final CoverFlow reflectingCoverFlow = (CoverFlow) findViewById(this.getResources().getIdentifier(
				"coverflowReflect", "id", "com.ib.qezyplay"));
		setupCoverFlow(reflectingCoverFlow, true);


		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy); 
		PreviewFrameImage= (ImageView)findViewById(R.id.previewScreenImageView);
		PreviewFrameImage.setOnTouchListener(this);
		PreviewFrame = (FrameLayout)findViewById(R.id.videoSurfaceContainer);

		PreviewFrame.setOnTouchListener(this);
		mSurface = (SurfaceView) findViewById(R.id.surface_main);    
		mSurface.setOnTouchListener(this);		
		mSurface.setVisibility(View.INVISIBLE);
	 
		mHolder = mSurface.getHolder();   

		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		mMediaPlayer = new MediaPlayer();

		controller = new VideoControllerView(this);
		youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
		
 
		youTubeView.setVisibility(View.INVISIBLE);
		// Initializing video player with developer key
		//	youTubeView.initialize(Config.DEVELOPER_KEY,this);
	  anim = (ObjectAnimator) AnimatorInflater.loadAnimator(this, R.anim.flip); 
	  anim.setTarget(FlipAnimation);
		anim.setDuration(3000);
		anim.start();
	}
	
	
	
	 

	/**
	 * Setup cover flow.
	 * 
	 * @param mCoverFlow
	 *            the m cover flow
	 * @param reflect
	 *            the reflect
	 */
	private void setupCoverFlow(final CoverFlow mCoverFlow, final boolean reflect) {
		BaseAdapter coverImageAdapter;
		if (reflect) {
			coverImageAdapter = new ReflectingImageAdapter(new ResourceImageAdapter(this));
		} else {
			coverImageAdapter = new ResourceImageAdapter(this);
		}
		mCoverFlow.setAdapter(coverImageAdapter);
		mCoverFlow.setSelection(2, true);
		setupListeners(mCoverFlow);
	}

	/**
	 * Sets the up listeners.
	 * 
	 * @param mCoverFlow
	 *            the new up listeners
	 */
	private void setupListeners(final CoverFlow mCoverFlow) {
 
		mCoverFlow.setOnItemClickListener(new OnItemClickListener() {
			@SuppressWarnings("static-access")
			@Override
			public void onItemClick(final AdapterView< ? > parent, final View view, final int position, final long id) {
				if(position == 0)
				{ 
					
				 
	             	mSurface.setVisibility(View.INVISIBLE);
	             
//					if(mMediaPlayer.isPlaying())
//					{
//						Log.e(TAG, "Player playing ");
//						controller.hide();
//						mMediaPlayer.stop();
//						mMediaPlayer.reset();							 
//					}
// 	              
//					youTubeView.setVisibility(view.INVISIBLE);
// 	              	mSurface.setVisibility(view.VISIBLE);
//					OCTOLINK1="octoshape://streams.octoshape.net/ideabytes/live/ib-ch3/auto";
//					initOctoshapeSystem(OCTOLINK1) ;
 	            }
			       	else if(position == 1)
				{
					//mSurface.setVisibility(View.INVISIBLE); 
					if(mMediaPlayer.isPlaying())
					{
						Log.e(TAG, "Player playing ");
						controller.hide();
						mMediaPlayer.stop();
						mMediaPlayer.reset();						
					} 
					
					youTubeView.setVisibility(view.INVISIBLE);
					mSurface.setVisibility(view.VISIBLE);
					OCTOLINK1= "http://wpc.C1A9.edgecastcdn.net/hls-live/20C1A9/bbc_world/ls_satlink/b_828.m3u8";					 	
					playHlsStreams(OCTOLINK1);
				}

				//Youtube Stream 
				else if(position == 2)
				{					
					controller.hide();
					mMediaPlayer.stop();
					mMediaPlayer.reset();
					mSurface.setVisibility(View.INVISIBLE);				 
					 
					youTubeView.setVisibility(view.VISIBLE);
					OCTOLINK1 = "5D5YWv1uD8s";

					Youtube utube = new Youtube();
					utube.youtubeInitialization();

				}

				else if(position == 3)
				{
					controller.hide();
					mMediaPlayer.stop();
					OCTOLINK1="octoshape://streams.octoshape.net/ideabytes/vod/QezyPlay/vod4";
					initOctoshapeSystem(OCTOLINK1) ;

				}
				else if(position == 4)
				{ 
					OCTOLINK1="octoshape://streams.octoshape.net/ideabytes/vod/QezyPlay/vod5";
					initOctoshapeSystem(OCTOLINK1) ;
				}
			}

		});
		mCoverFlow.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(final AdapterView< ? > parent, final View view, final int position, final long id) {
				//				textView.setText("Item selected! : " + id);
			}

			@Override
			public void onNothingSelected(final AdapterView< ? > parent) {
				//				textView.setText("Nothing clicked!");
			}
		});
	}
	private void playHlsStreams(String OCTOLINK1) {
		// TODO Auto-generated method stub
		try {
			
			mMediaPlayer.setDataSource(this, Uri.parse(OCTOLINK1));
			mMediaPlayer.setOnPreparedListener(this);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			mMediaPlayer.prepare();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mMediaPlayer.start();	

	}	

	public void initOctoshapeSystem(final String OCTOLINK1) 
	{
		os = OctoshapeSystemCreator.create(this, problemListener,
				new OctoshapePortListener() {
			@Override
			public void onPortBound(int port) {
				setupStream(OCTOLINK1).requestPlay();
			}
		}, Locale.ENGLISH.getLanguage());
		os.setProblemMessageLanguage(Locale.ENGLISH.getLanguage());
		os.setProblemListener(problemListener);

		os.addPlayerNameAndVersion(MediaPlayerConstants.NATIVE_PLAYER, MediaPlayerConstants.NATIVE_PLAYER,
				"" + Build.VERSION.SDK_INT);
		os.open();
	}

	public StreamPlayer setupStream(final String stream) {

		Log.d(LOGTAG, "Setting up stream: " + stream);
		StreamPlayer sp = os.createStreamPlayer(stream);

		sp.setProblemListener(new ProblemListener() {

			@Override
			public void gotProblem(Problem p) {
				StreamStatus="No Stream/Internet Problem";
				Log.i("Stream Status  ", "Not Ok");
				active="yellow";

				Log.e(LOGTAG, stream+": "+p.getMessage() + " " + p.toString());               
			}
		});
		sp.setListener(new StreamPlayerListener() {
			private String playerId;


			@Override
			public void gotUrl(String url, long seekOffset,
					boolean playAfterBuffer) {
				Log.i(LOGTAG, "gotUrl");
				if (playAfterBuffer)
					urlQueue.add(Uri.parse(url));
				else
					playStream(Uri.parse(url), playerId);
			}

			@Override
			public void gotNewOnDemandStreamDuration(long duration) {
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

		Log.d(LOGTAG, playerId + " now plays: " + mediaUrl);

		try {
			controller.setMediaPlayer(this);
			controller.setAnchorView(PreviewFrame);
			mMediaPlayer.setDisplay(mHolder);  
			mMediaPlayer.setDataSource(this, mediaUrl);
			mMediaPlayer.setOnVideoSizeChangedListener(this);

			mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) {
					if (!urlQueue.isEmpty())
						playStream(urlQueue.removeFirst(), playerId);

				}
			});
			mMediaPlayer.setOnErrorListener(new OnErrorListener() {
				@Override
				public boolean onError(MediaPlayer mp, int what, int extra) {
					error("MediaPlayer Error: " + what + ":" + extra);
					return true;
				}
			});
			mMediaPlayer.prepare();
			mMediaPlayer.start();	

		} catch (Exception e) {
			Log.e(LOGTAG, "Error preparing MediaPlayer", e);
			error("Error preparing MediaPlayer: " + e.getMessage());
		}
	}

	@Override
	public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {

		FrameLayout.LayoutParams vLayout = (FrameLayout.LayoutParams) mSurface
				.getLayoutParams();
		mSurface.setLayoutParams(vLayout);
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
		if (mMediaPlayer != null)
			mMediaPlayer.release();
		if (os != null) {
			os.close(new Runnable() {
				@Override
				public void run() {
					CoverFlowTestingActivity.this.finish();
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
	public void surfaceCreated(SurfaceHolder mHolder) {
		// TODO Auto-generated method stub
	
		//mMediaPlayer.prepareAsync();
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
	public void onPrepared(MediaPlayer mp) {
		// TODO Auto-generated method stub
		controller.setMediaPlayer(this);
		controller.setAnchorView(PreviewFrame);
		mMediaPlayer.setDisplay(mHolder);
		mMediaPlayer.start();
	
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
		return mMediaPlayer.getCurrentPosition();
	}

	@Override
	public int getDuration() {
		return mMediaPlayer.getDuration();
	}

	@Override
	public boolean isPlaying() {
		return mMediaPlayer.isPlaying();
	}

	@Override
	public void pause() {
		mMediaPlayer.pause();
	}

	@Override
	public void seekTo(int i) {
		mMediaPlayer.seekTo(i);
	}
	@Override
	public void start() {
		mMediaPlayer.start();
	}
	@Override
	public boolean isFullScreen() {
		return false;
	}

	public void toggleFullScreen() {

		controller.hide();
		mMediaPlayer.stop();

		intent = new Intent(CoverFlowTestingActivity.this,
				MiniAndroidPlayer.class);

		intent.putExtra("OCTO", OCTOLINK1);	
		startActivity(intent);

	}
	// End VideoMediaController.MediaPlayerControl


	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub

		if(PreviewFrame == v || mSurface == v   || PreviewFrameImage == v) {

			controller.show();	// do things

			return true;
		}
		return false;
	}

	@Override
	public void onInitializationFailure(YouTubePlayer.Provider provider,
			YouTubeInitializationResult errorReason) {
		// TODO Auto-generated method stub
		if (errorReason.isUserRecoverableError()) {
			errorReason.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();
		} else {
			String errorMessage = String.format(
					getString(R.string.error_player), errorReason.toString());
			Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
		}


	}

	@Override
	public void onInitializationSuccess(YouTubePlayer.Provider provider,
			YouTubePlayer player, boolean wasRestored) {
		// TODO Auto-generated method stub
		if (!wasRestored) {

			// loadVideo() will auto play video
			// Use cueVideo() method, if you don't want to play it automatically
			//	            player.loadVideo(Config.YOUTUBE_VIDEO_CODE);
			// 
			// Hiding player controls
			player.setPlayerStyle(PlayerStyle.MINIMAL);
		}
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == RECOVERY_DIALOG_REQUEST) {
			// Retry initialization if user performed a recovery action
			getYouTubePlayerProvider().initialize(Config.DEVELOPER_KEY, this);
		}
	}

	private YouTubePlayer.Provider getYouTubePlayerProvider()
	{
		return (YouTubePlayerView) findViewById(R.id.youtube_view);
	}

	@Override
	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub
		
	}

}



















