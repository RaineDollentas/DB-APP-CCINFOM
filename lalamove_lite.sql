CREATE DATABASE lalamove_lite;
USE lalamove_lite;

CREATE TABLE customers (
    customer_id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(45),
    last_name VARCHAR(45),
    address VARCHAR(100),
    contact_no VARCHAR(15),
    email VARCHAR(45),
    join_date DATE
);

CREATE TABLE couriers (
    courier_id INT AUTO_INCREMENT PRIMARY KEY,
    last_name VARCHAR(45),
    first_name VARCHAR(45),
    vehicle_type VARCHAR(45),
    hire_date DATE,
    email VARCHAR(45),
    contact_no VARCHAR(45)
);

CREATE TABLE parcels (
    parcel_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT,
    courier_id INT,
    status VARCHAR(12),
    booking_date DATETIME,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
    FOREIGN KEY (courier_id) REFERENCES couriers(courier_id)
);

CREATE TABLE parcel_status (
    tracking_id INT AUTO_INCREMENT PRIMARY KEY,
    parcel_id INT,
    courier_id INT,
    status_update VARCHAR(15),
    recipient_address VARCHAR(100),
    timestamp DATETIME,
    parcel_statuscol VARCHAR(45),
    FOREIGN KEY (parcel_id) REFERENCES parcels(parcel_id),
    FOREIGN KEY (courier_id) REFERENCES couriers(courier_id)
);

USE lalamove_lite;
ALTER TABLE parcel_status ADD COLUMN remarks TEXT; --added a remarks column to parcel_status table