CREATE DATABASE  IF NOT EXISTS `db_prj_TIW_playlist` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `db_prj_TIW_playlist`;

-- MySQL dump 10.13  Distrib 8.0.28
--
-- Host: 127.0.0.1    Database: db_prj_TIW_playlist
-- ------------------------------------------------------
-- Server version	8.0.28

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;



-- ------------------------------------------------------------------------------------------------------------------------------
-- Table structure for table `USERS`
--

DROP TABLE IF EXISTS `USERS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `USERS` (
	UserName VARCHAR(64) NOT NULL ,
	Pw VARCHAR(32) NOT NULL,
	PRIMARY KEY(userName)
);
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `USERS`
--
LOCK TABLES `USERS` WRITE;
/*!40000 ALTER TABLE `USERS` DISABLE KEYS */;
INSERT INTO `USERS` VALUES ('master', 'master'),('mirko', 'pwMirko'),('robert','ARPA'),('satoshi', 'may');
/*!40000 ALTER TABLE `USERS` ENABLE KEYS */;
UNLOCK TABLES;



-- ------------------------------------------------------------------------------------------------------------------------------
-- Table structure for table `PLAYLISTS`
--

DROP TABLE IF EXISTS `PLAYLISTS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `PLAYLISTS` (

	PlaylistTitle VARCHAR(64) NOT NULL,
	PlaylistUser VARCHAR(64) NOT NULL,
	CreationDate DATE NOT NULL,
	
	PRIMARY KEY(playlistTitle, playlistUser)
 
);
/*!40101 SET character_set_client = @saved_cs_client */;


-- ------------------------------------------------------------------------------------------------------------------------------
-- Table structure for table `PLAYLISTS`
--

DROP TABLE IF EXISTS `SONGS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `SONGS` (

	SongID int NOT NULL AUTO_INCREMENT,
	SongUser VARCHAR(64) NOT NULL,
	SongTitle VARCHAR(64) NOT NULL,
	AudioFile VARCHAR(200) NOT NULL,
	Genre VARCHAR(16) NOT NULL,
	Album int NOT NULL,
	
	PRIMARY KEY(SongID),
	FOREIGN KEY(SongUser) REFERENCES USERS(UserName) ON DELETE CASCADE ON UPDATE CASCADE
	FOREIGN KEY(Album) REFERENCES ALBUMS(AlbumID) ON DELETE CASCADE ON UPDATE CASCADE

)ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;


-- ------------------------------------------------------------------------------------------------------------------------------
-- Table structure for table `PLAYLISTS`
--

DROP TABLE IF EXISTS `ALBUMS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ALBUMS` (

	AlbumID int NOT NULL AUTO_INCREMENT,
	AlbumTitle VARCHAR(64) NOT NULL,
	Artist VARCHAR(64) NOT NULL,
	PublicationYear int NOT NULL,
	Img VARCHAR(200) NOT NULL,
	
	PRIMARY KEY(AlbumID)
 
);
/*!40101 SET character_set_client = @saved_cs_client */;


-- ------------------------------------------------------------------------------------------------------------------------------
-- Table structure for table `PLcontainsSONG`
--

DROP TABLE IF EXISTS `PLcontainsSONG`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `PLcontainsSONG` (

	PlTitle VARCHAR(64) NOT NULL,
	PlUser VARCHAR(64) NOT NULL,
	SgID int NOT NULL,
	
	PRIMARY KEY(PlTitle, PlUser, SgID),
	FOREIGN KEY (PlTitle,PlUser) REFERENCES PLAYLISTS(PlaylistTitle, PlaylistUser) ON DELETE CASCADE ON UPDATE CASCADE,
	FOREIGN KEY (SgID) REFERENCES SONGS(SongID) ON DELETE CASCADE ON UPDATE CASCADE
 
); 
/*!40101 SET character_set_client = @saved_cs_client */;
