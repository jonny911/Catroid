/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2014 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.uitest.stage;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.SpeakBrick;
import org.catrobat.catroid.content.bricks.WaitBrick;
import org.catrobat.catroid.exceptions.ProjectException;
import org.catrobat.catroid.io.SoundManager;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.uitest.annotation.Device;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.Reflection;
import org.catrobat.catroid.uitest.util.UiTestUtils;
import org.catrobat.catroid.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class SpeakStageTest extends BaseActivityInstrumentationTestCase<ProjectActivity> {

	private final String testText = "Test test.";
	private final long byteLengthOfTestText = 25804L;
	private final File speechFileTestText = new File(Constants.TEXT_TO_SPEECH_TMP_PATH, Utils.md5Checksum(testText)
			+ Constants.TEXT_TO_SPEECH_EXTENSION);

	private final String helloWorldText = "Hello World!";
	private final long byteLengthOfHelloWorldText = 23564L;
	private final File speechFileHelloWorlText = new File(Constants.TEXT_TO_SPEECH_TMP_PATH,
			Utils.md5Checksum(helloWorldText) + Constants.TEXT_TO_SPEECH_EXTENSION);

	private final String simultaneousText = "Speaking simultaneously";
	private final long byteLengthOfSimultaneousText = 51404L;
	private final File speechFileSimultaneousText = new File(Constants.TEXT_TO_SPEECH_TMP_PATH,
			Utils.md5Checksum(simultaneousText) + Constants.TEXT_TO_SPEECH_EXTENSION);

	private final String longText = "This is very very long long test text.";
	private final long byteLengthOfLongText = 69644L;
	private final File speechFileLongText = new File(Constants.TEXT_TO_SPEECH_TMP_PATH, Utils.md5Checksum(longText)
			+ Constants.TEXT_TO_SPEECH_EXTENSION);

	private SoundManagerMock soundManagerMock;

	public SpeakStageTest() throws InterruptedException {
		super(ProjectActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		// normally super.setUp should be called first
		// but kept the test failing due to view is null
		// when starting in ScriptActivity
		UiTestUtils.createEmptyProject();
		super.setUp();
		UiTestUtils.prepareStageForTest();
		deleteSpeechFiles();
		soundManagerMock = new SoundManagerMock();
		Reflection.setPrivateField(SoundManager.class, "INSTANCE", soundManagerMock);
	}

	@Override
	public void tearDown() throws Exception {
		super.tearDown();
		deleteSpeechFiles();
	}

	private void deleteSpeechFiles() {
		File pathToSpeechFiles = new File(Constants.TEXT_TO_SPEECH_TMP_PATH);
		pathToSpeechFiles.mkdirs();
		for (File file : pathToSpeechFiles.listFiles()) {
			file.delete();
		}
	}

	private void prepareStageForTesting(String projectName) {
		try {
			ProjectManager.getInstance().loadProject(projectName, getActivity().getApplicationContext());
			assertTrue("Load project worked correctly", true);
		} catch (ProjectException projectException) {
			fail("Could not load project.");
		}
		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
		solo.waitForActivity(StageActivity.class);
		solo.sleep(1500);
	}

	@Device
	public void testNormalBehaviour() {
		createNormalBehaviourProject();
		prepareStageForTesting(UiTestUtils.PROJECTNAME1);

		assertTrue("speechFileTestText does not exist", speechFileTestText.exists());
		assertFalse("speechFileHelloWorlText already created", speechFileHelloWorlText.exists());
		assertEquals("Length of speechFileTestText is different from original", byteLengthOfTestText,
				speechFileTestText.length());

		assertEquals("Wrong amount of soundfiles played", 1, soundManagerMock.playedSoundFiles.size());
		assertTrue("Wrong soundfile played",
				soundManagerMock.playedSoundFiles.contains(speechFileTestText.getAbsolutePath()));

		solo.sleep(1200);

		assertTrue("speechFileHelloWorlText does not exist", speechFileHelloWorlText.exists());
		assertEquals("Length of speechFileHelloWorlText is different from original", byteLengthOfHelloWorldText,
				speechFileHelloWorlText.length());

		assertEquals("Wrong amount of soundfiles played", 2, soundManagerMock.playedSoundFiles.size());
		assertTrue("Wrong soundfile played",
				soundManagerMock.playedSoundFiles.contains(speechFileHelloWorlText.getAbsolutePath()));
	}

	@Device
	public void testMultiSpeech() {
		createMultiSpeechesProject();
		prepareStageForTesting(UiTestUtils.PROJECTNAME3);
		solo.sleep(1000);

		assertTrue("speechFileLongText does not exist", speechFileLongText.exists());
		assertTrue("speechFileSimultaneousText does not exist", speechFileSimultaneousText.exists());

		assertEquals("Length of speechFileLongText is different from original", byteLengthOfLongText,
				speechFileLongText.length());
		assertEquals("Length of speechFileSimultaneousText is different from original", byteLengthOfSimultaneousText,
				speechFileSimultaneousText.length());

		assertEquals("Wrong amount of soundfiles played", 2, soundManagerMock.playedSoundFiles.size());
		assertTrue("Wrong soundfile played",
				soundManagerMock.playedSoundFiles.contains(speechFileLongText.getAbsolutePath()));
		assertTrue("Wrong soundfile played",
				soundManagerMock.playedSoundFiles.contains(speechFileSimultaneousText.getAbsolutePath()));
	}

	@Device
	public void testDeleteSpeechFiles() {
		createMultiSpeechesProject();
		prepareStageForTesting(UiTestUtils.PROJECTNAME3);
		solo.sleep(2000);

		int currentTry = 0;
		boolean found = false;
		while (++currentTry != 10) {
			if (speechFileLongText.exists()) {
				found = true;
				break;
			}
			solo.sleep(3000);
		}

		currentTry = 0;
		found = false;
		while (++currentTry != 10) {
			if (speechFileSimultaneousText.exists()) {
				found = true;
				break;
			}
			solo.sleep(3000);
		}

		assertTrue("speechFileLongText does not exist", speechFileLongText.exists());
		assertTrue("speechFileSimultaneousText does not exist", speechFileSimultaneousText.exists());

		UiTestUtils.goToHomeActivity(getActivity());
		solo.waitForActivity(MainMenuActivity.class);

		File speechTmpFolder = new File(Constants.TEXT_TO_SPEECH_TMP_PATH);

		currentTry = 0;
		boolean deleted = false;
		while (++currentTry != 10) {
			if (speechTmpFolder.listFiles().length == 0) {
				deleted = true;
				break;
			}
			solo.sleep(1500);
		}

		assertEquals("TextToSpeech folder is not empty", 0,
				speechTmpFolder.listFiles().length);
	}

	private void createNormalBehaviourProject() {
		Sprite spriteNormal = new Sprite("testNormalBehaviour");

		Script startScriptNormal = new StartScript();
		startScriptNormal.addBrick(new SpeakBrick(testText));
		startScriptNormal.addBrick(new WaitBrick(1500));
		startScriptNormal.addBrick(new SpeakBrick(helloWorldText));

		spriteNormal.addScript(startScriptNormal);

		ArrayList<Sprite> spriteListNormal = new ArrayList<Sprite>();
		spriteListNormal.add(spriteNormal);

		UiTestUtils.createProject(UiTestUtils.PROJECTNAME1, spriteListNormal, getActivity().getApplicationContext());
	}

	private void createMultiSpeechesProject() {
		Sprite spriteMultiSpeech = new Sprite("testMultiSpeech");
		Script startScriptMultiSpeech = new StartScript();
		startScriptMultiSpeech.addBrick(new SpeakBrick(longText));
		startScriptMultiSpeech.addBrick(new SpeakBrick(simultaneousText));

		spriteMultiSpeech.addScript(startScriptMultiSpeech);

		ArrayList<Sprite> spriteListMultiSpeech = new ArrayList<Sprite>();
		spriteListMultiSpeech.add(spriteMultiSpeech);

		UiTestUtils.createProject(UiTestUtils.PROJECTNAME3, spriteListMultiSpeech, getActivity()
				.getApplicationContext());
	}

	private class SoundManagerMock extends SoundManager {

		private final Set<String> playedSoundFiles = new HashSet<String>();

		@Override
		public synchronized void playSoundFile(String pathToSoundfile) {
			playedSoundFiles.add(pathToSoundfile);
		}
	}
}
