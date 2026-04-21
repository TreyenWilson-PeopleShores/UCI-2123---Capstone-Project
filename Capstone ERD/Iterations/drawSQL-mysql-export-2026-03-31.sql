CREATE TABLE `Events`(
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `Event Name` BIGINT NOT NULL,
    `Date` BIGINT NOT NULL,
    `Status` BIGINT NOT NULL,
    `Total Spots` BIGINT NOT NULL,
    `Venue id` BIGINT NOT NULL
);
ALTER TABLE
    `Events` ADD UNIQUE `events_venue id_unique`(`Venue id`);
CREATE TABLE `Venues`(
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `Venue Name` BIGINT NOT NULL,
    `Location` BIGINT NOT NULL,
    `Total Capacity` BIGINT NOT NULL
);
CREATE TABLE `Tickets`(
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `Event id` BIGINT NOT NULL,
    `Price` BIGINT NOT NULL,
    `Total Quantity` BIGINT NOT NULL,
    `Sold` BIGINT NOT NULL
);
ALTER TABLE
    `Tickets` ADD UNIQUE `tickets_event id_unique`(`Event id`);
CREATE TABLE `Users`(
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `Username` BIGINT NOT NULL,
    `Password` BIGINT NOT NULL,
    `Role` BIGINT NOT NULL
);
CREATE TABLE `Tickets Sold`(
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `User id` BIGINT NOT NULL,
    `Ticket id` BIGINT NOT NULL,
    `Date Sold` BIGINT NOT NULL
);
ALTER TABLE
    `Tickets Sold` ADD UNIQUE `tickets sold_user id_unique`(`User id`);
ALTER TABLE
    `Tickets Sold` ADD UNIQUE `tickets sold_ticket id_unique`(`Ticket id`);
ALTER TABLE
    `Events` ADD CONSTRAINT `events_id_foreign` FOREIGN KEY(`id`) REFERENCES `Tickets`(`Event id`);
ALTER TABLE
    `Tickets Sold` ADD CONSTRAINT `tickets sold_ticket id_foreign` FOREIGN KEY(`Ticket id`) REFERENCES `Tickets`(`id`);
ALTER TABLE
    `Events` ADD CONSTRAINT `events_venue id_foreign` FOREIGN KEY(`Venue id`) REFERENCES `Venues`(`id`);
ALTER TABLE
    `Tickets Sold` ADD CONSTRAINT `tickets sold_user id_foreign` FOREIGN KEY(`User id`) REFERENCES `Users`(`id`);