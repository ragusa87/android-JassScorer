/* LICENSE
 * This work is licensed under the 
 * Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License. 
 * To view a copy of this license, visit 
 * http://creativecommons.org/licenses/by-nc-sa/3.0/.
 * 
 * Copyright (c) 2013 by Laurent Constantin <constantin.laurent@gmail.com>
 */
package ch.laurent.scoreManager;

import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.util.Log;
import android.view.View;
import ch.laurent.chibre.R;
import ch.laurent.helpers.TeamNameHelper;
import ch.laurent.scoreManager.ScoreStack.Score;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewSeries.*;

import com.jjoe64.graphview.LineGraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;

/**
 * The the score as a graph For the library, see
 * https://github.com/jjoe64/GraphView/
 */
public abstract class ScoreGraph implements Observer {
	// Stack
	ScoreStack mScoreStack;
	// number of serie into the stack (0 or 2)
	int mNbSeries = 0;

	// Team's color
	final int mTeam1Color;
	final int mTeam2Color;

	final String[] mTeamName;
	final Context mActContext;
	/**
	 * Init
	 * 
	 * @param mActContext
	 *            Main activity context
	 * @param scoreStack
	 *            Score
	 */
	public ScoreGraph(Context mActContext, ScoreStack scoreStack) {
		this.mActContext = mActContext;
		mTeamName = new String[] { TeamNameHelper.getTeamName(mActContext, 1),
				TeamNameHelper.getTeamName(mActContext, 2) };

		mTeam1Color = mActContext.getResources().getColor(R.color.red);
		mTeam2Color = mActContext.getResources().getColor(R.color.blue);

		setScoreStack(scoreStack);

	}

	/**
	 * Updating the stack with a new one.
	 * 
	 * @param scoreStack
	 *            The new stack
	 */
	public synchronized void setScoreStack(ScoreStack scoreStack) {
		
		// Supprime l'observateur
		if (mScoreStack != null) {
			synchronized (mScoreStack) {
				mScoreStack.deleteObserver(this);
			}
		}
		// Ajout du nouvel observateur
		mScoreStack = scoreStack;
		if(scoreStack == null)
			return;
				
		synchronized (mScoreStack) {
			mScoreStack.addObserver(this);
		}
	}

	/**
	 * Generate the graph
	 */
	private synchronized GraphView generate() {
		// Graph
		final GraphView mGraphView = new LineGraphView(mActContext, "");		
		
		synchronized (mScoreStack) {
			if (mScoreStack == null)
				return mGraphView;
		}
		
		// Loop to display each team lines
		int topScore = 0;

		// Build the score for each team
		for (int team = 1; team < 3; team++) {
			final int mColor = (team == 1 ? mTeam1Color : mTeam2Color);

			String gfdString[];
			GraphViewData gfd[];
			synchronized (mScoreStack) {
				// scores table
				gfd = new GraphViewData[mScoreStack.getStack().size()];

				// Empty legend array
				gfdString = new String[gfd.length];

				// loop on the stack
				int i = 0;
				for (Score s : mScoreStack.getStack()) {
					final int score = (team == 1 ? s.score1 : s.score2);

					// Saving the maximum score for the scale legend
					topScore = Math.max(topScore, score);

					// Insert dots.
					gfd[i] = new GraphViewData(i + 1, score);

					// Horizontal legend are empty.
					gfdString[i] = "";
					i++;
				}
				Log.v("JassScorer","topscore: "+ topScore + "  i : " + i);
			}

			// Apply legend
			mGraphView.setVerticalLabels(new String[] { topScore + "", "0" });
			mGraphView.setHorizontalLabels(gfdString);

			// Add point for the current team
			mGraphView.addSeries(new GraphViewSeries(mTeamName[team - 1],
					new GraphViewStyle(mColor, 3), gfd));
			// We a a new serie into the graph
			mNbSeries++;
		}
		return mGraphView;
	}

	/**
	 * Generate and return the view
	 * 
	 * @return The view
	 */
	public synchronized View getView() {
		return generate();
	}

	/**
	 * Called when the score is updated.
	 */
	public abstract void update(Observable observable, Object data);
}
