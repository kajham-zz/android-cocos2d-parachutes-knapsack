package com.kajham.parachute;

import java.util.ArrayList;
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
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccColor3B;
import org.cocos2d.types.ccColor4B;

public class ParachuteGameLayer extends CCColorLayer {

	private CCSprite mCanon;
	private List<CCLabel> mTargetLabels = new ArrayList<CCLabel>();

	public static CCScene scene() {
		CCScene scene = CCScene.node();
		CCLayer layer = new ParachuteGameLayer(ccColor4B.ccc4(255, 255, 255,
				255));
		scene.addChild(layer);
		return scene;
	}

	public ParachuteGameLayer(ccColor4B color) {
		super(color);

		CGSize winSize = CCDirector.sharedDirector().displaySize();
		mCanon = CCSprite.sprite("canon.png");
		mCanon.setPosition(CGPoint.ccp(winSize.width / 2.0f, mCanon.getContentSize().height / 2.0f));
		addChild(mCanon);
		schedule("addTarget", 1.0f);
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
		int maxYPosition = (int) (winSize.getHeight() - targetLabel
				.getContentSize().height);

		int rangeYPosition = maxYPosition - minYPosition;
		int actualYPosition = 0;

		actualYPosition = rand.nextInt(rangeYPosition) + minYPosition;

		// set the initial position of the target label
		targetLabel.setPosition(CGPoint.ccp(targetLabel.getContentSize().width
				/ 2.0f + winSize.width, actualYPosition));
		addChild(targetLabel);
		mTargetLabels.add(targetLabel);

		// Set the actions
		targetLabel.runAction(CCSequence.actions(CCMoveTo.action(
				actualDuration, CGPoint.ccp(
						-targetLabel.getContentSize().width / 2.0f,
						actualYPosition)), CCCallFuncN.action(this,
				"labelMoveFinished")));
	}

	public void labelMoveFinished(Object sender) {
		CCLabel targetLabel = (CCLabel) sender;
		removeChild(targetLabel, true);
		mTargetLabels.remove(targetLabel);
	}

	public void update(float dt) {

	}
}
