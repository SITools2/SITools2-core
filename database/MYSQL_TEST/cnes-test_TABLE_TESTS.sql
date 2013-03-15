-- phpMyAdmin SQL Dump
-- version 2.11.9.5
-- http://www.phpmyadmin.net
--
-- Serveur: odysseus2.silogic.fr
-- Généré le : Lun 12 Septembre 2011 à 15:56
-- Version du serveur: 5.1.41
-- Version de PHP: 5.1.6

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";

--
-- Base de données: `cnes-test`
--

-- --------------------------------------------------------

--
-- Structure de la table `TABLE_TESTS`
--

DROP TABLE IF EXISTS `TABLE_TESTS`;
CREATE TABLE IF NOT EXISTS `TABLE_TESTS` (
  `field_varchar_id` varchar(4) NOT NULL,
  `field_tiny_int` tinyint(2) NOT NULL,
  `field_small_int` smallint(6) NOT NULL,
  `field_medium_int` mediumint(9) NOT NULL,
  `field_int` int(11) NOT NULL,
  `field_big_int` bigint(20) NOT NULL,
  `field_float` float NOT NULL,
  `field_double` double NOT NULL,
  `field_decimal` decimal(10,5) NOT NULL,
  `field_varchar` varchar(200) NOT NULL,
  `field_text` text NOT NULL,
  `field_timestamp` timestamp NULL DEFAULT NULL,
  `field_datetime` datetime NOT NULL,
  `field_date` date NOT NULL,
  `field_year` year(4) NOT NULL,
  `field_time` time NOT NULL,
  `field_char` char(10) NOT NULL,
  `field_bool` tinyint(1) NOT NULL,
  PRIMARY KEY (`field_varchar_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Contenu de la table `TABLE_TESTS`
--

INSERT INTO `TABLE_TESTS` (`field_varchar_id`, `field_tiny_int`, `field_small_int`, `field_medium_int`, `field_int`, `field_big_int`, `field_float`, `field_double`, `field_decimal`, `field_varchar`, `field_text`, `field_timestamp`, `field_datetime`, `field_date`, `field_year`, `field_time`, `field_char`, `field_bool`) VALUES
('0', 10, 1, 10, 10, 97, 1.15533, 8.96095939005054, 125.25360, 'varchar', 'Ceci est un enregistrement de test 1', '2011-04-27 11:23:36', '2011-03-11 11:01:50', '2010-04-01', 2011, '14:01:13', 'y', 1),
('1', -10, 0, 101, -10, 22, 1.31612, 5.91603873396269, -250.15200, 'varchar 2', 'Ceci est un enregistrement de test 2', '2011-04-26 16:31:38', '2011-04-11 11:01:50', '2010-05-01', 2010, '14:01:15', 'n', 0);
