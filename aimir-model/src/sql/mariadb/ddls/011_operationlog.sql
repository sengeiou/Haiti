CREATE TABLE `operation_constraint` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `CONDITION1` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `CONDITION2` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `DESCRIPTION` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `DEVICE_TYPE` int(11) DEFAULT NULL,
  `FIELD1` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `FIELD2` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `MODEL_ID` int(11) DEFAULT NULL,
  `OPERATION_CODE` int(11) DEFAULT NULL,
  `VALUE1` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `VALUE2` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `DTYPE` varchar(31) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_OPERATION_CONSTRAINT_OPERATION_CODE` (`OPERATION_CODE`),
  KEY `FK_OPERATION_CONSTRAINT_DEVICE_TYPE` (`DEVICE_TYPE`),
  KEY `FK_OPERATION_CONSTRAINT_MODEL_ID` (`MODEL_ID`),
  CONSTRAINT `FK_OPERATION_CONSTRAINT_DEVICE_TYPE` FOREIGN KEY (`DEVICE_TYPE`) REFERENCES `code` (`id`),
  CONSTRAINT `FK_OPERATION_CONSTRAINT_MODEL_ID` FOREIGN KEY (`MODEL_ID`) REFERENCES `devicemodel` (`id`),
  CONSTRAINT `FK_OPERATION_CONSTRAINT_OPERATION_CODE` FOREIGN KEY (`OPERATION_CODE`) REFERENCES `code` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `operation_list` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `DESCRIPTION` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `DEVICE_TYPE` int(11) DEFAULT NULL,
  `LEVELS` int(11) NOT NULL,
  `MODEL_ID` int(11) DEFAULT NULL,
  `OPERATION_CODE` int(11) DEFAULT NULL,
  `PARAM_TYPE` int(11) DEFAULT NULL,
  `DTYPE` varchar(31) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_OPERATION_LIST_MODEL_ID` (`MODEL_ID`),
  KEY `FK_OPERATION_LIST_OPERATION_CODE` (`OPERATION_CODE`),
  KEY `FK_OPERATION_LIST_DEVICE_TYPE` (`DEVICE_TYPE`),
  CONSTRAINT `FK_OPERATION_LIST_DEVICE_TYPE` FOREIGN KEY (`DEVICE_TYPE`) REFERENCES `code` (`id`),
  CONSTRAINT `FK_OPERATION_LIST_MODEL_ID` FOREIGN KEY (`MODEL_ID`) REFERENCES `devicemodel` (`id`),
  CONSTRAINT `FK_OPERATION_LIST_OPERATION_CODE` FOREIGN KEY (`OPERATION_CODE`) REFERENCES `code` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `operation_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `CONTRACTNUMBER` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `DESCRIPTION` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ERROR_REASON` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `HHMMSS` varchar(6) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `OPERATION_COMMAND_CODE` int(11) DEFAULT NULL,
  `OPERATOR_TYPE` int(11) DEFAULT NULL,
  `RESULT_SRC` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `STATUS` int(11) DEFAULT 255,
  `SUPPLIER_ID` int(11) DEFAULT NULL,
  `TARGET_NAME` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `TARGET_TYPE_CODE` int(11) DEFAULT NULL,
  `USER_ID` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `YYYYMMDD` varchar(8) COLLATE utf8mb4_unicode_ci NOT NULL,
  `YYYYMMDDHHMMSS` varchar(14) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_OPERATION_LOG_TARGET_TYPE_CODE` (`TARGET_TYPE_CODE`),
  KEY `FK_OPERATION_LOG_OPERATION_COMMAND_CODE` (`OPERATION_COMMAND_CODE`),
  KEY `FK_OPERATION_LOG_SUPPLIER_ID` (`SUPPLIER_ID`),
  CONSTRAINT `FK_OPERATION_LOG_OPERATION_COMMAND_CODE` FOREIGN KEY (`OPERATION_COMMAND_CODE`) REFERENCES `code` (`id`),
  CONSTRAINT `FK_OPERATION_LOG_SUPPLIER_ID` FOREIGN KEY (`SUPPLIER_ID`) REFERENCES `supplier` (`id`),
  CONSTRAINT `FK_OPERATION_LOG_TARGET_TYPE_CODE` FOREIGN KEY (`TARGET_TYPE_CODE`) REFERENCES `code` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
