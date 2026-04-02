-- MySQLShell dump 2.0.1  Distrib Ver 9.6.1 for Win64 on x86_64 - for MySQL 9.6.1 (MySQL Community Server (GPL)), for Win64 (x86_64)
--
-- Host: localhost    Database: capstone_improved    Table: users
-- ------------------------------------------------------
-- Server version	8.0.45

--
-- Current Database: `capstone_improved`
--

USE `capstone_improved`;

--
-- Table structure for table `users`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` enum('ADMIN','USER') DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
