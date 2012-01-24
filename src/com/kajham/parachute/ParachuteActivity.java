package com.kajham.parachute;

import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.opengl.CCGLSurfaceView;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

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
		setContentView(mGlSurfaceView);
    }
    
	@Override
	protected void onStart() {
		super.onStart();
		CCDirector.sharedDirector().attachInView(mGlSurfaceView);
		CCDirector.sharedDirector().setDisplayFPS(true);
		CCDirector.sharedDirector().setAnimationInterval(1.0f / 60.0f);

		CCScene scene = ParachuteGameLayer.scene();
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