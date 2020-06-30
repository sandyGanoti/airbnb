DROP DATABASE IF EXISTS `airbnb`;

CREATE SCHEMA IF NOT EXISTS`airbnb` DEFAULT CHARACTER SET utf8;
USE `airbnb`;

CREATE TABLE IF NOT EXISTS `user_` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
    `password` VARCHAR(250) NOT NULL,
    `first_name` VARCHAR(128) NOT NULL,
    `last_name` VARCHAR(128) NOT NULL,
    `username` VARCHAR(128) NOT NULL,
    `phone_number` VARCHAR(20) NOT NULL,
    `role` VARCHAR(120) NOT NULL,
    `created_at` DATETIME NOT NULL,
    `email` VARCHAR(30) NOT NULL,
    PRIMARY KEY(`id`),
    UNIQUE KEY (`username`),
    UNIQUE KEY (`email`)
);

-- https://stackoverflow.com/questions/12504208/what-mysql-data-type-should-be-used-for-latitude-longitude-with-8-decimal-places
CREATE TABLE IF NOT EXISTS `property_to_rent` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(128) NULL,
    `property_type` VARCHAR(120) NOT NULL,
    `country_id` BIGINT(20) NOT NULL,
    `city_id` BIGINT(20) NOT NULL,
    `district_id` BIGINT(20) NOT NULL,
    `host_id`  BIGINT(20) NOT NULL,
    `price` DECIMAL NOT NULL,
    `extra_price_per_person` DECIMAL NOT NULL,
    `beds` INTEGER NOT NULL,
    `bedrooms` INTEGER NOT NULL,
    `bathrooms` INTEGER NOT NULL,
    `minimum_days` INTEGER NOT NULL,
    `maximum_tenants` INTEGER NOT NULL,
    `property_size` DECIMAL NOT NULL,
    `free_text` TEXT DEFAULT NULL,
    `latitude` DECIMAL(10, 8) NOT NULL,
    `longitude` DECIMAL(11, 8) NOT NULL,
    `historic` TINYINT(1) DEFAULT FALSE,
    PRIMARY KEY(`id`),
    UNIQUE KEY (`name`),
    KEY fk_property_user (host_id)
);

CREATE TABLE IF NOT EXISTS `renting_rules` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
    `property_id` BIGINT(20) NOT NULL,
    `aircondition` TINYINT(1) DEFAULT FALSE,
    `tv` TINYINT(1) DEFAULT FALSE,
    `internet` TINYINT(1) DEFAULT FALSE,
    `living_room` TINYINT(1) DEFAULT FALSE,
    `kitchen` TINYINT(1) DEFAULT FALSE,
    `party_friendly` TINYINT(1) DEFAULT FALSE,
    `pet_friendly` TINYINT(1) DEFAULT FALSE,
    `smoking_friendly` TINYINT(1) DEFAULT FALSE,
    `free_text` TEXT DEFAULT NULL,
    PRIMARY KEY(`id`),
    KEY fk_rentingrules_property (property_id),
    CONSTRAINT fk_rentingrules_property FOREIGN KEY (property_id) REFERENCES property_to_rent (id),
    UNIQUE KEY (`property_id`)
);

CREATE TABLE IF NOT EXISTS `booking` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
    `tenant_id` BIGINT(20) NOT NULL,
    `property_id` BIGINT(20) NOT NULL,
    `from_datetime` DATETIME NOT NULL,
    `to_datetime` DATETIME NOT NULL,
    `created_at` DATETIME NOT NULL,
    PRIMARY KEY(id),
    KEY fk_booking_user (tenant_id),
    CONSTRAINT fk_booking_user FOREIGN KEY (tenant_id) REFERENCES user_ (id),
    KEY fk_booking_property (property_id),
    CONSTRAINT fk_booking_property FOREIGN KEY (property_id) REFERENCES property_to_rent (id)
);

CREATE TABLE IF NOT EXISTS `rating` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
    `rater_id` BIGINT(20) NOT NULL,
    `property_id` BIGINT(20),
    `host_id` BIGINT(20),
    `text` TEXT DEFAULT NULL,
    `mark` INTEGER NOT NULL,
    `created_at` DATETIME NOT NULL,
    PRIMARY KEY(id),
    KEY fk_rating_user (rater_id),
    CONSTRAINT fk_rating_user FOREIGN KEY (rater_id) REFERENCES user_ (id),
    KEY fk_rating_property (property_id),
    CONSTRAINT fk_rating_property FOREIGN KEY (property_id) REFERENCES property_to_rent (id),
    CONSTRAINT rating_at UNIQUE (rater_id, property_id, created_at)
);

CREATE TABLE IF NOT EXISTS `messaging` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
    `sender` BIGINT(20) NOT NULL,
    `recipient` BIGINT(20) NOT NULL,
    `created_at` DATETIME NOT NULL,
    `message_body` VARCHAR(250) NOT NULL,
    `read_status` TINYINT(1) DEFAULT FALSE,
    PRIMARY KEY (id),
    KEY fk_messaging_sender_user (sender),
    CONSTRAINT fk_messaging_sender_user FOREIGN KEY (sender) REFERENCES user_ (id),
    KEY fk_messaging_recipient_user (recipient),
    CONSTRAINT fk_messaging_recipient_user FOREIGN KEY (recipient) REFERENCES user_ (id)
);

CREATE TABLE IF NOT EXISTS `image_table` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
    `user` BIGINT(20) NULL,
    `property` BIGINT(20) NULL,
    `name` VARCHAR(250) NOT NULL,
    `type` VARCHAR(250) NOT NULL,
    `picture` blob NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS `country` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(250) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS `city` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
    `name` varchar(200) NOT NULL,
    `country_id` BIGINT(20) NOT NULL,
    PRIMARY KEY (`id`),
    KEY fk_city_country (country_id),
    CONSTRAINT fk_city_country FOREIGN KEY (country_id) REFERENCES country (id)
);

CREATE TABLE IF NOT EXISTS `district` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
    `name` varchar(200) NOT NULL,
    `city_id` BIGINT(20) NOT NULL,
    PRIMARY KEY (`id`),
    KEY fk_district_city (city_id),
    CONSTRAINT fk_district_city FOREIGN KEY (city_id) REFERENCES city (id)
);

CREATE TABLE IF NOT EXISTS `property_availability` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
    `property_id` BIGINT(20) NOT NULL,
    `available_from` DATETIME NOT NULL,
    `available_to` DATETIME NOT NULL,
    PRIMARY KEY (`id`),
    KEY fk_property_availability (property_id),
    CONSTRAINT fk_property_availability FOREIGN KEY (property_id) REFERENCES property_to_rent (id)
);

INSERT INTO `user_`(id, password, first_name, last_name, username, phone_number, role, created_at, email) values(1, "$2a$10$JXKXOqdQzQ5QgYGQKTOdbuHE9nCX3REzkJ86BewrDCEc39FyKzx.y", "user1", "user1", "user1", "1234", "TENANT", now(), "user1@user1.com");
INSERT INTO `user_`(id, password, first_name, last_name, username, phone_number, role, created_at, email) values(2, "$2a$10$AnrFPILx2oOeFVgVgpovj.lQXi9oMMY2r8nyAq1mhO7LpCF9K8ZS2", "user2", "user2", "user2", "1234", "TENANT", now(), "user2@user2.com");
INSERT INTO `user_`(id, password, first_name, last_name, username, phone_number, role, created_at, email) values(3, "$2a$10$JHyawNxERXu8XgSQsy/uXe/EdU8Nr3SCEgm0MxFN5pS2JRwk8bx6u", "user3", "user3", "user3", "1234", "TENANT_AND_HOST", now(), "user3@user3.com");

INSERT INTO `property_to_rent`(id, name, property_type, country_id, city_id, district_id, host_id, price, beds, bedrooms, bathrooms, minimum_days, maximum_tenants, property_size, free_text, latitude, longitude, historic) values(1, "A great place to stay by the sea", "ROOM", 1, 1, 2, 1, 29.34, 2, 2, 2, 4,  4, 32, NULL, 40.71727401, -74.00898606, 1);
INSERT INTO `property_to_rent`(id, name, property_type, country_id, city_id, district_id, host_id, price, beds, bedrooms, bathrooms, minimum_days, maximum_tenants, property_size, free_text, latitude, longitude, historic) values(2, "A nice house", "HOUSE", 1, 1, 1, 2, 29.34, 5, 3, 2, 2, 3, 30, NULL, 40.71727401, -74.00898606, 1);
INSERT INTO `property_to_rent`(id, name, property_type, country_id, city_id, district_id, host_id, price, beds, bedrooms, bathrooms, minimum_days, maximum_tenants, property_size, free_text, latitude, longitude, historic) values(3, "Another nice house", "HOUSE", 1, 1, 3, 3, 200, 1, 1, 1, 1, 1, 28, "A nice place to stay", 40.71727401, -74.00898606, 1);
INSERT INTO `property_to_rent`(id, name, property_type, country_id, city_id, district_id, host_id, price, beds, bedrooms, bathrooms, minimum_days, maximum_tenants, property_size, free_text, latitude, longitude, historic) values(4, "A beautiful house", "ROOM", 1, 1, 4, 1, 32, 1, 1, 3, 1, 3, 34, "Another nice place to stay", 40.71727401, -74.00898606, 1);

INSERT INTO `renting_rules`(property_id, aircondition, tv, internet, living_room, kitchen, party_friendly, pet_friendly, smoking_friendly, free_text) VALUES (1, 1, 1, 1, 1, 1, 1, 1, 1, NULL);
INSERT INTO `renting_rules`(property_id, aircondition, tv, internet, living_room, kitchen, party_friendly, pet_friendly, smoking_friendly, free_text) VALUES (2, 1, 1, 0, 1, 0, 0, 1, 0, NULL);
INSERT INTO `renting_rules`(property_id, aircondition, tv, internet, living_room, kitchen, party_friendly, pet_friendly, smoking_friendly, free_text) VALUES (3, 1, 0, 0, 1, 0, 0, 1, 0, NULL);
INSERT INTO `renting_rules`(property_id, aircondition, tv, internet, living_room, kitchen, party_friendly, pet_friendly, smoking_friendly, free_text) VALUES (4, 0, 1, 0, 1, 0, 0, 1, 0, NULL);

INSERT INTO `booking`(id, tenant_id, property_id, from_datetime, to_datetime, created_at) values(1, 1, 1, now(), now(), now());

INSERT INTO `rating`(id, rater_id, property_id, host_id, text, mark, created_at) VALUES(1, 1, 1, 1, "free text review", 3, now());

INSERT INTO `messaging`(id, sender, recipient, created_at, message_body, read_status) VALUES(1, 1, 1, now(), "la la", 0);
INSERT INTO `messaging`(id, sender, recipient, created_at, message_body, read_status) VALUES(2, 1, 1, now(), "la la", 1);

INSERT INTO `country`(id, name) VALUES(1, "Greece");
INSERT INTO `country`(id, name) VALUES(2, "England");
INSERT INTO `country`(id, name) VALUES(3, "Scotland");

INSERT INTO `city`(id, name, country_id) VALUES(1, "Athens", 1);
INSERT INTO `city`(id, name, country_id) VALUES(2, "London", 2);
INSERT INTO `city`(id, name, country_id) VALUES(3, "Edinburgh", 3);

INSERT INTO `district`(id, name, city_id) VALUES(1, "Plaka",1);
INSERT INTO `district`(id, name, city_id) VALUES(2, "Kolonaki",1);
INSERT INTO `district`(id, name, city_id) VALUES(3, "Exarchia",1);
INSERT INTO `district`(id, name, city_id) VALUES(4, "Psirri",1);
INSERT INTO `district`(id, name, city_id) VALUES(5, "Gazi",1);
INSERT INTO `district`(id, name, city_id) VALUES(6, "Thissio",1);
INSERT INTO `district`(id, name, city_id) VALUES(7, "Metaxourgio",1);

INSERT INTO `district`(id, name, city_id) VALUES(8, "Whitehall and Westminster",2);
INSERT INTO `district`(id, name, city_id) VALUES(9, "Piccadilly and St James’s",2);
INSERT INTO `district`(id, name, city_id) VALUES(10, "Soho and Trafalgar Square",2);
INSERT INTO `district`(id, name, city_id) VALUES(11, "Covent Garden and Strand",2);
INSERT INTO `district`(id, name, city_id) VALUES(12, "Bloomsbury and Fitzrovia",2);
INSERT INTO `district`(id, name, city_id) VALUES(13, "Holborn and Inns of Court",2);
INSERT INTO `district`(id, name, city_id) VALUES(14, "The City",1);

INSERT INTO `district`(id, name, city_id) VALUES(15, "Central Edinburgh",3);
INSERT INTO `district`(id, name, city_id) VALUES(16, "Stockbridge",3);
INSERT INTO `district`(id, name, city_id) VALUES(17, "Leith",3);
INSERT INTO `district`(id, name, city_id) VALUES(18, "Morningside and Bruntsfield",3);
INSERT INTO `district`(id, name, city_id) VALUES(19, "Newington and the South Bridge Area",3);
