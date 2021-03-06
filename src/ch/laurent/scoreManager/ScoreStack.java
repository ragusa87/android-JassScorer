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
import java.util.Stack;

/**
 * Managing score for 2 teams in a stack, so it's possible to cancel the last
 * score.
 */
public class ScoreStack extends Observable {
	/**
	 * Internal class to stock the score
	 */
	static class Score {
		int score1;
		int score2;

		/**
		 * Set score
		 * 
		 * @param score1
		 *            Score for team 1
		 * @param score2
		 *            Score for team 2
		 */
		Score(final int score1, final int score2) {
			this.score1 = score1;
			this.score2 = score2;
		}

		/**
		 * Get score for a team
		 * 
		 * @param teamId
		 *            Team id (1,2)
		 * @return score
		 */
		public int get(final int teamId) {
			return (teamId == 1 ? score1 : score2);
		}
	}

	/**
	 * Stack
	 */
	private final Stack<Score> stack = new Stack<Score>();

	/**
	 * Add score. Negative score are remplaced by 0.
	 */
	private Score pushQuiet(final Score s) {
		// no negative score
		if (s.score1 < 0)
			s.score1 = 0;

		if (s.score2 < 0)
			s.score2 = 0;

		synchronized (stack) {
			return stack.push(s);
		}
	}

	private Score pushQuiet(final int score1, final int score2) {
		return pushQuiet(new Score(score1, score2));
	}

	/**
	 * Starting score is (0,0)
	 */
	public ScoreStack() {
		// notifyObservers() sera appele
		setAndNotify(0, 0);
	}

	/**
	 * Clear all previous value and set a new one
	 * 
	 * @param score1
	 *            Score for team 1
	 * @param score2
	 *            Score for team 2
	 */
	private void setAndNotify(final int score1, final int score2) {
		synchronized (stack) {
			stack.clear();	
		}
		this.pushQuiet(new Score(score1, score2));
		notifyObservers();

	}

	/**
	 * Add the previous score with the new one and insert it into the stack.
	 * 
	 * @param score1
	 *            Score for team 1
	 * @param score2
	 *            Score for team 2
	 */
	public void addAndNotify(final int score1, final int score2) {
		synchronized (stack) {
			Score s = (stack.isEmpty() ? new Score(0, 0) : stack.peek());	
			this.pushQuiet(new Score(s.score1 + score1, s.score2 + score2));
			
		}
		notifyObservers();
	}

	/**
	 * Add an anounce
	 * 
	 * @param team
	 *            Team id
	 * @param announce
	 *            Value
	 */
	public void addAnnounce(final int team, final int announce) {
		int score1 = (team == 1 ? announce : 0);
		int score2 = (team == 2 ? announce : 0);
		// notifyObservers() will be called.
		this.addAndNotify(score1, score2);
	}

	/**
	 * Get score for the specified team
	 * 
	 * @param team
	 *            Team id
	 * @return Score
	 */
	public int getScore(final int team) {
		// stack is never empty.
		synchronized (stack) {
			return stack.peek().get(team);
		}
	}

	/**
	 * Reset to (0,0)
	 */
	public void reset() {
		// notifyObservers() will be called
		setAndNotify(0, 0);
	}

	/**
	 * Cancel the last score
	 */
	public void cancel() {
		synchronized (stack) {

			// Remove from stack
			if (!stack.empty())
				stack.pop();

			// Be sure the stack is never empty
			if (stack.empty()) {
				// notifyObservers() will be called
				setAndNotify(0, 0);
			} else {
				// We notify in this case..
				notifyObservers();
			}
		}
	}

	/**
	 * Notify observer with an implicit setChanged.
	 */
	public void notifyObservers() {
		setChanged();
		super.notifyObservers();
	}

	/**
	 * Tell if there is a score into the stack
	 * 
	 * @return there is a score into the stack
	 */
	public boolean isCancellable() {
		// La pile contient toujours une valeur (0,0)
		synchronized (stack) {
			return stack.size() > 1;
		}
	}

	/**
	 * Saving stack into a string Stack = Score ; Score ; Score Score =
	 * score1,score2
	 * 
	 * @return a string
	 */
	public String saveAsString() {
		StringBuffer s = new StringBuffer();
		synchronized (stack) {
			// Stack = Score ; Score ; Score
			// Score = score1,score2
			final int size = stack.size();
			for (int i = 0; i < size; i++) {
				Score score = stack.get(i);
				s.append(score.score1 + "," + score.score2);
				if (i != size - 1) {
					s.append(";");
				}
			}
		}
		return s.toString();
	}

	/**
	 * Return the stack for the graph
	 * 
	 * @return the stack
	 */
	public Stack<ScoreStack.Score> getStack() {
		synchronized (stack) {
			return stack;
		}
	}

	/**
	 * Build stack from string Stack = Score ; Score ; Score Score =
	 * score1,score2
	 * 
	 * @param s
	 *            string
	 */
	public void restoreFromString(String s) {
		if (s == null || s.trim().equals(""))
			return;
		String tmpScores[] = s.split(";");

		synchronized (stack) {
			stack.clear();
		}

		String[] score;
		for (int i = 0; i < tmpScores.length; i++) {
			score = tmpScores[i].split(",");
			// Score = score1,score2
			pushQuiet(Integer.valueOf(score[0]), Integer.valueOf(score[1]));
		}
		notifyObservers();
	}
}