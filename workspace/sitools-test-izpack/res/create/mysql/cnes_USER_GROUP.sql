-- phpMyAdmin SQL Dump
-- version 2.11.9.5
-- http://www.phpmyadmin.net
--
-- Serveur: odysseus2.silogic.fr
-- Généré le : Mar 21 Juin 2011 à 11:03
-- Version du serveur: 5.1.41
-- Version de PHP: 5.1.6

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Base de données: `cnes`
--

-- --------------------------------------------------------
DROP TABLE IF EXISTS `USER_GROUP`;
--
-- Structure de la table `USER_GROUP`
--

CREATE TABLE IF NOT EXISTS `USER_GROUP` (
  `identifier` varchar(64) COLLATE utf8_unicode_ci NOT NULL,
  `name` varchar(32) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`identifier`,`name`),
  KEY `FK_GROUP_2` (`name`),
  KEY `FK_USER_2` (`identifier`)
) ENGINE=INNODB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Contenu de la table `USER_GROUP`
--

INSERT INTO `USER_GROUP` (`identifier`, `name`) VALUES
('admin', 'administrator');
