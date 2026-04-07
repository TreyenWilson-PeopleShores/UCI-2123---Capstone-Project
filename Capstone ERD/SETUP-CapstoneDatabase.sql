
CREATE DATABASE capstone_improved;
use capstone_improved;
CREATE TABLE events (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    event_name VARCHAR(255) NOT NULL,
    date DATE NOT NULL,
    status ENUM('SCHEDULED','CANCELLED','COMPLETED') NOT NULL,
    total_spots INT NOT NULL,
    venue_id INT NOT NULL
);

ALTER TABLE events
ADD UNIQUE events_venue_date_unique (venue_id, date);

CREATE TABLE venues (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    venue_name VARCHAR(255) NOT NULL,
    location VARCHAR(255) NOT NULL,
    total_capacity BIGINT NOT NULL
);

CREATE TABLE tickets (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    event_id INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    total_quantity INT NOT NULL,
    sold INT NOT NULL
);

ALTER TABLE tickets
ADD UNIQUE tickets_event_id_unique (event_id);

CREATE TABLE users (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'USER')
);

CREATE TABLE tickets_sold (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    ticket_id INT NOT NULL,
    date_sold DATE NOT NULL
);

ALTER TABLE events
ADD CONSTRAINT events_venue_id_foreign
FOREIGN KEY (venue_id) REFERENCES venues (id);

ALTER TABLE tickets
ADD CONSTRAINT tickets_event_id_foreign
FOREIGN KEY (event_id) REFERENCES events (id);

ALTER TABLE tickets_sold
ADD CONSTRAINT tickets_sold_user_id_foreign
FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE tickets_sold
ADD CONSTRAINT tickets_sold_ticket_id_foreign
FOREIGN KEY (ticket_id) REFERENCES tickets (id);


-- ===========================
-- BASE DATA: VENUES + USERS
-- ===========================

INSERT INTO venues (venue_name, location, total_capacity) VALUES
('Columbus Convention Center', 'Columbus, OH', 20000),
('Nationwide Arena', 'Columbus, OH', 18000),
('House of Blues', 'Cleveland, OH', 2500),
('Riverfront Pavilion', 'Cincinnati, OH', 12000),
('Grant Park', 'Chicago, IL', 50000),
('United Center', 'Chicago, IL', 23000),
('Madison Square Garden', 'New York, NY', 21000),
('Barclays Center', 'Brooklyn, NY', 19000),
('TD Garden', 'Boston, MA', 19500),
('Wells Fargo Center', 'Philadelphia, PA', 21000),
('Crypto.com Arena', 'Los Angeles, CA', 20000),
('Hollywood Bowl', 'Los Angeles, CA', 17500),
('Red Rocks Amphitheatre', 'Morrison, CO', 9525),
('Ball Arena', 'Denver, CO', 19500),
('AT&T Stadium', 'Arlington, TX', 80000),
('NRG Stadium', 'Houston, TX', 72000),
('Kaseya Center', 'Miami, FL', 19000),
('Amalie Arena', 'Tampa, FL', 20000),
('Wembley Stadium', 'London, UK', 90000),
('O2 Arena', 'London, UK', 20000);

INSERT INTO users (username, password, role) VALUES
('admin', 'adminpass', 'ADMIN'),
('treyen', 'password123', 'USER'),
('alex', 'password123', 'USER'),
('jordan', 'password123', 'USER'),
('sam', 'password123', 'USER'),
('casey', 'password123', 'USER'),
('taylor', 'password123', 'USER'),
('morgan', 'password123', 'USER');

-- =========================================================
-- PROCEDURE: GENERATE ~800 EVENTS (2007–2030)
-- - Each venue gets ~40 event-days spread across the timeline
-- - Some events become multi-day by inserting consecutive days
-- - CANCELLED sprinkled in
-- =========================================================

DELIMITER $$

DROP PROCEDURE IF EXISTS seed_events $$
CREATE PROCEDURE seed_events()
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

END $$

DELIMITER ;

-- Run the seeder
CALL seed_events();

-- =========================================================
-- TICKETS: one ticket row per event (enforces UNIQUE(event_id))
-- price depends on venue size + slight randomness
-- sold respects status:
--   COMPLETED: 60–98% sold
--   SCHEDULED: 0–40% sold
--   CANCELLED: 0 sold (clean default)
-- =========================================================

INSERT INTO tickets (event_id, price, total_quantity, sold)
SELECT
    e.id AS event_id,
    -- price: base on size
    ROUND(
        CASE
            WHEN e.total_spots >= 70000 THEN 180 + (RAND()*40)
            WHEN e.total_spots >= 20000 THEN 85 + (RAND()*35)
            WHEN e.total_spots >= 8000  THEN 45 + (RAND()*25)
            ELSE 20 + (RAND()*20)
        END
    , 2) AS price,
    e.total_spots AS total_quantity,
    CASE
        WHEN e.status = 'CANCELLED' THEN 0
        WHEN e.status = 'COMPLETED' THEN FLOOR(e.total_spots * (0.60 + RAND()*0.38))
        ELSE FLOOR(e.total_spots * (RAND()*0.40))
    END AS sold
FROM events e;

-- =========================================================
-- TICKET SALES: create many tickets_sold rows
-- We generate ~6000 sales records:
-- - choose random user and random ticket
-- - date_sold is before event date when possible
-- - for CANCELLED tickets (sold=0) this will naturally avoid them by selection filter
-- =========================================================

DELIMITER $$

DROP PROCEDURE IF EXISTS seed_ticket_sales $$
CREATE PROCEDURE seed_ticket_sales()
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
END $$

DELIMITER ;

CALL seed_ticket_sales();

-- =========================================================
-- QUICK CHECKS (optional)
-- =========================================================
-- How many events?
-- SELECT COUNT(*) AS total_events FROM events;

-- Status distribution
-- SELECT status, COUNT(*) AS cnt FROM events GROUP BY status;

-- Verify no double-booking
-- SELECT venue_id, date, COUNT(*) c FROM events GROUP BY venue_id, date HAVING c > 1;

-- A few cancelled examples
-- SELECT id, event_name, date, venue_id FROM events WHERE status='CANCELLED' ORDER BY date LIMIT 20;
