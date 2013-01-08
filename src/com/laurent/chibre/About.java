/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.laurent.chibre;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Boite de dialogue "A Propos" et "Licence"
 * 
 * @author Laurent Constantin 
 * 
 * Source: https://github.com/FunkyAndroid/markers-for-android-updated/ 
 * License : Apache 2
 */
class About {
	static char buf[] = new char[1024];

	/**
	 * Charge un fichier
	 * 
	 * @param context
	 *            Context
	 * @param filename
	 *            Nom du fichier
	 * @return Contenu du fichier
	 */
	private static String loadFileText(Context context, String filename) {
		try {
			StringBuffer fileData = new StringBuffer();
			final BufferedReader reader = new BufferedReader(
					new InputStreamReader(context.getAssets().open(filename)));
			String line;
			while ((line = reader.readLine()) != null) {
				fileData.append(line);
			}
			return fileData.toString();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Renvoie la version de l'application
	 * 
	 * @param activity
	 *            Activite
	 * @return Version de l'application
	 */
	private static String getVersionString(final Activity activity) {
		String version = "";
		try {
			PackageInfo pi = activity.getPackageManager().getPackageInfo(
					activity.getPackageName(), 0);
			if (pi != null) {
				version = pi.versionName;
			}
		} catch (NameNotFoundException e) {
			// pass
		}
		return version;
	}

	/**
	 * Affiche la boite de dialogue "A Propos"
	 * 
	 * @param activity
	 */
	public static void showAbout(final Activity activity) {
		show(activity, true);
	}

	/**
	 * Affiche la boite de dialogue "Licence"
	 * 
	 * @param activity
	 */
	public static void showLicence(final Activity activity) {
		show(activity, false);
	}

	/**
	 * Affiche "A propos" ou "Licence"
	 * 
	 * @param activity
	 *            L'activite
	 * @param about
	 *            vrai pour "A propos", faux pour "Licence"
	 */
	private static void show(final Activity activity, boolean about) {

		final String WEB = activity.getString(R.string.about_site);
		final String WEB_URL = activity.getString(R.string.about_site_url);
		final String LICENCE = activity.getString(R.string.about_licence);

		String file = "about.html";
		if (!about) {
			file = "licenses.html";
		}

		// Cree une AlertBox
		final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle(null);
		builder.setCancelable(true);

		// Charge le layout
		LayoutInflater inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.about_box, null);

		// Recupere les elements
		TextView title = (TextView) layout.findViewById(R.id.about_title);
		TextView author = (TextView) layout.findViewById(R.id.about_author);
		WebView webview = (WebView) layout.findViewById(R.id.html);
		ImageView logo = (ImageView) layout.findViewById(R.id.about_logo);

		// Change la typo du titre
		Typeface light = Typeface.create("sans-serif-light", Typeface.NORMAL);
		title.setTypeface(light);
		title.setText(activity.getString(R.string.app_name) + " "
				+ getVersionString(activity));

		// Choix des elements a afficher suivant la fonctionnalite choisie
		if (about) {

			webview.setVisibility(View.GONE);
			title.setVisibility(View.VISIBLE);
			logo.setVisibility(View.VISIBLE);
			author.setVisibility(View.VISIBLE);
			title.setText(activity.getString(R.string.app_name) + " "
					+ getVersionString(activity));
		} else {

			logo.setVisibility(View.GONE);
			author.setVisibility(View.GONE);

			webview.setVisibility(View.VISIBLE);
			title.setVisibility(View.GONE);
			webview.setEnabled(true);
			webview.loadDataWithBaseURL("file:///android_asset/",
					loadFileText(activity, file), "text/html", "utf-8", null);
		}
		// Definit la vue
		builder.setView(layout);

		// Ajoute des boutons pour "A Propos"
		if (about) {
			// Ouvre la page web
			builder.setNeutralButton(WEB, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					Intent urlIntent = new Intent(Intent.ACTION_VIEW, Uri
							.parse(WEB_URL));
					activity.startActivity(urlIntent);
				}
			});
			// Ouvre la licence
			builder.setNegativeButton(LICENCE, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					showLicence(activity);
				}
			});
		}
		// Affiche la boite de dialgue cree
		builder.create().show();
	}
}
