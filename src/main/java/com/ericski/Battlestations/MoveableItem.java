package com.ericski.Battlestations;

import java.awt.Image;

public interface MoveableItem {

	String getDescription();

	MoveableItem copy();

	boolean isBlankMoveableItem();

	int getRotation();

	Image getNonRotatedImage();

	boolean isUpgraded();

	void setRotation(int angle);

	void setName(String moduleName);

	void setUpgraded(boolean b);

	String getName();

}
