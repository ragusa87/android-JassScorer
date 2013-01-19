/* LICENSE
 * This work is licensed under the 
 * Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License. 
 * To view a copy of this license, visit 
 * http://creativecommons.org/licenses/by-nc-sa/3.0/.
 * 
 * Copyright (c) 2013 by Laurent Constantin <constantin.laurent@gmail.com>
 */

package com.laurent.chibre;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceClickListener;

/**
 * A {@link PreferenceActivity} to configure team's name and show the about
 * dialog. 
 * @see http://developer.android.com/guide/topics/ui/settings.html.
 */
public class SettingsActivity extends PreferenceActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// TODO Fix all deprecated methods
		addPreferencesFromResource(R.xml.pref_general);

		// Add version number
		final Preference about = findPreference("about");
		about.setTitle(about.getTitle() + " " + About.getVersionString(this));

		// OnClick functions,..
		showLicences();
		showAbout();
		showWebsite();
	}

	/**
	 * Show the webiste
	 */
	private void showWebsite() {
		final Preference website = findPreference("website");
		website.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(final Preference preference) {
				About.openWebsite(SettingsActivity.this);
				return true;
			}
		});
	}

	/**
	 * Show the about dialog
	 */
	private void showAbout() {
		final Preference about = findPreference("about");
		about.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(final Preference preference) {
				About.showAbout(SettingsActivity.this);
				return true;
			}
		});
	}

	/**
	 * Show the licenses
	 */
	private void showLicences() {
		final Preference mOpenSourceLicenses = findPreference("opensource");
		mOpenSourceLicenses
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(final Preference preference) {
						About.showLicence(SettingsActivity.this);
						return true;
					}
				});
	}
}
