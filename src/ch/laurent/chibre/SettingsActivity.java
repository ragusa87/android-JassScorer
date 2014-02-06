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
 * {@link PreferenceActivity} pour la configuration (nom des equipes) et
 * A-Propos
 * 
 * @see http://developer.android.com/guide/topics/ui/settings.html.
 */
public class SettingsActivity extends SherlockPreferenceActivity implements OnSharedPreferenceChangeListener {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			getClass().getMethod("getFragmentManager");
			AddResourceApi11AndGreater();
		} catch (NoSuchMethodException e) { // Api < 11
			AddResourceApiLessThan11();
		}
	}


	@SuppressWarnings("deprecation")
	// Old api
	protected void AddResourceApiLessThan11() {
		addPreferencesFromResource(R.xml.pref_general);
		final Context activityContext = this;
		setAboutSection(activityContext, findPreference("about"));
		showLicences(activityContext, findPreference("opensource"));
		showAbout(activityContext, findPreference("about"));
		showWebsite(activityContext, findPreference("website"));
		setTeamsSummary(activityContext, findPreference("pref_team_name1"),
				findPreference("pref_team_name2"));
		getPreferenceManager().getSharedPreferences()
		.registerOnSharedPreferenceChangeListener(this);

	}

	@TargetApi(11)
	protected void AddResourceApi11AndGreater() {
		getFragmentManager().beginTransaction()
				.replace(android.R.id.content, new MyPreferenceFragment())
				.commit();

	}

	private static void setAboutSection(final Context context,
			final Preference about) {
		about.setTitle(context.getString(R.string.app_name) + " "
				+ AboutHelper.getVersionString(context));
		about.setSummary(context.getString(R.string.app_author));
	}

	/**
	 * Affiche le site
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

	private static void setTeamsSummary(final Context context,
			final Preference team1, final Preference team2) {
		// Team name is never empty or null.
		team1.setSummary(TeamNameHelper.getTeamName(context, 1));
		team2.setSummary(TeamNameHelper.getTeamName(context, 2));
	}

	/**
	 * Affiche la boite de dialgue A-Propos
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
	 * Affiche les licences
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

	@TargetApi(11)
	public static class MyPreferenceFragment extends PreferenceFragment
			implements OnSharedPreferenceChangeListener {
		@Override
		public void onCreate(final Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			getPreferenceManager().getSharedPreferences()
					.registerOnSharedPreferenceChangeListener(this);
	
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
		public void onSharedPreferenceChanged(
				SharedPreferences sharedPreferences, String key) {
			final Preference team1 = findPreference("pref_team_name1");
			final Preference team2 = findPreference("pref_team_name2");
			if (team1 == null || team2 == null)
				return;
	
			SettingsActivity.setTeamsSummary(getActivity(), team1, team2);
	
		}
	}

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
