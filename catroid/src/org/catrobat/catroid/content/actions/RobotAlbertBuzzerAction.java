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
package org.catrobat.catroid.content.actions;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.robot.albert.RobotAlbert;

public class RobotAlbertBuzzerAction extends TemporalAction {
	private static final int MIN = 0;
	private static final int MAX = 100;

	private Formula value;
	private Sprite sprite;

	@Override
	protected void update(float percent) {

		int loudness = value.interpretInteger(sprite);
		if (loudness < MIN) {
			loudness = MIN;
		} else if (loudness > MAX) {
			loudness = MAX;
		}

		//Log.d("RobotAlbert", "RobotAlbertBuzzerAction before send: value=" + value);
		RobotAlbert.sendRobotAlbertBuzzerMessage(loudness);
		//Log.d("RobotAlbert", "RobotAlbertBuzzerAction after sended");
	}

	public void setValue(Formula value) {
		this.value = value;
	}

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}

}
