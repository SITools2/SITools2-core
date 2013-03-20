-- MySQL dump 10.13  Distrib 5.5.9, for Win32 (x86)
--
-- Host: odysseus2    Database: cnes-test
-- ------------------------------------------------------
-- Server version	5.1.41-3ubuntu12.10

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `TABLE_TESTS`
--

DROP TABLE IF EXISTS `TABLE_TESTS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `TABLE_TESTS` (
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
  `field_timestamp` timestamp NOT NULL,
  `field_datetime` datetime NOT NULL,
  `field_date` date NOT NULL,
  `field_year` year(4) NOT NULL,
  `field_time` time NOT NULL,
  `field_char` char(10) NOT NULL,
  `field_bool` tinyint(1) NOT NULL,
  PRIMARY KEY (`field_varchar_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `TABLE_TESTS`
--

LOCK TABLES `TABLE_TESTS` WRITE;
/*!40000 ALTER TABLE `TABLE_TESTS` DISABLE KEYS */;
INSERT INTO `TABLE_TESTS` VALUES ('0',10,1,10,10,97,1.15533,8.96095939005054,125.25360,'varchar','Ceci est un enregistrement de test 1','2011-04-27 09:23:36','2011-03-11 11:01:50','2010-04-01',2011,'14:01:13','y',1),('1',-10,0,101,-10,22,1.31612,5.91603873396269,-250.15200,'varchar 2','Ceci est un enregistrement de test 2','2011-04-26 14:31:38','2011-04-11 11:01:50','2010-05-01',2010,'14:01:15','n',0);
/*!40000 ALTER TABLE `TABLE_TESTS` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2011-09-05 16:59:33
