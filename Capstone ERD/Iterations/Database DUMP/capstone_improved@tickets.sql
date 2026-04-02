-- MySQLShell dump 2.0.1  Distrib Ver 9.6.1 for Win64 on x86_64 - for MySQL 9.6.1 (MySQL Community Server (GPL)), for Win64 (x86_64)
--
-- Host: localhost    Database: capstone_improved    Table: tickets
-- ------------------------------------------------------
-- Server version	8.0.45

--
-- Current Database: `capstone_improved`
--

USE `capstone_improved`;

--
-- Table structure for table `tickets`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tickets` (
  `id` int NOT NULL AUTO_INCREMENT,
  `event_id` int NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `total_quantity` int NOT NULL,
  `sold` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `tickets_event_id_unique` (`event_id`),
  CONSTRAINT `tickets_event_id_foreign` FOREIGN KEY (`event_id`) REFERENCES `events` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1042 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
