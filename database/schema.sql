-- Stray Care System Database Schema
-- Created by Nessa Savard

CREATE DATABASE IF NOT EXISTS stray_care_system;
USE stray_care_system;

CREATE TABLE IF NOT EXISTS users (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100),
  email VARCHAR(100) UNIQUE,
  password_hash VARCHAR(255),
  role ENUM('PUBLIC', 'VOLUNTEER', 'ADMIN'),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS dogs (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255),
  breed VARCHAR(255),
  status ENUM('active', 'recovered', 'adopted'),
  photo_url VARCHAR(255),
  qr_code TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS health_records (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  dog_id BIGINT,
  vaccine_name VARCHAR(255),
  date_given DATE,
  notes VARCHAR(255),
  volunteer_id BIGINT,
  FOREIGN KEY (dog_id) REFERENCES dogs(id),
  FOREIGN KEY (volunteer_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS sightings (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  dog_id BIGINT,
  latitude FLOAT,
  longitude FLOAT,
  photo_url VARCHAR(255),
  notes VARCHAR(255),
  reported_by BIGINT,
  reported_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (dog_id) REFERENCES dogs(id)
);

CREATE TABLE IF NOT EXISTS qr_codes (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  dog_id BIGINT UNIQUE,
  qr_data TEXT,
  generated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (dog_id) REFERENCES dogs(id)
);