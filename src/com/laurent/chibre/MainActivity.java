package com.laurent.chibre;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

// TODO Ajouter un log/graph des dernieres actions
// TODO Forcer le choix du rating apres chaque saisie

public class MainActivity extends Activity implements OnRatingBarChangeListener {
	// Valeurs par defaut
	private final int INIT_RATING = 1;
	private final int TOUT_ATOUT_RATING = 6;
	// Position de l'element XX dans le menu (0..n)
	private final int MENU_CANCEL_POSITION = 0;
	private final int MENU_RESET_POSITION = MENU_CANCEL_POSITION + 1;

	// Noms des champs de sauvegarde
	private final String SAVE_SCORESTACK = "scoreStack";
	private final String SAVE_RATING = "rating";

	// Noms des champs de preferences
	private final String PREF_TEAM1_NAME = "pref_team_name1";
	private final String PREF_TEAM2_NAME = "pref_team_name2";

	// Nombre de points en cas de match
	private final int BONUS = 100;
	private final int MATCH_VALUE = 157;
	private final int MATCH_TOUTATOUT_VALUE = 253;

	// Score des equipes
	private ScoreStack score = new ScoreStack();

	// Coefficient de la partie (P.ex: 2x pour pique double)
	private int coefficient = 1;

	// Elements de controles
	private RatingBar ratingBar;
	EditText input_score1;
	EditText input_score2;

	/**
	 * Lorsque l'activite est cree.
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Gere la bare de "rating"
		ratingBar = (RatingBar) findViewById(R.id.multiply);
		ratingBar.setOnRatingBarChangeListener(this);
		ratingBar.setRating(INIT_RATING);

		// Recupere les champs de saisie
		input_score1 = (EditText) findViewById(R.id.input_score1);
		input_score2 = (EditText) findViewById(R.id.input_score2);

		// Verification des saisies a la volee
		input_score1.addTextChangedListener(new TextWatcherAdapter());
		input_score2.addTextChangedListener(new TextWatcherAdapter());
		textChangedListener();

	}

	/**
	 * Lorsque l'application est interompue, on sauve le score
	 */
	public void onPause() {
		super.onPause();

		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		SharedPreferences.Editor ed = sharedPrefs.edit();
		// Valeurs a sauver
		ed.putString(SAVE_SCORESTACK, score.saveAsString());
		ed.putFloat(SAVE_RATING, ratingBar.getRating());
		// Sauve
		ed.commit();
	}

	/**
	 * Lorsque l'application est reprise, on restaure le score
	 */
	public void onResume() {
		super.onResume();

		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		// Valeur a restaurer
		// 0,0 est equivalent a new ScoreStack().saveAsString();
		// il s'agit de la valeur par defaut
		String temp = sharedPrefs.getString(SAVE_SCORESTACK, "0,0");
		score = ScoreStack.restoreFromString(temp);

		ratingBar.setRating(sharedPrefs.getFloat(SAVE_RATING, INIT_RATING));

		// Raffraichit la vue
		displayScore();
		textChangedListener();
	}

	/**
	 * Cree le menu contextuel
	 * 
	 * @param le menu
	 * @return true;
	 * @Override
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	/**
	 * Active ou desactive les options du menu a la volee
	 */
	public boolean onPrepareOptionsMenu(Menu menu) {
		final boolean enable = score.isCancellable();
		menu.getItem(MENU_CANCEL_POSITION).setEnabled(enable);
		menu.getItem(MENU_RESET_POSITION).setEnabled(enable);
		return true;
	}

	/**
	 * Lors de la selection d'un element du menu
	 * @param item L'element selectionne
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// Reset
		case R.id.menu_reset:
			// Confirmation pour reset()
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(getString(R.string.score_reset_confirm))
					.setCancelable(true)
					.setPositiveButton(getString(android.R.string.yes),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									// Reset
									reset();
									dialog.cancel();
								}
							})
					.setNegativeButton(getString(android.R.string.no),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									// Annule le reset
									dialog.cancel();
								}
							});
			AlertDialog alert = builder.create();
			alert.show();
			return true;
		case R.id.menu_settings:
			// Lance les parametres
			startActivity(new Intent(this, SettingsActivity.class));
			return true;
		case R.id.menu_cancel:
			// Annule la derniere saisie
			score.cancel();
			// Affiche le nouveau score
			displayScore();
			return true;
		default:
			return false;
		}
	}

	/**
	 * Appelee lorsqu'on change le type de jeu. Modifie le coefficient des
	 * points et mets a jour la legende
	 * 
	 * @Override
	 * @param ratinBar La barre de rating
	 * @param rating La valeur de la barre de rating
	 * @param fromUser Indique si la mise a jour est faite par l'utilisateur
	 */
	public void onRatingChanged(RatingBar ratingBar, float rating,
			boolean fromUser) {
		final TextView txt_legend = (TextView) findViewById(R.id.multiplyLegend);
		final String[] legends = getResources().getStringArray(
				R.array.type_array);
		// Index du coefficient suivant le rating
		int i = (int) rating - 1;
		// Force un rating a une etoile minimum
		if (i < 0) {
			i = 0;
			ratingBar.setRating(1);
		}
		// Le coefficient se definit suivant le nombre d'etoiles.
		// Voir dans res/values/integers.xml,
		coefficient = getResources().getIntArray(R.array.coeff)[i];
		txt_legend.setText(legends[i] + " x" + coefficient);
	}

	/**
	 * Ajoute une annonce a une equipe
	 * 
	 * @param team L'equipe (1/2)
	 * @param points Le nombre de points de l'annonce
	 */
	private void addAnnounce(final int team, final int points, final int coeff) {
		score.addAnnounce(team, points * coeff);
		displayScore();
	}

	/**
	 * Ajoute des points aux equipes. Passer toutes les valeurs en x1 et ajuster
	 * le coefficient si besoin
	 * 
	 * @param team L'ID de l'equipe
	 * @param point Points de l'equipe
	 * @param max Nombre de points maximum de la partie (sans le bonus de match)
	 * @param coeff Coefficient de la partie (P.ex 2 pour pique double)
	 */
	private void addPoints(final int team, int point, int max, final int coeff) {
		// Multiplie les points suivant le coeficient
		max *= coeff;
		point *= coeff;
		int sign = 1;
		// Un score negatif enleve les points aux deux equipes
		if (point < 0) {
			sign = -1;
			point = Math.abs(point);
		}

		// Le reste est pour l'autre equipe
		int rest = max - point;
		// Si le reste est negatif (on a un match)
		if (rest < 0)
			rest = 0;
		// Si le score est de 0, l'autre equipe a un match
		if (point == 0)
			rest = max + BONUS * coeff;

		// On s'assure qu'avec un score negatif, on corrige le score des 2 equipes
		point *= sign;
		rest *= sign;

		// Si on a saisit le score de l'equipe 1, l'equipe 2 a le reste
		if (team == 1) {
			score.add(point, rest);
		} else { // Et vice-versa
			score.add(rest, point);
		}

		// Affiche le score
		displayScore();
	}

	/**
	 * Efface le score et le type de jeu
	 */
	private void reset() {
		score.reset();
		displayScore();
		ratingBar.setRating(INIT_RATING);
	}

	/**
	 * Affiche le score des deux equipes, avec le nom tire des preferences.
	 */
	private void displayScore() {
		final Resources r = getResources();

		SharedPreferences mPreferences = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());

		int id_string, id_label, points;
		String pref_teamName_label;

		// Iterate for team 1 and 2
		for (int team = 1; team <= 2; team++) {
			// Score
			points = score.getScore(team);
			// Default team name
			id_string = (team == 1 ? R.string.team_1 : R.string.team_2);
			// TextView
			id_label = (team == 1 ? R.id.team1 : R.id.team2);
			// Preference ID
			pref_teamName_label = (team == 1 ? PREF_TEAM1_NAME
					: PREF_TEAM2_NAME);

			// Get the team name form pref, or use default value
			String teamName = mPreferences.getString(pref_teamName_label,
					getString(id_string));

			// Uppercase the first char
			if (teamName.length() >= 2)
				teamName = Character.toUpperCase(teamName.charAt(0))
						+ teamName.substring(1);

			// Set score for current team.
			TextView txt = (TextView) findViewById(id_label);
			String displayScore = teamName + " : ";
			displayScore += r.getQuantityString(R.plurals.points, points,
					points);
			txt.setText(displayScore);
		}
	}

	/**
	 * Verifie les textes saisits..
	 * 
	 * @return true si valide, false sinon
	 */
	public boolean validInput() {
		// Verifie les champs
		String error = "";
		// Les 2 sont vides
		if (input_score1.getText().toString().equals("")
				&& input_score2.getText().toString().equals("")) {
			error = getString(R.string.error_empty);
		}
		// Les 2 sont pleins (plus possible, mais bon...)
		if (!input_score1.getText().toString().equals("")
				&& !input_score2.getText().toString().equals("")) {
			error = getString(R.string.error_full);
		}
		// Affiche l'erreur si il y en a
		if (!error.equals("")) {
			Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG)
					.show();
			return false;
		}
		// Les saisies sont valides,..
		// Attention toutefois a pouvoir caster le string en int
		return true;
	}

	/**
	 * Action lorsqu'on clique sur un des boutons "Annonce" ou "Calculer.."
	 * @param v Le bouton clique
	 */
	public void onClick(View v) {
		// Si les saisies ne sont pas valides, rien a faire
		if (!validInput()) {
			return;
		}


		// Recuperer les points de l'equipe
		int point = 0;
		// Choix du champ 1 ou 2.
		final boolean team1 = !input_score1.getText().toString().equals("");
		EditText input = (team1 ? input_score1 : input_score2);
		// Parse la saisie en int
		try {
			point = Integer.parseInt(input.getText().toString());
		} catch (NumberFormatException e) {
			// Erreur (overflow,.. etc)
			Toast.makeText(getApplicationContext(),
					getString(R.string.error_format), Toast.LENGTH_LONG).show();
			input_score1.setText("");
			input_score2.setText("");
			return;
		}

		/* calculs les points de l'equipe */
		// Regarde si on a tout atout
		final boolean toutAtout = ratingBar.getRating() == TOUT_ATOUT_RATING;
		
		// Points maximum
		// En tout atout, la partie vaut plus de points
		final int max = (toutAtout?MATCH_TOUTATOUT_VALUE:MATCH_VALUE);
		
		// Erreur, les points sont trop eleves (accepte le bonus de match)
		if (v.getId() == R.id.btn_score && point > max && point != max + BONUS) {

			Toast.makeText(getApplicationContext(),
					getString(R.string.error_high_value), Toast.LENGTH_LONG)
					.show();
			return;
		}

		// Plus d'erreur,... on se charge des saisies
		input_score1.setText("");
		input_score2.setText("");

		/* Ajoute les points comme une annonce */
		if (v.getId() == R.id.btn_announcement) {
			addAnnounce((team1 ? 1 : 2), point, coefficient);
			return;
		}
		/* Ajoute les points comme score */
		addPoints((team1 ? 1 : 2), point, max, coefficient);
	}

	/**
	 * Gere les boutons lors de la saisie. (Couleur, Inhibation) S'assure que
	 * les saisies de score peuvent se faire pour l'equipe 1 ou l'equipe 2.
	 * Change la couleur du bouton "Annonce" suivant l'equipe.
	 */
	public void textChangedListener() {
		// Si les inputs 1 et 2 sont vides
		final boolean isEmpty1 = input_score1.getText().toString().equals("");
		final boolean isEmpty2 = input_score2.getText().toString().equals("");
		// Recupere les boutons
		final Button btn_score = (Button) findViewById(R.id.btn_score);
		final Button btn_announcement = (Button) findViewById(R.id.btn_announcement);
	
		// Active les boutons
		btn_score.setEnabled(true);
		btn_announcement.setEnabled(true);
	
		// Style des boutons par default
		int style_an = (isEmpty2 ? R.style.Team1 : R.style.Team2);
		int style_score = R.style.TeamAll;
	
		// Si les 2 champs de saisie sont vide ou pleins,
		// on desactive les boutons
		if ((isEmpty1 && isEmpty2) || (!isEmpty1 && !isEmpty2)) {
			btn_score.setEnabled(false);
			btn_announcement.setEnabled(false);
			style_an = style_score = R.style.TeamNone;
		}
		// Active la saisie du score pour une seule equipe a la fois
		input_score1.setEnabled(isEmpty2);
		input_score2.setEnabled(isEmpty1);
	
		// Applique le style sur les boutons
		btn_score.setTextAppearance(getApplicationContext(), style_score);
		btn_announcement.setTextAppearance(getApplicationContext(), style_an);
	
	}

	/**
	 * Lors de la saisie des scores, on verifie les boutons a activer.
	 */
	class TextWatcherAdapter implements TextWatcher {

		@Override
		public void afterTextChanged(Editable arg0) {
			textChangedListener();
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}
	}
}
