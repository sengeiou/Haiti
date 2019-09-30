


CREATE TABLE `notification` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `CONDITION_VALUE` int(11) DEFAULT NULL,
  `EMAIL_ADDRESS` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `EMAILYN` int(11) DEFAULT 1,
  `NOTIFICATION_NAME` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `PERIOD_1` int(11) DEFAULT 1,
  `PERIOD_2` int(11) DEFAULT 1,
  `PERIOD_3` int(11) DEFAULT 1,
  `PERIOD_4` int(11) DEFAULT 1,
  `PERIOD_5` int(11) DEFAULT 1,
  `SMS_ADDRESS` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `SMSYN` int(11) DEFAULT 1,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `notification_template` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `BODY` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `NAME` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `TITLE` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_NOTITEMPLT_01` (`NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `notified_results` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `contents` varchar(2000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `noticeDate` varchar(14) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `REPORT_ID` int(11) DEFAULT NULL,
  `successEmail` int(11) DEFAULT NULL,
  `successSms` int(11) DEFAULT NULL,
  `title` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `totalEmail` int(11) DEFAULT NULL,
  `totalSms` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;




CREATE TABLE `report_file` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `filename` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `report_result_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_REPORT_FILE_report_result_id` (`report_result_id`),
  CONSTRAINT `FK_REPORT_FILE_report_result_id` FOREIGN KEY (`report_result_id`) REFERENCES `notified_results` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE `report_contacts_group` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `NAME` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `OPERATOR_ID` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_REPORT_CONTACTS_GROUP_OPERATOR_ID` (`OPERATOR_ID`),
  CONSTRAINT `FK_REPORT_CONTACTS_GROUP_OPERATOR_ID` FOREIGN KEY (`OPERATOR_ID`) REFERENCES `operator` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `report_board` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `CATEGORY_ITEM` bit(1) DEFAULT NULL,
  `DESCRIPTION` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `METALINK` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `NAME` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `parent_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_REPORT_BOARD_parent_id` (`parent_id`),
  CONSTRAINT `FK_REPORT_BOARD_parent_id` FOREIGN KEY (`parent_id`) REFERENCES `report_board` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `report_contacts` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `EMAIL` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `NAME` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `OPERATOR_ID` int(11) DEFAULT NULL,
  `GROUP_ID` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_REPORT_CONTACTS_GROUP_ID` (`GROUP_ID`),
  KEY `FK_REPORT_CONTACTS_OPERATOR_ID` (`OPERATOR_ID`),
  CONSTRAINT `FK_REPORT_CONTACTS_GROUP_ID` FOREIGN KEY (`GROUP_ID`) REFERENCES `report_contacts_group` (`id`),
  CONSTRAINT `FK_REPORT_CONTACTS_OPERATOR_ID` FOREIGN KEY (`OPERATOR_ID`) REFERENCES `operator` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;



CREATE TABLE `report_operator` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `isEmail` bit(1) DEFAULT NULL,
  `isSms` bit(1) DEFAULT NULL,
  `OPERATOR_ID` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_REPORT_OPERATOR_OPERATOR_ID` (`OPERATOR_ID`),
  CONSTRAINT `FK_REPORT_OPERATOR_OPERATOR_ID` FOREIGN KEY (`OPERATOR_ID`) REFERENCES `operator` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `report_operator_result` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `isEmail` bit(1) DEFAULT NULL,
  `isSms` bit(1) DEFAULT NULL,
  `report_result_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_REPORT_OPERATOR_RESULT_report_result_id` (`report_result_id`),
  CONSTRAINT `FK_REPORT_OPERATOR_RESULT_report_result_id` FOREIGN KEY (`report_result_id`) REFERENCES `notified_results` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `report_parameter` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `PARAMETER_TYPE` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `REPORT_ID` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_REPORT_PARAMETER_report_id` (`REPORT_ID`),
  CONSTRAINT `FK_REPORT_PARAMETER_report_id` FOREIGN KEY (`REPORT_ID`) REFERENCES `report_board` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `report_schedule` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `CRON_FORMAT` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `EMAIL` varchar(1024) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `EXPORT_FORMAT` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `NAME` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `OPERATOR_ID` int(11) DEFAULT NULL,
  `IS_USED` bit(1) DEFAULT NULL,
  `WRITE_TIME` varchar(14) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_REPORT_SCHEDULE_OPERATOR_ID` (`OPERATOR_ID`),
  CONSTRAINT `FK_REPORT_SCHEDULE_OPERATOR_ID` FOREIGN KEY (`OPERATOR_ID`) REFERENCES `operator` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `report_parameterdata` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `REPORTSCHEDULE_ID` int(11) DEFAULT NULL,
  `VALUE` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `REPORTPARAMETER_ID` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_REPORT_PARAMETERDATA_REPORTPARAMETER_ID` (`REPORTPARAMETER_ID`),
  KEY `FK_REPORT_PARAMETERDATA_REPORTSCHEDULE_ID` (`REPORTSCHEDULE_ID`),
  CONSTRAINT `FK_REPORT_PARAMETERDATA_REPORTPARAMETER_ID` FOREIGN KEY (`REPORTPARAMETER_ID`) REFERENCES `report_parameter` (`id`),
  CONSTRAINT `FK_REPORT_PARAMETERDATA_REPORTSCHEDULE_ID` FOREIGN KEY (`REPORTSCHEDULE_ID`) REFERENCES `report_schedule` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `report_result` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `FAIL_REASON` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `OPERATOR_ID` int(11) DEFAULT NULL,
  `REPORTSCHEDULE_ID` int(11) DEFAULT NULL,
  `RESULT` int(11) DEFAULT NULL,
  `RESULT_FILE_LINK` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `RESULT_LINK` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `WRITE_TIME` varchar(14) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_REPORT_RESULT_REPORTSCHEDULE_ID` (`REPORTSCHEDULE_ID`),
  KEY `FK_REPORT_RESULT_OPERATOR_ID` (`OPERATOR_ID`),
  CONSTRAINT `FK_REPORT_RESULT_OPERATOR_ID` FOREIGN KEY (`OPERATOR_ID`) REFERENCES `operator` (`id`),
  CONSTRAINT `FK_REPORT_RESULT_REPORTSCHEDULE_ID` FOREIGN KEY (`REPORTSCHEDULE_ID`) REFERENCES `report_schedule` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `report_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `REPORT_ID` int(11) DEFAULT NULL,
  `ROLE_ID` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_REPORT_ROLE_REPORT_ID` (`REPORT_ID`),
  KEY `FK_REPORT_ROLE_ROLE_ID` (`ROLE_ID`),
  CONSTRAINT `FK_REPORT_ROLE_REPORT_ID` FOREIGN KEY (`REPORT_ID`) REFERENCES `report_board` (`id`),
  CONSTRAINT `FK_REPORT_ROLE_ROLE_ID` FOREIGN KEY (`ROLE_ID`) REFERENCES `role` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;



