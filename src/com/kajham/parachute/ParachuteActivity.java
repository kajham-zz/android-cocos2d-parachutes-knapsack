package com.kajham.parachute;

import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.opengl.CCGLSurfaceView;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.kajham.parachute.utils.KeyUtils;
import com.parse.Parse;

public class ParachuteActivity extends Activity {

	protected CCGLSurfaceView mGlSurfaceView;
	private static int MAX_GAME_TIME_SECONDS = 300;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		// get full screen setup and keep screen bright and on when the window
		// is visible
		getWindow().setFlags(
				WindowManager.LayoutParams.FLAG_FULLSCREEN
						| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_FULLSCREEN
						| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		mGlSurfaceView = new CCGLSurfaceView(this);
		setContentView(R.layout.main);

		final Button newGame = (Button) findViewById(R.id.start_game_btn);
		final TextView numSecondsTextView = (TextView) findViewById(R.id.num_seconds_text_view);
		final TextView numSacksTextView = (TextView) findViewById(R.id.num_sacks_text_view);
		OnSeekBarChangeListener changeListener = new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				int currentGameTimeInSeconds = (progress * MAX_GAME_TIME_SECONDS) / 100;
				numSecondsTextView.setText(Integer
						.toString(currentGameTimeInSeconds));
				numSacksTextView.setText(Integer.toString(ParachuteGameLayer
						.numSacksInGame(currentGameTimeInSeconds)));
				newGame.setEnabled(currentGameTimeInSeconds > 0);
			}
		};

		final SeekBar timerSeekBar = (SeekBar) findViewById(R.id.timer_seek_bar);
		timerSeekBar.setOnSeekBarChangeListener(changeListener);

		newGame.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ParachuteActivity.this.setContentView(mGlSurfaceView);
				playGame((timerSeekBar.getProgress() * MAX_GAME_TIME_SECONDS) / 100);
				// hide the keyboard when the user starts a new game
				InputMethodManager im = (InputMethodManager) (getSystemService(Context.INPUT_METHOD_SERVICE));
				im.hideSoftInputFromWindow(timerSeekBar.getWindowToken(), 0);
			}
		});

		// initialize Parse
		Parse.initialize(this, KeyUtils.PARSE_APPLICATION_ID,
				KeyUtils.PARSE_CLIENT_ID);
	}

	@Override
	protected void onStart() {
		super.onStart();
		CCDirector.sharedDirector().attachInView(mGlSurfaceView);
		CCDirector.sharedDirector().setDisplayFPS(true);
		CCDirector.sharedDirector().setAnimationInterval(1.0f / 60.0f);
	}

	private void playGame(int timerValue) {
		CCScene scene = ParachuteGameLayer.scene(timerValue);
		CCDirector.sharedDirector().runWithScene(scene);
	}

	@Override
	protected void onPause() {
		CCDirector.sharedDirector().pause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		CCDirector.sharedDirector().resume();
		super.onResume();
	}

	@Override
	protected void onStop() {
		CCDirector.sharedDirector().end();
		super.onStop();
	}
}