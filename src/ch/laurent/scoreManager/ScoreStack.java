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
 * Gestion des score pour les deux equipes dans une pile. Ainsi il est possible
 * d'annuler un score.
 */
public class ScoreStack extends Observable {
	/**
	 * Classe interne pour stoquer le score. Le score est dans 2 champs
	 * publiques.
	 */
	static class Score {
		/**
		 * Score de l'equipe 1
		 */
		public int score1;
		/**
		 * Score de l'equipe 2
		 */
		public int score2;

		/**
		 * Definit le score
		 * 
		 * @param score1 Score de l'equipe 1
		 * @param score2 Score de l'equipe 2
		 */
		Score(final int score1, final int score2) {
			this.score1 = score1;
			this.score2 = score2;
		}

		/**
		 * Obtient le score d'une des deux equipes
		 * 
		 * @param teamId Identifiant de l'equipe 1 ou 2.
		 * @return Le score de l'equipe 1 ou 2
		 */
		public int get(final int teamId) {
			return (teamId == 1 ? score1 : score2);
		}
	}

	/**
	 * Pile des scores
	 */
	private final Stack<Score> stack = new Stack<Score>();

	/**
	 * Ajoute le score dans la pile. Si le score est negatif, on le met a zero
	 */
	private Score push(final Score s) {
		// Pas de score negatif
		if (s.score1 < 0)
			s.score1 = 0;

		if (s.score2 < 0)
			s.score2 = 0;

		Score s2 = stack.push(s);
		// notifie, (pour mettre a jour le graph)
		setChanged();
		notifyObservers();
		return s2;
	}

	/**
	 * Ajoute le score, utilise pour la sauvegarde
	 * 
	 * @param score1 Le score de l'equipe 1
	 * @param score2 Le score de l'equipe 2
	 */
	private void push(int score1, int score2) {
		push(new Score(score1, score2));
	}

	/**
	 * Le score de depart est 0,0
	 */
	public ScoreStack() {
		// notifyObservers() sera appele
		set(0, 0);
	}

	/**
	 * Efface touts les scores precedents et definit le nouveau score
	 * 
	 * @param score1 Score de l'equipe 1
	 * @param score2 Score de l'equipe 2
	 */
	public void set(final int score1, final int score2) {
		stack.clear();
		this.push(new Score(score1, score2));
		setChanged();
		notifyObservers();
	}

	/**
	 * Ajoute les points au score (en comptant le score precedent)
	 * 
	 * @param score1 Points de l'equipe 1
	 * @param score2 Points de l'equipe 2
	 */
	public void add(final int score1, final int score2) {
		Score s = (stack.isEmpty() ? new Score(0, 0) : stack.peek());
		this.push(new Score(s.score1 + score1, s.score2 + score2));
		setChanged();
		notifyObservers();
	}

	/**
	 * Ajoute une annonce dans le score
	 * 
	 * @param team Id de l'equipe
	 * @param announce Montant de l'annonce
	 */
	public void addAnnounce(final int team, final int announce) {
		int score1 = (team == 1 ? announce : 0);
		int score2 = (team == 2 ? announce : 0);
		// notifyObservers() sera appele
		this.add(score1, score2);
	}

	/**
	 * Obtient le score de la team desiree
	 * 
	 * @param team Equipe 1 ou 2
	 * @return Score de l'equipe 1 ou 2
	 */
	public int getScore(final int team) {
		// La pile n'est jamais vide
		return stack.peek().get(team);
	}

	/**
	 * Remet a zero le score
	 */
	public void reset() {
		// notifyObservers() sera appele
		set(0, 0);
	}

	/**
	 * Annule la derniere modification du score
	 */
	public void cancel() {
		// Enleve le dernier score de la pile
		if (!stack.empty())
			stack.pop();

		// S'assure que la pile ne soit jamais vide
		if (stack.empty()) {
			// notifyObservers() sera appele
			set(0, 0);
		} else {
			// On notifie l'observateur car pas appele
			setChanged();
			notifyObservers();
		}
	}

	/**
	 * Indique si on peut annuler la derniere modificiation
	 * 
	 * @return si on peut annuler la derniere modificiation
	 */
	public boolean isCancellable() {
		// La pile contient toujours une valeur (0,0)
		return stack.size() > 1;
	}

	/**
	 * Sauve la pile dans un string
	 * 
	 * @return La pile en string
	 */
	public String saveAsString() {
		// Stack = Score ; Score ; Score
		// Score = score1,score2
		StringBuffer s = new StringBuffer();
		final int size = stack.size();
		for (int i = 0; i < size; i++) {
			Score score = stack.get(i);
			s.append(score.score1 + "," + score.score2);
			if (i != size - 1) {
				s.append(";");
			}
		}
		return s.toString();
	}

	/**
	 * Renvoie la pile pour iterer sur le score
	 * 
	 * @return la pile des scores
	 */
	public Stack<ScoreStack.Score> getStack() {
		return stack;
	}

	/**
	 * Reconstruit la pile depuis un string
	 * @param s Le string
	 */
	public void restoreFromString(String s) {
		if (s == null || s.trim().equals("")) return;
		// Stack = Score ; Score ; Score
		String tmpScores[] = s.split(";");
		stack.clear();
		String[] score;
		for (int i = 0; i < tmpScores.length; i++) {
			score = tmpScores[i].split(",");
			// Score = score1,score2
			push(Integer.valueOf(score[0]), Integer.valueOf(score[1]));
		}
	}
}