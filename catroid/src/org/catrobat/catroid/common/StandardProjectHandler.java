/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2012 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.common;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.BroadcastScript;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.WhenScript;
import org.catrobat.catroid.content.bricks.BroadcastBrick;
import org.catrobat.catroid.content.bricks.PlaceAtBrick;
import org.catrobat.catroid.content.bricks.PointInDirectionBrick;
import org.catrobat.catroid.content.bricks.PointInDirectionBrick.Direction;
import org.catrobat.catroid.content.bricks.SetBounceFactorBrick;
import org.catrobat.catroid.content.bricks.SetCostumeBrick;
import org.catrobat.catroid.content.bricks.SetFrictionBrick;
import org.catrobat.catroid.content.bricks.SetGravityBrick;
import org.catrobat.catroid.content.bricks.SetPhysicObjectTypeBrick;
import org.catrobat.catroid.content.bricks.TurnLeftBrick;
import org.catrobat.catroid.content.bricks.TurnLeftSpeedBrick;
import org.catrobat.catroid.content.bricks.WaitBrick;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.physics.PhysicObject;
import org.catrobat.catroid.physics.PhysicObject.Type;
import org.catrobat.catroid.physics.PhysicWorld;
import org.catrobat.catroid.utils.ImageEditing;
import org.catrobat.catroid.utils.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;

import com.badlogic.gdx.math.Vector2;

public class StandardProjectHandler {

	private static final String FILENAME_SEPARATOR = "_";

	public static Project createAndSaveStandardProject(Context context) throws IOException {
		String projectName = context.getString(R.string.default_project_name);
		return createAndSaveStandardProject(projectName, context);
	}

	private static Context context;
	private static String projectName;

	public static Project createAndSaveStandardProjectPinball(String projectName, Context context) throws IOException {
		Project defaultProject = new Project(context, projectName);
		defaultProject.virtualScreenWidth = 480;
		defaultProject.virtualScreenHeight = 800;
		StorageHandler.getInstance().saveProject(defaultProject);
		ProjectManager.getInstance().setProject(defaultProject);

		StandardProjectHandler.context = context;
		StandardProjectHandler.projectName = defaultProject.getName();
		PhysicWorld physicWorld = defaultProject.getPhysicWorld();

		Sprite background = defaultProject.getSpriteList().get(0);

		Sprite ball = new Sprite("Ball");

		Sprite leftButton = new Sprite("Left button");
		Sprite rightButton = new Sprite("Right button");

		Sprite leftArm = new Sprite("Left arm");
		Sprite rightArm = new Sprite("Right arm");

		Sprite[] upperBouncers = { new Sprite("Middle cat bouncer"), new Sprite("Right cat bouncer") };

		Sprite[] lowerBouncers = { new Sprite("Left wool bouncer"), new Sprite("Middle wool bouncer"),
				new Sprite("Right wool bouncer") };

		Sprite middleBouncer = new Sprite("Cat head bouncer");

		Sprite leftHardBouncer = new Sprite("Left hard bouncer");
		Sprite leftHardBouncerBouncer = new Sprite("Left hard bouncer bouncer");
		Sprite rightHardBouncer = new Sprite("Right hard bouncer");
		Sprite rightHardBouncerBouncer = new Sprite("Right hard bouncer bouncer");

		Sprite leftVerticalWall = new Sprite("Left vertical wall");
		Sprite leftBottomWall = new Sprite("Left bottom wall");
		Sprite rightVerticalWall = new Sprite("Right vertical wall");
		Sprite rightBottomWall = new Sprite("Right bottom wall");

		final String leftButtonPressed = "Left button pressed";
		final String rightButtonPressed = "Right button pressed";

		final float armMovingSpeed = 720.0f;
		float doodlydoo = 50.0f;

		// Background
		createElement(background, physicWorld, "background_480_800", R.drawable.background_480_800, new Vector2(),
				Float.NaN);
		StartScript startScript = new StartScript(background);
		startScript.addBrick(new SetGravityBrick(physicWorld, background, new Vector2(0.0f, -8.0f)));
		background.addScript(startScript);

		// Ball
		createElement(ball, physicWorld, "pinball", R.drawable.pinball, new Vector2(-200.0f, 300.0f), Float.NaN);
		setPhysicProperties(ball, physicWorld, Type.DYNAMIC, 20.0f, 80.0f);

		// Buttons
		createElement(leftButton, physicWorld, "button", R.drawable.button, new Vector2(-175.0f, -330.0f), Float.NaN);
		createButtonPressed(leftButton, leftButtonPressed);
		createElement(rightButton, physicWorld, "button", R.drawable.button, new Vector2(175.0f, -330.0f), Float.NaN);
		createButtonPressed(rightButton, rightButtonPressed);

		// Arms
		createElement(leftArm, physicWorld, "left_arm", R.drawable.left_arm, new Vector2(-80.0f, -315.0f), Float.NaN);
		setPhysicProperties(leftArm, physicWorld, Type.FIXED, 50.0f, -1.0f);
		createMovingArm(leftArm, leftButtonPressed, physicWorld, armMovingSpeed);
		createElement(rightArm, physicWorld, "right_arm", R.drawable.right_arm, new Vector2(80.0f, -315.0f), Float.NaN);
		setPhysicProperties(rightArm, physicWorld, Type.FIXED, 50.0f, -1.0f);
		createMovingArm(rightArm, rightButtonPressed, physicWorld, -armMovingSpeed);

		// Lower walls
		createElement(leftVerticalWall, physicWorld, "vertical_wall", R.drawable.vertical_wall, new Vector2(-232.0f,
				-160.0f), 8.0f);
		setPhysicProperties(leftVerticalWall, physicWorld, Type.FIXED, 5.0f, -1.0f);
		createElement(rightVerticalWall, physicWorld, "vertical_wall", R.drawable.vertical_wall, new Vector2(232.0f,
				-160.0f), -8.0f);
		setPhysicProperties(rightVerticalWall, physicWorld, Type.FIXED, 5.0f, -1.0f);

		createElement(leftBottomWall, physicWorld, "wall_bottom", R.drawable.wall_bottom,
				new Vector2(-155.0f, -255.0f), 58.5f);
		setPhysicProperties(leftBottomWall, physicWorld, Type.FIXED, 5.0f, -1.0f);
		createElement(rightBottomWall, physicWorld, "wall_bottom", R.drawable.wall_bottom,
				new Vector2(155.0f, -255.0f), -58.5f);
		setPhysicProperties(rightBottomWall, physicWorld, Type.FIXED, 5.0f, -1.0f);

		// Hard Bouncer
		createElement(leftHardBouncer, physicWorld, "left_hard_bouncer", R.drawable.left_hard_bouncer, new Vector2(
				-140.0f, -165.0f), Float.NaN);
		setPhysicProperties(leftHardBouncer, physicWorld, Type.FIXED, 10.0f, -1.0f);
		createElement(leftHardBouncerBouncer, physicWorld, "left_light_bouncer", R.drawable.left_light_bouncer,
				new Vector2(-129.0f, -163.0f), Float.NaN);
		setPhysicProperties(leftHardBouncerBouncer, physicWorld, Type.FIXED, 124.0f, -1.0f);

		createElement(rightHardBouncer, physicWorld, "right_hard_bouncer", R.drawable.right_hard_bouncer, new Vector2(
				140.0f, -165.0f), Float.NaN);
		setPhysicProperties(rightHardBouncer, physicWorld, Type.FIXED, 10.0f, -1.0f);
		createElement(rightHardBouncerBouncer, physicWorld, "right_light_bouncer", R.drawable.right_light_bouncer,
				new Vector2(129.0f, -163.0f), Float.NaN);
		setPhysicProperties(rightHardBouncerBouncer, physicWorld, Type.FIXED, 124.0f, -1.0f);

		// Lower wool bouncers
		Vector2[] lowerBouncersPositions = { new Vector2(-100.0f, -80.0f + doodlydoo),
				new Vector2(0.0f, -140.0f + doodlydoo), new Vector2(100.0f, -80.0f + doodlydoo) };
		for (int index = 0; index < lowerBouncers.length; index++) {
			createElement(lowerBouncers[index], physicWorld, "wolle_bouncer", R.drawable.wolle_bouncer,
					lowerBouncersPositions[index], new Random().nextInt(360));
			setPhysicProperties(lowerBouncers[index], physicWorld, Type.FIXED, 116.0f, -1.0f);
		}

		// Middle bouncer
		createElement(middleBouncer, physicWorld, "middle_cat_bouncer", R.drawable.middle_cat_bouncer, new Vector2(
				0.0f, 75.0f + doodlydoo), Float.NaN);
		setPhysicProperties(middleBouncer, physicWorld, Type.FIXED, 40.0f, 80.0f);

		// Upper bouncers
		Vector2[] upperBouncersPositions = { new Vector2(0.0f, 240.f + doodlydoo),
				new Vector2(150.0f, 200.0f + doodlydoo) };
		for (int index = 0; index < upperBouncers.length; index++) {
			createElement(upperBouncers[index], physicWorld, "cat_bouncer", R.drawable.cat_bouncer,
					upperBouncersPositions[index], Float.NaN);
			setPhysicProperties(upperBouncers[index], physicWorld, Type.FIXED, 106.0f, -1.0f);
		}

		defaultProject.addSprite(leftButton);
		defaultProject.addSprite(rightButton);
		defaultProject.addSprite(ball);
		defaultProject.addSprite(leftArm);
		defaultProject.addSprite(rightArm);
		defaultProject.addSprite(middleBouncer);
		defaultProject.addSprite(leftHardBouncerBouncer);
		defaultProject.addSprite(leftHardBouncer);
		defaultProject.addSprite(rightHardBouncerBouncer);
		defaultProject.addSprite(rightHardBouncer);
		defaultProject.addSprite(leftVerticalWall);
		defaultProject.addSprite(leftBottomWall);
		defaultProject.addSprite(rightVerticalWall);
		defaultProject.addSprite(rightBottomWall);

		for (Sprite sprite : upperBouncers) {
			defaultProject.addSprite(sprite);
		}

		for (Sprite sprite : lowerBouncers) {
			defaultProject.addSprite(sprite);
		}

		return defaultProject;
	}

	private static void createElement(Sprite sprite, PhysicWorld physicWorld, String fileName, int fileId,
			Vector2 position, float angle) throws IOException {
		File file = savePictureFromResourceInProject(projectName, fileName, fileId, context);
		CostumeData costumeData = new CostumeData();
		costumeData.setCostumeName(fileName);
		costumeData.setCostumeFilename(file.getName());

		List<CostumeData> costumes = sprite.getCostumeDataList();
		costumes.add(costumeData);

		SetCostumeBrick costumeBrick = new SetCostumeBrick(sprite);
		costumeBrick.setCostume(costumeData);

		Script startScript = new StartScript(sprite);
		startScript.addBrick(new PlaceAtBrick(sprite, (int) position.x, (int) position.y));
		startScript.addBrick(costumeBrick);

		if (!Float.isNaN(angle)) {
			TurnLeftBrick turnLeftBrick = new TurnLeftBrick(sprite, angle);
			startScript.addBrick(turnLeftBrick);
		}

		sprite.addScript(startScript);
	}

	private static void setPhysicProperties(Sprite sprite, PhysicWorld physicWorld, PhysicObject.Type type,
			float bounce, float friction) {
		Script startScript = new StartScript(sprite);

		startScript.addBrick(new SetPhysicObjectTypeBrick(physicWorld, sprite, type));

		if (bounce >= 0.0f) {
			startScript.addBrick(new SetBounceFactorBrick(physicWorld, sprite, bounce));
		}

		if (friction >= 0.0f) {
			startScript.addBrick(new SetFrictionBrick(physicWorld, sprite, friction));
		}

		sprite.addScript(startScript);
	}

	private static void createButtonPressed(Sprite sprite, String broadcastMessage) throws IOException {
		ProjectManager.getInstance().getMessageContainer().addMessage(broadcastMessage);

		WhenScript whenPressedScript = new WhenScript(sprite);
		whenPressedScript.setAction(0);

		BroadcastBrick leftButtonBroadcastBrick = new BroadcastBrick(sprite);
		leftButtonBroadcastBrick.setSelectedMessage(broadcastMessage);

		String filename = "button_pressed";
		File file = savePictureFromResourceInProject(projectName, filename, R.drawable.button_pressed, context);
		CostumeData costumeData = new CostumeData();
		costumeData.setCostumeName(filename);
		costumeData.setCostumeFilename(file.getName());

		List<CostumeData> costumes = sprite.getCostumeDataList();
		costumes.add(costumeData);

		SetCostumeBrick costumeBrick = new SetCostumeBrick(sprite);
		costumeBrick.setCostume(costumeData);

		WaitBrick waitBrick = new WaitBrick(sprite, 500);

		SetCostumeBrick costumeBack = new SetCostumeBrick(sprite);
		costumeBack.setCostume(costumes.get(0));

		whenPressedScript.addBrick(leftButtonBroadcastBrick);
		whenPressedScript.addBrick(costumeBrick);
		whenPressedScript.addBrick(waitBrick);
		whenPressedScript.addBrick(costumeBack);
		sprite.addScript(whenPressedScript);
	}

	private static void createMovingArm(Sprite sprite, String broadcastMessage, PhysicWorld physicWorld,
			float degreeSpeed) {
		BroadcastScript broadcastScript = new BroadcastScript(sprite);
		broadcastScript.setBroadcastMessage(broadcastMessage);

		int waitInMillis = 110;

		broadcastScript.addBrick(new TurnLeftSpeedBrick(physicWorld, sprite, degreeSpeed));
		broadcastScript.addBrick(new WaitBrick(sprite, waitInMillis));

		broadcastScript.addBrick(new TurnLeftSpeedBrick(physicWorld, sprite, 0));
		broadcastScript.addBrick(new PointInDirectionBrick(sprite, Direction.DIRECTION_RIGHT));
		broadcastScript.addBrick(new WaitBrick(sprite, 25));
		broadcastScript.addBrick(new PointInDirectionBrick(sprite, Direction.DIRECTION_RIGHT));

		sprite.addScript(broadcastScript);
	}

	public static Project createAndSaveStandardProject(String projectName, Context context) throws IOException {
		String normalCatName = context.getString(R.string.default_project_sprites_catroid_normalcat);
		String banzaiCatName = context.getString(R.string.default_project_sprites_catroid_banzaicat);
		String cheshireCatName = context.getString(R.string.default_project_sprites_catroid_cheshirecat);
		String backgroundName = context.getString(R.string.default_project_backgroundname);

		Project defaultProject = new Project(context, projectName);
		StorageHandler.getInstance().saveProject(defaultProject);
		ProjectManager.getInstance().setProject(defaultProject);
		Sprite sprite = new Sprite(context.getString(R.string.default_project_sprites_catroid_name));
		Sprite backgroundSprite = defaultProject.getSpriteList().get(0);

		Script backgroundStartScript = new StartScript(backgroundSprite);
		Script startScript = new StartScript(sprite);
		Script whenScript = new WhenScript(sprite);

		File backgroundFile = createBackgroundImage(projectName, backgroundName,
				context.getString(R.string.default_project_backgroundcolor));

		File normalCat = copyAndScaleImageToProject(projectName, context, normalCatName, R.drawable.catroid);
		File banzaiCat = copyAndScaleImageToProject(projectName, context, banzaiCatName, R.drawable.catroid_banzai);
		File cheshireCat = copyAndScaleImageToProject(projectName, context, cheshireCatName,
				R.drawable.catroid_cheshire);

		CostumeData normalCatCostumeData = new CostumeData();
		normalCatCostumeData.setCostumeName(normalCatName);
		normalCatCostumeData.setCostumeFilename(normalCat.getName());

		CostumeData banzaiCatCostumeData = new CostumeData();
		banzaiCatCostumeData.setCostumeName(banzaiCatName);
		banzaiCatCostumeData.setCostumeFilename(banzaiCat.getName());

		CostumeData cheshireCatCostumeData = new CostumeData();
		cheshireCatCostumeData.setCostumeName(cheshireCatName);
		cheshireCatCostumeData.setCostumeFilename(cheshireCat.getName());

		CostumeData backgroundCostumeData = new CostumeData();
		backgroundCostumeData.setCostumeName(backgroundName);
		backgroundCostumeData.setCostumeFilename(backgroundFile.getName());

		ArrayList<CostumeData> costumeDataList = sprite.getCostumeDataList();
		costumeDataList.add(normalCatCostumeData);
		costumeDataList.add(banzaiCatCostumeData);
		costumeDataList.add(cheshireCatCostumeData);
		ArrayList<CostumeData> costumeDataList2 = backgroundSprite.getCostumeDataList();
		costumeDataList2.add(backgroundCostumeData);

		SetCostumeBrick setCostumeBrick = new SetCostumeBrick(sprite);
		setCostumeBrick.setCostume(normalCatCostumeData);

		SetCostumeBrick setCostumeBrick1 = new SetCostumeBrick(sprite);
		setCostumeBrick1.setCostume(normalCatCostumeData);

		SetCostumeBrick setCostumeBrick2 = new SetCostumeBrick(sprite);
		setCostumeBrick2.setCostume(banzaiCatCostumeData);

		SetCostumeBrick setCostumeBrick3 = new SetCostumeBrick(sprite);
		setCostumeBrick3.setCostume(cheshireCatCostumeData);

		SetCostumeBrick backgroundBrick = new SetCostumeBrick(backgroundSprite);
		backgroundBrick.setCostume(backgroundCostumeData);

		WaitBrick waitBrick1 = new WaitBrick(sprite, 500);
		WaitBrick waitBrick2 = new WaitBrick(sprite, 500);

		startScript.addBrick(setCostumeBrick);

		whenScript.addBrick(setCostumeBrick2);
		whenScript.addBrick(waitBrick1);
		whenScript.addBrick(setCostumeBrick3);
		whenScript.addBrick(waitBrick2);
		whenScript.addBrick(setCostumeBrick1);
		backgroundStartScript.addBrick(backgroundBrick);

		defaultProject.addSprite(sprite);
		sprite.addScript(startScript);
		sprite.addScript(whenScript);
		backgroundSprite.addScript(backgroundStartScript);

		StorageHandler.getInstance().saveProject(defaultProject);

		return defaultProject;
	}

	private static File createBackgroundImage(String projectName, String backgroundName, String backgroundColor)
			throws FileNotFoundException {
		String directoryName = Utils.buildPath(Utils.buildProjectPath(projectName), Constants.IMAGE_DIRECTORY);
		File backgroundTemp = new File(Utils.buildPath(directoryName, backgroundName));
		Bitmap backgroundBitmap = ImageEditing.createSingleColorBitmap(Values.SCREEN_WIDTH, Values.SCREEN_HEIGHT,
				Color.parseColor(backgroundColor));
		StorageHandler.saveBitmapToImageFile(backgroundTemp, backgroundBitmap);
		File backgroundFile = new File(directoryName, Utils.md5Checksum(backgroundTemp) + FILENAME_SEPARATOR
				+ backgroundTemp.getName());
		backgroundTemp.renameTo(backgroundFile);
		return backgroundFile;
	}

	private static File copyAndScaleImageToProject(String projectName, Context context, String imageName, int imageId)
			throws IOException {
		String directoryName = Utils.buildPath(Utils.buildProjectPath(projectName), Constants.IMAGE_DIRECTORY);
		File tempImageFile = savePictureFromResourceInProject(projectName, imageName, imageId, context);

		int[] dimensions = ImageEditing.getImageDimensions(tempImageFile.getAbsolutePath());
		int originalWidth = dimensions[0];
		int originalHeight = dimensions[1];
		double ratio = (double) originalHeight / (double) originalWidth;

		// scale the cat, that its always 1/3 of the screen width
		Bitmap tempBitmap = ImageEditing.getScaledBitmapFromPath(tempImageFile.getAbsolutePath(),
				Values.SCREEN_WIDTH / 3, (int) (Values.SCREEN_WIDTH / 3 * ratio), false);
		StorageHandler.saveBitmapToImageFile(tempImageFile, tempBitmap);

		String finalImageFileString = Utils.buildPath(directoryName, Utils.md5Checksum(tempImageFile)
				+ FILENAME_SEPARATOR + tempImageFile.getName());
		File finalImageFile = new File(finalImageFileString);
		tempImageFile.renameTo(finalImageFile);

		return finalImageFile;
	}

	private static File savePictureFromResourceInProject(String project, String outputName, int fileId, Context context)
			throws IOException {

		final String imagePath = Utils
				.buildPath(Utils.buildProjectPath(project), Constants.IMAGE_DIRECTORY, outputName);
		File testImage = new File(imagePath);
		if (!testImage.exists()) {
			testImage.createNewFile();
		}
		InputStream in = context.getResources().openRawResource(fileId);
		OutputStream out = new BufferedOutputStream(new FileOutputStream(testImage), Constants.BUFFER_8K);
		byte[] buffer = new byte[Constants.BUFFER_8K];
		int length = 0;
		while ((length = in.read(buffer)) > 0) {
			out.write(buffer, 0, length);
		}

		in.close();
		out.flush();
		out.close();

		return testImage;
	}

}
