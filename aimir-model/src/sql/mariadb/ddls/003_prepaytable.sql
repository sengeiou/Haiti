CREATE TABLE `add_balance_ws_charging` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `account_id` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `amount` double NOT NULL,
  `arrears` double DEFAULT NULL,
  `auth_code` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `contract_number` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `date_time` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `encryption_key` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `mds_id` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `municipalityCode_id` int(11) DEFAULT NULL,
  `power_limit` double DEFAULT NULL,
  `source` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `supplier_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tariff_code` int(11) DEFAULT NULL,
  `transaction_id` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `write_date` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_ADD_BALANCE_WS_CHARGING_municipalityCode_id` (`municipalityCode_id`),
  CONSTRAINT `FK_ADD_BALANCE_WS_CHARGING_municipalityCode_id` FOREIGN KEY (`municipalityCode_id`) REFERENCES `code` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE `change_credit_type_ws` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `apply_date_time` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `contract_number` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `credit_display_func` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `date_time` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `disconn_func` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `encryption_key` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `mds_id` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `payment_mode` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `supplier_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;



CREATE TABLE `check_balance_setting_ws` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `contract_number` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `date_time` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `encryption_key` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `mds_id` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `mobile_device_id` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `NOTIFICATION_INTERVAL` int(11) DEFAULT NULL,
  `NOTIFICATION_PERIOD` int(11) DEFAULT NULL,
  `NOTIFICATION_TIME` int(11) DEFAULT NULL,
  `NOTIFICATION_WEEKLY_FRI` bit(1) DEFAULT NULL,
  `NOTIFICATION_WEEKLY_MON` bit(1) DEFAULT NULL,
  `NOTIFICATION_WEEKLY_SAT` bit(1) DEFAULT NULL,
  `NOTIFICATION_WEEKLY_SUN` bit(1) DEFAULT NULL,
  `NOTIFICATION_WEEKLY_THU` bit(1) DEFAULT NULL,
  `NOTIFICATION_WEEKLY_TUE` bit(1) DEFAULT NULL,
  `NOTIFICATION_WEEKLY_WED` bit(1) DEFAULT NULL,
  `supplier_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `threshold` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;



CREATE TABLE `verify_prepayment_customer_ws` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `customer_number` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `supplier_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `transaction_id` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `write_date` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE `prepayment_ws_change_credit` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `change_credit_yn` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `contract_number` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `credit` double NOT NULL,
  `date_time` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `encryption_key` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `increment_yn` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `mds_id` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `supplier_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `prepayment_ws_change_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `change_yn` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `contract_number` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `date_time` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `encryption_key` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `mds_id` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `new_supplier_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `supplier_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE `get_balance_ws_get_history` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `contract_number` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `supplier_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `get_balance_ws_get_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `contract_number` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `mds_id` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `supplier_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `transaction_id` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `write_date` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;



CREATE TABLE `prepayment_ws_change_param` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `contract_number` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `date_time` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `device_id` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `emergency_auto_yn` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `emergency_yn` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `encryption_key` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `max_duration` int(11) DEFAULT NULL,
  `mds_id` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `supplier_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `prepayment_ws_change_tariff` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `apply_date_time` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `contract_number` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `date_time` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `encryption_key` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `mds_id` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `supplier_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `tariff_id` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `prepayment_ws_restart_account` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `contract_number` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `date_time` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `encryption_key` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `mds_id` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `rebilling_yn` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `restart_yn` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `supplier_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `zero_credit_yn` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `zero_emergency_yn` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE `emergency_credit_ws_start` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `apply_date_time` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `contract_number` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `date_time` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `device_id` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `encryption_key` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `mds_id` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `supplier_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;




CREATE TABLE `ws_cms_tariffent` (
  `TARIFF_CODE` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `TARIFF_GROUP` int(11) NOT NULL,
  `WRITE_DATE` varchar(14) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`TARIFF_CODE`,`TARIFF_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE `ws_cms_debtent` (
  `CUSTOMER_ID` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `DEBT_REF` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `DEBT_AMOUNT` double DEFAULT NULL,
  `DEBT_CONTRACT_COUNT` int(11) DEFAULT NULL,
  `DEBT_PAYMENT_COUNT` int(11) DEFAULT NULL,
  `DEBT_STATUS` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `DEBT_TYPE` varchar(5) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `FIRST_DEBT` double DEFAULT NULL,
  `INSTALLMENT_DUE_DATE` varchar(8) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `WRITE_DATE` varchar(14) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`CUSTOMER_ID`,`DEBT_REF`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE `ws_cms_meterent` (
  `MAKE` varchar(5) COLLATE utf8mb4_unicode_ci NOT NULL,
  `METER_SERIAL_NO` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `BATCH_NO` varchar(15) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `MODEL` varchar(5) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `WRITE_DATE` varchar(14) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`MAKE`,`METER_SERIAL_NO`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `ws_cms_custent` (
  `CUSTOMER_ID` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `ADDRESS_1` varchar(90) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ADDRESS_2` varchar(130) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ADDRESS_3` varchar(80) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `EMAIL` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `EXIST` bit(1) DEFAULT NULL,
  `FAX` varchar(22) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ID_NO` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ID_TYPE` varchar(5) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `OTHER_NAMES` varchar(80) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `SURNAME` varchar(60) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `TAX_REF_NO` varchar(14) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `TELEPHONE_1` varchar(15) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `TELEPHONE_2` varchar(15) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `TELEPHONE_3` varchar(15) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `WRITE_DATE` varchar(14) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `servPointId` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`CUSTOMER_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;



CREATE TABLE `ws_cms_servpoint` (
  `SERVPOINT_ID` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `ADDRESS_1` varchar(90) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ADDRESS_2` varchar(130) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ADDRESS_3` varchar(80) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `BLOCK_FLAG` bit(1) DEFAULT NULL,
  `BLOCK_REASON` varchar(1024) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `EXIST` bit(1) DEFAULT NULL,
  `GEO_CODE` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `METER_SERIAL_NO` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `TARIFF_CODE` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `TARIFF_GROUP` int(11) DEFAULT NULL,
  `WRITE_DATE` varchar(14) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `customerId` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `MAKE` varchar(5) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`SERVPOINT_ID`),
  KEY `FK_WS_CMS_SERVPOINT_CUSTOMERID` (`customerId`),
  KEY `FK_WS_CMS_SERVPOINT_02` (`MAKE`,`METER_SERIAL_NO`),
  KEY `FK_WS_CMS_SERVPOINT_03` (`TARIFF_CODE`,`TARIFF_GROUP`),
  CONSTRAINT `FK_WS_CMS_SERVPOINT_02` FOREIGN KEY (`MAKE`, `METER_SERIAL_NO`) REFERENCES `ws_cms_meterent` (`MAKE`, `METER_SERIAL_NO`),
  CONSTRAINT `FK_WS_CMS_SERVPOINT_03` FOREIGN KEY (`TARIFF_CODE`, `TARIFF_GROUP`) REFERENCES `ws_cms_tariffent` (`TARIFF_CODE`, `TARIFF_GROUP`),
  CONSTRAINT `FK_WS_CMS_SERVPOINT_CUSTOMERID` FOREIGN KEY (`customerId`) REFERENCES `ws_cms_custent` (`CUSTOMER_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;