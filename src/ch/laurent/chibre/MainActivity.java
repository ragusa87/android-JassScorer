/* LICENSE
 * This work is licensed under the 
 * Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License. 
 * To view a copy of this license, visit 
 * http://creativecommons.org/licenses/by-nc-sa/3.0/.
 * 
 * Copyright (c) 2013 by Laurent Constantin <constantin.laurent@gmail.com>
 */

package ch.laurent.chibre;

import java.util.Observable;

import ch.laurent.helpers.TeamNameHelper;
import ch.laurent.scoreManager.ScoreGraph;
import ch.laurent.scoreManager.ScoreStack;

import com.actionbarsherlock.app.SherlockActivity;

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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

/**
 * Main activity, allow user to input a score for a team while playing a party
 * of Jass. The application will calculate the score for each team depending on
 * the game type and coefficient. The score is displayed with a graph.
 * 
 * @author Laurent Constantin
 * 
 */
public class MainActivity extends SherlockActivity implements
		OnRatingBarChangeListener {
	// Default values
	private final static int sInitRating = 1;
	private final static int sAllAssetRating = 6;
	// Position of element XX into the menu (0..n)
	private final static int sMenuCancelPosition = 0;
	private final static int sMenuResetPosition = sMenuCancelPosition + 1;

	// Backup fields
	private final static String sSaveScoreStack = "scoreStack";
	private final static String sSaveRating = "rating";

	// Number of points for a match
	private final static int sBonus = 100;
	private final static int sMatch = 157;
	private final static int sMatchAllAsset = 253;

	// Team's scores
	private ScoreStack mScore = new ScoreStack();

	// Graphs
	private ScoreGraph mGraph;
	private Thread mGraphWorker;
	// Game coefficient (F.ex: 2x for double spade)
	private int mCoefficient = 1;

	// Inputs elements
	private RatingBar mRatingBar;
	private EditText mInputScore1;
	private EditText mInputScore2;

	/**
	 * Creating activity
	 * 
	 * @param savedInstanceState
	 *            Bundle for saving state
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Manage the rating bar
		mRatingBar = (RatingBar) findViewById(R.id.multiply);
		mRatingBar.setOnRatingBarChangeListener(this);
		mRatingBar.setRating(sInitRating);

		// Input fields
		mInputScore1 = (EditText) findViewById(R.id.input_score1);
		mInputScore2 = (EditText) findViewById(R.id.input_score2);

		// Check input on the fly
		mInputScore1.addTextChangedListener(new TextWatcherAdapter());
		mInputScore2.addTextChangedListener(new TextWatcherAdapter());
		textChangedListener();

		// Graphics
		mGraph = new ScoreGraph(this, mScore) {
			public void update(Observable observable, Object data) {
				// On score change, update graphic and score
				graphUpdate();
				displayScore();
			}
		};
		// Init graphic and score
		graphUpdate();
		displayScore();
	}

	/**
	 * Mise a jour du graphique
	 */
	public void graphUpdate() {
		if(false)
			return;
		
		// Update actionbar
		supportInvalidateOptionsMenu();

		// If the score is not empty, display it.
		if (mScore.isCancellable()) {
			final Activity activity = this;
			// In a new thread, do :
			// - layout.removeAllViews();
			// - layout.addView(mGraph.getView());
			waitForGraphicWorker();
			mGraphWorker = new Thread(new Runnable() {

				@Override
				public void run() {
					final View v = mGraph.getView();
					final LinearLayout layout = (LinearLayout) findViewById(R.id.layoutGraph);
					layout.postInvalidate();
					activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							// Remove previous views
							synchronized (activity) {
								layout.removeAllViews();
								layout.addView(v);
								layout.invalidate();
							}
						}
					});
				}
			});
			mGraphWorker.start();
		}else{
			Log.v("JassScorer","No need to display graph");
		}
	}

	/**
	 * Saving score on pause
	 */
	public void onPause() {
		super.onPause();

		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		SharedPreferences.Editor ed = sharedPrefs.edit();
		// Valeurs a sauver
		ed.putString(sSaveScoreStack, mScore.saveAsString());
		ed.putFloat(sSaveRating, mRatingBar.getRating());
		// Sauve
		ed.commit();
		
		waitForGraphicWorker();
	}

	/**
	 * Restoring score on pause
	 */
	public void onResume() {
		super.onResume();

		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		// Valeur a restaurer
		// 0,0 est equivalent a new ScoreStack().saveAsString();
		// il s'agit de la valeur par defaut
		String temp = sharedPrefs.getString(sSaveScoreStack, "0,0");
		// La pile est remplacee
		mScore.restoreFromString(temp);
		mRatingBar.setRating(sharedPrefs.getFloat(sSaveRating, sInitRating));

		// Raffraichit la vue
		graphUpdate();
		textChangedListener();
	}

	/**
	 * Create contextual menu
	 * 
	 * @param menu
	 *            menu
	 * @return true;
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_main, menu);

		return true;
	}

	/**
	 * Enable or disable menu options on the fly
	 * 
	 * @param menu
	 *            le menu
	 * @return true;
	 */
	public boolean onPrepareOptionsMenu(Menu menu) {
		final boolean enable = mScore.isCancellable();
		// Cancel and reset are enable if there is a score
		menu.getItem(sMenuCancelPosition).setEnabled(enable).setVisible(enable);
		menu.getItem(sMenuResetPosition).setEnabled(enable).setVisible(enable);
		return true;
	}

	/**
	 * On item selected from the menu
	 * 
	 * @param item
	 *            Selected item
	 * @return true If item is managed
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		final int id = item.getItemId();
		// Reset
		if (id == R.id.menu_reset) {
			// Confirm for reset()
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(getString(R.string.score_reset_confirm))
					.setCancelable(true)
					.setTitle(getString(R.string.score_reset_confirm_title))
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
									// Cancel reset
									dialog.cancel();
								}
							});
			builder.create().show();
			return true;
		} else if (id == R.id.menu_settings) {
			// Settings activity
			startActivity(new Intent(this, SettingsActivity.class));
			return true;
		} else if (id == R.id.menu_cancel) {
			// Cancel the last score
			mScore.cancel();
			return true;
		}
		return false;

	}

	/**
	 * Called when we change the game type. Changing coefficient and update
	 * legend
	 * 
	 * @Override
	 * @param ratingBar
	 *            Rating bar
	 * @param rating
	 *            Rating value
	 * @param fromUser
	 *            Tell if the update is made by user choice.
	 */
	public void onRatingChanged(RatingBar ratingBar, float rating,
			boolean fromUser) {
		final TextView txt_legend = (TextView) findViewById(R.id.multiplyLegend);
		final String[] legends = getResources().getStringArray(
				R.array.type_array);
		// Coefficient index depending on the rating value
		int i = (int) rating - 1;
		// Avoid negative index.
		if (i < 0) {
			i = 0;
			ratingBar.setRating(1);
		}
		// The coefficient is set, see res/values/integers.xml for possible
		// values.
		mCoefficient = getResources().getIntArray(R.array.coeff)[i];
		txt_legend.setText(legends[i] + " x" + mCoefficient);
	}

	/**
	 * Add point to both team *
	 * 
	 * @param team
	 *            Team ID
	 * @param point
	 *            Points for the team specified with the ID
	 * @param max
	 *            Number of maximum points for a match (without bonus)
	 * @param coeff
	 *            Game coefficient
	 */
	private void addPoints(final int team, int point, int max, final int coeff) {
		// Handling coeff
		max *= coeff;
		point *= coeff;

		// The rest if for the other team
		int rest = max - point;
		// With a negative rest, is a match
		if (rest < 0) {
			rest = 0;
		}
		// If score is 0, the other team win a match
		if (point == 0) {
			rest = max + sBonus * coeff;
		}
		// Add point to both teams.
		if (team == 1) {
			mScore.addAndNotify(point, rest);
		} else { // And vice-versa
			mScore.addAndNotify(rest, point);
		}
	}

	/**
	 * Reset score and game type
	 */
	private void reset() {
		mRatingBar.setRating(sInitRating);
		mScore.reset();
	}

	/**
	 * Show the two team's score and name
	 */
	private void displayScore() {
		final Resources r = getResources();

		int id_label, points;

		// Iterate for team 1 and 2
		for (int team = 1; team <= 2; team++) {
			// Score
			points = mScore.getScore(team);

			// TextView
			id_label = (team == 1 ? R.id.team1 : R.id.team2);

			// Set score for current team.
			TextView txt = (TextView) findViewById(id_label);
			String displayScore = TeamNameHelper.getTeamName(this, team)
					+ " : ";
			displayScore += r.getQuantityString(R.plurals.points, points,
					points);
			txt.setText(displayScore);
		}
	}

	/**
	 * Action called from any of the button
	 * 
	 * @param button
	 *            The button pressed
	 */
	public void onClick(View button) {
		// Fetch team's point
		int point = 0;
		// Choose the team.
		final int team = (!mInputScore1.getText().toString().equals("") ? 1 : 2);
		final EditText input = (team == 1 ? mInputScore1 : mInputScore2);
		// Parse input to int
		try {
			point = Integer.parseInt(input.getText().toString());
		} catch (NumberFormatException e) {
			// On Error (overflow,.. etc)
			Toast.makeText(getApplicationContext(),
					getString(R.string.error_format), Toast.LENGTH_LONG).show();
			mInputScore1.setText("");
			mInputScore2.setText("");
			return;
		}

		/* Process teams's point */
		// Check if all asset is selected
		final boolean isAllAsset = mRatingBar.getRating() == sAllAssetRating;

		// Max points
		// With all asset maximum point is higher
		final int max = (isAllAsset ? sMatchAllAsset : sMatch);

		// Error, input is to high (with match bonus)
		if (button.getId() == R.id.btn_score && point > max
				&& point != max + sBonus) {

			Toast.makeText(getApplicationContext(),
					getString(R.string.error_high_value), Toast.LENGTH_LONG)
					.show();
			return;
		}

		// No moe errors, we load input
		mInputScore1.setText("");
		mInputScore2.setText("");

		/* Add an anounce */
		if (button.getId() == R.id.btn_announcement) {
			mScore.addAnnounce(team, point * mCoefficient);
			return;
		}
		/* Add a score */
		addPoints(team, point, max, mCoefficient);
	}

	/**
	 * Managing button. Enable the button is an input is set, disable them
	 * otherwise. Change the anounce button's color.
	 */
	public void textChangedListener() {
		// If empty input
		final boolean isEmpty1 = mInputScore1.getText().toString().trim()
				.equals("");
		final boolean isEmpty2 = mInputScore2.getText().toString().trim()
				.equals("");
		// Fetch buttons
		final Button btn_score = (Button) findViewById(R.id.btn_score);
		final Button btn_announcement = (Button) findViewById(R.id.btn_announcement);

		// Enable buttons
		btn_score.setEnabled(true);
		btn_announcement.setEnabled(true);

		// Get button's style
		int style_an = (isEmpty2 ? R.style.Team1 : R.style.Team2);
		int style_score = R.style.TeamAll;

		// Enable or disable button depending on the input fields.
		if ((isEmpty1 && isEmpty2) || (!isEmpty1 && !isEmpty2)) {
			btn_score.setEnabled(false);
			btn_announcement.setEnabled(false);
			style_an = style_score = R.style.TeamNone;
		}
		// Enable score input for only one team at a time.
		mInputScore1.setEnabled(isEmpty2);
		mInputScore2.setEnabled(isEmpty1);

		// Set button's style
		btn_score.setTextAppearance(getApplicationContext(), style_score);
		btn_announcement.setTextAppearance(getApplicationContext(), style_an);

	}
	
	private void waitForGraphicWorker(){
		try {
			if (mGraphWorker != null)
				mGraphWorker.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * On input, we call a listener
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
