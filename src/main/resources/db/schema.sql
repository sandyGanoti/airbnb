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

CREATE TABLE IF NOT EXISTS `property_to_rent` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(128) NULL,
  `property_type` VARCHAR(120) NOT NULL,
  `country` VARCHAR(128) NULL,
  `city` VARCHAR(128) NULL,
  `district` VARCHAR(128) NULL,
  `host_id`  BIGINT(20) NOT NULL,
  `price` DECIMAL NOT NULL,
  `beds` INTEGER NOT NULL,
  `bedrooms` INTEGER NOT NULL,
  `bathrooms` INTEGER NOT NULL,
  `minimum_days` INTEGER NOT NULL,
  `maximum_tenants` INTEGER NOT NULL,
  `property_size` DECIMAL NOT NULL,
  `free_text` TEXT DEFAULT NULL,
  PRIMARY KEY(`id`),
  UNIQUE KEY (`name`),
  UNIQUE KEY (`host_id`),
  KEY fk_property_user (host_id),
  CONSTRAINT fk_property_user FOREIGN KEY (host_id) REFERENCES user_ (id)
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
  `name` VARCHAR(250) NOT NULL,
  `type` VARCHAR(250) NOT NULL,
  `picByte` blob NOT NULL,
  `owner_id` BIGINT(20) NOT NULL,
  PRIMARY KEY (id)
);

INSERT INTO `user_`(id, password, first_name, last_name, username, phone_number, role, created_at, email) values(1, "22", "jon", "doe", "jonDoe", "1234", "TENANT", now(), "jon@doe.com");
INSERT INTO `user_`(id, password, first_name, last_name, username, phone_number, role, created_at, email) values(2, "222", "sandu", "doe", "sanduDoe", "1234", "TENANT", now(), "sandu@doe.com");
INSERT INTO `user_`(id, password, first_name, last_name, username, phone_number, role, created_at, email) values(3, "333", "bourdou", "doe", "bourdouDoe", "1234", "TENANT_AND_HOST", now(), "bourdou@doe.com");

INSERT INTO `property_to_rent`(id, name, property_type, country, city, district, host_id, price, beds, bedrooms, bathrooms, minimum_days, maximum_tenants, property_size, free_text) values(1, "A great place to stay by the sea", "ROOM", "Greece", "Athens", "Melissia", 1, 29.34, 2, 2, 2, 4,  4, 32, NULL);
INSERT INTO `property_to_rent`(id, name, property_type, country, city, district, host_id, price, beds, bedrooms, bathrooms, minimum_days, maximum_tenants, property_size, free_text) values(2, "A nice house", "HOUSE", "Greece", "Athens", "Patisia", 2, 29.34, 5, 3, 2, 2, 3, 30, NULL);
INSERT INTO `property_to_rent`(id, name, property_type, country, city, district, host_id, price, beds, bedrooms, bathrooms, minimum_days, maximum_tenants, property_size, free_text) values(3, "Another nice house", "HOUSE", "Greece", "Athens", "Agios Stefanos", 3, 200, 1, 1, 1, 1, 1, 28, "A nice place to stay");
INSERT INTO `property_to_rent`(id, name, property_type, country, city, district, host_id, price, beds, bedrooms, bathrooms, minimum_days, maximum_tenants, property_size, free_text) values(4, "A beautiful house", "ROOM", "Greece", "Athens", "Patisia", 1, 32, 1, 1, 3, 1, 3, 34, "Another nice place to stay");

INSERT INTO `renting_rules`(property_id, aircondition, tv, internet, living_room, kitchen, party_friendly, pet_friendly, smoking_friendly, free_text) VALUES (1, 1, 1, 1, 1, 1, 1, 1, 1, NULL);
INSERT INTO `renting_rules`(property_id, aircondition, tv, internet, living_room, kitchen, party_friendly, pet_friendly, smoking_friendly, free_text) VALUES (2, 1, 1, 0, 1, 0, 0, 1, 0, NULL);
INSERT INTO `renting_rules`(property_id, aircondition, tv, internet, living_room, kitchen, party_friendly, pet_friendly, smoking_friendly, free_text) VALUES (3, 1, 0, 0, 1, 0, 0, 1, 0, NULL);
INSERT INTO `renting_rules`(property_id, aircondition, tv, internet, living_room, kitchen, party_friendly, pet_friendly, smoking_friendly, free_text) VALUES (4, 0, 1, 0, 1, 0, 0, 1, 0, NULL);

INSERT INTO `booking`(id, tenant_id, property_id, from_datetime, to_datetime, created_at) values(1, 1, 1, now(), now(), now());

INSERT INTO `rating`(id, rater_id, property_id, host_id, text, mark, created_at) VALUES(1, 1, 1, 1, "free text review", 3, now());