/* LICENSE
 * This work is licensed under the 
 * Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License. 
 * To view a copy of this license, visit 
 * http://creativecommons.org/licenses/by-nc-sa/3.0/.
 * 
 * Copyright (c) 2013 by Laurent Constantin <constantin.laurent@gmail.com>
 */

package ch.laurent.chibre;

import com.actionbarsherlock.app.SherlockPreferenceActivity;

import helpers.AboutHelper;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceClickListener;
import ch.laurent.chibre.R;

/**
 * {@link PreferenceActivity} pour la configuration (nom des equipes) et
 * A-Propos
 * 
 * @see http://developer.android.com/guide/topics/ui/settings.html.
 */
public class SettingsActivity extends SherlockPreferenceActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.pref_general);

		// Ajoute la version dans le champ a-propos
		final Preference about = findPreference("about");
		about.setTitle(about.getTitle() + " "
				+ AboutHelper.getVersionString(this));

		// Ecoute du clique
		showLicences();
		showAbout();
		showWebsite();
	}

	/**
	 * Affiche le site
	 */
	private void showWebsite() {
		final Preference website = findPreference("website");
		website.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(final Preference preference) {
				AboutHelper.openWebsite(SettingsActivity.this);
				return true;
			}
		});
	}

	/**
	 * Affiche la boite de dialgue A-Propos
	 */
	private void showAbout() {
		final Preference about = findPreference("about");
		about.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(final Preference preference) {
				AboutHelper.showAbout(SettingsActivity.this);
				return true;
			}
		});
	}

	/**
	 * Affiche les licences
	 */
	private void showLicences() {
		final Preference mOpenSourceLicenses = findPreference("opensource");
		mOpenSourceLicenses
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(final Preference preference) {
						AboutHelper.showLicence(SettingsActivity.this);
						return true;
					}
				});
	}
}
