 /*******************************************************************************
 * Copyright 2010-2013 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
 *
 * This file is part of SITools2.
 *
 * SITools2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SITools2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SITools2.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package test;

public class ADM42 {
	/**
	 * Tests réalisés le 05/10/2010 par David Arpin. 
	 * 
	 * Afficher la liste des datasets : OK
	 * Rechercher sur le nom : OK
	 * Trier la liste des datasets : OK
	 * 
	 * Créer, modifier, supprimer un dataset : OK
	 * 
	 * Activer, désactiver un dataset : OK
	 * 
	 * En création ou modification : 
		 * Panel Dataset Information : 
			 * Renseigner le nom, la description, l'image, le datasource, le user attachment : OK
			 * action Modifier le datasource : supprime les fields, tables et criteres saisis : OK
			 * Upload de fichier image : Non Implémenté	
		 * Panel select Tables : 
		 	 * Récupérer la liste des tables de la dataSource sélectionnée : OK
		 	 * Ajouter la(es) tables sélectionnées dans le dataSet : OK
		 	 * Ajouter toutes les tables de la source de données : OK
		 	 * Supprimer la(es) tables sélectionnées : OK
		 	 * Supprimer toutes les tables du dataset : OK
		 	 * Ajouter un alias a chacune des tables : OK
		 * Panel Select Fields : 
		 	 * Récupérer la liste des champs des tables sélectionnées dans le dataset : OK
		 	 * Ajouter le(es) champs sélectionnés dans le dataSet : OK
		 	 * Ajouter tous les champs des tables sélectionnées : OK
		 	 * Supprimer le(es) champs sélectionnés : OK
		 	 * Supprimer tous les champs du dataset : OK
		 * Panel Fields Setup: 
		 	 * Récupérer la liste des champs sélectionnés : OK
		 	 * Modifier header (texte libre) : OK
		 	 * Modifier tooltip (texte libre) : OK
		 	 * Modifier width (numeric) : OK
		 	 * Modifier sortable (boolean) : OK
		 	 * Modifier visible (boolean) : OK
		 	 * Modifier filterType (liste : ['Numeric', 'String']): OK
		 	 * Modifier l'ordre des champs dans la table avec les fleches Up & Down : OK
		 * Panel Assignation notion :
		 	 * Récupérer la liste des dictionary : OK
		 	 * Pour chaque dictionary, récupérer la liste des notions associées : OK
		 	 * Sélectionner une notion pour l'assigner a la colonne : OK
		 * Panel Criteria :
		 	 * Pouvoir choisir le type de requétage (Wizard ou Sql) : OK
		 	 * Afficher le type de requetage en fonction du choix utilisateur : OK
		 	 * Wizard (jointure) : 
		 	 	* Ajouter une condition de jointure (menu contextuel : add Condition) : OK
		 	 	* Choisir l'attribut gauche (double clic sur left attribute) en fonction des champs des tables sélectionnées : OK
		 	 	* Choisir l'attribut droit (double clic sur right attribute) en fonction des champs des tables sélectionnées : OK
		 	 	* Choisir l'opérateur de comparaison (double clic sur operator) : OK
		 	 	* Choisir l'opérateur logique (liste : ['and', 'or']) (double clic sur la premiere colonne) : OK
		 	 	* Ajouter des parenthéses (double clic sur parenthése) : OK
		 	 	* Supprimer des parenthéses (menu contextuel : delete parenthesis) : OK
		 	 	* Supprimer une condition (menu contextuel : delete conditions) : OK
		 	 	* Ordonner les conditions de jointure (fleches up&down): OK
		 	 * Wizard (where clause) : 
		 	 	* Ajouter une condition (menu contextuel : add Condition) : OK
		 	 	* Choisir l'attribut gauche (double clic sur left attribute) en fonction des champs des tables sélectionnées : OK
		 	 	* Choisir l'attribut droit (double clic sur right attribute) en texte libre : OK
		 	 	* Choisir l'opérateur de comparaison (double clic sur operator) : OK
		 	 	* Choisir l'opérateur logique (liste : ['and', 'or']) (double clic sur la premiere colonne) : OK
		 	 	* Ajouter des parenthéses (double clic sur parenthése) : OK
		 	 	* Supprimer des parenthéses (menu contextuel : delete parenthesis) : OK
		 	 	* Supprimer une condition (menu contextuel : delete conditions) : OK
		 	 	* Ordonner les conditions de jointure (fleches up&down): OK
		 	 * Sql : 
		 	 	* Afficher un texte libre pour saisie de clauses SQL : OK
		 * valider la saisie (enregistre les données saisies et ferme la fenétre) : OK
		 * Annuler la saisie (n'enregistre pas les données saisies et ferme la fenétre) : OK
		 * 
	 * Afficher l'aide correspondante : NOK -> définir le fichier HTML correspondant. 
	 * 
	 */
}
