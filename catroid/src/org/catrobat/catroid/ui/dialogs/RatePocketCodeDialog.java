/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import org.catrobat.catroid.R;

public class RatePocketCodeDialog extends DialogFragment {
	public static final String TAG = "dialog_rate_pocketcode";

	@Override
	public Dialog onCreateDialog(Bundle bundle) {
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_rate_pocketcode, null);

		Dialog rateUsDialog = new AlertDialog.Builder(getActivity()).setView(view).setTitle(getString(R.string.dialog_title_rate))
				.setPositiveButton(R.string.rate_now, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						try {
							startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="
									+ getActivity().getPackageName())).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
						} catch (android.content.ActivityNotFoundException anfe) {
							startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id="
									+ getActivity().getPackageName())));
						}
						Log.d(TAG, "Yes Button Clicked! :)");

					}
				})
				.setNeutralButton(getString(R.string.rate_later), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						Log.d(TAG, "neutral Button Clicked! :|");
					}
				})

				.setNegativeButton(getString(R.string.rate_never), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						Log.d(TAG, "Never Button Clicked! :C");

					}
				}).create();

		rateUsDialog.setCanceledOnTouchOutside(true);

		return rateUsDialog;
	}
}
