DROP DATABASE IF EXISTS `airbnb`;

CREATE SCHEMA IF NOT EXISTS`airbnb` DEFAULT CHARACTER SET utf8;
USE `airbnb`;

CREATE TABLE IF NOT EXISTS `airbnb`.`user` (
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

CREATE TABLE IF NOT EXISTS `airbnb`.`homestay` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(128) NULL,
  `country` VARCHAR(128) NULL,
  `city` VARCHAR(128) NULL,
  `region` VARCHAR(128) NULL,
  `landlord_id`  BIGINT(20) NOT NULL,
  `price` DECIMAL NOT NULL,
  `beds` INTEGER NOT NULL,
  `bedrooms` INTEGER NOT NULL,
  `bathrooms` INTEGER NOT NULL,
  `minimum_days` INTEGER NOT NULL,
  `minimum_tenants` INTEGER NOT NULL,
  `maximum_tenants` INTEGER NOT NULL,
  `property_size` DECIMAL NOT NULL,
  `free_text` TEXT DEFAULT NULL,
  PRIMARY KEY(`id`),
  KEY fk_homestay_user (landlord_id),
  CONSTRAINT fk_homestay_user FOREIGN KEY (landlord_id) REFERENCES user (id)
);

CREATE TABLE IF NOT EXISTS `airbnb`.`renting_rules` (
  `homestay_id` BIGINT(20) NOT NULL,
  `aircondition` TINYINT(1) DEFAULT FALSE,
  `tv` TINYINT(1) DEFAULT FALSE,
  `internet` TINYINT(1) DEFAULT FALSE,
  `living_room` TINYINT(1) DEFAULT FALSE,
  `kitchen` TINYINT(1) DEFAULT FALSE,
  `party_friendly` TINYINT(1) DEFAULT FALSE,
  `party_friendly` TINYINT(1) DEFAULT FALSE,
  `smoking_friendly` TINYINT(1) DEFAULT FALSE,
  `free_text` TEXT DEFAULT NULL,
  PRIMARY KEY(`homestay_id`),
  KEY fk_rentingrules_user (homestay_id),
  CONSTRAINT fk_rentingrules_user FOREIGN KEY (homestay_id) REFERENCES homestay (id)
);

CREATE TABLE IF NOT EXISTS `airbnb`.`booking` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `tenant_id` BIGINT(20) NOT NULL,
  `homestay_id` BIGINT(20) NOT NULL,
  `from_datetime` DATETIME NOT NULL,
  `to_datetime` DATETIME NOT NULL,
  `created_at` DATETIME NOT NULL,
  PRIMARY KEY(id),
  KEY fk_booking_user (tenant_id),
  CONSTRAINT fk_booking_user FOREIGN KEY (tenant_id) REFERENCES user (id),
  KEY fk_booking_homestay (homestay_id),
  CONSTRAINT fk_booking_homestay FOREIGN KEY (homestay_id) REFERENCES homestay (id)
);

CREATE TABLE IF NOT EXISTS `airbnb`.`rating` (
  `rater_id` BIGINT(20) NOT NULL,
  `homestay_id` BIGINT(20) NOT NULL,
  `rating` INTEGER NOT NULL,
  `created_at` DATETIME NOT NULL,
  PRIMARY KEY (rater_id, homestay_id),
  KEY fk_rating_user (rater_id),
  CONSTRAINT fk_rating_user FOREIGN KEY (rater_id) REFERENCES user (id),
  KEY fk_rating_homestay (homestay_id),
  CONSTRAINT fk_rating_homestay FOREIGN KEY (homestay_id) REFERENCES homestay (id),
  CONSTRAINT rating_at UNIQUE (rater_id, homestay_id, created_at)
);

CREATE TABLE IF NOT EXISTS `airbnb`.`messaging` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `sender` BIGINT(20) NOT NULL,
  `recipient` BIGINT(20) NOT NULL,
  `created_at` DATETIME NOT NULL,
  `message_body` VARCHAR(250) NOT NULL,
  `read_status` TINYINT(1) DEFAULT FALSE,
  PRIMARY KEY (id),
  KEY fk_messaging_sender_user (sender),
  CONSTRAINT fk_messaging_sender_user FOREIGN KEY (sender) REFERENCES user (id),
  KEY fk_messaging_recipient_user (recipient),
  CONSTRAINT fk_messaging_recipient_user FOREIGN KEY (recipient) REFERENCES user (id)
);

INSERT INTO `airbnb`.`user`(id, password, first_name, last_name, username, phone_number, role, created_at, email) values(1, "22", "jon", "doe", "jonDoe", "1234", "TENANT", now(), "jon@doe.com");
INSERT INTO `airbnb`.`user`(id, password, first_name, last_name, username, phone_number, role, created_at, email) values(2, "222", "sandu", "doe", "sanduDoe", "1234", "TENANT", now(), "sandu@doe.com");
INSERT INTO `airbnb`.`user`(id, password, first_name, last_name, username, phone_number, role, created_at, email) values(3, "333", "bourdou", "doe", "bourdouDoe", "1234", "TENANT_AND_LANDLORD", now(), "bourdou@doe.com");

INSERT INTO `airbnb`.`homestay`(id, name, country, city, region, landlord_id, price, beds, bedrooms, bathrooms, minimum_days, minimum_tenants, maximum_tenants, property_size, free_text) values(1, "A great place to stay by the sea", "Greece", "Athens", "Melissia", 1, 29.34, 2, 2, 2, 4, 2, 4, 32, NULL);
INSERT INTO `airbnb`.`homestay`(id, name, country, city, region, landlord_id, price, beds, bedrooms, bathrooms, minimum_days, minimum_tenants, maximum_tenants, property_size, free_text) values(2, "A nice house", "Greece", "Athens", "Patisia", 2, 29.34, 5, 3, 2, 2, 3, 3, 30, NULL);
INSERT INTO `airbnb`.`homestay`(id, name, country, city, region, landlord_id, price, beds, bedrooms, bathrooms, minimum_days, minimum_tenants, maximum_tenants, property_size, free_text) values(3, "Another nice house", "Greece", "Athens", "Agios Stefanos", 3, 200, 1, 1, 1, 1, 1, 1, 28, "A nice place to stay");
INSERT INTO `airbnb`.`homestay`(id, name, country, city, region, landlord_id, price, beds, bedrooms, bathrooms, minimum_days, minimum_tenants, maximum_tenants, property_size, free_text) values(4, "A beautiful house", "Greece", "Athens", "Patisia", 1, 32, 1, 1, 3, 1, 1, 3, 34, "Another nice place to stay");

INSERT INTO `airbnb`.`renting_rules`(homestay_id, aircondition, tv, internet, living_room, kitchen, party_friendly, pet_friendly, smoking, free_text) VALUES (1, 1, 1, 1, 1, 1, 1, 1, 1, NULL);
INSERT INTO `airbnb`.`renting_rules`(homestay_id, aircondition, tv, internet, living_room, kitchen, party_friendly, pet_friendly, smoking, free_text) VALUES (2, 1, 1, 0, 1, 0, 0, 1, 0, NULL);
INSERT INTO `airbnb`.`renting_rules`(homestay_id, aircondition, tv, internet, living_room, kitchen, party_friendly, pet_friendly, smoking, free_text) VALUES (3, 1, 0, 0, 1, 0, 0, 1, 0, NULL);
INSERT INTO `airbnb`.`renting_rules`(homestay_id, aircondition, tv, internet, living_room, kitchen, party_friendly, pet_friendly, smoking, free_text) VALUES (4, 0, 1, 0, 1, 0, 0, 1, 0, NULL);
