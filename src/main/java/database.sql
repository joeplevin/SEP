-- MySQL dump 10.13  Distrib 8.0.28, for Win64 (x86_64)
--
-- Host: localhost    Database: vultureapp
-- ------------------------------------------------------
-- Server version	8.0.28

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

--
-- Table structure for table `job`
--

DROP TABLE IF EXISTS `job`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `job` (
  `job_id` bigint NOT NULL AUTO_INCREMENT,
  `job_name` varchar(45) DEFAULT NULL,
  `job_created_by` bigint DEFAULT NULL,
  `job_created_date` date DEFAULT NULL,
  `job_cost_est` decimal(10,2) DEFAULT NULL,
  `job_duration_est` int DEFAULT NULL,
  `job_completed` int DEFAULT NULL,
  `job_notes` varchar(1000) DEFAULT NULL,
  `job_completed_date` date DEFAULT NULL,
  PRIMARY KEY (`job_id`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `job`
--

LOCK TABLES `job` WRITE;
/*!40000 ALTER TABLE `job` DISABLE KEYS */;
INSERT INTO `job` VALUES (1,'AC Brushless',1,'2022-05-02',20.99,5,1,NULL,NULL),(2,'Direct Drive',1,'2022-05-02',100.00,2,0,NULL,NULL),(3,'Linear',1,'2022-05-02',100.00,2,0,NULL,NULL),(4,'Servo',1,'2022-05-02',50.00,1,0,NULL,NULL),(5,'Stepper',1,'2022-05-03',50.00,1,0,NULL,NULL),(6,'AC Brushless',1,'2022-05-03',6.00,5,0,NULL,NULL),(7,'Direct Drive',1,'2022-05-03',100.00,2,0,NULL,NULL),(8,'Linear',1,'2022-05-04',1000.00,15,0,NULL,NULL),(9,'Servo',1,'2022-05-04',1.00,10,0,NULL,NULL),(10,'Servo',1,'2022-05-04',10.00,10,0,NULL,NULL),(11,'Servo',1,'2022-05-04',10.00,10,0,NULL,NULL),(12,'Stepper',1,'2022-05-05',10.00,10,0,NULL,NULL),(13,'Direct Drive',1,'2022-05-05',6.00,5,1,NULL,NULL),(14,'Stepper',1,'2022-05-05',9.99,1,0,NULL,NULL),(15,'AC Brushless',1,'2022-05-06',10.99,1,0,NULL,NULL),(16,'Linear',1,'2022-05-06',10.45,1,1,'Inspection Failed','2022-05-06');
/*!40000 ALTER TABLE `job` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `job_AFTER_INSERT` AFTER INSERT ON `job` FOR EACH ROW BEGIN
insert into task(task_name,task_created_by,task_created_date,task_duration,task_tasktype,task_priority)
values("Initial Inspection",(select user_id from user_cred where cred_logged_in = 1),curdate(),2,"Initial Inspection",5);
insert into job_task(job_id,task_id)
values(New.job_id,(select max(task.task_id) from task));
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `job_task`
--

DROP TABLE IF EXISTS `job_task`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `job_task` (
  `job_id` bigint NOT NULL,
  `task_id` bigint NOT NULL,
  `jobtask_id` bigint NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`jobtask_id`),
  KEY `jobtask_job_id_idx` (`job_id`),
  KEY `jobtask_task_id_idx` (`task_id`),
  CONSTRAINT `jobtask_job_id` FOREIGN KEY (`job_id`) REFERENCES `job` (`job_id`) ON DELETE CASCADE,
  CONSTRAINT `jobtask_task_id` FOREIGN KEY (`task_id`) REFERENCES `task` (`task_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=69 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `job_task`
--

LOCK TABLES `job_task` WRITE;
/*!40000 ALTER TABLE `job_task` DISABLE KEYS */;
INSERT INTO `job_task` VALUES (1,1,1),(2,2,2),(3,3,3),(4,4,4),(5,5,5),(6,6,6),(7,7,7),(8,8,8),(9,9,9),(10,10,10),(11,11,11),(12,12,12),(13,13,13),(14,14,14),(15,15,15),(16,16,16),(1,17,17),(2,24,18),(3,28,19),(4,30,20),(5,34,21),(6,43,22),(7,44,23),(8,51,24),(9,52,25),(10,18,26),(11,27,27),(12,29,28),(13,31,29),(14,35,30),(15,37,31),(1,19,32),(2,26,33),(3,32,34),(4,38,35),(5,41,36),(6,54,37),(7,55,38),(8,56,39),(9,57,40),(10,58,41),(11,59,42),(12,60,43),(13,61,44),(14,62,45),(15,63,46),(1,20,47),(2,23,48),(3,25,49),(4,33,50),(5,39,51),(6,45,52),(7,21,53),(8,36,54),(9,40,55),(10,46,56),(11,22,57),(12,47,58),(13,48,59),(14,53,60),(15,49,61),(1,64,62),(2,65,63),(3,66,64),(4,67,65),(14,73,68);
/*!40000 ALTER TABLE `job_task` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary view structure for view `role`
--

DROP TABLE IF EXISTS `role`;
/*!50001 DROP VIEW IF EXISTS `role`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `role` AS SELECT 
 1 AS `role`*/;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `task`
--

DROP TABLE IF EXISTS `task`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `task` (
  `task_id` bigint NOT NULL AUTO_INCREMENT,
  `task_name` varchar(45) DEFAULT NULL,
  `task_tasktype` varchar(45) DEFAULT NULL,
  `task_created_by` bigint NOT NULL,
  `task_created_date` date DEFAULT NULL,
  `task_duration` int DEFAULT NULL,
  `task_priority` int DEFAULT NULL,
  `task_completed` int DEFAULT NULL,
  `task_pass_fail` varchar(45) DEFAULT NULL,
  `task_completed_date` date DEFAULT NULL,
  `task_notes` varchar(1000) DEFAULT NULL,
  `task_techs_required` int DEFAULT NULL,
  PRIMARY KEY (`task_id`)
) ENGINE=InnoDB AUTO_INCREMENT=74 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `task`
--

LOCK TABLES `task` WRITE;
/*!40000 ALTER TABLE `task` DISABLE KEYS */;
INSERT INTO `task` VALUES (1,'Initial Inspection','Initial Inspection',2,'2022-05-02',2,5,1,'Pass','2022-05-03',NULL,2),(2,'Initial Inspection','Initial Inspection',2,'2022-05-02',2,5,1,'Pass','2022-05-03',NULL,1),(3,'Initial Inspection','Initial Inspection',2,'2022-05-02',2,5,1,'Pass','2022-05-03',NULL,1),(4,'Initial Inspection','Initial Inspection',2,'2022-05-02',2,5,1,'Pass','2022-05-03',NULL,1),(5,'Initial Inspection','Initial Inspection',2,'2022-05-03',2,5,1,'Pass','2022-05-04',NULL,1),(6,'Initial Inspection','Initial Inspection',1,'2022-05-03',2,4,1,'Pass','2022-05-04',NULL,1),(7,'Initial Inspection','Initial Inspection',3,'2022-05-03',1,3,1,'Pass','2022-05-04',NULL,1),(8,'Initial Inspection','Initial Inspection',2,'2022-05-04',2,5,1,'Pass','2022-05-05',NULL,1),(9,'Initial Inspection','Initial Inspection',3,'2022-05-04',1,2,1,'Pass','2022-05-05',NULL,1),(10,'Initial Inspection','Initial Inspection',4,'2022-05-04',2,2,1,'Pass','2022-05-05',NULL,1),(11,'Initial Inspection','Initial Inspection',5,'2022-05-04',3,3,1,'Pass','2022-05-05',NULL,1),(12,'Initial Inspection','Initial Inspection',2,'2022-05-05',2,5,1,'Pass','2022-05-06',NULL,1),(13,'Initial Inspection','Initial Inspection',2,'2022-05-05',2,5,1,'Pass','2022-05-06',NULL,1),(14,'Initial Inspection','Initial Inspection',2,'2022-05-05',2,5,1,'Pass','2022-05-06',NULL,1),(15,'Initial Inspection','Initial Inspection',2,'2022-05-06',2,5,1,'Pass',NULL,NULL,1),(16,'Initial Inspection','Initial Inspection',2,'2022-05-06',2,5,1,'Fail',NULL,NULL,1),(17,'Other','Disassembly',2,'2022-05-02',2,4,1,'','2022-05-03',NULL,2),(18,'Other','Refurbishment',2,'2022-05-04',2,4,1,'','2022-05-05',NULL,1),(19,'Other','Cleaning',2,'2022-05-02',2,4,1,'','2022-05-03',NULL,1),(20,'Other','Repair',2,'2022-05-02',2,4,1,'','2022-05-03',NULL,1),(21,'Other','Manufacture',2,'2022-05-03',2,4,1,'','2022-05-04',NULL,1),(22,'Other','Reassembly',2,'2022-05-04',2,5,1,'','2022-05-05',NULL,1),(23,'Other','Repair',7,'2022-05-02',1,1,1,'','2022-05-03',NULL,1),(24,'Other','Disassembly',2,'2022-05-02',3,2,1,'','2022-05-03',NULL,1),(25,'Other','Repair',6,'2022-05-02',1,5,1,NULL,'2022-05-03',NULL,1),(26,'Other','Cleaning',8,'2022-05-02',2,3,1,NULL,'2022-05-03',NULL,1),(27,'Other','Refurbishment',9,'2022-05-04',3,4,1,NULL,'2022-05-05',NULL,1),(28,'Other','Disassembly',10,'2022-05-02',1,2,1,NULL,'2022-05-03',NULL,1),(29,'Other','Refurbishment',2,'2022-05-05',4,4,0,'',NULL,NULL,1),(30,'Other','Disassembly',3,'2022-05-02',1,4,1,NULL,'2022-05-03',NULL,1),(31,'Cleaning','Refurbishment',7,'2022-05-05',2,3,1,NULL,'2022-05-06',NULL,1),(32,'Other','Cleaning',7,'2022-05-02',4,5,1,NULL,'2022-05-03',NULL,1),(33,'Other','Repair',7,'2022-05-02',3,4,1,NULL,'2022-05-03',NULL,1),(34,'Other','Disassembly',7,'2022-05-03',1,3,1,NULL,'2022-05-04',NULL,1),(35,'Other','Refurbishment',7,'2022-05-05',1,3,1,NULL,'2022-05-06',NULL,1),(36,'Other','Manufacture',7,'2022-05-04',3,2,1,NULL,'2022-05-05',NULL,1),(37,'Other','Refurbishment',7,'2022-05-06',3,3,0,NULL,NULL,NULL,1),(38,'Other','Cleaning',7,'2022-05-02',3,3,1,NULL,'2022-05-03',NULL,1),(39,'Other','Repair',7,'2022-05-03',2,3,1,NULL,'2022-05-04',NULL,1),(40,'Other','Manufacture',7,'2022-05-04',3,3,1,NULL,'2022-05-05',NULL,1),(41,'Other','Cleaning',7,'2022-05-03',3,3,1,NULL,'2022-05-04',NULL,1),(43,'Other','Disassembly',7,'2022-05-03',3,3,1,NULL,'2022-05-04',NULL,1),(44,'Other','Disassembly',7,'2022-05-03',3,3,1,NULL,'2022-05-04',NULL,1),(45,'Other','Repair',7,'2022-05-03',2,3,1,NULL,'2022-05-04',NULL,1),(46,'Other','Manufacture',7,'2022-05-04',3,3,1,NULL,'2022-05-05',NULL,1),(47,'Other','Reassembly',7,'2022-05-05',3,3,0,NULL,NULL,NULL,1),(48,'Other','Reassembly',7,'2022-05-05',1,3,1,NULL,'2022-05-06',NULL,1),(49,'Other','Reassembly',7,'2022-05-06',1,3,0,NULL,NULL,NULL,1),(51,'Other','Disassembly',7,'2022-05-04',1,3,1,NULL,'2022-05-05',NULL,1),(52,'Other','Disassembly',7,'2022-05-04',1,3,1,NULL,'2022-05-05',NULL,1),(53,'Other','Reassembly',7,'2022-05-05',1,3,1,NULL,'2022-05-06',NULL,1),(54,'Other','Cleaning',7,'2022-05-03',3,3,1,NULL,'2022-05-04',NULL,1),(55,'Other','Cleaning',7,'2022-05-03',3,3,1,NULL,'2022-05-04',NULL,1),(56,'Other','Cleaning',7,'2022-05-04',3,3,1,NULL,'2022-05-05',NULL,1),(57,'Other','Cleaning',7,'2022-05-04',3,3,1,NULL,'2022-05-05',NULL,1),(58,'Other','Cleaning',7,'2022-05-04',3,3,1,NULL,'2022-05-05',NULL,1),(59,'Other','Cleaning',7,'2022-05-04',3,3,1,NULL,'2022-05-05',NULL,1),(60,'Other','Cleaning',7,'2022-05-05',3,3,1,NULL,'2022-05-06',NULL,1),(61,'Other','Cleaning',7,'2022-05-05',3,3,1,NULL,'2022-05-06',NULL,1),(62,'Other','Cleaning',7,'2022-05-05',3,3,0,NULL,NULL,NULL,1),(63,'Other','Cleaning',7,'2022-05-12',3,3,0,NULL,NULL,NULL,1),(64,'Final Inspection','Final Inspection',7,'2022-05-04',2,5,1,'Pass','2022-05-05',NULL,1),(65,'Final Inspection','Final Inspection',7,'2022-05-04',2,5,1,'Pass','2022-05-05',NULL,1),(66,'Final Inspection','Final Inspection',7,'2022-05-04',2,5,1,'Pass','2022-05-05',NULL,1),(67,'Final Inspection','Final Inspection',7,'2022-05-04',2,5,1,'Pass','2022-05-05',NULL,1),(73,'Test Task','Repair',7,'2022-05-15',8,5,0,NULL,NULL,NULL,2);
/*!40000 ALTER TABLE `task` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary view structure for view `tasktype`
--

DROP TABLE IF EXISTS `tasktype`;
/*!50001 DROP VIEW IF EXISTS `tasktype`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `tasktype` AS SELECT 
 1 AS `tasktype`*/;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `user_id` bigint NOT NULL AUTO_INCREMENT,
  `user_first_name` varchar(45) DEFAULT NULL,
  `user_last_name` varchar(45) DEFAULT NULL,
  `user_email` varchar(45) DEFAULT NULL,
  `user_image` varchar(45) DEFAULT NULL,
  `user_role` varchar(45) NOT NULL,
  `user_active` int DEFAULT NULL,
  `user_available_time` int DEFAULT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=57 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'Chad','Smith',NULL,'smith.jpg','Technician',1,8),(2,'Miranda','Hume','miranda@miranda.com','miranda.jpg','Customer Service',1,8),(3,'Stewart','Copeland','stewart@thepolice.com','copeland.jpg','Technician',1,8),(4,'Ginger','Baker','ginger@cream.com','baker.jpg','Technician',1,8),(5,'John','Bonham','john@ledzeppelin.com','bonham.jpg','Technician',1,8),(6,'Keith','Moon','keith@thewho.com','moon.jpg','Technician',1,8),(7,'Joe','Plevin','joe@joe.com','followill.jpg','Manager',1,8),(8,'Jeff','Porcaro','jeff@toto.com','porcaro.jpg','Technician',1,8),(9,'Mitch','Mitchell','mitch@experience.com','mitchell.jpg','Technician',1,8),(10,'Al','Jackson Jr.',NULL,'jackson.jpg','Technician',1,8);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_cred`
--

DROP TABLE IF EXISTS `user_cred`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_cred` (
  `user_id` bigint NOT NULL,
  `cred_password` varchar(255) DEFAULT NULL,
  `cred_username` varchar(255) DEFAULT NULL,
  `cred_logged_in` int DEFAULT NULL,
  `cred_id` bigint NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`cred_id`),
  KEY `user_id_idx` (`user_id`),
  CONSTRAINT `usercred_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_cred`
--

LOCK TABLES `user_cred` WRITE;
/*!40000 ALTER TABLE `user_cred` DISABLE KEYS */;
INSERT INTO `user_cred` VALUES (1,NULL,'chad',0,1),(2,'mOQ3tQKJV/2Sm7D3wO6uFk+N0+y567+v8C7pAWx5SW8=','miranda',0,2),(3,'oTI/byJq+sXvcU5E6pl8QbgprgSHzs7wjwZiW7f7WyA=','stewart',0,3),(4,'oTI/byJq+sXvcU5E6pl8QbgprgSHzs7wjwZiW7f7WyA=','ginger',0,4),(5,'oTI/byJq+sXvcU5E6pl8QbgprgSHzs7wjwZiW7f7WyA=','john',0,5),(6,'oTI/byJq+sXvcU5E6pl8QbgprgSHzs7wjwZiW7f7WyA=','keith',0,6),(7,'oTI/byJq+sXvcU5E6pl8QbgprgSHzs7wjwZiW7f7WyA=','j',1,7),(8,'oTI/byJq+sXvcU5E6pl8QbgprgSHzs7wjwZiW7f7WyA=','jeff',0,8),(9,'oTI/byJq+sXvcU5E6pl8QbgprgSHzs7wjwZiW7f7WyA=','mitch',0,9),(10,'oTI/byJq+sXvcU5E6pl8QbgprgSHzs7wjwZiW7f7WyA=','al',0,10);
/*!40000 ALTER TABLE `user_cred` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_task`
--

DROP TABLE IF EXISTS `user_task`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_task` (
  `user_id` bigint NOT NULL,
  `task_id` bigint NOT NULL,
  `usertask_id` bigint NOT NULL AUTO_INCREMENT,
  `usertask_created_date` date DEFAULT NULL,
  PRIMARY KEY (`usertask_id`),
  KEY `usertask_user_id_idx` (`user_id`),
  KEY `usertask_task_id_idx` (`task_id`),
  CONSTRAINT `usertask_task_id` FOREIGN KEY (`task_id`) REFERENCES `task` (`task_id`) ON DELETE CASCADE,
  CONSTRAINT `usertask_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=87 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_task`
--

LOCK TABLES `user_task` WRITE;
/*!40000 ALTER TABLE `user_task` DISABLE KEYS */;
INSERT INTO `user_task` VALUES (6,2,2,'2022-05-03'),(4,3,3,'2022-05-03'),(1,4,4,'2022-05-03'),(1,5,5,'2022-05-04'),(6,6,6,'2022-05-04'),(4,7,7,'2022-05-04'),(3,8,8,'2022-05-05'),(4,9,9,'2022-05-05'),(5,10,10,'2022-05-05'),(6,11,11,'2022-05-05'),(1,12,12,'2022-05-06'),(9,13,13,'2022-05-06'),(10,14,14,'2022-05-06'),(10,24,16,'2022-05-03'),(1,28,17,'2022-05-03'),(5,30,18,'2022-05-03'),(4,34,19,'2022-05-04'),(10,43,20,'2022-05-04'),(1,44,21,'2022-05-04'),(5,51,22,'2022-05-05'),(5,52,23,'2022-05-05'),(3,27,25,'2022-05-05'),(9,29,26,'2022-05-06'),(8,31,27,'2022-05-06'),(3,35,28,'2022-05-06'),(9,37,29,'2022-05-07'),(8,19,30,'2022-05-03'),(5,26,31,'2022-05-03'),(3,32,32,'2022-05-03'),(1,38,33,'2022-05-03'),(5,41,34,'2022-05-04'),(3,54,35,'2022-05-04'),(8,55,36,'2022-05-04'),(8,56,37,'2022-05-05'),(5,57,38,'2022-05-05'),(3,58,39,'2022-05-05'),(4,59,40,'2022-05-05'),(3,60,41,'2022-05-06'),(5,61,42,'2022-05-06'),(8,62,43,'2022-05-06'),(8,20,44,'2022-05-03'),(6,23,45,'2022-05-03'),(10,25,46,'2022-05-03'),(8,33,47,'2022-05-03'),(6,39,48,'2022-05-04'),(10,45,49,'2022-05-04'),(9,21,50,'2022-05-04'),(5,36,51,'2022-05-05'),(6,40,52,'2022-05-05'),(9,46,53,'2022-05-05'),(10,22,54,'2022-05-05'),(3,47,55,'2022-05-06'),(3,48,56,'2022-05-06'),(9,53,57,'2022-05-06'),(3,64,58,'2022-05-05'),(4,65,59,'2022-05-05'),(4,66,60,'2022-05-05'),(1,67,61,'2022-05-05'),(1,1,80,'2022-05-12'),(3,1,82,'2022-05-12'),(3,17,84,'2022-05-12'),(4,17,85,'2022-05-12'),(10,15,86,'2022-05-13');
/*!40000 ALTER TABLE `user_task` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_taskpref`
--

DROP TABLE IF EXISTS `user_taskpref`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_taskpref` (
  `taskpref_id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `taskpref_tasktype` varchar(45) DEFAULT NULL,
  `taskpref_proficiency` int DEFAULT NULL,
  PRIMARY KEY (`taskpref_id`),
  KEY `userttype_user_id_idx` (`user_id`),
  CONSTRAINT `userttype_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=90 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_taskpref`
--

LOCK TABLES `user_taskpref` WRITE;
/*!40000 ALTER TABLE `user_taskpref` DISABLE KEYS */;
INSERT INTO `user_taskpref` VALUES (1,1,'Initial Inspection',5),(2,3,'Refurbishment',5),(3,4,'Disassembly',5),(4,5,'Cleaning',5),(5,6,'Initial Inspection',5),(6,8,'Repair',5),(7,9,'Manufacture',5),(8,10,'Reassembly',5),(9,1,'Final Inspection',4),(10,3,'Reassembly',4),(11,4,'Initial Inspection',4),(12,5,'Manufacture',4),(13,6,'Repair',4),(14,8,'Cleaning',4),(15,9,'Refurbishment',4),(16,10,'Disassembly',4),(17,1,'Disassembly',5),(18,3,'Cleaning',4),(19,4,'Final Inspection',5),(20,5,'Disassembly',4),(21,6,'Manufacture',5),(22,8,'Refurbishment',4),(23,9,'Reassembly',5),(24,10,'Repair',4),(88,1,'Cleaning',4),(89,1,'Refurbishment',4);
/*!40000 ALTER TABLE `user_taskpref` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'vultureapp'
--
/*!50106 SET @save_time_zone= @@TIME_ZONE */ ;
/*!50106 DROP EVENT IF EXISTS `reset_user_available_time` */;
DELIMITER ;;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;;
/*!50003 SET character_set_client  = utf8mb4 */ ;;
/*!50003 SET character_set_results = utf8mb4 */ ;;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_ENGINE_SUBSTITUTION' */ ;;
/*!50003 SET @saved_time_zone      = @@time_zone */ ;;
/*!50003 SET time_zone             = 'SYSTEM' */ ;;
/*!50106 CREATE*/ /*!50117 DEFINER=`root`@`localhost`*/ /*!50106 EVENT `reset_user_available_time` ON SCHEDULE EVERY 1 DAY STARTS '2022-05-12 17:34:02' ON COMPLETION NOT PRESERVE ENABLE DO UPDATE vultureapp.user set user_available_time = 8 */ ;;
/*!50003 SET time_zone             = @saved_time_zone */ ;;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;;
/*!50003 SET character_set_client  = @saved_cs_client */ ;;
/*!50003 SET character_set_results = @saved_cs_results */ ;;
/*!50003 SET collation_connection  = @saved_col_connection */ ;;
DELIMITER ;
/*!50106 SET TIME_ZONE= @save_time_zone */ ;

--
-- Dumping routines for database 'vultureapp'
--

--
-- Final view structure for view `role`
--

/*!50001 DROP VIEW IF EXISTS `role`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `role` AS select distinct `user`.`user_role` AS `role` from `user` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `tasktype`
--

/*!50001 DROP VIEW IF EXISTS `tasktype`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `tasktype` AS select distinct `task`.`task_tasktype` AS `tasktype` from `task` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2022-05-15 18:44:15
