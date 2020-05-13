-- MySQL dump 10.13  Distrib 5.5.62, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: taxomap
-- ------------------------------------------------------
-- Server version	5.5.62-0ubuntu0.14.04.1

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
-- Table structure for table `ncbi_div`
--

DROP TABLE IF EXISTS `ncbi_div`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ncbi_div` (
  `div_id` int(11) NOT NULL,
  `div_code` varchar(3) NOT NULL,
  `div_name` varchar(45) DEFAULT NULL,
  `div_description` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`div_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ncbi_merged`
--

DROP TABLE IF EXISTS `ncbi_merged`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ncbi_merged` (
  `old_tax_id` int(10) unsigned NOT NULL,
  `tax_id` int(10) unsigned NOT NULL,
  PRIMARY KEY (`old_tax_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ncbi_node`
--

DROP TABLE IF EXISTS `ncbi_node`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ncbi_node` (
  `tax_id` int(10) unsigned NOT NULL,
  `parent_tax_id` int(10) unsigned DEFAULT NULL,
  `rank` varchar(45) DEFAULT NULL,
  `name` mediumtext,
  `class_name` varchar(45) DEFAULT NULL,
  `div_id` int(11) NOT NULL,
  `hierarchy` mediumtext,
  PRIMARY KEY (`tax_id`),
  KEY `fk_NCBI_NODE_NCBI_NODE1_idx` (`parent_tax_id`),
  KEY `fk_NCBI_NODE_NCBI_DIV1_idx` (`div_id`),
  KEY `name_indx` (`name`(50))
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ncbi_nr`
--

DROP TABLE IF EXISTS `ncbi_nr`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ncbi_nr` (
  `accession` varchar(20) NOT NULL,
  `ncbi_gi` int(10) unsigned DEFAULT NULL,
  `seq_id` varchar(25) DEFAULT NULL,
  `tax_id` int(10) DEFAULT NULL,
  `title` mediumtext,
  PRIMARY KEY (`accession`),
  KEY `fk_tax_id` (`ncbi_gi`),
  KEY `key_seq` (`seq_id`),
  KEY `key_gi` (`ncbi_gi`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ncbi_nt`
--

DROP TABLE IF EXISTS `ncbi_nt`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ncbi_nt` (
  `accession` varchar(20) NOT NULL,
  `ncbi_gi` int(10) unsigned DEFAULT NULL,
  `seq_id` varchar(50) DEFAULT NULL,
  `tax_id` int(10) DEFAULT NULL,
  `title` mediumtext,
  PRIMARY KEY (`accession`),
  KEY `key_seq` (`seq_id`),
  KEY `key_gi` (`ncbi_gi`),
  KEY `tax_id_key` (`tax_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ncbi_syn`
--

DROP TABLE IF EXISTS `ncbi_syn`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ncbi_syn` (
  `tax_id` int(10) unsigned NOT NULL,
  `syn_name` varchar(255) DEFAULT NULL,
  `class_name` varchar(45) DEFAULT NULL,
  KEY `fk_NCBI_NODE1_idx` (`tax_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `taxon`
--

DROP TABLE IF EXISTS `taxon`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `taxon` (
  `tax_id` int(11) NOT NULL,
  `taxon` varchar(200) DEFAULT NULL,
  `rank` varchar(45) DEFAULT NULL,
  `kingdom` varchar(50) DEFAULT NULL,
  `subkingdom` varchar(50) DEFAULT NULL,
  `superphylum` varchar(50) DEFAULT NULL,
  `phylum` varchar(100) DEFAULT NULL,
  `subphylum` varchar(50) DEFAULT NULL,
  `superclass` varchar(50) DEFAULT NULL,
  `infraclass` varchar(50) DEFAULT NULL,
  `class` varchar(50) DEFAULT NULL,
  `subclass` varchar(50) DEFAULT NULL,
  `parvorder` varchar(50) DEFAULT NULL,
  `superorder` varchar(50) DEFAULT NULL,
  `infraorder` varchar(50) DEFAULT NULL,
  `orden` varchar(50) DEFAULT NULL,
  `suborder` varchar(50) DEFAULT NULL,
  `superfamily` varchar(50) DEFAULT NULL,
  `family` varchar(50) DEFAULT NULL,
  `subfamily` varchar(50) DEFAULT NULL,
  `tribe` varchar(50) DEFAULT NULL,
  `subtribe` varchar(50) DEFAULT NULL,
  `genus` varchar(50) DEFAULT NULL,
  `subgenus` varchar(50) DEFAULT NULL,
  `species` varchar(120) DEFAULT NULL,
  `species_group` varchar(120) DEFAULT NULL,
  `species_subgroup` varchar(120) DEFAULT NULL,
  `subspecies` varchar(120) DEFAULT NULL,
  `forma` varchar(120) DEFAULT NULL,
  `varietas` varchar(120) DEFAULT NULL,
  `no_rank` varchar(120) DEFAULT NULL,
  `degradadora` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`tax_id`),
  KEY `reino` (`kingdom`),
  KEY `idxphyl` (`phylum`),
  KEY `idxclass` (`class`),
  KEY `idxorder` (`orden`),
  KEY `idxfamily` (`family`),
  KEY `idxgenus` (`genus`),
  KEY `idxspecie` (`species`),
  KEY `idxno_rank` (`no_rank`),
  KEY `idxtaxon` (`taxon`),
  KEY `multiIndex` (`kingdom`,`phylum`,`class`,`orden`,`family`,`genus`,`species`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2020-05-12 19:18:21
