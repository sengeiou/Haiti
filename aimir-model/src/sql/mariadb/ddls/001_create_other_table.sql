

/*DROP TABLE aimir.code CASCADE;

CREATE TABLE `code` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `code` varchar(100)  NOT NULL,
  `descr` varchar(100)  DEFAULT NULL,
  `name` varchar(100) NOT NULL,
  `codeorder` int(11) DEFAULT NULL,
  `parent_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_CODE_code` (`code`),
  KEY `FK_CODE_parent_id` (`parent_id`),
  CONSTRAINT `FK_CODE_parent_id` FOREIGN KEY (`parent_id`) REFERENCES `code` (`id`) on delete CASCADE,
) ENGINE=InnoDB;



SELECT DISTINCT table_name
FROM INFORMATION_SCHEMA.STATISTICS
WHERE TABLE_SCHEMA = 'aimir';
*/

CREATE TABLE `language` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `code_2` varchar(2) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `code_3` varchar(3) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `name` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_LANGUAGE_01` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE `country` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `code_2` varchar(2) COLLATE utf8mb4_unicode_ci NOT NULL,
  `code_3` varchar(3) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `code_number` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `name` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_COUNTRY_01` (`code_2`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE `aimirtimezone` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `city` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `continent` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `DST_ABBR` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `DST_ADJUSTMENT` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `DST_ENDDATERULE` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `DST_NAME` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `DST_STARTDATERULE` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `endTime` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `GMT_OFFSET` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `startTime` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `STD_ABBR` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `STD_NAME` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `use_enable` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_AIMIRTIMEZONE_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE `aimirseason` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `START_YEAR` varchar(4) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `syear` varchar(4) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `smonth` varchar(2) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `sday` varchar(2) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `eyear` varchar(4) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `emonth` varchar(2) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `eday` varchar(2) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_AIMIRSEASON_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `code` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `descr` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `codeorder` int(11) DEFAULT NULL,
  `parent_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_CODE_01` (`code`),
  KEY `FK_CODE_parent_id` (`parent_id`),
  CONSTRAINT `FK_CODE_parent_id` FOREIGN KEY (`parent_id`) REFERENCES `code` (`id`) on delete CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;



CREATE TABLE `co2formula` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `co2emissions` double DEFAULT NULL,
  `co2factor` double DEFAULT NULL,
  `name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `type_id` int(11) DEFAULT NULL,
  `unit` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `unitUsage` double DEFAULT NULL,
   PRIMARY KEY (`id`),
  KEY `FK_CO2FORMULA_type_id` (`type_id`),
  CONSTRAINT `FK_CO2FORMULA_type_id` FOREIGN KEY (`type_id`) REFERENCES `code` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;







CREATE TABLE `supplier` 
   (	
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(50) NOT NULL, 
	`address` VARCHAR(150), 
	`administrator` VARCHAR(100), 
	`area` DECIMAL(19,4), 
	`attribute` VARCHAR(100), 
	`commission_rate` DOUBLE DEFAULT 0, 
	`country_id` BIGINT, 
	`descr` VARCHAR(100), 
	`image` VARCHAR(200), 
	`lang_id` BIGINT, 
	`license_meter_count` BIGINT, 
	`license_use` BIGINT, 
	`sys_date_pattern` VARCHAR(50), 
	`tax_rate` DOUBLE DEFAULT 0, 
	`telno` VARCHAR(20), 
	`timezone_id` BIGINT, 
	`cd_decimalseperator` VARCHAR(50), 
	`cd_groupingseperator` VARCHAR(50), 
	`cd_pattern` VARCHAR(50), 
	`cd_round` VARCHAR(50), 
	`md_decimalseperator` VARCHAR(50), 
	`md_groupingseperator` VARCHAR(50), 
	`md_pattern` VARCHAR(50), 
	`md_round` VARCHAR(50), 
	 PRIMARY KEY (`id`), 
	 UNIQUE KEY `UK_SUPPLIER_01` (`name`)
   ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

  

CREATE TABLE `location` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `geocode` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `supplier_id` int(11) DEFAULT NULL,
  `name` varchar(40) COLLATE utf8mb4_unicode_ci NOT NULL,
  `orderNo` int(11) DEFAULT NULL,
  `parent_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_LOCATION_01` (`geocode`,`supplier_id`),
  KEY `FK_LOCATION_parent_id` (`parent_id`),
  KEY `FK_LOCATION_supplier_id` (`supplier_id`),
  CONSTRAINT `FK_LOCATION_parent_id` FOREIGN KEY (`parent_id`) REFERENCES `location` (`id`),
  CONSTRAINT `FK_LOCATION_supplier_id` FOREIGN KEY (`supplier_id`) REFERENCES `supplier` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `aimirgroup` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `NAME` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `GroupName` varchar(31) COLLATE utf8mb4_unicode_ci NOT NULL,
  `ALL_USERS_ACCESS` bit(1) DEFAULT NULL,
  `descr` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `GROUP_TYPE` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `mobileNo` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `operator_id` int(11) DEFAULT NULL,
  `SUPPLIER_ID` int(11) DEFAULT NULL,
  `WRITE_DATE` varchar(14) COLLATE utf8mb4_unicode_ci NOT NULL,
  `CHECK_INTERVAL` int(11) DEFAULT NULL,
  `LOAD_TYPE` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `SUPPLY_CAPACITY` double DEFAULT NULL,
  `SUPPLY_THRESHOLD` double DEFAULT NULL,
  `TRACE_LOG` bit(1) DEFAULT NULL,
  `GROUP_KEY` int(11) DEFAULT NULL,
  `HomeGroup_MCU_ID` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_AIMIRGROUP_NAME` (`NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE `group_member` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `group_id` int(11) DEFAULT NULL,
  `isRegistration` bit(1) DEFAULT NULL,
  `LAST_SYNC_DATE` varchar(14) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `member` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `WRITE_DATE` varchar(14) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_GROUP_MEMBER_group_id` (`group_id`),
  CONSTRAINT `FK_GROUP_MEMBER_group_id` FOREIGN KEY (`group_id`) REFERENCES `aimirgroup` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `group_strategy` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `config_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `config_value` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `create_date` varchar(14) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `group_id` int(11) DEFAULT NULL,
  `login_id` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `prev_value` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `update_date` varchar(14) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_GROUP_STRATEGY_group_id` (`group_id`),
  CONSTRAINT `FK_GROUP_STRATEGY_group_id` FOREIGN KEY (`group_id`) REFERENCES `aimirgroup` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `supplier_id` int(11) DEFAULT NULL,
  `customerRole` bit(1) DEFAULT NULL,
  `dlmsAuthority` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `hasDashboardAuth` bit(1) DEFAULT NULL,
  `loginAuthority` bit(1) DEFAULT NULL,
  `maxMeters` int(11) DEFAULT NULL,
  `mtrAuthority` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `systemAuthority` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `descr` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_ROLE_01` (`name`),
  KEY `FK_ROLE_supplier_id` (`supplier_id`),
  CONSTRAINT `FK_ROLE_supplier_id` FOREIGN KEY (`supplier_id`) REFERENCES `supplier` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `role_code` (
  `Role_id` int(11) NOT NULL,
  `commands_id` int(11) NOT NULL,
  PRIMARY KEY (`Role_id`,`commands_id`),
  KEY `FK_ROLE_CODE_commands_ID` (`commands_id`),
  CONSTRAINT `FK_ROLE_CODE_Role_ID` FOREIGN KEY (`Role_id`) REFERENCES `role` (`id`),
  CONSTRAINT `FK_ROLE_CODE_commands_ID` FOREIGN KEY (`commands_id`) REFERENCES `code` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE `gadget` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `role_id` int(11) DEFAULT NULL,
  `gadgetCode` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `maxUrl` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `miniUrl` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `fullHeight` int(11) DEFAULT 800,
  `miniHeight` int(11) DEFAULT 350,
  `descr` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `iconSrc` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_GADGET_01` (`name`),
  KEY `FK_GADGET_role_id` (`role_id`),
  CONSTRAINT `FK_GADGET_role_id` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `gadget_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `role_id` int(11) DEFAULT NULL,
  `gadget_id` int(11) DEFAULT NULL,
  `supplier_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_GADGET_ROLE_01` (`role_id`,`gadget_id`,`supplier_id`),
  KEY `FK_GADGET_ROLE_supplier_id` (`supplier_id`),
  KEY `FK_GADGET_ROLE_gadget_id` (`gadget_id`),
  CONSTRAINT `FK_GADGET_ROLE_gadget_id` FOREIGN KEY (`gadget_id`) REFERENCES `gadget` (`id`),
  CONSTRAINT `FK_GADGET_ROLE_role_id` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`),
  CONSTRAINT `FK_GADGET_ROLE_supplier_id` FOREIGN KEY (`supplier_id`) REFERENCES `supplier` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;




CREATE TABLE `operator` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `loginId` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `address1` varchar(150) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `address2` varchar(150) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `aliasName` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `deniedReason` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `email` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `emailYn` int(11) DEFAULT 1,
  `failedLoginCount` int(11) DEFAULT NULL,
  `IS_FIRSTLOGIN` bit(1) DEFAULT NULL,
  `LAST_CHARGE_DATE` varchar(14) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `lastLoginTime` varchar(14) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `lastPasswordChangeTime` varchar(14) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `locale` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `LOCATION_ID` int(11) DEFAULT NULL,
  `loginDenied` bit(1) DEFAULT NULL,
  `mobileNumber` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `name` varchar(256) COLLATE utf8mb4_unicode_ci NOT NULL,
  `OPERATOR_STATUS` int(11) DEFAULT 1,
  `password` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `pucNumber` varchar(250) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `role_id` int(11) DEFAULT NULL,
  `showDefaultDashboard` bit(1) DEFAULT NULL,
  `smsYn` int(11) DEFAULT 1,
  `supplier_id` int(11) DEFAULT NULL,
  `telNo` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `UPDATE_DATE` varchar(14) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `USE_LOCATION` bit(1) DEFAULT NULL,
  `WRITE_DATE` varchar(14) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `zipCode` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `CASH_POINT` int(11) DEFAULT NULL,
  `DEPOSIT` float DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_OPERATOR_01` (`loginId`),
  KEY `FK_OPERATOR_supplier_id` (`supplier_id`),
  KEY `FK_OPERATOR_LOCATION_ID` (`LOCATION_ID`),
  KEY `FK_OPERATOR_role_id` (`role_id`),
  CONSTRAINT `FK_OPERATOR_LOCATION_ID` FOREIGN KEY (`LOCATION_ID`) REFERENCES `location` (`id`),
  CONSTRAINT `FK_OPERATOR_role_id` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`),
  CONSTRAINT `FK_OPERATOR_supplier_id` FOREIGN KEY (`supplier_id`) REFERENCES `supplier` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE `dashboard` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `descr` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `maxGridX` int(11) DEFAULT NULL,
  `maxGridY` int(11) DEFAULT NULL,
  `operator_id` int(11) DEFAULT NULL,
  `orderNo` int(11) DEFAULT NULL,
  `role_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_DASHBOARD_role_id` (`role_id`),
  KEY `FK_DASHBOARD_operator_id` (`operator_id`),
  CONSTRAINT `FK_DASHBOARD_operator_id` FOREIGN KEY (`operator_id`) REFERENCES `operator` (`id`),
  CONSTRAINT `FK_DASHBOARD_role_id` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `dashboardgadget` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `collapsible` bit(1) DEFAULT NULL,
  `dashboard_id` int(11) DEFAULT NULL,
  `gadget_id` int(11) DEFAULT NULL,
  `gridx` int(11) NOT NULL,
  `gridy` int(11) NOT NULL,
  `layout` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_DASHBOARDGADGET_01` (`dashboard_id`,`gridx`,`gridy`,`gadget_id`),
  KEY `FK_DASHBOARDGADGET_gadget_id` (`gadget_id`),
  CONSTRAINT `FK_DASHBOARDGADGET_dashboard_id` FOREIGN KEY (`dashboard_id`) REFERENCES `dashboard` (`id`),
  CONSTRAINT `FK_DASHBOARDGADGET_gadget_id` FOREIGN KEY (`gadget_id`) REFERENCES `gadget` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;



CREATE TABLE `tag` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `gadget_id` int(11) DEFAULT NULL,
  `tag` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_TAG_gadget_id` (`gadget_id`),
  CONSTRAINT `FK_TAG_gadget_id` FOREIGN KEY (`gadget_id`) REFERENCES `gadget` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;








CREATE TABLE `circuitbreakerlog` (
  `id` bigint(20) NOT NULL  AUTO_INCREMENT,
  `condition_method` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `result` int(11) DEFAULT NULL,
  `status` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `supplier_id` int(11) DEFAULT NULL,
  `target_id` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `target_type` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `write_time` varchar(14) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_CIRCUITBREAKERLOG_supplier_id` (`supplier_id`),
  CONSTRAINT `FK_CIRCUITBREAKERLOG_supplier_id` FOREIGN KEY (`supplier_id`) REFERENCES `supplier` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `circuitbreakersetting` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `alarm` bit(1) DEFAULT NULL,
  `alarm_threshold` double DEFAULT NULL,
  `automatic_activation` bit(1) DEFAULT NULL,
  `automatic_deactivation` bit(1) DEFAULT NULL,
  `blocking_threshold` double DEFAULT NULL,
  `condition_method` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `recovery_time` int(11) DEFAULT NULL,
  `time_unit` int(11) DEFAULT NULL,
  `SUPPLIER_ID` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_CIRCUITBREAKERSETTING_SUPPLIER_ID` (`SUPPLIER_ID`),
  CONSTRAINT `FK_CIRCUITBREAKERSETTING_SUPPLIER_ID` FOREIGN KEY (`SUPPLIER_ID`) REFERENCES `supplier` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;




CREATE TABLE `commstatus` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `EVENTRCVCOUNT` int(11) DEFAULT NULL,
  `MTRRECEIVECOUNT` int(11) DEFAULT NULL,
  `MTRSAVECOUNT` int(11) DEFAULT NULL,
  `ONDEMANDCOUNT` int(11) DEFAULT NULL,
  `SUPPLIERID` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `YYYYMMDD` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `commstatus_by_commdevice` (
  `comm_dev_id` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `YYYYMMDDHHMMSS` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `EVENTRCVCOUNT` int(11) DEFAULT NULL,
  `INFORECEIVECOUNT` int(11) DEFAULT NULL,
  `MTRRECEIVECOUNT` int(11) DEFAULT NULL,
  `MTRSAVECOUNT` int(11) DEFAULT NULL,
  `ONDEMANDCOUNT` int(11) DEFAULT NULL,
  `YYYYMMDD` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`comm_dev_id`,`YYYYMMDDHHMMSS`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;



CREATE TABLE `devicereg_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `CREATE_DATE` varchar(14) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `devicemodel_id` int(11) DEFAULT NULL,
  `device_name` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `deviceType` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `FAIL_COUNT` varchar(11) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `operator_id` int(11) DEFAULT NULL,
  `REG_TYPE` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `result` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `SHIPMENT_FILE_NAME` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `SUCCESS_COUNT` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `SUPPLIER_ID` int(11) DEFAULT NULL,
  `TOTAL_COUNT` varchar(11) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `message` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;




CREATE TABLE `disttrfmrsubstation` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `LOCATION_ID` int(11) DEFAULT NULL,
  `SUPPLIER_ID` int(11) DEFAULT NULL,
  `address` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `threshold` double DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;




CREATE TABLE `ebs_device` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `address` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `create_dt` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `descr` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `LOCATION_ID` int(11) DEFAULT NULL,
  `meter_id` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `modify_dt` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `orderId` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `parent_mid` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `parent_type_id` int(11) DEFAULT NULL,
  `SUPPLIER_ID` int(11) DEFAULT NULL,
  `threshold` double DEFAULT NULL,
  `top_parent_mid` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `type_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `ecgbillingintegration` (
  `batchNo` int(11) NOT NULL,
  `fileName` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `meterReadingDate` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `sendResult` bit(1) DEFAULT NULL,
  `totalMeterCount` int(11) DEFAULT NULL,
  `totalReadingCount` int(11) DEFAULT NULL,
  `writeDate` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`batchNo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;



CREATE TABLE `enddevice` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `category_id` int(11) DEFAULT NULL,
  `controller_id` int(11) DEFAULT NULL,
  `devicemodel_id` int(11) DEFAULT NULL,
  `drLevel` int(11) DEFAULT NULL,
  `drProgramMandatory` bit(1) DEFAULT NULL,
  `ENERGY_EFFICIENCY` int(11) DEFAULT NULL,
  `energyType` tinyblob DEFAULT NULL,
  `friendly_Name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `HOMEDEVICE_GROUP_NAME` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `HOMEDEVICE_IMG_FILENAME` varchar(250) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `INSTALL_DATE` varchar(14) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `installStatus_id` int(11) DEFAULT NULL,
  `loadControl` bit(1) DEFAULT NULL,
  `LOCATION_ID` int(11) DEFAULT NULL,
  `MAC_ADDR` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `MANUFACTURE_DATE` varchar(14) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `manufacturer` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `MODEL_NAME` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `MODEL_NUMBER` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `MODEM_ID` int(11) DEFAULT NULL,
  `POWER_CONSUMPTION` double DEFAULT NULL,
  `PROTOCOL_TYPE` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `SERIAL_NUMBER` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `simpleSignalLevel` int(11) DEFAULT NULL,
  `status_id` int(11) DEFAULT NULL,
  `SUPPLIER_ID` int(11) DEFAULT NULL,
  `UPC` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `UUID` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ZONE_ID` int(11) DEFAULT NULL,
  `ENERGYTYPE_ID` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `enddevicelog` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `categoryCode` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `enddevice_id` int(11) DEFAULT NULL,
  `friendly_Name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `LOCATION_ID` int(11) DEFAULT NULL,
  `locationName` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `preStatusCode` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `statusCode` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `writeDatetime` varchar(14) COLLATE utf8mb4_unicode_ci NOT NULL,
  `ZONE_ID` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE `firealarmmessagelog` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `alarm` bit(1) NOT NULL,
  `alarmType` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `batteryLevel` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `correlationId` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `eventType` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `message` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `messageId` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `reason` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `result` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `sendDate` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `sended` bit(1) NOT NULL,
  `source` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `target` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `temperature` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `timestamp` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `unitType` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `writeDate` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;



CREATE TABLE `home_device_drlevel` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `category_id` int(11) DEFAULT NULL,
  `DRLEVEL` int(11) NOT NULL,
  `DRLEVEL_IMG_FILENAME` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `DRNAME` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_HOME_DEVICE_DRLEVEL_category_id` (`category_id`),
  CONSTRAINT `FK_HOME_DEVICE_DRLEVEL_category_id` FOREIGN KEY (`category_id`) REFERENCES `code` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `home_group_assets` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `ENDDEVICE_ID` int(11) DEFAULT NULL,
  `CUSTOMER_NUMBER` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `UPDATE_DATE` varchar(14) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `WRITE_DATE` varchar(14) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_HOME_GROUP_ASSETS_ENDDEVICE_ID` (`ENDDEVICE_ID`),
  CONSTRAINT `FK_HOME_GROUP_ASSETS_ENDDEVICE_ID` FOREIGN KEY (`ENDDEVICE_ID`) REFERENCES `enddevice` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `jsgt_message_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `jmsCorrelationId` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `jmsMessageId` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `lPTime` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `lPValue` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `message` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `nuriMeterId` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `processMethod` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `requestDateHour` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `requestDateTime` varchar(14) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `responseDateTime` varchar(14) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `sendDateTime` varchar(14) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `sendSeq` int(11) DEFAULT NULL,
  `sended` varchar(1) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `smartMeterId` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;




CREATE TABLE `zone` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `location_id` int(11) DEFAULT NULL,
  `name` varchar(40) COLLATE utf8mb4_unicode_ci NOT NULL,
  `orderNo` int(11) DEFAULT NULL,
  `parent_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_ZONE_0` (`name`,`location_id`),
  KEY `FK_ZONE_parent_id` (`parent_id`),
  KEY `FK_ZONE_location_id` (`location_id`),
  CONSTRAINT `FK_ZONE_location_id` FOREIGN KEY (`location_id`) REFERENCES `location` (`id`)  on delete CASCADE,
  CONSTRAINT `FK_ZONE_parent_id` FOREIGN KEY (`parent_id`) REFERENCES `zone` (`id`)  on delete CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE `loadcontrolschedule` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `create_time` varchar(14) COLLATE utf8mb4_unicode_ci NOT NULL,
  `delay` int(11) NOT NULL,
  `end_time` varchar(14) COLLATE utf8mb4_unicode_ci NOT NULL,
  `ON_OFF` int(11) NOT NULL,
  `run_time` int(11) DEFAULT NULL,
  `SCHEDULE_TYPE` int(11) NOT NULL,
  `start_time` varchar(14) COLLATE utf8mb4_unicode_ci NOT NULL,
  `status` int(11) DEFAULT NULL,
  `SUPPLIER_ID` int(11) DEFAULT NULL,
  `target_id` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `target_type` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `week_day` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `loadlimitschedule` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `create_time` varchar(14) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `end_time` varchar(14) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `power_limit` double DEFAULT NULL,
  `limit_type` int(11) DEFAULT NULL,
  `open_period` int(11) DEFAULT NULL,
  `interval_type` int(11) DEFAULT NULL,
  `SCHEDULE_TYPE` int(11) NOT NULL,
  `start_time` varchar(14) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `SUPPLIER_ID` int(11) DEFAULT NULL,
  `target_id` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `target_type` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `week_day` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE `login_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `LOGIN_DATE` varchar(14) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `LOGOUT_DATE` varchar(14) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `login_id` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `IP_ADDR` varchar(25) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `operator_id` int(11) DEFAULT NULL,
  `SESSION_ID` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `memo` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `cont` varchar(800) COLLATE utf8mb4_unicode_ci NOT NULL,
  `coord` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `in_date` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `userId` bigint(20) NOT NULL,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE `measurement_history` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `YYYYMMDD` varchar(8) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `HHMMSS` varchar(6) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `WRITE_DATE` varchar(14) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `DEVICE_ID` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `DATA_COUNT` int(11) DEFAULT NULL,
  `DATA_TYPE` int(11) DEFAULT NULL,
  `DEVICE_TYPE` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `RAW_DATA` tinyblob DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;





CREATE TABLE `display_channel` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `ch_method` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `channel_value` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `local_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `reverse_energy` bit(1) DEFAULT NULL,
  `service_type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `unit` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_DISPLAY_CHANNEL_01` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;




CREATE TABLE `meter_attr` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `METER_ID` int(11) NOT NULL,
  `ALARM_DATE` varchar(14) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ALARM_VALUE` varchar(16) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `INT_ATTR_00` int(11) DEFAULT NULL,
  `INT_ATTR_01` int(11) DEFAULT NULL,
  `INT_ATTR_02` int(11) DEFAULT NULL,
  `TEXT_ATTR_00` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `TEXT_ATTR_01` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `TEXT_ATTR_02` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `TEXT_ATTR_03` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `TEXT_ATTR_04` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `TEXT_ATTR_05` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `TEXT_ATTR_06` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `TEXT_ATTR_07` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `TEXT_ATTR_08` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `TEXT_ATTR_09` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_METER_ATTR_01` (`METER_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;






CREATE TABLE `metering_fail` (
  `mdev_id` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `mdev_type` int(11) NOT NULL,
  `YYYYMMDD` varchar(8) COLLATE utf8mb4_unicode_ci NOT NULL,
  `device_id` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `device_type` int(11) DEFAULT NULL,
  `FAIL_REASON` int(11) DEFAULT NULL,
  `LAST_COMMDATE` varchar(14) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `meteringType` int(11) DEFAULT NULL,
  `writeDate` varchar(14) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `contract_id` int(11) DEFAULT NULL,
  `enddevice_id` int(11) DEFAULT NULL,
  `location_id` int(11) DEFAULT NULL,
  `meter_id` int(11) DEFAULT NULL,
  `modem_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`mdev_id`,`mdev_type`,`YYYYMMDD`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `metering_sla` (
  `supplier_id` int(11) NOT NULL,
  `YYYYMMDD` varchar(8) COLLATE utf8mb4_unicode_ci NOT NULL,
  `COMM_PERMITTED_METERS` bigint(20) DEFAULT NULL,
  `delivered_meters` bigint(20) DEFAULT NULL,
  `PERMITTED_METERS` bigint(20) DEFAULT NULL,
  `SLA_METERS` bigint(20) DEFAULT NULL,
  `success_rate` double DEFAULT NULL,
  `TOTAL_GATHERED_METERS` bigint(20) DEFAULT NULL,
  `TOTAL_INSTALLED_METERS` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`supplier_id`,`YYYYMMDD`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;



CREATE TABLE `meterinstallimg` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `currentTimeMillisName` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `METER_ID` int(11) DEFAULT NULL,
  `orginalName` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `meterprogram` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `KIND` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `LAST_MODIFIED_DATE` varchar(16) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `METERCONFIG_ID` int(11) NOT NULL,
  `SETTINGS` longtext COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `meterprogramlog` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `APPLIED_DATE` varchar(16) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `METER_ID` int(11) NOT NULL,
  `METERPROGRAM_ID` int(11) NOT NULL,
  `RESULT` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `metertimesync_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `AFTER_DATE` varchar(14) COLLATE utf8mb4_unicode_ci NOT NULL,
  `BEFORE_DATE` varchar(14) COLLATE utf8mb4_unicode_ci NOT NULL,
  `DESCR` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `LOCATION_ID` int(11) DEFAULT NULL,
  `METER_DATE` varchar(14) COLLATE utf8mb4_unicode_ci NOT NULL,
  `meter_id` int(11) DEFAULT NULL,
  `OPERATOR` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `OPERATOR_TYPE` int(11) DEFAULT NULL,
  `RESULT` int(11) NOT NULL,
  `SUPPLIER_ID` int(11) DEFAULT NULL,
  `TIME_DIFF` bigint(20) DEFAULT NULL,
  `WRITE_DATE` varchar(14) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE `modem_power_log` (
  `device_id` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `device_type` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `HHMMSS` varchar(6) COLLATE utf8mb4_unicode_ci NOT NULL,
  `YYYYMMDD` varchar(8) COLLATE utf8mb4_unicode_ci NOT NULL,
  `BATTERY_VOLT` double DEFAULT NULL,
  `RESET_COUNT` bigint(20) DEFAULT NULL,
  `SOLAR_ADV` double DEFAULT NULL,
  `SOLAR_BCDV` double DEFAULT NULL,
  `SOLAR_CHGBV` double DEFAULT NULL,
  `SUPPLIER_ID` int(11) DEFAULT NULL,
  `VOLTAGE_CURRENT` double DEFAULT NULL,
  `VOLTAGE_OFFSET` int(11) DEFAULT NULL,
  PRIMARY KEY (`device_id`,`device_type`,`HHMMSS`,`YYYYMMDD`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;



CREATE TABLE `power_alarm_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `closeTime` varchar(14) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `duration` bigint(20) DEFAULT NULL,
  `lineType` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `massivePowerOutage` bit(1) DEFAULT NULL,
  `message` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `meter_id` int(11) DEFAULT NULL,
  `openTime` varchar(14) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `plannedPO` bit(1) DEFAULT NULL,
  `status` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `SUPPLIER_ID` int(11) DEFAULT NULL,
  `TYPE_ID` int(11) DEFAULT NULL,
  `writeTime` varchar(14) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;




CREATE TABLE `poweronofforder` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `applicationFault` int(11) DEFAULT NULL,
  `failMessage` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `handleDate` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `isSend` bit(1) DEFAULT NULL,
  `meterReading` double DEFAULT NULL,
  `meterSerialNumber` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `meterType` int(11) DEFAULT NULL,
  `orderStatus` int(11) DEFAULT NULL,
  `powerOperation` int(11) DEFAULT NULL,
  `powerOperationDate` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `referenceId` bigint(20) DEFAULT NULL,
  `testResult` int(11) DEFAULT NULL,
  `userCreateDate` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `userName` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `userReference` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;





CREATE TABLE `scheduleresultlog` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `CREATE_TIME` varchar(14) COLLATE utf8mb4_unicode_ci NOT NULL,
  `COMMAND_PARAMETER` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `CRON_EXP` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `END_DATE` varchar(8) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ERROR_MESSAGE` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `JOB_NAME` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `NEXT_FIRED` varchar(14) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `OPERATOR` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `OPERATOR_TYPE` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `REPEAT_COUNT` int(11) DEFAULT NULL,
  `REPEAT_INTERVAL` bigint(20) DEFAULT NULL,
  `RESPONSE_TIME` varchar(14) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `RESULT` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `RESULT_TARGET` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `RESULT_TYPE` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `START_DATE` varchar(8) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `TARGET` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `TARGET_TYPE` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `TRIGGER_NAME` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `TRIGGER_TYPE` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;



CREATE TABLE `sim_card` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `ICC_ID` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `IMSI` varchar(15) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `PHONE_NUMBER` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `smsinfo` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `contract_id` int(11) NOT NULL,
  `LAST_NOTIFICATION_DATE` varchar(14) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `SMS_NUMBER` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `smsservicelog` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `EUI_ID` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `MSG` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `MSG_ID` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `RECEIVE_NO` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `RESULT` varchar(2000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `SEND_NO` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `SENDTIME` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `snr_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `DCU_ID` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `device_id` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `device_type` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `HHMMSS` varchar(6) COLLATE utf8mb4_unicode_ci NOT NULL,
  `SNR` double DEFAULT NULL,
  `YYYYMMDD` varchar(8) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;



CREATE TABLE `supply_control_ws_interrupt` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `apply_date_time` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `contract_number` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `date_time` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `encryption_key` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `mds_id` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `power_delay` double NOT NULL,
  `supplier_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `supply_control_ws_rearm` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `contract_number` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `date_time` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `encryption_key` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `mds_id` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `rearm_date_time` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `supplier_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `supplycapacitylog` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `contractCapacity` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `CONTRACT_NUMBER` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `location_id` int(11) DEFAULT NULL,
  `supplier_id` int(11) DEFAULT NULL,
  `supplyType` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `supplyTypeLocation` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `writeDatetime` varchar(14) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `supplytype` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `billDate` varchar(8) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `co2formula_id` int(11) DEFAULT NULL,
  `supplier_id` int(11) DEFAULT NULL,
  `type_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_SUPPLYTYPE_co2formula_id` (`co2formula_id`),
  KEY `FK_SUPPLYTYPE_supplier_id` (`supplier_id`),
  KEY `FK_SUPPLYTYPE_type_id` (`type_id`),
  CONSTRAINT `FK_SUPPLYTYPE_co2formula_id` FOREIGN KEY (`co2formula_id`) REFERENCES `co2formula` (`id`),
  CONSTRAINT `FK_SUPPLYTYPE_supplier_id` FOREIGN KEY (`supplier_id`) REFERENCES `supplier` (`id`),
  CONSTRAINT `FK_SUPPLYTYPE_type_id` FOREIGN KEY (`type_id`) REFERENCES `code` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `supplytypelocation` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `contractcapacity_id` int(11) DEFAULT NULL,
  `location_id` int(11) DEFAULT NULL,
  `supplytype_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;





CREATE TABLE `tarifftype` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `code` int(11) DEFAULT NULL,
  `servicetype_id` int(11) DEFAULT NULL,
  `supplier_id` int(11) DEFAULT NULL,
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_TARIFFTYPE_01` (`name`),
  UNIQUE KEY `UK_TARIFFTYPE_02` (`code`),
  KEY `FK_TARIFFTYPE_servicetype_id` (`servicetype_id`),
  KEY `FK_TARIFFTYPE_supplier_id` (`supplier_id`),
  CONSTRAINT `FK_TARIFFTYPE_servicetype_id` FOREIGN KEY (`servicetype_id`) REFERENCES `code` (`id`),
  CONSTRAINT `FK_TARIFFTYPE_supplier_id` FOREIGN KEY (`supplier_id`) REFERENCES `supplier` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;



CREATE TABLE `tariff_em` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tarifftype_id` int(11) DEFAULT NULL,
  `yyyymmdd` varchar(8) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `active_energy_charge` double DEFAULT NULL,
  `admin_charge` double DEFAULT NULL,
  `condition1` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `condition2` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `distribution_network_charge` double DEFAULT NULL,
  `END_HOUR` varchar(2) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `energy_demand_charge` double DEFAULT NULL,
  `ers` double DEFAULT NULL,
  `maxDemand` double DEFAULT NULL,
  `peak_type` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `rate_rebalancing_levy` double DEFAULT NULL,
  `reactive_energy_charge` double DEFAULT NULL,
  `season_id` int(11) DEFAULT NULL,
  `service_charge` double DEFAULT NULL,
  `START_HOUR` varchar(2) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `supply_size_max` double DEFAULT NULL,
  `supply_size_min` double DEFAULT NULL,
  `supply_size_unit` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `transmission_network_charge` double DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_TARIFF_EM_season_id` (`season_id`),
  KEY `FK_TARIFF_EM_tarifftype_id` (`tarifftype_id`),
  CONSTRAINT `FK_TARIFF_EM_season_id` FOREIGN KEY (`season_id`) REFERENCES `aimirseason` (`id`),
  CONSTRAINT `FK_TARIFF_EM_tarifftype_id` FOREIGN KEY (`tarifftype_id`) REFERENCES `tarifftype` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `tariff_gm` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tarifftype_id` int(11) DEFAULT NULL,
  `yyyymmdd` varchar(8) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `season_id` int(11) DEFAULT NULL,
  `ADJUSTMENT_FACTOR` double DEFAULT NULL,
  `BASIC_RATE` double DEFAULT NULL,
  `SALE_PRICE` double DEFAULT NULL,
  `USAGE_UNIT_PRICE` double DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_TARIFF_GM_tarifftype_id` (`tarifftype_id`),
  KEY `FK_TARIFF_GM_season_id` (`season_id`),
  CONSTRAINT `FK_TARIFF_GM_season_id` FOREIGN KEY (`season_id`) REFERENCES `aimirseason` (`id`),
  CONSTRAINT `FK_TARIFF_GM_tarifftype_id` FOREIGN KEY (`tarifftype_id`) REFERENCES `tarifftype` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `tariff_wm` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tarifftype_id` int(11) DEFAULT NULL,
  `yyyymmdd` varchar(8) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `condition1` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `condition2` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `share_cost` double DEFAULT NULL,
  `supply_size_max` double DEFAULT NULL,
  `supply_size_min` double DEFAULT NULL,
  `supply_size_unit` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `USAGE_UNIT_PRICE` double DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_TARIFF_WM_tarifftype_id` (`tarifftype_id`),
  CONSTRAINT `FK_TARIFF_WM_tarifftype_id` FOREIGN KEY (`tarifftype_id`) REFERENCES `tarifftype` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `tariff_wm_caliber` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `BASIC_RATE` double NOT NULL,
  `BASIC_RATE_HOT` double DEFAULT NULL,
  `CALIBER` double NOT NULL,
  `supplier_id` int(11) DEFAULT NULL,
  `WRITE_TIME` varchar(14) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_TARIFF_WM_CALIBER_01` (`CALIBER`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `tou_rate` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `end_time` varchar(2) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `local_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `peak_type` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `season_id` int(11) DEFAULT NULL,
  `start_time` varchar(2) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tarifftype_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_TOU_RATE_tarifftype_id` (`tarifftype_id`),
  KEY `FK_TOU_RATE_season_id` (`season_id`),
  CONSTRAINT `FK_TOU_RATE_season_id` FOREIGN KEY (`season_id`) REFERENCES `aimirseason` (`id`),
  CONSTRAINT `FK_TOU_RATE_tarifftype_id` FOREIGN KEY (`tarifftype_id`) REFERENCES `tarifftype` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `threshold` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `THRESHOLD_DURATION` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `THRESHOLD_LIMIT` int(11) DEFAULT NULL,
  `MORE` int(11) DEFAULT NULL,
  `THRESHOLD_NAME` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `supplier_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_THRESHOLD_supplier_id` (`supplier_id`),
  CONSTRAINT `FK_THRESHOLD_supplier_id` FOREIGN KEY (`supplier_id`) REFERENCES `supplier` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `thresholdwarning` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `DEVICE_TYPE` int(11) DEFAULT NULL,
  `DEVICEID` int(11) DEFAULT NULL,
  `IP_ADDR` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `supplier_id` int(11) DEFAULT NULL,
  `THRESHOLD_ID` int(11) DEFAULT NULL,
  `VALUE` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_THRESHOLDWARNING_supplier_id` (`supplier_id`),
  CONSTRAINT `FK_THRESHOLDWARNING_supplier_id` FOREIGN KEY (`supplier_id`) REFERENCES `supplier` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;



CREATE TABLE `uploadhistory_em` (
  `id` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `DATA_TYPE` int(11) DEFAULT NULL,
  `END_DATE` varchar(14) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `FAIL_CNT` int(11) DEFAULT NULL,
  `FILE_PATH` varchar(2048) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `LOGIN_ID` varchar(31) COLLATE utf8mb4_unicode_ci NOT NULL,
  `MDS_ID` varchar(31) COLLATE utf8mb4_unicode_ci NOT NULL,
  `METER_REGISTRATION` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `START_DATE` varchar(14) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `SUCCESS_CNT` int(11) DEFAULT NULL,
  `TOTAL_CNT` int(11) DEFAULT NULL,
  `UPLOAD_DATE` varchar(14) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `uploadhistory_faillist_em` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `DATA_TYPE` int(11) DEFAULT NULL,
  `EXTRA_VALUE_1` varchar(2048) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `EXTRA_VALUE_2` varchar(2048) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `FAIL_REASON` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `MD_VALUE` varchar(2048) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `METERING_TIME` varchar(14) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ROW_LINE` int(11) NOT NULL,
  `UPLOAD_ID` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_UPLOADHISTORY_FAILLIST_EM_UPLOAD_ID` (`UPLOAD_ID`),
  CONSTRAINT `FK_UPLOADHISTORY_FAILLIST_EM_UPLOAD_ID` FOREIGN KEY (`UPLOAD_ID`) REFERENCES `uploadhistory_em` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `vee_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `AFTER_VALUE` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ATTR_NAME` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `BEFORE_VALUE` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `channel` int(11) DEFAULT NULL,
  `contract_id` int(11) DEFAULT NULL,
  `DESCR` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `dst` int(11) DEFAULT 0,
  `edit_item` int(11) DEFAULT NULL,
  `HH` varchar(2) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `LOCATION_ID` int(11) DEFAULT NULL,
  `mdev_id` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `mdev_type` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `OPERATOR` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `OPERATOR_TYPE` int(11) DEFAULT NULL,
  `RESULT` int(11) NOT NULL,
  `supplier_id` int(11) DEFAULT NULL,
  `TABLE_NAME` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `WRITE_DATE` varchar(14) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `YYYYMMDD` varchar(8) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `vee_parameter` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `CONDITION_ITEM` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ITEM` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `LOCAL_NAME` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `PARAMETER` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `RULE_TYPE` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `THRESHOLD1` int(11) DEFAULT NULL,
  `THRESHOLD2` int(11) DEFAULT NULL,
  `THRESHOLD_CONDITION1` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `THRESHOLD_CONDITION2` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `THRESHOLD_ITEM` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `THRESHOLD_PERIOD` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `USE_THRESHOLD` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;




CREATE TABLE `ondemandreadingorder` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `applicationFault` int(11) DEFAULT NULL,
  `failMessage` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `handleDate` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `isSend` bit(1) DEFAULT NULL,
  `meterReadingEnergy` double DEFAULT NULL,
  `meterReadingVolume` double DEFAULT NULL,
  `meterSerialNumber` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `meterType` int(11) DEFAULT NULL,
  `meterValueDate` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `orderStatus` int(11) DEFAULT NULL,
  `referenceId` bigint(20) DEFAULT NULL,
  `testResult` int(11) DEFAULT NULL,
  `userName` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `ws_meterconfig_result` (
  `REQUEST_DATE` varchar(14) COLLATE utf8mb4_unicode_ci NOT NULL,
  `trId` varchar(40) COLLATE utf8mb4_unicode_ci NOT NULL,
  `COMMAND` varchar(256) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `deviceId` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `num` int(11) NOT NULL,
  `resultValue` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `WRITE_DATE` varchar(14) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`REQUEST_DATE`,`trId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `ws_meterconfig_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `password` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `update_date` varchar(14) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `userid` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `write_date` varchar(14) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_WMU_userid` (`userid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `ws_meterconfig_log` (
  `REQUEST_DATE` varchar(14) COLLATE utf8mb4_unicode_ci NOT NULL,
  `trId` varchar(40) COLLATE utf8mb4_unicode_ci NOT NULL,
  `ATTRIBUTE_NO` varchar(256) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `CLASS_ID` varchar(256) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `command` varchar(256) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `DESCRIPTION` varchar(256) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `deviceId` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `deviceType` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ERRORCODE` int(11) DEFAULT NULL,
  `modemId` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `MODEM_TYPE` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `OBIS_CODE` varchar(256) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `operator` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `parameter` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `PROTOCOL_TYPE` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `STATE` int(11) DEFAULT NULL,
  `UPDATE_DATE` varchar(14) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `WRITE_DATE` varchar(14) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`REQUEST_DATE`,`trId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `ws_meterconfig_obis` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `attribute_no` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `class_id` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ws_meterconfig_user_id` int(11) DEFAULT NULL,
  `obis_code` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `permission` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_WS_METERCONFIG_OBIS_ws_meterconfig_user_id` (`ws_meterconfig_user_id`),
  CONSTRAINT `FK_WS_METERCONFIG_OBIS_ws_meterconfig_user_id` FOREIGN KEY (`ws_meterconfig_user_id`) REFERENCES `ws_meterconfig_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE `authdelay` (
  `id` bigint(20) NOT NULL  AUTO_INCREMENT,
  `ERRORCNT` int(11) DEFAULT NULL,
  `IPADDRESS` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `LASTDATE` varchar(14) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `supplier_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_AUTHDELAY_supplier_id` (`supplier_id`),
  CONSTRAINT `FK_AUTHDELAY_supplier_id` FOREIGN KEY (`supplier_id`) REFERENCES `supplier` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

