/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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
package org.catrobat.catroid.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import org.catrobat.catroid.R;

public class FeedbackDialog extends DialogFragment {
	public static final String TAG = "dialog_feedback";

	@Override
	public Dialog onCreateDialog(Bundle bundle) {
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_feedback, null);
		//View view = View.inflate(getActivity(), R.layout.dialog_feedback, null);
		Dialog rateUsDialog = new AlertDialog.Builder(getActivity()).setView(view).setTitle(getString(R.string
				.dialog_title_feedback))
				.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {

						dialog.dismiss();
						Intent intent = new Intent(Intent.ACTION_SENDTO);
						intent.setData(Uri.parse("mailto:"));
						intent.putExtra(Intent.EXTRA_EMAIL, new String[] { "marketing@catrobat.org" });
						intent.putExtra(Intent.EXTRA_SUBJECT, "[Feedback] Pocket Code Feedback");

						startActivity(Intent.createChooser(intent, "EMail via..."));
					}
				})
				.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						Log.d(TAG, "No Button Clicked! :(");
						dialog.dismiss();
					}
				}).create();

		rateUsDialog.setCanceledOnTouchOutside(true);

		return rateUsDialog;
	}
}
