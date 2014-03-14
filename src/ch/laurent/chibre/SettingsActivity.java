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
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.Preference.OnPreferenceClickListener;
import ch.laurent.chibre.R;
import ch.laurent.helpers.AboutHelper;
import ch.laurent.helpers.TeamNameHelper;

/**
 * {@link PreferenceActivity} For team's configuration (team's name) and the about dialog
 * This code use fragment for api >= 11, or old school way for older devices.
 * The code is duplicated between the two API and it is ugly
 * 
 * http://developer.android.com/guide/topics/ui/settings.html.
 */
public class SettingsActivity extends SherlockPreferenceActivity implements
		OnSharedPreferenceChangeListener {
	/**
	 * Tell if device support fragment They are supported since android
	 * HONEYCOMB (v11)
	 * 
	 * @return true if fragments are supported
	 */
	private boolean supportFragment() {
		try {
			getClass().getMethod("getFragmentManager");
			return true;
		} catch (NoSuchMethodException e) {
			return false;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (supportFragment()) {
			// Add preferences as a frament
			AddResourceApi11AndGreater();
		} else {
			// Use old school preferences
			AddResourceApiLessThan11();
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onResume() {
		super.onResume();
		// Listener to change the teams names
		if (!supportFragment())
			getPreferenceScreen().getSharedPreferences()
					.registerOnSharedPreferenceChangeListener(this);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onPause() {
		super.onPause();
		// Stop listening on teams names
		if (!supportFragment())
			getPreferenceScreen().getSharedPreferences()
					.unregisterOnSharedPreferenceChangeListener(this);
	}

	@SuppressWarnings("deprecation")
	// Old api
	protected void AddResourceApiLessThan11() {
		addPreferencesFromResource(R.xml.pref_general);
		final Context appContext = this;
		setAboutSection(appContext, findPreference("about"));
		showLicences(appContext, findPreference("opensource"));
		showAbout(appContext, findPreference("about"));
		showWebsite(appContext, findPreference("website"));
		setTeamsSummary(appContext, findPreference("pref_team_name1"),
				findPreference("pref_team_name2"));
		onSharedPreferenceChanged(null, "");
	}

	@TargetApi(11)
	protected void AddResourceApi11AndGreater() {
		// Add the fragment
		getFragmentManager().beginTransaction()
				.replace(android.R.id.content, new MyPreferenceFragment())
				.commit();

	}

	/**
	 * Set the about preferences with app_name
	 * @param context Activity context
	 * @param about The about preference
	 */
	private static void setAboutSection(final Context context,
			final Preference about) {
		about.setTitle(context.getString(R.string.app_name) + " "
				+ AboutHelper.getVersionString(context));
		about.setSummary(context.getString(R.string.app_author));
	}

	/**
	 * Show the website
	 */
	private static void showWebsite(final Context context,
			final Preference website) {
		website.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(final Preference preference) {
				AboutHelper.openWebsite(context);
				return true;
			}
		});
	}

	/**
	 * Set the teams summary with team's names
	 * @param context Activity context
	 * @param team1 Team 1 pref
	 * @param team2 Team 2 pref
	 */
	private static void setTeamsSummary(final Context context,
			final Preference team1, final Preference team2) {
		// Team name is never empty or null.
		team1.setSummary(TeamNameHelper.getTeamName(context, 1));
		team2.setSummary(TeamNameHelper.getTeamName(context, 2));
	}

	/**
	 * Show the about dialog
	 */
	private static void showAbout(final Context activityContext,
			final Preference about) {

		about.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(final Preference preference) {
				AboutHelper.showAbout(activityContext);
				return true;
			}
		});
	}

	/**
	 * Show the licences
	 */
	private static void showLicences(final Context context,
			final Preference mOpenSourceLicenses) {
		mOpenSourceLicenses
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(final Preference preference) {
						AboutHelper.showLicence(context);
						return true;
					}
				});
	}

	/**
	 * Fragment used to manage preferences
	 */
	@TargetApi(11)
	public static class MyPreferenceFragment extends PreferenceFragment
			implements OnSharedPreferenceChangeListener {
		@Override
		public void onCreate(final Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.pref_general);

			final Context c = getActivity(); // use app context here.
			SettingsActivity.setAboutSection(c, findPreference("about"));
			SettingsActivity.showLicences(c, findPreference("opensource"));
			SettingsActivity.showAbout(c, findPreference("about"));
			SettingsActivity.showWebsite(c, findPreference("website"));
			
			SettingsActivity.setTeamsSummary(c,
					findPreference("pref_team_name1"),
					findPreference("pref_team_name2"));
		}

		@Override
		public void onResume() {
			super.onResume();
			// Set up a listener whenever a key changes
			getPreferenceScreen().getSharedPreferences()
					.registerOnSharedPreferenceChangeListener(this);
		}

		@Override
		public void onPause() {
			super.onPause();
			// Set up a listener whenever a key changes
			getPreferenceScreen().getSharedPreferences()
					.unregisterOnSharedPreferenceChangeListener(this);
		}

		@Override
		/**
		 * Update the teams name on pref changes
		 */
		public void onSharedPreferenceChanged(
				SharedPreferences sharedPreferences, String key) {
			final Preference team1 = findPreference("pref_team_name1");
			final Preference team2 = findPreference("pref_team_name2");
			if (team1 == null || team2 == null)
				return;

			SettingsActivity.setTeamsSummary(getActivity(), team1, team2);

		}
	}

	/**
	 * Update the teams name on pref changes
	 */
	@Override
	@SuppressWarnings("deprecation")
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		// API level < 11 only
		Preference team1 = findPreference("pref_team_name1");
		Preference team2 = findPreference("pref_team_name2");
		if (team1 != null & team2 != null)
			setTeamsSummary(this, team1, team2);

	}
}
