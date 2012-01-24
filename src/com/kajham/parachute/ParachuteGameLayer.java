package com.kajham.parachute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.cocos2d.actions.instant.CCCallFuncN;
import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.layers.CCColorLayer;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccColor3B;
import org.cocos2d.types.ccColor4B;

import android.view.MotionEvent;

public class ParachuteGameLayer extends CCColorLayer {

	private CCSprite mCanon;
	private List<CCLabel> mTargetLabels = new ArrayList<CCLabel>();
	private HashMap<CCLabel, Integer> mTargetLabelValues = new HashMap<CCLabel, Integer>();
	private List<CCSprite> mProjectiles = new ArrayList<CCSprite>();

	private CCLabel mScoreLabel;
	private int mScore;

	private long mTimerValue = 10000L;
	private long mStartTime;
	private CCLabel mTimerLabel;

	private static int TAG_TARGET = 1;
	private static int TAG_PROJECTILE = 2;

	public static CCScene scene(int timerValue) {
		CCScene scene = CCScene.node();
		CCLayer layer = new ParachuteGameLayer(ccColor4B.ccc4(255, 255, 255,
				255), timerValue);
		scene.addChild(layer);
		return scene;
	}

	public ParachuteGameLayer(ccColor4B color, int timerValue) {
		super(color);
		mTimerValue = timerValue * 1000L;
		CGSize winSize = CCDirector.sharedDirector().displaySize();

		// add the canon to the screen
		mCanon = CCSprite.sprite("canon.png");
		mCanon.setPosition(CGPoint.ccp(winSize.width / 2.0f,
				mCanon.getContentSize().height / 2.0f));
		addChild(mCanon);

		mScoreLabel = CCLabel
				.makeLabel(Integer.toString(0), "DroidSans", 40.0f);
		mScoreLabel.setPosition(CGPoint.ccp(
				winSize.width - mScoreLabel.getContentSize().width,
				winSize.height - mScoreLabel.getContentSize().height));
		mScoreLabel.setColor(ccColor3B.ccRED);
		addChild(mScoreLabel);

		mStartTime = System.currentTimeMillis();
		mTimerLabel = CCLabel.makeLabel(getTimerStringValue(mStartTime),
				"DroidSans", 40.0f);
		mTimerLabel.setPosition(CGPoint.ccp(mTimerLabel.getContentSize().width,
				winSize.getHeight() - mTimerLabel.getContentSize().height));
		mTimerLabel.setColor(ccColor3B.ccBLUE);
		addChild(mTimerLabel);

		// schedule the addition of new targets every second of the game
		schedule("addTarget", 1.0f);

		this.setIsTouchEnabled(true);

		schedule("update");
	}

	@Override
	public boolean ccTouchesEnded(MotionEvent event) {

		CGSize winSize = CCDirector.sharedDirector().displaySize();
		CCSprite projectile = CCSprite.sprite("Projectile.png");
		projectile.setPosition(CGPoint.ccp(winSize.width / 2.0f,
				-projectile.getContentSize().height / 2.0f));
		projectile.setTag(TAG_PROJECTILE);
		addChild(projectile);
		mProjectiles.add(projectile);

		CGPoint touchLocation = CCDirector.sharedDirector().convertToGL(
				CGPoint.ccp(event.getX(), event.getY()));

		if (touchLocation.y < mCanon.getContentSize().height) {
			return true;
		}

		float slope = (projectile.getPosition().y - touchLocation.y)
				/ (projectile.getPosition().x - touchLocation.x);
		float projectedY = winSize.height
				- projectile.getContentSize().getHeight() / 2.0f;
		float projectedX = (projectedY / slope) + projectile.getPosition().x;

		// offset between projected position and current center
		float projectedOffsetX = projectedX - projectile.getPosition().x;
		float projectedOffsetY = projectedY - projectile.getPosition().y;

		double actualDistance = Math.sqrt((projectedOffsetX * projectedOffsetX)
				+ (projectedOffsetY * projectedOffsetY));

		float velocity = 480.0f / 1.0f;

		float actualDuration = (float) (actualDistance / velocity);

		projectile.runAction(CCSequence.actions(
				CCMoveTo.action(actualDuration,
						CGPoint.ccp(projectedX, projectedY)),
				CCCallFuncN.action(this, "spriteMoveFinished")));

		return true;
	}

	public void addTarget(float dt) {

		Random rand = new Random();

		CGSize winSize = CCDirector.sharedDirector().displaySize();

		// determine any number between -100 and 100.
		int randomNumber = ((rand.nextInt() % 2) == 0) ? rand.nextInt(100)
				: -rand.nextInt(100);

		CCLabel targetLabel = CCLabel.makeLabel(Integer.toString(randomNumber),
				"DroidSans", 40);
		targetLabel.setColor(ccColor3B.ccBLACK);

		// Determine the speed of the target
		int minDuration = 2;
		int maxDuration = 4;
		int rangeDuration = maxDuration - minDuration;
		float actualDuration = rand.nextInt(rangeDuration) + rand.nextFloat()
				+ minDuration;

		// Determine the range possible on the Y-axis for the target label
		// to exist
		int minYPosition = (int) (mCanon.getContentSize().height + targetLabel
				.getContentSize().height);
		int maxYPosition = (int) (winSize.getHeight()
				- targetLabel.getContentSize().height - mScoreLabel
				.getContentSize().height);

		int rangeYPosition = maxYPosition - minYPosition;
		int actualYPosition = 0;

		actualYPosition = rand.nextInt(rangeYPosition) + minYPosition;

		// set the initial position of the target label
		targetLabel.setPosition(CGPoint.ccp(targetLabel.getContentSize().width
				/ 2.0f + winSize.width, actualYPosition));
		targetLabel.setTag(TAG_TARGET);
		addChild(targetLabel);
		mTargetLabels.add(targetLabel);
		mTargetLabelValues.put(targetLabel, randomNumber);

		// Set the actions
		targetLabel.runAction(CCSequence.actions(CCMoveTo.action(
				actualDuration, CGPoint.ccp(
						-targetLabel.getContentSize().width / 2.0f,
						actualYPosition)), CCCallFuncN.action(this,
				"spriteMoveFinished")));
	}

	public void spriteMoveFinished(Object sender) {
		CCSprite sprite = (CCSprite) sender;
		if (sprite.getTag() == TAG_TARGET) {
			mTargetLabels.remove(sprite);
			mTargetLabelValues.remove(sprite);
		} else if (sprite.getTag() == TAG_PROJECTILE) {
			mProjectiles.remove(sprite);
		}
		removeChild(sprite, true);
	}

	public void update(float dt) {
		updateTimer();

		ArrayList<CCSprite> projectilesToDelete = new ArrayList<CCSprite>();
		ArrayList<CCSprite> targetsToDelete = new ArrayList<CCSprite>();
		for (CCSprite projectile : mProjectiles) {
			CGRect projectileRect = CGRect.make(projectile.getPosition().x
					- projectile.getContentSize().width / 2.0f,
					projectile.getPosition().y
							- projectile.getContentSize().height / 2.0f,
					projectile.getContentSize().width,
					projectile.getContentSize().height);
			targetsToDelete.clear();
			for (CCSprite target : mTargetLabels) {
				CGRect targetRect = CGRect
						.make(target.getPosition().x
								- target.getContentSize().width,
								target.getPosition().y
										- target.getContentSize().height,
								target.getContentSize().width,
								target.getContentSize().height);
				if (CGRect.intersects(projectileRect, targetRect)) {
					targetsToDelete.add(target);
					updateScore(mTargetLabelValues.get(target));
				}
			}

			for (CCSprite targetToDelete : targetsToDelete) {
				removeChild(targetToDelete, true);
				mTargetLabels.remove(targetToDelete);
				mTargetLabelValues.remove(targetToDelete);
			}

			if (targetsToDelete.size() > 0) {
				projectilesToDelete.add(projectile);
			}
		}

		for (CCSprite projectileToDelete : projectilesToDelete) {
			removeChild(projectileToDelete, true);
			mProjectiles.remove(projectileToDelete);
		}
	}

	protected void updateScore(int incrementalScore) {

		mScore += incrementalScore;

		CGSize winSize = CCDirector.sharedDirector().displaySize();
		mScoreLabel.setString(Integer.toString(mScore));
		mScoreLabel.setPosition(CGPoint.ccp(
				winSize.width - mScoreLabel.getContentSize().width,
				winSize.height - mScoreLabel.getContentSize().height));
	}

	protected String getTimerStringValue(long currTime) {
		long timeElapsed = currTime - mStartTime;
		if (timeElapsed > mTimerValue) {
			CCDirector.sharedDirector().replaceScene(
					ParachuteGameOverLayer.scene(mScore));
		}
		long remainingTime = mTimerValue - timeElapsed;
		int minutes = (int) (remainingTime / 60000);
		int seconds = (int) (remainingTime - minutes * 60000) / 1000;

		return Integer.toString(minutes) + ":" + Integer.toString(seconds);
	}

	protected void updateTimer() {
		mTimerLabel.setString(getTimerStringValue(System.currentTimeMillis()));

		CGSize winSize = CCDirector.sharedDirector().displaySize();
		mTimerLabel.setPosition(CGPoint.ccp(mTimerLabel.getContentSize().width,
				winSize.height - mTimerLabel.getContentSize().height));
	}
}
