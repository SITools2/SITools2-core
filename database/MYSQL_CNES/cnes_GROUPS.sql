-- phpMyAdmin SQL Dump
-- version 2.11.9.5
-- http://www.phpmyadmin.net
--
-- Serveur: odysseus2.silogic.fr
-- Généré le : Mar 21 Juin 2011 à 11:02
-- Version du serveur: 5.1.41
-- Version de PHP: 5.1.6

-- SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Base de données: `cnes`
--

-- --------------------------------------------------------

--
-- Structure de la table `GROUPS`
--

CREATE TABLE IF NOT EXISTS `GROUPS` (
  `name` varchar(32) COLLATE utf8_unicode_ci NOT NULL,
  `description` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`name`)
) ENGINE=INNODB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Contenu de la table `GROUPS`
--

INSERT INTO `GROUPS` (`name`, `description`) VALUES
('register', 'Group of registered persons'),
('administrator', 'Group of persons managing the archive system');
