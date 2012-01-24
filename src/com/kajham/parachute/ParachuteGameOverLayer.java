package com.kajham.parachute;

import org.cocos2d.layers.CCColorLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccColor3B;
import org.cocos2d.types.ccColor4B;

public class ParachuteGameOverLayer extends CCColorLayer {

	public static CCScene scene(int score) {
		CCScene scene = CCScene.node();
		ParachuteGameOverLayer gameOverLayer = new ParachuteGameOverLayer(
				ccColor4B.ccc4(255, 255, 255, 255), score);
		scene.addChild(gameOverLayer);

		return scene;
	}

	public ParachuteGameOverLayer(ccColor4B color, int score) {
		super(color);

		CGSize winSize = CCDirector.sharedDirector().displaySize();
		CCLabel gameOverLabel = CCLabel.makeLabel("Game over :(", "DroidSans",
				40);
		gameOverLabel.setPosition(CGPoint.ccp(winSize.width / 2.0f,
				winSize.height / 2.0f));
		gameOverLabel.setColor(ccColor3B.ccRED);
		addChild(gameOverLabel);

		CCLabel yourScoreLabel = CCLabel.makeLabel(
				"Your score = " + Integer.toString(score), "DroidSans", 40);
		yourScoreLabel.setPosition(CGPoint.ccp(winSize.width / 2.0f,
				winSize.height / 2.0f - gameOverLabel.getContentSize().height));
		yourScoreLabel.setColor(ccColor3B.ccRED);
		addChild(yourScoreLabel);
	}
}
