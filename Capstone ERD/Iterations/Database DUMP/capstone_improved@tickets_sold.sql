-- MySQLShell dump 2.0.1  Distrib Ver 9.6.1 for Win64 on x86_64 - for MySQL 9.6.1 (MySQL Community Server (GPL)), for Win64 (x86_64)
--
-- Host: localhost    Database: capstone_improved    Table: tickets_sold
-- ------------------------------------------------------
-- Server version	8.0.45

--
-- Current Database: `capstone_improved`
--

USE `capstone_improved`;

--
-- Table structure for table `tickets_sold`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tickets_sold` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `ticket_id` int NOT NULL,
  `date_sold` date NOT NULL,
  PRIMARY KEY (`id`),
  KEY `tickets_sold_user_id_foreign` (`user_id`),
  KEY `tickets_sold_ticket_id_foreign` (`ticket_id`),
  CONSTRAINT `tickets_sold_ticket_id_foreign` FOREIGN KEY (`ticket_id`) REFERENCES `tickets` (`id`),
  CONSTRAINT `tickets_sold_user_id_foreign` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6001 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
