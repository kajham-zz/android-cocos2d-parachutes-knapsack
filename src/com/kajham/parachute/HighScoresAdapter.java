package com.kajham.parachute;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kajham.parachute.data.HighScore;

public class HighScoresAdapter extends BaseAdapter {
	
	private List<HighScore> mHighScores;
	private LayoutInflater mInflater;

	public HighScoresAdapter(Context context) {
		super();
		mHighScores = new ArrayList<HighScore>();
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public void setHighScore(List<HighScore> highScores) {
		mHighScores = highScores;
	}

	@Override
	public int getCount() {
		return mHighScores.size();
	}

	@Override
	public Object getItem(int position) {
		return mHighScores.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = mInflater.inflate(R.layout.row_high_score, null);
		TextView highScoreName = (TextView) view.findViewById(R.id.high_score_individual_name);
		TextView highScoreScore = (TextView) view.findViewById(R.id.high_score_individual_score);
		
		HighScore score = mHighScores.get(position);
		highScoreName.setText(score.name);
		highScoreScore.setText(Integer.toString(score.score));
		
		return view;
	}
}
