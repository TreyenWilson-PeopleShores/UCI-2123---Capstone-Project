-- MySQLShell dump 2.0.1  Distrib Ver 9.6.1 for Win64 on x86_64 - for MySQL 9.6.1 (MySQL Community Server (GPL)), for Win64 (x86_64)
--
-- Host: localhost    Database: capstone_improved
-- ------------------------------------------------------
-- Server version	8.0.45

--
-- Dumping database 'capstone_improved'
--

-- begin database `capstone_improved`
CREATE DATABASE /*!32312 IF NOT EXISTS*/ `capstone_improved` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
-- end database `capstone_improved`


--
-- Current Database: `capstone_improved`
--

USE `capstone_improved`;

--
-- Dumping events for database 'capstone_improved'
--

--
-- Current Database: `capstone_improved`
--

USE `capstone_improved`;

--
-- Dumping libraries for database 'capstone_improved'
--


--
-- Current Database: `capstone_improved`
--

USE `capstone_improved`;

--
-- Dumping routines for database 'capstone_improved'
--

-- begin procedure `capstone_improved`.`seed_events`
/*!50003 DROP PROCEDURE IF EXISTS `seed_events` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `seed_events`()
BEGIN
    DECLARE v_id INT;
    DECLARE v_count INT;
    DECLARE i INT;
    DECLARE day_attempts INT;

    DECLARE chosen_date DATE;
    DECLARE chosen_status VARCHAR(10);
    DECLARE spots INT;
    DECLARE base_name VARCHAR(255);

    -- number of venues
    SELECT COUNT(*) INTO v_count FROM venues;

    SET v_id = 1;
    WHILE v_id <= v_count DO

        -- create ~40 events per venue (~20 venues => ~800 events)
        SET i = 1;
        WHILE i <= 40 DO

            -- Pick a random date between 2007-01-01 and 2030-12-31
            -- and ensure (venue_id, date) is unique by retrying.
            SET day_attempts = 0;

            pick_date: LOOP
                SET chosen_date = DATE_ADD('2007-01-01', INTERVAL FLOOR(RAND() * (DATEDIFF('2030-12-31','2007-01-01') + 1)) DAY);
                SET day_attempts = day_attempts + 1;

                IF NOT EXISTS (
                    SELECT 1 FROM events e WHERE e.venue_id = v_id AND e.date = chosen_date
                ) THEN
                    LEAVE pick_date;
                END IF;

                -- Safety break in case something goes odd
                IF day_attempts > 50 THEN
                    -- fallback: shove it forward by attempts count
                    SET chosen_date = DATE_ADD('2007-01-01', INTERVAL (v_id * 50 + i) DAY);
                    LEAVE pick_date;
                END IF;
            END LOOP;

            -- Decide status based on date (past mostly COMPLETED, future mostly SCHEDULED)
            IF chosen_date < CURDATE() THEN
                -- Past: mostly COMPLETED, some CANCELLED
                IF RAND() < 0.10 THEN
                    SET chosen_status = 'CANCELLED';
                ELSE
                    SET chosen_status = 'COMPLETED';
                END IF;
            ELSE
                -- Future: mostly SCHEDULED, some CANCELLED
                IF RAND() < 0.07 THEN
                    SET chosen_status = 'CANCELLED';
                ELSE
                    SET chosen_status = 'SCHEDULED';
                END IF;
            END IF;

            -- Spots: based on venue capacity (keep it reasonable)
            SELECT LEAST(GREATEST(FLOOR(total_capacity * (0.6 + RAND()*0.35)), 200), 100000)
            INTO spots
            FROM venues
            WHERE id = v_id;

            -- Event base name pattern
            SET base_name = CONCAT(
                CASE FLOOR(RAND()*6)
                    WHEN 0 THEN 'Tech Summit'
                    WHEN 1 THEN 'Music Night'
                    WHEN 2 THEN 'Championship Series'
                    WHEN 3 THEN 'Comedy Showcase'
                    WHEN 4 THEN 'Startup Expo'
                    ELSE 'Community Fest'
                END,
                ' ',
                YEAR(chosen_date)
            );

            -- Insert the main event day
            INSERT INTO events (event_name, date, status, total_spots, venue_id)
            VALUES (base_name, chosen_date, chosen_status, spots, v_id);

            -- Occasionally create a multi-day event by inserting 1–2 more consecutive days
            IF RAND() < 0.22 THEN
                -- Day 2
                IF NOT EXISTS (SELECT 1 FROM events e WHERE e.venue_id = v_id AND e.date = DATE_ADD(chosen_date, INTERVAL 1 DAY)) THEN
                    INSERT INTO events (event_name, date, status, total_spots, venue_id)
                    VALUES (CONCAT(base_name, ' - Day 2'), DATE_ADD(chosen_date, INTERVAL 1 DAY), chosen_status, spots, v_id);
                END IF;

                -- Day 3 (less likely)
                IF RAND() < 0.35 THEN
                    IF NOT EXISTS (SELECT 1 FROM events e WHERE e.venue_id = v_id AND e.date = DATE_ADD(chosen_date, INTERVAL 2 DAY)) THEN
                        INSERT INTO events (event_name, date, status, total_spots, venue_id)
                        VALUES (CONCAT(base_name, ' - Day 3'), DATE_ADD(chosen_date, INTERVAL 2 DAY), chosen_status, spots, v_id);
                    END IF;
                END IF;
            END IF;

            SET i = i + 1;
        END WHILE;

        SET v_id = v_id + 1;
    END WHILE;

END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
-- end procedure `capstone_improved`.`seed_events`

-- begin procedure `capstone_improved`.`seed_ticket_sales`
/*!50003 DROP PROCEDURE IF EXISTS `seed_ticket_sales` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `seed_ticket_sales`()
BEGIN
    DECLARE n INT DEFAULT 1;
    DECLARE max_users INT;
    DECLARE chosen_user INT;
    DECLARE chosen_ticket INT;
    DECLARE ev_date DATE;
    DECLARE sold_date DATE;

    SELECT COUNT(*) INTO max_users FROM users;

    WHILE n <= 6000 DO
        -- choose a ticket that has sold > 0
        SELECT t.id, e.date
        INTO chosen_ticket, ev_date
        FROM tickets t
        JOIN events e ON e.id = t.event_id
        WHERE t.sold > 0
        ORDER BY RAND()
        LIMIT 1;

        SET chosen_user = 1 + FLOOR(RAND() * max_users);

        -- pick a sold date from 1 to 120 days before event date (or same day)
        SET sold_date = DATE_SUB(ev_date, INTERVAL FLOOR(RAND()*120) DAY);

        -- Ensure sold_date isn't before 2007-01-01 (keep in dataset range)
        IF sold_date < '2007-01-01' THEN
            SET sold_date = '2007-01-01';
        END IF;

        INSERT INTO tickets_sold (user_id, ticket_id, date_sold)
        VALUES (chosen_user, chosen_ticket, sold_date);

        SET n = n + 1;
    END WHILE;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
-- end procedure `capstone_improved`.`seed_ticket_sales`

