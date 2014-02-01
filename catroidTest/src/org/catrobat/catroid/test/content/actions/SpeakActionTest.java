/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
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
package org.catrobat.catroid.test.content.actions;

import android.test.AndroidTestCase;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.content.actions.SpeakAction;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.SpeakBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.test.utils.Reflection;

public class SpeakActionTest extends AndroidTestCase {

	private Sprite sprite;
	private Formula text;
	private Formula text2;
	private Formula textString;

	@Override
	protected void setUp() throws Exception {
		sprite = new Sprite("testSprite");
		text = new Formula(666);
		text2 = new Formula(888.88);
		textString = new Formula("hello world!");
		super.setUp();
	}

	public void testSpeak() {
		SpeakBrick speakBrick = new SpeakBrick(sprite, text);
		SpeakAction action = ExtendedActions.speak(text, sprite);
		Formula textAfterExecution = (Formula) Reflection.getPrivateField(action, "text");

		assertEquals("Text is not updated after SpeakBrick executed", text, speakBrick.getFormula());
		assertEquals("Text is not updated after SpeakBrick executed", text, textAfterExecution);
		speakBrick = new SpeakBrick(sprite, text2);
		action = ExtendedActions.speak(text, sprite);
		textAfterExecution = (Formula) Reflection.getPrivateField(action, "text");

		assertEquals("Text is not updated after SpeakBrick executed", text2, speakBrick.getFormula());
		assertEquals("Text is not updated after SpeakBrick executed", text, textAfterExecution);
	}

	public void testNullSprite() {
		SpeakBrick speakBrick = new SpeakBrick(null, text);
		SpeakAction action = ExtendedActions.speak(text, sprite);
		try {
			action.act(1.0f);
			fail("Execution of ShowBrick with null Sprite did not cause a NullPointerException to be thrown");
		} catch (NullPointerException expected) {
			return;
		}
		assertEquals("Stored wrong text in speak brick", text, speakBrick.getFormula());
	}

	public void testRequirements() {
		SpeakBrick speakBrick = new SpeakBrick(sprite, new Formula(""));
		assertEquals("Wrong required brick resources", Brick.TEXT_TO_SPEECH, speakBrick.getRequiredResources());
	}

	public void testBrickWithStringFormula() {
		SpeakBrick speakBrick = new SpeakBrick(sprite, textString);
		SpeakAction action = ExtendedActions.speak(textString, sprite);
		Reflection.invokeMethod(action, "begin");

		assertEquals("Text is not updated after SpeakBrick executed", textString, speakBrick.getFormula());
		assertEquals("Text is not updated after SpeakBrick executed", textString.interpretString(sprite),
				String.valueOf(Reflection.getPrivateField(action, "interpretedText")));
	}

	public void testNullFormula() {
		SpeakAction action = ExtendedActions.speak((Formula) null, sprite);
		Reflection.invokeMethod(action, "begin");

		assertEquals("Text is not updated after SpeakBrick executed", "",
				String.valueOf(Reflection.getPrivateField(action, "interpretedText")));
	}

	public void testNotANumberFormula() {
		SpeakAction action = ExtendedActions.speak(new Formula(Double.NaN), sprite);
		Reflection.invokeMethod(action, "begin");

		assertEquals("Text is not updated after SpeakBrick executed", "",
				String.valueOf(Reflection.getPrivateField(action, "interpretedText")));
	}
}
