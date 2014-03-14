/* LICENSE
 * This work is licensed under the 
 * Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License. 
 * To view a copy of this license, visit 
 * http://creativecommons.org/licenses/by-nc-sa/3.0/.
 * 
 * Copyright (c) 2013 by Laurent Constantin <constantin.laurent@gmail.com>
 */
package ch.laurent.helpers;

import ch.laurent.chibre.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
/**
 * Helpeur pour obtenir le nom d'une equipe suivant les preferences
 * @author Laurent Constantin
 *
 */
public class TeamNameHelper {
	// Preferences fields.
	private final static String sPrefTeam1Name = "pref_team_name1";
	private final static String sPrefTeam2Name = "pref_team_name2";
	
	/**
	 * Get a team name
	 * 
	 * @param c Context
	 * @param teamId Team id (1 or 2)
	 * @return Team name
	 */
	public static String getTeamName(Context c,int teamId) {

		// Preference ID
		final String pref_teamName_label = (teamId == 1 ? sPrefTeam1Name
				: sPrefTeam2Name);

		// Default name
		final int id_string = (teamId == 1 ? R.string.team_1 : R.string.team_2);
		final SharedPreferences mPreferences = PreferenceManager
				.getDefaultSharedPreferences(c);

		// Load pref
		String teamName = mPreferences.getString(pref_teamName_label,
				c.getString(id_string));

		// First char as uppercase
		if (teamName.length() >= 2)
			teamName = Character.toUpperCase(teamName.charAt(0))
					+ teamName.substring(1);
		return teamName;
	}
}
