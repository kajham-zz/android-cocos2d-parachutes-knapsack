package com.kajham.parachute;

import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.opengl.CCGLSurfaceView;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

public class ParachuteActivity extends Activity {

	protected CCGLSurfaceView mGlSurfaceView;

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
		final EditText timerValueEditText = (EditText) findViewById(R.id.timer_value_edit_text);
		newGame.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ParachuteActivity.this.setContentView(mGlSurfaceView);
				playGame(Integer.parseInt(timerValueEditText.getText()
						.toString()));
			}
		});

		// text watcher to ensure that the new game button is disabled 
		// until some value is entered for the game timer
		TextWatcher textWatcher = new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (s == null || s.toString().equals("")) {
					newGame.setEnabled(false);
				} else {
					newGame.setEnabled(true);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		};
		timerValueEditText.addTextChangedListener(textWatcher);

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
		super.onPause();
		CCDirector.sharedDirector().pause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		CCDirector.sharedDirector().resume();
	}

	@Override
	protected void onStop() {
		super.onStop();
		CCDirector.sharedDirector().end();
	}
}