package com.kajham.parachute;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.kajham.parachute.data.HighScore;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class ParachuteHighScoresActivity extends ListActivity {

	private HighScoresAdapter mAdapter;

	private static final int DIALOG_UPLOAD_SCORE_ID = 10;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		// get full screen setup and keep screen bright and on when the window
		// is visible
		getWindow().setFlags(
				WindowManager.LayoutParams.FLAG_FULLSCREEN
						| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_FULLSCREEN
						| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		setContentView(R.layout.activity_high_score);
		mAdapter = new HighScoresAdapter(this);

		Button newGameButton = (Button) findViewById(R.id.new_game_button);
		newGameButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent newGameIntent = new Intent(
						ParachuteHighScoresActivity.this,
						ParachuteActivity.class);
				startActivity(newGameIntent);
				finish();
			}
		});

		showDialog(DIALOG_UPLOAD_SCORE_ID);
	}

	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {
		switch (id) {
		case DIALOG_UPLOAD_SCORE_ID:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			View view = getLayoutInflater().inflate(
					R.layout.dialog_upload_score, null);
			final TextView highScore = (TextView) view
					.findViewById(R.id.high_score_text_view);
			final TextView individualName = (TextView) view
					.findViewById(R.id.high_score_individual_name);
			highScore.setText(Integer.toString(getIntent().getIntExtra(
					"HIGH_SCORE", 0)));

			Button uploadScoreButton = (Button) view
					.findViewById(R.id.upload_score_btn);
			uploadScoreButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					ParseObject pObject = new ParseObject("IndividualScore");
					pObject.put("Name", individualName.getText().toString());
					pObject.put("Score",
							Integer.parseInt(highScore.getText().toString()));
					pObject.saveInBackground();
					fetchHighScores();
					dismissDialog(DIALOG_UPLOAD_SCORE_ID);
				}
			});

			builder.setView(view);
			
			Dialog dialog = builder.create();
			dialog.setCanceledOnTouchOutside(false);
			
			return dialog;
		}
		return super.onCreateDialog(id, args);
	}

	private void fetchHighScores() {
		ParseQuery query = new ParseQuery("IndividualScore");
		query.setLimit(10);
		query.orderByDescending("Score");

		query.findInBackground(new FindCallback() {

			@Override
			public void done(List<ParseObject> highScoreList,
					ParseException exception) {

				// convert to list of HighScores
				List<HighScore> highScores = new ArrayList<HighScore>();
				for (ParseObject pObject : highScoreList) {
					HighScore score = new HighScore(pObject.getString("Name"),
							pObject.getInt("Score"));
					highScores.add(score);
				}
				mAdapter.setHighScore(highScores);
				setListAdapter(mAdapter);
			}
		});
	}

}
