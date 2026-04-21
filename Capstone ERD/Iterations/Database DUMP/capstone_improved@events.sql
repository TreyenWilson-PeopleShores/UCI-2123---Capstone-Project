-- MySQLShell dump 2.0.1  Distrib Ver 9.6.1 for Win64 on x86_64 - for MySQL 9.6.1 (MySQL Community Server (GPL)), for Win64 (x86_64)
--
-- Host: localhost    Database: capstone_improved    Table: events
-- ------------------------------------------------------
-- Server version	8.0.45

--
-- Current Database: `capstone_improved`
--

USE `capstone_improved`;

--
-- Table structure for table `events`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `events` (
  `id` int NOT NULL AUTO_INCREMENT,
  `event_name` varchar(255) NOT NULL,
  `date` date NOT NULL,
  `status` enum('SCHEDULED','CANCELLED','COMPLETED') NOT NULL,
  `total_spots` int NOT NULL,
  `venue_id` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `events_venue_date_unique` (`venue_id`,`date`),
  CONSTRAINT `events_venue_id_foreign` FOREIGN KEY (`venue_id`) REFERENCES `venues` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1042 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
