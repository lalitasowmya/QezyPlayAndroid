
package com.ib.qezyplay;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.PlayerStyle;
import com.google.android.youtube.player.YouTubePlayerView;

public class Youtube extends YouTubeBaseActivity implements
YouTubePlayer.OnInitializedListener {

	private static final int RECOVERY_DIALOG_REQUEST = 1;

	// YouTube player view
	private YouTubePlayerView youTubeView;
	private String YoutubeIdentifier=null;

	//	    Intent intent = getIntent();
	//		YoutubeIdentifier =  intent.getStringExtra("OCTO");
	//	


	public void youtubeInitialization()
	{
		YoutubeIdentifier =com.ib.qezyplay.CoverFlowTestingActivity.OCTOLINK1; 

		YouTubePlayerView youTubeView1= com.ib.qezyplay.CoverFlowTestingActivity.youTubeView;
		youTubeView1.initialize(Config.DEVELOPER_KEY, this );

	}

	
	@Override
	public void onInitializationFailure(YouTubePlayer.Provider provider,
			YouTubeInitializationResult errorReason) {
		if (errorReason.isUserRecoverableError()) {
			errorReason.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();
		} else
		{
			String errorMessage = String.format(
					getString(R.string.error_player), errorReason.toString());
			Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onInitializationSuccess(YouTubePlayer.Provider provider,
			YouTubePlayer player, boolean wasRestored) {
		if (!wasRestored) {

			// loadVideo() will auto play video
			// Use cueVideo() method, if you don't want to play it automatically
			player.loadVideo(YoutubeIdentifier);

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

}
