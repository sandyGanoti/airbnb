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
    PRIMARY KEY(id)
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
    `picture` longblob NOT NULL,
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

CREATE TABLE IF NOT EXISTS `user_activity` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT(20) NOT NULL,
    `property_id` BIGINT(20) NOT NULL,
    PRIMARY KEY (`id`),
    KEY fk_user_activity_user (user_id),
    CONSTRAINT fk_user_activity_user FOREIGN KEY (user_id) REFERENCES user_ (id),
    KEY fk_user_activity_property (property_id),
    CONSTRAINT fk_user_activity_property FOREIGN KEY (property_id) REFERENCES property_to_rent (id)
);

-- create user data
INSERT INTO `user_`(id, password, first_name, last_name, username, phone_number, role, created_at, email) values(1, "$2a$10$t0gJsVamNGsTzowL3eCQguZx8n8S2i8Jt5msrPUrcBl7epybhS6wS", "user1", "user1", "user1", "1234", "TENANT", now(), "user1@user1.com");
INSERT INTO `user_`(id, password, first_name, last_name, username, phone_number, role, created_at, email) values(2, "$2a$10$t0gJsVamNGsTzowL3eCQguZx8n8S2i8Jt5msrPUrcBl7epybhS6wS", "user2", "user2", "user2", "1234", "TENANT", now(), "user2@user2.com");
INSERT INTO `user_`(id, password, first_name, last_name, username, phone_number, role, created_at, email) values(3, "$2a$10$t0gJsVamNGsTzowL3eCQguZx8n8S2i8Jt5msrPUrcBl7epybhS6wS", "user3", "user3", "user3", "1234", "TENANT_AND_HOST", now(), "user3@user3.com");
INSERT INTO `user_`(id, password, first_name, last_name, username, phone_number, role, created_at, email) values(4, "$2a$10$t0gJsVamNGsTzowL3eCQguZx8n8S2i8Jt5msrPUrcBl7epybhS6wS", "user4", "user4", "user4", "1234", "TENANT_AND_HOST", now(), "user4@user4.com");
INSERT INTO `user_`(id, password, first_name, last_name, username, phone_number, role, created_at, email) values(5, "$2a$10$t0gJsVamNGsTzowL3eCQguZx8n8S2i8Jt5msrPUrcBl7epybhS6wS", "user5", "user5", "user5", "1234", "TENANT_AND_HOST", now(), "user5@user5.com");
INSERT INTO `user_`(id, password, first_name, last_name, username, phone_number, role, created_at, email) values(6, "$2a$10$t0gJsVamNGsTzowL3eCQguZx8n8S2i8Jt5msrPUrcBl7epybhS6wS", "user6", "user6", "user6", "1234", "TENANT_AND_HOST", now(), "user6@user6.com");
INSERT INTO `user_`(id, password, first_name, last_name, username, phone_number, role, created_at, email) values(7, "$2a$10$t0gJsVamNGsTzowL3eCQguZx8n8S2i8Jt5msrPUrcBl7epybhS6wS", "user7", "user7", "user7", "1234", "TENANT_AND_HOST", now(), "user7@user7.com");
INSERT INTO `user_`(id, password, first_name, last_name, username, phone_number, role, created_at, email) values(8, "$2a$10$t0gJsVamNGsTzowL3eCQguZx8n8S2i8Jt5msrPUrcBl7epybhS6wS", "user8", "user8", "user8", "1234", "TENANT_AND_HOST", now(), "user8@user8.com");
INSERT INTO `user_`(id, password, first_name, last_name, username, phone_number, role, created_at, email) values(9, "$2a$10$t0gJsVamNGsTzowL3eCQguZx8n8S2i8Jt5msrPUrcBl7epybhS6wS", "user9", "user9", "user9", "1234", "TENANT_AND_HOST", now(), "user9@user9.com");
INSERT INTO `user_`(id, password, first_name, last_name, username, phone_number, role, created_at, email) values(10, "$2a$10$t0gJsVamNGsTzowL3eCQguZx8n8S2i8Jt5msrPUrcBl7epybhS6wS", "user10", "user10", "user10", "1234", "TENANT_AND_HOST", now(), "user10@user10.com");
INSERT INTO `user_`(id, password, first_name, last_name, username, phone_number, role, created_at, email) values(11, "$2a$10$t0gJsVamNGsTzowL3eCQguZx8n8S2i8Jt5msrPUrcBl7epybhS6wS", "use11", "user11", "user11", "1234", "TENANT_AND_HOST", now(), "user11@user11.com");
INSERT INTO `user_`(id, password, first_name, last_name, username, phone_number, role, created_at, email) values(12, "$2a$10$t0gJsVamNGsTzowL3eCQguZx8n8S2i8Jt5msrPUrcBl7epybhS6wS", "user12", "user12", "user12", "1234", "TENANT_AND_HOST", now(), "user12@user12.com");
INSERT INTO `user_`(id, password, first_name, last_name, username, phone_number, role, created_at, email) values(13, "$2a$10$t0gJsVamNGsTzowL3eCQguZx8n8S2i8Jt5msrPUrcBl7epybhS6wS", "user13", "user13", "user13", "1234", "TENANT_AND_HOST", now(), "user13@user13.com");
INSERT INTO `user_`(id, password, first_name, last_name, username, phone_number, role, created_at, email) values(14, "$2a$10$t0gJsVamNGsTzowL3eCQguZx8n8S2i8Jt5msrPUrcBl7epybhS6wS", "user14", "user14", "user14", "1234", "TENANT_AND_HOST", now(), "user14@user14.com");
INSERT INTO `user_`(id, password, first_name, last_name, username, phone_number, role, created_at, email) values(15, "$2a$10$t0gJsVamNGsTzowL3eCQguZx8n8S2i8Jt5msrPUrcBl7epybhS6wS", "user15", "user15", "user15", "1234", "TENANT_AND_HOST", now(), "user15@user15.com");
INSERT INTO `user_`(id, password, first_name, last_name, username, phone_number, role, created_at, email) values(16, "$2a$10$t0gJsVamNGsTzowL3eCQguZx8n8S2i8Jt5msrPUrcBl7epybhS6wS", "user16", "user16", "user16", "1234", "TENANT_AND_HOST", now(), "user16@user16.com");
INSERT INTO `user_`(id, password, first_name, last_name, username, phone_number, role, created_at, email) values(17, "$2a$10$t0gJsVamNGsTzowL3eCQguZx8n8S2i8Jt5msrPUrcBl7epybhS6wS", "user17", "user17", "user17", "1234", "TENANT_AND_HOST", now(), "user17@user17.com");
INSERT INTO `user_`(id, password, first_name, last_name, username, phone_number, role, created_at, email) values(18, "$2a$10$t0gJsVamNGsTzowL3eCQguZx8n8S2i8Jt5msrPUrcBl7epybhS6wS", "user18", "user18", "user18", "1234", "TENANT_AND_HOST", now(), "user18@user18.com");
INSERT INTO `user_`(id, password, first_name, last_name, username, phone_number, role, created_at, email) values(19, "$2a$10$t0gJsVamNGsTzowL3eCQguZx8n8S2i8Jt5msrPUrcBl7epybhS6wS", "user19", "user19", "user19", "1234", "TENANT_AND_HOST", now(), "user19@user19.com");
INSERT INTO `user_`(id, password, first_name, last_name, username, phone_number, role, created_at, email) values(20, "$2a$10$t0gJsVamNGsTzowL3eCQguZx8n8S2i8Jt5msrPUrcBl7epybhS6wS", "user20", "user20", "user20", "1234", "TENANT_AND_HOST", now(), "user20@user20.com");
INSERT INTO `user_`(id, password, first_name, last_name, username, phone_number, role, created_at, email) values(21, "$2a$10$t0gJsVamNGsTzowL3eCQguZx8n8S2i8Jt5msrPUrcBl7epybhS6wS", "user21", "user21", "user21", "1234", "TENANT_AND_HOST", now(), "user21@user21.com");
INSERT INTO `user_`(id, password, first_name, last_name, username, phone_number, role, created_at, email) values(22, "$2a$10$t0gJsVamNGsTzowL3eCQguZx8n8S2i8Jt5msrPUrcBl7epybhS6wS", "user22", "user22", "user22", "1234", "TENANT_AND_HOST", now(), "user22@user22.com");
INSERT INTO `user_`(id, password, first_name, last_name, username, phone_number, role, created_at, email) values(23, "$2a$10$t0gJsVamNGsTzowL3eCQguZx8n8S2i8Jt5msrPUrcBl7epybhS6wS", "user23", "user23", "user23", "1234", "TENANT_AND_HOST", now(), "user23@user23.com");
INSERT INTO `user_`(id, password, first_name, last_name, username, phone_number, role, created_at, email) values(24, "$2a$10$t0gJsVamNGsTzowL3eCQguZx8n8S2i8Jt5msrPUrcBl7epybhS6wS", "user24", "user24", "user24", "1234", "TENANT_AND_HOST", now(), "user24@user24.com");
INSERT INTO `user_`(id, password, first_name, last_name, username, phone_number, role, created_at, email) values(25, "$2a$10$t0gJsVamNGsTzowL3eCQguZx8n8S2i8Jt5msrPUrcBl7epybhS6wS", "user25", "user25", "user25", "1234", "TENANT_AND_HOST", now(), "user25@user25.com");
INSERT INTO `user_`(id, password, first_name, last_name, username, phone_number, role, created_at, email) values(26, "$2a$10$t0gJsVamNGsTzowL3eCQguZx8n8S2i8Jt5msrPUrcBl7epybhS6wS", "user26", "user26", "user26", "1234", "TENANT_AND_HOST", now(), "user26@user26.com");
INSERT INTO `user_`(id, password, first_name, last_name, username, phone_number, role, created_at, email) values(27, "$2a$10$t0gJsVamNGsTzowL3eCQguZx8n8S2i8Jt5msrPUrcBl7epybhS6wS", "user27", "user27", "user27", "1234", "TENANT_AND_HOST", now(), "user27@user27.com");
INSERT INTO `user_`(id, password, first_name, last_name, username, phone_number, role, created_at, email) values(28, "$2a$10$t0gJsVamNGsTzowL3eCQguZx8n8S2i8Jt5msrPUrcBl7epybhS6wS", "user28", "user28", "user28", "1234", "TENANT_AND_HOST", now(), "user28@user28.com");
INSERT INTO `user_`(id, password, first_name, last_name, username, phone_number, role, created_at, email) values(29, "$2a$10$t0gJsVamNGsTzowL3eCQguZx8n8S2i8Jt5msrPUrcBl7epybhS6wS", "user29", "user29", "user29", "1234", "TENANT_AND_HOST", now(), "user29@user29.com");
INSERT INTO `user_`(id, password, first_name, last_name, username, phone_number, role, created_at, email) values(30, "$2a$10$t0gJsVamNGsTzowL3eCQguZx8n8S2i8Jt5msrPUrcBl7epybhS6wS", "user30", "user30", "user30", "1234", "TENANT_AND_HOST", now(), "user30@user30.com");
INSERT INTO `user_`(id, password, first_name, last_name, username, phone_number, role, created_at, email) values(31, "$2a$10$t0gJsVamNGsTzowL3eCQguZx8n8S2i8Jt5msrPUrcBl7epybhS6wS", "user31", "user31", "user31", "1234", "TENANT_AND_HOST", now(), "user31@user31.com");
INSERT INTO `user_`(id, password, first_name, last_name, username, phone_number, role, created_at, email) values(32, "$2a$10$t0gJsVamNGsTzowL3eCQguZx8n8S2i8Jt5msrPUrcBl7epybhS6wS", "user32", "user32", "user32", "1234", "TENANT_AND_HOST", now(), "user32@user32.com");
INSERT INTO `user_`(id, password, first_name, last_name, username, phone_number, role, created_at, email) values(33, "$2a$10$t0gJsVamNGsTzowL3eCQguZx8n8S2i8Jt5msrPUrcBl7epybhS6wS", "user33", "user33", "user33", "1234", "TENANT_AND_HOST", now(), "user33@user33.com");

-- create property data
INSERT INTO `property_to_rent`(id, name, property_type, country_id, city_id, district_id, host_id, price, beds, bedrooms, bathrooms, minimum_days, maximum_tenants, property_size, free_text, latitude, longitude, historic) values(1, "Room 1", "ROOM", 1, 1, 2, 1, 29.34, 2, 2, 2, 4,  4, 32, NULL, 40.71727401, -74.00898606, 1);
INSERT INTO `property_to_rent`(id, name, property_type, country_id, city_id, district_id, host_id, price, beds, bedrooms, bathrooms, minimum_days, maximum_tenants, property_size, free_text, latitude, longitude, historic) values(2, "Room 3", "ROOM", 1, 1, 1, 2, 29.34, 5, 3, 2, 2, 3, 30, NULL, 40.71727401, -74.00898606, 1);
INSERT INTO `property_to_rent`(id, name, property_type, country_id, city_id, district_id, host_id, price, beds, bedrooms, bathrooms, minimum_days, maximum_tenants, property_size, free_text, latitude, longitude, historic) values(3, "Room 3", "ROOM", 1, 1, 3, 3, 200, 1, 1, 1, 1, 1, 28, "A nice place to stay", 40.71727401, -74.00898606, 1);
INSERT INTO `property_to_rent`(id, name, property_type, country_id, city_id, district_id, host_id, price, beds, bedrooms, bathrooms, minimum_days, maximum_tenants, property_size, free_text, latitude, longitude, historic) values(4, "Room 4", "ROOM", 1, 1, 4, 1, 32, 1, 1, 3, 1, 3, 34, "Another nice place to stay", 40.71727401, -74.00898606, 1);
INSERT INTO `property_to_rent`(id, name, property_type, country_id, city_id, district_id, host_id, price, beds, bedrooms, bathrooms, minimum_days, maximum_tenants, property_size, free_text, latitude, longitude, historic) values(5, "Room 5", "ROOM", 1, 1, 4, 5, 32, 1, 1, 3, 1, 3, 34, "la la", 40.71727401, -74.00898606, 0);
INSERT INTO `property_to_rent`(id, name, property_type, country_id, city_id, district_id, host_id, price, beds, bedrooms, bathrooms, minimum_days, maximum_tenants, property_size, free_text, latitude, longitude, historic) values(6, "Room 6", "ROOM", 1, 1, 4, 6, 2, 1, 1, 3, 1, 3, 34, "la la", 40.71727401, -74.00898606, 0);
INSERT INTO `property_to_rent`(id, name, property_type, country_id, city_id, district_id, host_id, price, beds, bedrooms, bathrooms, minimum_days, maximum_tenants, property_size, free_text, latitude, longitude, historic) values(7, "Room 7", "ROOM", 1, 1, 4, 7, 29, 1, 1, 3, 1, 3, 34, "la la", 40.71727401, -74.00898606, 0);
INSERT INTO `property_to_rent`(id, name, property_type, country_id, city_id, district_id, host_id, price, beds, bedrooms, bathrooms, minimum_days, maximum_tenants, property_size, free_text, latitude, longitude, historic) values(8, "Room 8", "ROOM", 1, 1, 4, 8, 54, 1, 1, 3, 1, 3, 34, "la la", 40.71727401, -74.00898606, 0);
INSERT INTO `property_to_rent`(id, name, property_type, country_id, city_id, district_id, host_id, price, beds, bedrooms, bathrooms, minimum_days, maximum_tenants, property_size, free_text, latitude, longitude, historic) values(9, "Room 9", "ROOM", 1, 1, 4, 9, 82, 1, 1, 3, 1, 3, 34, "la la", 40.71727401, -74.00898606, 0);
INSERT INTO `property_to_rent`(id, name, property_type, country_id, city_id, district_id, host_id, price, beds, bedrooms, bathrooms, minimum_days, maximum_tenants, property_size, free_text, latitude, longitude, historic) values(10, "Room 10", "ROOM", 1, 1, 4, 10, 27, 1, 1, 3, 1, 3, 34, "la la", 40.71727401, -74.00898606, 0);
INSERT INTO `property_to_rent`(id, name, property_type, country_id, city_id, district_id, host_id, price, beds, bedrooms, bathrooms, minimum_days, maximum_tenants, property_size, free_text, latitude, longitude, historic) values(11, "Room 11", "ROOM", 1, 1, 4, 11, 32, 1, 1, 3, 1, 3, 34, "la la", 40.71727401, -74.00898606, 0);
INSERT INTO `property_to_rent`(id, name, property_type, country_id, city_id, district_id, host_id, price, beds, bedrooms, bathrooms, minimum_days, maximum_tenants, property_size, free_text, latitude, longitude, historic) values(12, "Room 12", "ROOM", 1, 1, 4, 12, 32, 1, 1, 3, 1, 3, 34, "la la", 40.71727401, -74.00898606, 0);
INSERT INTO `property_to_rent`(id, name, property_type, country_id, city_id, district_id, host_id, price, beds, bedrooms, bathrooms, minimum_days, maximum_tenants, property_size, free_text, latitude, longitude, historic) values(13, "Room 13", "ROOM", 1, 1, 4, 13, 32, 1, 1, 3, 1, 3, 34, "la la", 40.71727401, -74.00898606, 0);
INSERT INTO `property_to_rent`(id, name, property_type, country_id, city_id, district_id, host_id, price, beds, bedrooms, bathrooms, minimum_days, maximum_tenants, property_size, free_text, latitude, longitude, historic) values(14, "House 14", "HOUSE", 1, 1, 4, 14, 32, 1, 1, 3, 1, 3, 34, "la la", 40.71727401, -74.00898606, 0);
INSERT INTO `property_to_rent`(id, name, property_type, country_id, city_id, district_id, host_id, price, beds, bedrooms, bathrooms, minimum_days, maximum_tenants, property_size, free_text, latitude, longitude, historic) values(15, "House 15", "HOUSE", 1, 1, 4, 15, 32, 1, 1, 3, 1, 3, 34, "la la", 40.71727401, -74.00898606, 0);
INSERT INTO `property_to_rent`(id, name, property_type, country_id, city_id, district_id, host_id, price, beds, bedrooms, bathrooms, minimum_days, maximum_tenants, property_size, free_text, latitude, longitude, historic) values(16, "House 16", "HOUSE", 1, 1, 4, 16, 32, 1, 1, 3, 1, 3, 34, "la la", 40.71727401, -74.00898606, 0);
INSERT INTO `property_to_rent`(id, name, property_type, country_id, city_id, district_id, host_id, price, beds, bedrooms, bathrooms, minimum_days, maximum_tenants, property_size, free_text, latitude, longitude, historic) values(17, "House 17", "HOUSE", 1, 1, 4, 17, 32, 1, 1, 3, 1, 3, 34, "la la", 40.71727401, -74.00898606, 0);
INSERT INTO `property_to_rent`(id, name, property_type, country_id, city_id, district_id, host_id, price, beds, bedrooms, bathrooms, minimum_days, maximum_tenants, property_size, free_text, latitude, longitude, historic) values(18, "House 18", "HOUSE", 1, 1, 4, 18, 32, 1, 1, 3, 1, 3, 34, "la la", 40.71727401, -74.00898606, 0);
INSERT INTO `property_to_rent`(id, name, property_type, country_id, city_id, district_id, host_id, price, beds, bedrooms, bathrooms, minimum_days, maximum_tenants, property_size, free_text, latitude, longitude, historic) values(19, "House 19", "HOUSE", 1, 1, 4, 19, 32, 1, 1, 3, 1, 3, 34, "la la", 40.71727401, -74.00898606, 0);
INSERT INTO `property_to_rent`(id, name, property_type, country_id, city_id, district_id, host_id, price, beds, bedrooms, bathrooms, minimum_days, maximum_tenants, property_size, free_text, latitude, longitude, historic) values(20, "House 20", "HOUSE", 1, 1, 4, 20, 32, 1, 1, 3, 1, 3, 34, "la la", 40.71727401, -74.00898606, 0);
INSERT INTO `property_to_rent`(id, name, property_type, country_id, city_id, district_id, host_id, price, beds, bedrooms, bathrooms, minimum_days, maximum_tenants, property_size, free_text, latitude, longitude, historic) values(21, "House 21", "HOUSE", 1, 1, 4, 21, 32, 1, 1, 3, 1, 3, 34, "la la", 40.71727401, -74.00898606, 0);
INSERT INTO `property_to_rent`(id, name, property_type, country_id, city_id, district_id, host_id, price, beds, bedrooms, bathrooms, minimum_days, maximum_tenants, property_size, free_text, latitude, longitude, historic) values(22, "House 22", "HOUSE", 1, 1, 4, 22, 32, 1, 1, 3, 1, 3, 34, "la la", 40.71727401, -74.00898606, 0);
INSERT INTO `property_to_rent`(id, name, property_type, country_id, city_id, district_id, host_id, price, beds, bedrooms, bathrooms, minimum_days, maximum_tenants, property_size, free_text, latitude, longitude, historic) values(23, "House 23", "HOUSE", 1, 1, 4, 23, 32, 1, 1, 3, 1, 3, 34, "la la", 40.71727401, -74.00898606, 0);
INSERT INTO `property_to_rent`(id, name, property_type, country_id, city_id, district_id, host_id, price, beds, bedrooms, bathrooms, minimum_days, maximum_tenants, property_size, free_text, latitude, longitude, historic) values(24, "House 24", "HOUSE", 1, 1, 4, 24, 32, 1, 1, 3, 1, 3, 34, "la la", 40.71727401, -74.00898606, 0);
INSERT INTO `property_to_rent`(id, name, property_type, country_id, city_id, district_id, host_id, price, beds, bedrooms, bathrooms, minimum_days, maximum_tenants, property_size, free_text, latitude, longitude, historic) values(25, "House 25", "HOUSE", 1, 1, 4, 25, 32, 1, 1, 3, 1, 3, 34, "la la", 40.71727401, -74.00898606, 0);
INSERT INTO `property_to_rent`(id, name, property_type, country_id, city_id, district_id, host_id, price, beds, bedrooms, bathrooms, minimum_days, maximum_tenants, property_size, free_text, latitude, longitude, historic) values(26, "House 26", "HOUSE", 1, 1, 4, 26, 32, 1, 1, 3, 1, 3, 34, "la la", 40.71727401, -74.00898606, 0);
INSERT INTO `property_to_rent`(id, name, property_type, country_id, city_id, district_id, host_id, price, beds, bedrooms, bathrooms, minimum_days, maximum_tenants, property_size, free_text, latitude, longitude, historic) values(27, "House 27", "HOUSE", 1, 1, 4, 27, 32, 1, 1, 3, 1, 3, 34, "la la", 40.71727401, -74.00898606, 0);
INSERT INTO `property_to_rent`(id, name, property_type, country_id, city_id, district_id, host_id, price, beds, bedrooms, bathrooms, minimum_days, maximum_tenants, property_size, free_text, latitude, longitude, historic) values(28, "House 28", "HOUSE", 1, 1, 4, 28, 32, 1, 1, 3, 1, 3, 34, "la la", 40.71727401, -74.00898606, 0);
INSERT INTO `property_to_rent`(id, name, property_type, country_id, city_id, district_id, host_id, price, beds, bedrooms, bathrooms, minimum_days, maximum_tenants, property_size, free_text, latitude, longitude, historic) values(29, "House 29", "HOUSE", 1, 1, 4, 29, 32, 1, 1, 3, 1, 3, 34, "la la", 40.71727401, -74.00898606, 0);
INSERT INTO `property_to_rent`(id, name, property_type, country_id, city_id, district_id, host_id, price, beds, bedrooms, bathrooms, minimum_days, maximum_tenants, property_size, free_text, latitude, longitude, historic) values(30, "House 30", "HOUSE", 1, 1, 4, 30, 32, 1, 1, 3, 1, 3, 34, "la la", 40.71727401, -74.00898606, 0);
INSERT INTO `property_to_rent`(id, name, property_type, country_id, city_id, district_id, host_id, price, beds, bedrooms, bathrooms, minimum_days, maximum_tenants, property_size, free_text, latitude, longitude, historic) values(31, "House 31", "HOUSE", 1, 1, 4, 31, 32, 1, 1, 3, 1, 3, 34, "la la", 40.71727401, -74.00898606, 0);

-- create renting rules data
INSERT INTO `renting_rules`(id, property_id, aircondition, tv, internet, living_room, kitchen, party_friendly, pet_friendly, smoking_friendly, free_text) VALUES (1, 1, 1, 1, 1, 1, 1, 1, 1, 1, "a nice property");
INSERT INTO `renting_rules`(id, property_id, aircondition, tv, internet, living_room, kitchen, party_friendly, pet_friendly, smoking_friendly, free_text) VALUES (2, 2, 1, 1, 0, 1, 0, 0, 1, 0, NULL);
INSERT INTO `renting_rules`(id, property_id, aircondition, tv, internet, living_room, kitchen, party_friendly, pet_friendly, smoking_friendly, free_text) VALUES (3, 3, 1, 0, 0, 1, 0, 0, 1, 0, "a nice property");
INSERT INTO `renting_rules`(id, property_id, aircondition, tv, internet, living_room, kitchen, party_friendly, pet_friendly, smoking_friendly, free_text) VALUES (4, 4, 0, 1, 0, 1, 0, 0, 1, 0, NULL);
INSERT INTO `renting_rules`(id, property_id, aircondition, tv, internet, living_room, kitchen, party_friendly, pet_friendly, smoking_friendly, free_text) VALUES (5, 5, 0, 1, 0, 1, 0, 0, 1, 0, NULL);
INSERT INTO `renting_rules`(id, property_id, aircondition, tv, internet, living_room, kitchen, party_friendly, pet_friendly, smoking_friendly, free_text) VALUES (6, 6, 0, 1, 0, 1, 0, 0, 1, 0, NULL);
INSERT INTO `renting_rules`(id, property_id, aircondition, tv, internet, living_room, kitchen, party_friendly, pet_friendly, smoking_friendly, free_text) VALUES (7, 7, 0, 1, 0, 1, 0, 0, 1, 0, NULL);
INSERT INTO `renting_rules`(id, property_id, aircondition, tv, internet, living_room, kitchen, party_friendly, pet_friendly, smoking_friendly, free_text) VALUES (8, 8, 0, 1, 0, 1, 0, 0, 1, 0, NULL);
INSERT INTO `renting_rules`(id, property_id, aircondition, tv, internet, living_room, kitchen, party_friendly, pet_friendly, smoking_friendly, free_text) VALUES (9, 9, 0, 1, 0, 1, 0, 0, 1, 0, NULL);
INSERT INTO `renting_rules`(id, property_id, aircondition, tv, internet, living_room, kitchen, party_friendly, pet_friendly, smoking_friendly, free_text) VALUES (10, 10, 0, 1, 0, 1, 0, 0, 1, 0, NULL);
INSERT INTO `renting_rules`(id, property_id, aircondition, tv, internet, living_room, kitchen, party_friendly, pet_friendly, smoking_friendly, free_text) VALUES (11, 11, 0, 1, 0, 1, 0, 0, 1, 0, "a nice property");
INSERT INTO `renting_rules`(id, property_id, aircondition, tv, internet, living_room, kitchen, party_friendly, pet_friendly, smoking_friendly, free_text) VALUES (12, 12, 0, 1, 0, 1, 0, 0, 1, 0, "a nice property");
INSERT INTO `renting_rules`(id, property_id, aircondition, tv, internet, living_room, kitchen, party_friendly, pet_friendly, smoking_friendly, free_text) VALUES (13, 13, 0, 1, 0, 1, 0, 0, 1, 0, "a nice property");
INSERT INTO `renting_rules`(id, property_id, aircondition, tv, internet, living_room, kitchen, party_friendly, pet_friendly, smoking_friendly, free_text) VALUES (14, 14, 0, 1, 0, 1, 0, 0, 1, 0, "a nice property");
INSERT INTO `renting_rules`(id, property_id, aircondition, tv, internet, living_room, kitchen, party_friendly, pet_friendly, smoking_friendly, free_text) VALUES (15, 15, 0, 1, 0, 1, 0, 0, 1, 0, NULL);
INSERT INTO `renting_rules`(id, property_id, aircondition, tv, internet, living_room, kitchen, party_friendly, pet_friendly, smoking_friendly, free_text) VALUES (16, 16, 0, 1, 0, 1, 0, 0, 1, 0, "a nice property");
INSERT INTO `renting_rules`(id, property_id, aircondition, tv, internet, living_room, kitchen, party_friendly, pet_friendly, smoking_friendly, free_text) VALUES (17, 17, 0, 1, 0, 1, 0, 0, 1, 0, NULL);
INSERT INTO `renting_rules`(id, property_id, aircondition, tv, internet, living_room, kitchen, party_friendly, pet_friendly, smoking_friendly, free_text) VALUES (18, 18, 0, 1, 0, 1, 0, 0, 1, 0, NULL);
INSERT INTO `renting_rules`(id, property_id, aircondition, tv, internet, living_room, kitchen, party_friendly, pet_friendly, smoking_friendly, free_text) VALUES (19, 19, 0, 1, 0, 1, 0, 0, 1, 0, NULL);
INSERT INTO `renting_rules`(id, property_id, aircondition, tv, internet, living_room, kitchen, party_friendly, pet_friendly, smoking_friendly, free_text) VALUES (20, 20, 0, 1, 0, 1, 0, 0, 1, 0, NULL);
INSERT INTO `renting_rules`(id, property_id, aircondition, tv, internet, living_room, kitchen, party_friendly, pet_friendly, smoking_friendly, free_text) VALUES (21, 21, 0, 1, 0, 1, 0, 0, 1, 0, "a nice property");
INSERT INTO `renting_rules`(id, property_id, aircondition, tv, internet, living_room, kitchen, party_friendly, pet_friendly, smoking_friendly, free_text) VALUES (22, 22, 0, 1, 0, 1, 0, 0, 1, 0, NULL);
INSERT INTO `renting_rules`(id, property_id, aircondition, tv, internet, living_room, kitchen, party_friendly, pet_friendly, smoking_friendly, free_text) VALUES (23, 23, 0, 1, 0, 1, 0, 0, 1, 0, NULL);
INSERT INTO `renting_rules`(id, property_id, aircondition, tv, internet, living_room, kitchen, party_friendly, pet_friendly, smoking_friendly, free_text) VALUES (24, 24, 0, 1, 0, 1, 0, 0, 1, 0, "a nice property");
INSERT INTO `renting_rules`(id, property_id, aircondition, tv, internet, living_room, kitchen, party_friendly, pet_friendly, smoking_friendly, free_text) VALUES (25, 25, 0, 1, 0, 1, 0, 0, 1, 0, NULL);
INSERT INTO `renting_rules`(id, property_id, aircondition, tv, internet, living_room, kitchen, party_friendly, pet_friendly, smoking_friendly, free_text) VALUES (26, 26, 0, 1, 0, 1, 0, 0, 1, 0, NULL);
INSERT INTO `renting_rules`(id, property_id, aircondition, tv, internet, living_room, kitchen, party_friendly, pet_friendly, smoking_friendly, free_text) VALUES (27, 27, 0, 1, 0, 1, 0, 0, 1, 0, NULL);
INSERT INTO `renting_rules`(id, property_id, aircondition, tv, internet, living_room, kitchen, party_friendly, pet_friendly, smoking_friendly, free_text) VALUES (28, 28, 0, 1, 0, 1, 0, 0, 1, 0, "a nice property");
INSERT INTO `renting_rules`(id, property_id, aircondition, tv, internet, living_room, kitchen, party_friendly, pet_friendly, smoking_friendly, free_text) VALUES (29, 29, 0, 1, 0, 1, 0, 0, 1, 0, NULL);
INSERT INTO `renting_rules`(id, property_id, aircondition, tv, internet, living_room, kitchen, party_friendly, pet_friendly, smoking_friendly, free_text) VALUES (30, 30, 0, 1, 0, 1, 0, 0, 1, 0, NULL);
INSERT INTO `renting_rules`(id, property_id, aircondition, tv, internet, living_room, kitchen, party_friendly, pet_friendly, smoking_friendly, free_text) VALUES (31, 31, 0, 1, 0, 1, 0, 0, 1, 0, NULL);


-- create property availability data
INSERT INTO `property_availability`(id, property_id, available_from, available_to) VALUES (1, 1, "2020-04-01 00:00:00", "2020-10-01 00:00:00");
INSERT INTO `property_availability`(id, property_id, available_from, available_to) VALUES (2, 2, "2020-04-01 00:00:00", "2020-10-01 00:00:00");
INSERT INTO `property_availability`(id, property_id, available_from, available_to) VALUES (3, 3, "2020-04-01 00:00:00", "2020-08-01 00:00:00");
INSERT INTO `property_availability`(id, property_id, available_from, available_to) VALUES (4, 4, "2020-04-01 00:00:00", "2020-10-01 00:00:00");
INSERT INTO `property_availability`(id, property_id, available_from, available_to) VALUES (5, 5, "2020-04-01 00:00:00", "2020-10-01 00:00:00");
INSERT INTO `property_availability`(id, property_id, available_from, available_to) VALUES (6, 6, "2020-04-01 00:00:00", "2020-08-01 00:00:00");
INSERT INTO `property_availability`(id, property_id, available_from, available_to) VALUES (7, 7, "2020-04-01 00:00:00", "2020-10-01 00:00:00");
INSERT INTO `property_availability`(id, property_id, available_from, available_to) VALUES (8, 8, "2020-04-01 00:00:00", "2020-10-01 00:00:00");
INSERT INTO `property_availability`(id, property_id, available_from, available_to) VALUES (9, 9, "2020-04-01 00:00:00", "2020-10-01 00:00:00");
INSERT INTO `property_availability`(id, property_id, available_from, available_to) VALUES (10, 10, "2020-04-01 00:00:00", "2020-10-01 00:00:00");
INSERT INTO `property_availability`(id, property_id, available_from, available_to) VALUES (11, 11, "2020-04-01 00:00:00", "2020-10-01 00:00:00");
INSERT INTO `property_availability`(id, property_id, available_from, available_to) VALUES (12, 12, "2020-04-01 00:00:00", "2020-10-01 00:00:00");
INSERT INTO `property_availability`(id, property_id, available_from, available_to) VALUES (13, 13, "2020-04-01 00:00:00", "2020-08-01 00:00:00");
INSERT INTO `property_availability`(id, property_id, available_from, available_to) VALUES (14, 14, "2020-04-01 00:00:00", "2020-10-01 00:00:00");
INSERT INTO `property_availability`(id, property_id, available_from, available_to) VALUES (15, 15, "2020-04-01 00:00:00", "2020-10-01 00:00:00");
INSERT INTO `property_availability`(id, property_id, available_from, available_to) VALUES (16, 16, "2020-04-01 00:00:00", "2020-08-01 00:00:00");
INSERT INTO `property_availability`(id, property_id, available_from, available_to) VALUES (17, 17, "2020-04-01 00:00:00", "2020-10-01 00:00:00");
INSERT INTO `property_availability`(id, property_id, available_from, available_to) VALUES (18, 18, "2020-04-01 00:00:00", "2020-10-01 00:00:00");
INSERT INTO `property_availability`(id, property_id, available_from, available_to) VALUES (19, 19, "2020-04-01 00:00:00", "2020-10-01 00:00:00");
INSERT INTO `property_availability`(id, property_id, available_from, available_to) VALUES (20, 20, "2020-04-01 00:00:00", "2020-10-01 00:00:00");
INSERT INTO `property_availability`(id, property_id, available_from, available_to) VALUES (21, 21, "2020-04-01 00:00:00", "2020-10-01 00:00:00");
INSERT INTO `property_availability`(id, property_id, available_from, available_to) VALUES (22, 22, "2020-04-01 00:00:00", "2020-10-01 00:00:00");
INSERT INTO `property_availability`(id, property_id, available_from, available_to) VALUES (23, 23, "2020-04-01 00:00:00", "2020-08-01 00:00:00");
INSERT INTO `property_availability`(id, property_id, available_from, available_to) VALUES (24, 24, "2020-04-01 00:00:00", "2020-10-01 00:00:00");
INSERT INTO `property_availability`(id, property_id, available_from, available_to) VALUES (25, 25, "2020-04-01 00:00:00", "2020-10-01 00:00:00");
INSERT INTO `property_availability`(id, property_id, available_from, available_to) VALUES (26, 26, "2020-04-01 00:00:00", "2020-08-01 00:00:00");
INSERT INTO `property_availability`(id, property_id, available_from, available_to) VALUES (27, 27, "2020-04-01 00:00:00", "2020-10-01 00:00:00");
INSERT INTO `property_availability`(id, property_id, available_from, available_to) VALUES (28, 28, "2020-04-01 00:00:00", "2020-10-01 00:00:00");
INSERT INTO `property_availability`(id, property_id, available_from, available_to) VALUES (29, 29, "2020-04-01 00:00:00", "2020-10-01 00:00:00");
INSERT INTO `property_availability`(id, property_id, available_from, available_to) VALUES (30, 30, "2020-04-01 00:00:00", "2020-10-01 00:00:00");
INSERT INTO `property_availability`(id, property_id, available_from, available_to) VALUES (31, 31, "2020-04-01 00:00:00", "2020-10-01 00:00:00");


INSERT INTO `booking`(id, tenant_id, property_id, from_datetime, to_datetime, created_at) values(1, 1, 1, now(), now(), now());

INSERT INTO `rating`(id, rater_id, property_id, host_id, text, mark, created_at) VALUES(1, 1, 1, 1, "free text review", 3, now());





INSERT INTO `messaging`(id, sender, recipient, created_at, message_body, read_status) VALUES(1, 1, 1, now(), "la la", 0);
INSERT INTO `messaging`(id, sender, recipient, created_at, message_body, read_status) VALUES(2, 3, 15, now(), "la la", 1);
INSERT INTO `messaging`(id, sender, recipient, created_at, message_body, read_status) VALUES(2, 3, 15, now(), "la la", 1);
INSERT INTO `messaging`(id, sender, recipient, created_at, message_body, read_status) VALUES(2, 3, 15, now(), "la la", 1);
INSERT INTO `messaging`(id, sender, recipient, created_at, message_body, read_status) VALUES(2, 3, 15, now(), "la la", 1);
INSERT INTO `messaging`(id, sender, recipient, created_at, message_body, read_status) VALUES(2, 3, 15, now(), "la la", 1);
INSERT INTO `messaging`(id, sender, recipient, created_at, message_body, read_status) VALUES(2, 3, 15, now(), "la la", 1);
INSERT INTO `messaging`(id, sender, recipient, created_at, message_body, read_status) VALUES(2, 3, 15, now(), "la la", 1);

INSERT INTO `messaging`(id, sender, recipient, created_at, message_body, read_status) VALUES(2, 3, 16, now(), "la la", 1);
INSERT INTO `messaging`(id, sender, recipient, created_at, message_body, read_status) VALUES(2, 3, 16, now(), "la la", 1);
INSERT INTO `messaging`(id, sender, recipient, created_at, message_body, read_status) VALUES(2, 3, 16, now(), "la la", 1);

INSERT INTO `messaging`(id, sender, recipient, created_at, message_body, read_status) VALUES(2, 3, 17, now(), "la la", 1);
INSERT INTO `messaging`(id, sender, recipient, created_at, message_body, read_status) VALUES(2, 3, 18, now(), "la la", 1);
INSERT INTO `messaging`(id, sender, recipient, created_at, message_body, read_status) VALUES(2, 3, 19, now(), "la la", 1);
INSERT INTO `messaging`(id, sender, recipient, created_at, message_body, read_status) VALUES(2, 3, 19, now(), "la la", 1);

INSERT INTO `messaging`(id, sender, recipient, created_at, message_body, read_status) VALUES(2, 3, 20, now(), "la la", 1);
INSERT INTO `messaging`(id, sender, recipient, created_at, message_body, read_status) VALUES(2, 3, 20, now(), "la la", 1);
INSERT INTO `messaging`(id, sender, recipient, created_at, message_body, read_status) VALUES(2, 3, 20, now(), "la la", 1);
INSERT INTO `messaging`(id, sender, recipient, created_at, message_body, read_status) VALUES(2, 3, 20, now(), "la la", 1);

INSERT INTO `messaging`(id, sender, recipient, created_at, message_body, read_status) VALUES(2, 3, 21, now(), "la la", 1);
INSERT INTO `messaging`(id, sender, recipient, created_at, message_body, read_status) VALUES(2, 3, 22, now(), "la la", 1);
INSERT INTO `messaging`(id, sender, recipient, created_at, message_body, read_status) VALUES(2, 3, 22, now(), "la la", 1);
INSERT INTO `messaging`(id, sender, recipient, created_at, message_body, read_status) VALUES(2, 3, 22, now(), "la la", 1);




-- create geographic data
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
INSERT INTO `district`(id, name, city_id) VALUES(14, "The City",2);

INSERT INTO `district`(id, name, city_id) VALUES(15, "Central Edinburgh",3);
INSERT INTO `district`(id, name, city_id) VALUES(16, "Stockbridge",3);
INSERT INTO `district`(id, name, city_id) VALUES(17, "Leith",3);
INSERT INTO `district`(id, name, city_id) VALUES(18, "Morningside and Bruntsfield",3);
INSERT INTO `district`(id, name, city_id) VALUES(19, "Newington and the South Bridge Area",3);
