CREATE DATABASE ups_monitoring;

USE ups_monitoring;

CREATE TABLE users (
    username VARCHAR(20) NOT NULL,
    password VARCHAR(100) NOT NULL,
    name VARCHAR(50) NOT NULL,
    token VARCHAR(100),
    token_expired_at BIGINT,
    PRIMARY KEY (username),
    UNIQUE (token)
) ENGINE InnoDB;

CREATE TABLE devices (
    id INT NOT NULL AUTO_INCREMENT,
    username VARCHAR(20) NOT NULL,
    code VARCHAR(25) NOT NULL,
    name VARCHAR(50) NOT NULL,
    location VARCHAR(30),
    battery_count INT NOT NULL,
    registered_at TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY fk_users_devices (username) REFERENCES users (username),
    UNIQUE (code)
) ENGINE InnoDB;

CREATE TABLE device_logs (
    id BIGINT NOT NULL AUTO_INCREMENT,
    device_id INT NOT NULL,
    battery_voltage JSON,
    input_voltage DOUBLE,
    charging_state VARCHAR(20),
    recorded_at TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY fk_devices_device_logs (device_id) REFERENCES devices (id)
) ENGINE InnoDB;

