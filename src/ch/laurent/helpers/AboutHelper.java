/* LICENSE
 * This work is licensed under the 
 * Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License. 
 * To view a copy of this license, visit 
 * http://creativecommons.org/licenses/by-nc-sa/3.0/.
 * 
 * Copyright (c) 2013 by Laurent Constantin <constantin.laurent@gmail.com>
 */

package ch.laurent.helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import ch.laurent.chibre.R;

/**
 * Help for dialog box (about, licence,..)
 * 
 * @author Laurent Constantin
 */
public class AboutHelper {
	/**
	 * Load an asset file and return file's content
	 * 
	 * @param mContext Context
	 * @param mFilename filename
	 * @return file's content
	 */
	private static String loadFileText(final Context mContext,
			final String mFilename) {
		try {
			StringBuffer mFileData = new StringBuffer();
			final BufferedReader mReader = new BufferedReader(
					new InputStreamReader(mContext.getAssets().open(mFilename)));
			String line;
			while ((line = mReader.readLine()) != null) {
				mFileData.append(line);
			}
			return mFileData.toString();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Open website
	 * @param mContext Application context
	 */
	public static void openWebsite(Context mContext) {
		final String WEB_URL = mContext.getString(R.string.about_site_url);
		Intent urlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(WEB_URL));
		mContext.startActivity(urlIntent);
	}

	/**
	 * Show the about box.
	 * @param activityContext Context of the activity
	 */
	public static void showAbout(final Context activityContext) {
		final String mWebBoutton = activityContext.getString(R.string.about_site);
		final String mLicenceButton = activityContext
				.getString(R.string.about_licence);
		final String mAboutTitle = activityContext.getString(R.string.menu_about);
	
		// Create alert
		final AlertDialog.Builder builder = new AlertDialog.Builder(activityContext);
		builder.setTitle(mAboutTitle);
		builder.setCancelable(true);
	
		// Load layout
		LayoutInflater inflater = (LayoutInflater) activityContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.about_box, null);
		builder.setView(layout);
	
		// Change title's font
		TextView title = (TextView) layout.findViewById(R.id.about_title);
		Typeface light = Typeface.create("sans-serif-light", Typeface.NORMAL);
		title.setTypeface(light);
		title.setText(activityContext.getString(R.string.app_name) + " "
				+ getVersionString(activityContext));
	
		// Change title (nom + version)
		title.setText(activityContext.getString(R.string.app_name) + " "
				+ getVersionString(activityContext));
	
		// Adding button (website)
		builder.setPositiveButton(mWebBoutton, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				AboutHelper.openWebsite(activityContext);
			}
	
		});
		// Adding button (licence)
		builder.setNegativeButton(mLicenceButton, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				showLicence(activityContext);
			}
		});
	
		// Show the form
		builder.create().show();
	}

	/**
	 * Get application's version
	 * 
	 * @param mContext Activite
	 * @return App version
	 */
	public static String getVersionString(final Context mContext) {
		String version = "";
		try {
			PackageInfo pi = mContext.getPackageManager().getPackageInfo(
					mContext.getPackageName(), 0);
			if (pi != null) {
				version = pi.versionName;
			}
		} catch (NameNotFoundException e) {
			return "";
		}
		return version;
	}

	/**
	 * Show the licence dialog
	 * 
	 * @param mContext
	 */
	
	public static void showLicence(final Context mContext) {
		final String LICENCE_TITLE = mContext.getString(R.string.about_licence);
		final String BOUTTON_OK = mContext.getString(android.R.string.ok);
		final String LICENCE_FILE = "licenses.html";
		
		// Create a dialog
		final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle(LICENCE_TITLE);
		builder.setCancelable(true);
	
		// load layout
		LinearLayout linearLayout = new LinearLayout(mContext);
		WebView webview = new WebView(mContext);
		// load page from asset
		webview.loadDataWithBaseURL("file:///android_asset/",
				loadFileText(mContext, LICENCE_FILE), "text/html", "utf-8",
				null);
		linearLayout.addView(webview);
		builder.setView(linearLayout);
	
		// Set buttons (back)
		builder.setNeutralButton(BOUTTON_OK, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
	
		// Show form
		builder.create().show();
	}
}
