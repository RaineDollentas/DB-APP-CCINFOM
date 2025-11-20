-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: lalamove_lite
-- ------------------------------------------------------
-- Server version	8.0.44

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

CREATE DATABASE lalamove_lite;
USE lalamove_lite;

--
-- Table structure for table `couriers`
--

DROP TABLE IF EXISTS `couriers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `couriers` (
  `courier_id` int NOT NULL AUTO_INCREMENT,
  `last_name` varchar(45) DEFAULT NULL,
  `first_name` varchar(45) DEFAULT NULL,
  `vehicle_type` varchar(45) DEFAULT NULL,
  `hire_date` date DEFAULT NULL,
  `email` varchar(45) DEFAULT NULL,
  `contact_no` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`courier_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `couriers`
--

LOCK TABLES `couriers` WRITE;
/*!40000 ALTER TABLE `couriers` DISABLE KEYS */;
INSERT INTO `couriers` VALUES (1,'Kim','Chaewon','Sedan','2025-05-15','kchae@gmail.com','1-800-468-6386'),(2,'Yoo','Jeongyeon','Motorcycle','2025-08-22','imoutofideas@hotmail.com','09288222288'),(3,'Kang','Seulgi','Bicycle','2025-01-10','sendhelp@yrocketmail.com','09111111111');
/*!40000 ALTER TABLE `couriers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customers`
--

DROP TABLE IF EXISTS `customers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customers` (
  `customer_id` int NOT NULL AUTO_INCREMENT,
  `first_name` varchar(45) DEFAULT NULL,
  `last_name` varchar(45) DEFAULT NULL,
  `address` varchar(100) DEFAULT NULL,
  `contact_no` varchar(15) DEFAULT NULL,
  `email` varchar(45) DEFAULT NULL,
  `join_date` date DEFAULT NULL,
  PRIMARY KEY (`customer_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customers`
--

LOCK TABLES `customers` WRITE;
/*!40000 ALTER TABLE `customers` DISABLE KEYS */;
INSERT INTO `customers` VALUES (1,'Joseph','Joestar','123 Pablo Ocampo Sr. St, Malate','09123456789','joseph.joestar@gmail.com','2025-01-15'),(2,'Gregory','House','321 DefinitelyReal St, Mandaluyong','09774300356','hatdog@yahoo.com','2025-02-20'),(3,'Kazuma','Kiryu','213 Fake St, Sampaloc','09999999999','thisisarealemail@email.com','2025-03-10');
/*!40000 ALTER TABLE `customers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `parcel_status`
--

DROP TABLE IF EXISTS `parcel_status`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `parcel_status` (
  `tracking_id` int NOT NULL AUTO_INCREMENT,
  `parcel_id` int DEFAULT NULL,
  `courier_id` int DEFAULT NULL,
  `status_update` varchar(50) DEFAULT NULL,
  `recipient_address` varchar(100) DEFAULT NULL,
  `timestamp` datetime DEFAULT NULL,
  `remarks` text,
  PRIMARY KEY (`tracking_id`),
  KEY `parcel_id` (`parcel_id`),
  KEY `courier_id` (`courier_id`),
  CONSTRAINT `parcel_status_ibfk_1` FOREIGN KEY (`parcel_id`) REFERENCES `parcels` (`parcel_id`),
  CONSTRAINT `parcel_status_ibfk_2` FOREIGN KEY (`courier_id`) REFERENCES `couriers` (`courier_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1007 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `parcel_status`
--

LOCK TABLES `parcel_status` WRITE;
/*!40000 ALTER TABLE `parcel_status` DISABLE KEYS */;
INSERT INTO `parcel_status` VALUES (1001,101,1,'Delivered','123 Pablo Ocampo Sr. St, Malate','2025-11-01 09:30:00','Package received by recipient'),(1002,101,1,'In Transit','123 Pablo Ocampo Sr. St, Malate','2025-11-01 11:45:00','Out for delivery'),(1003,101,1,'Cancelled','123 Pablo Ocampo Sr. St, Malate','2025-11-01 14:20:00','Cancelled by Customer'),(1004,102,2,'Booked','321 DefinitelyReal St, Mandaluyong','2025-11-02 14:15:00','Package processed'),(1005,102,2,'Returned','321 DefinitelyReal St, Mandaluyong','2025-11-03 08:30:00','Returned to Customer'),(1006,103,3,'Booked','213 Fake St, Sampaloc','2025-11-03 10:00:00','Awaiting pickup');
/*!40000 ALTER TABLE `parcel_status` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `parcels`
--

DROP TABLE IF EXISTS `parcels`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `parcels` (
  `parcel_id` int NOT NULL AUTO_INCREMENT,
  `customer_id` int DEFAULT NULL,
  `courier_id` int DEFAULT NULL,
  `status` varchar(50) DEFAULT NULL,
  `booking_date` datetime DEFAULT NULL,
  PRIMARY KEY (`parcel_id`),
  KEY `customer_id` (`customer_id`),
  KEY `courier_id` (`courier_id`),
  CONSTRAINT `parcels_ibfk_1` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`),
  CONSTRAINT `parcels_ibfk_2` FOREIGN KEY (`courier_id`) REFERENCES `couriers` (`courier_id`)
) ENGINE=InnoDB AUTO_INCREMENT=104 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `parcels`
--

LOCK TABLES `parcels` WRITE;
/*!40000 ALTER TABLE `parcels` DISABLE KEYS */;
INSERT INTO `parcels` VALUES (101,1,1,'Delivered','2024-11-01 09:30:00'),(102,2,2,'In Transit','2024-11-02 14:15:00'),(103,3,3,'Booked','2024-11-03 10:00:00');
/*!40000 ALTER TABLE `parcels` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-11-19 18:19:27
