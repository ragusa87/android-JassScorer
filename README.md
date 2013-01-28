# Chibre pour Android

Laurent Constantin -  Janvier 2013

Cette application a pour but de notter les points lors d'une partie de chibre / Jass.
Chaque variante de jeu a un coefficient qui lui est propre.

# Variantes de jeu

	1. Simple (Carreau ou Coeur)
	2. Double (Treffle ou Pique)
	3. De Haut en bas (Pas d'atout, l'as est le plus fort)
	4. De bas en haut (Pas d'atout, le 6 est le plus fort)
	5. Slalom (Pas d'atout, l'as et le 6 sont les plus forts, une fois sur 2)
	6. Tout-atout (Pas d'atout, le buur est le plus fort, suivit du nell)
	
- Les coefficients sont respectivement de : 1,2,3,4,5,2.
- Le score et les annonces sont à saisir avec un coefficient de 1, le reste est calculé automatiquement.
- Une partie vaut 157 points, et 259 points en tout-atout.
- En cas de match, vous devez saisir 100 points de plus (257 points ou 359 points).
Il est aussi possible de saisir 0 pour l'équipe perdante afin de valider un match.
	

# Saisie du score
1. Choisir le champ de saisie de l'équipe 1 ou de l'équipe 2.
2. Entrer le score de l'équipe (en x1).
3. Cliquer sur "CALCULER LE SCORE", les scores des deux équipes sont calculés et ajoutés.

Si un message vous indique que le score est trop grand, c'est qu'il se situe entre
157 et 257 points (non compris) ou entre 259 et 359 points en tout atout.

# Saisie d'une annonce
1. Choisir le champ de saisie de l'équipe 1 ou de l'équipe 2.
2. Entrer la valeur de l'annonce (x1).
3. Cliquer sur "Annonce" pour ajouter le score a l'équipe choisie.

Aucune validation sur la valeur de l'annonce n'est faite.

# Correction
Pour corriger un score, utiliser la fonction "Annuler" du menu.

# Remise à zéro
Le score peut être remis à zéro par l'intermédiaire du menu.

# Licence
Ce programme est sous licence [Attribution-NonCommercial-ShareAlike 3.0 Unported](http://creativecommons.org/licenses/by-nc-sa/3.0/)

Le logo, contient des éléments tirés de [Wikipedia](http://en.wikipedia.org/wiki/File:Playing_card_club_A.svg) sous licence [Attribution-ShareAlike 3.0 Unported](http://creativecommons.org/licenses/by-sa/3.0/deed.en). Il a été réalisée par Laurent Constantin sur la base des cartes de [Cburnett](http://en.wikipedia.org/wiki/User:Cburnett).

La boîte de dialogue "A propos" est tirée du dépot [markers-for-android-updated](https://github.com/FunkyAndroid/markers-for-android-updated/tree/master/res/drawable-xhdpi) et est sous licence "Apache 2"

Le graphique est réalisé via la librairie [GraphView 3.0](http://www.jjoe64.com/p/graphview-library.html) produite par Jonas Gehring sous licence [LGPL](http://www.gnu.org/licenses/lgpl.html).

	Pour me contacter: constantin(dot)laurent[4t]gmail.com

# Traduction
Le programme est disponible dans les langues suivantes:

* Français
* Anglais
* Allemand (Traduit via Google Translate)

Si vous souhaitez d'autres traductions, envoyez-moi un email (où un pull request) avec les fichiers [strings.xml](https://github.com/ragusa87/android-Chibre/blob/master/res/values/strings.xml) et [strings_activity_settings.xml](https://github.com/ragusa87/android-Chibre/blob/master/res/values/strings_activity_settings.xml) traduits.
	
	
# Voir aussi
Application [aJass](https://play.google.com/store/apps/details?id=com.ajass) (gratuite)
