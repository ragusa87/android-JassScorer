/* LICENSE
 * This work is licensed under the 
 * Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License. 
 * To view a copy of this license, visit 
 * http://creativecommons.org/licenses/by-nc-sa/3.0/.
 * 
 * Copyright (c) 2013 by Laurent Constantin <constantin.laurent@gmail.com>
 */
package helpers;

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
	// Noms des champs de preferences
	private final static String sPrefTeam1Name = "pref_team_name1";
	private final static String sPrefTeam2Name = "pref_team_name2";
	
	/**
	 * Obtient le nom d'une equipe
	 * 
	 * @param teamId Le numero de l'equipe (1 ou 2)
	 * @return Le nom de l'equipe
	 */
	public static String getTeamName(Context c,int teamId) {

		// Preference ID
		final String pref_teamName_label = (teamId == 1 ? sPrefTeam1Name
				: sPrefTeam2Name);

		// Nom de l'equipe par default
		final int id_string = (teamId == 1 ? R.string.team_1 : R.string.team_2);
		final SharedPreferences mPreferences = PreferenceManager
				.getDefaultSharedPreferences(c);

		// Recupere le nom de l'equipe depuis les preferences
		String teamName = mPreferences.getString(pref_teamName_label,
				c.getString(id_string));

		// Premier caractere en majuscule
		if (teamName.length() >= 2)
			teamName = Character.toUpperCase(teamName.charAt(0))
					+ teamName.substring(1);
		return teamName;
	}
}
