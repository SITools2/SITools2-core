/**
 *  Objectif : sécuriser les services client d'inscription, de modification de profil (password)
 *  par l'utilisation d'une image capcha. 
 *  
 *  Le formulaire d'inscription est généré sur le client avec une image
 *  Logiquement, le contrôle est effectué par le serveur, qui doit donc conserver une resource captcha ...
 * 
 * Comme expliqué ici :
 * http://tech.groups.yahoo.com/group/rest-discuss/message/14699
 * 
 * Depuis le client, un appel POST sur une /sitools/capchas/ qui crée une image captcha accessible par
 * GET /sitools/captchas/{id}
 * au max X (5?) images captchas sont conservées en mémoire dans une FIFO
 * une fois récupérée par GET l'image est effacée.
 * 
 * Un filtre de sécurité FilterCaptcha (qui hérite de SecurityFilter) détecte si withCaptcha est positionné dans le contexte des attributs.
 * Si oui, l'identifiant de captcha est récupéré et le mot de passe associé.
 * Un appel au store (mémoire) des captchas est réalisé pour récupérer le code et comparer.
 * 
 */
package fr.cnes.sitools.security.captcha;