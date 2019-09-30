CREATE TABLE `firmware` (
  `Firmware` varchar(31) COLLATE utf8mb4_unicode_ci NOT NULL,
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `ARM` bit(1) NOT NULL,
  `BINARYFILENAME` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `BUILD` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `CHECK_SUM` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `CRC` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `DEVICEMODEL_ID` int(11) DEFAULT NULL,
  `EQUIP_KIND` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `EQUIP_MODEL` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `EQUIP_TYPE` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `EQUIP_VENDOR` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `FILENAME` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `FILE_PATH` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `FILE_URL_PATH` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `FIRMWARE_ID` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `FW_VERSION` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `HW_VERSION` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `IMAGE_KEY` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `RELEASED_DATE` varchar(14) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `SUPPLIER_ID` int(11) DEFAULT NULL,
  `MCU_TYPE` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `MODEM_TYPE` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `STACK_NAME` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_FIRMWARE_01` (`FIRMWARE_ID`),
  KEY `FK_Firmware_SUPPLIER_ID` (`SUPPLIER_ID`),
  CONSTRAINT `FK_Firmware_SUPPLIER_ID` FOREIGN KEY (`SUPPLIER_ID`) REFERENCES `supplier` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;



CREATE TABLE `firmware_trigger` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `CREATE_DATE` varchar(14) COLLATE utf8mb4_unicode_ci NOT NULL,
  `END_DATE` varchar(14) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `SRC_FWBUILD` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `SRC_FWVER` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `SRC_FIRMWARE` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `SRC_HWVER` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `TARGET_FWBUILD` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `TARGET_FWVER` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `TARGET_FIRMWARE` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `TARGET_HWVER` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `firmwareboard` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `CONTENT` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `FIRMWARE_ID` int(11) DEFAULT NULL,
  `OPERATOR_ID` int(11) DEFAULT NULL,
  `READCOUNT` int(11) DEFAULT NULL,
  `SUPPLIER_ID` int(11) DEFAULT NULL,
  `TITLE` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `TRIGGER_ID` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `WRITEDATE` varchar(14) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_FirmwareBoard_FIRMWARE_ID` (`FIRMWARE_ID`),
  KEY `FK_FirmwareBoard_OPERATOR_ID` (`OPERATOR_ID`),
  KEY `FK_FirmwareBoard_SUPPLIER_ID` (`SUPPLIER_ID`),
  CONSTRAINT `FK_FirmwareBoard_FIRMWARE_ID` FOREIGN KEY (`FIRMWARE_ID`) REFERENCES `firmware` (`id`),
  CONSTRAINT `FK_FirmwareBoard_OPERATOR_ID` FOREIGN KEY (`OPERATOR_ID`) REFERENCES `operator` (`id`),
  CONSTRAINT `FK_FirmwareBoard_SUPPLIER_ID` FOREIGN KEY (`SUPPLIER_ID`) REFERENCES `supplier` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `firmwareconstraints` (
  `FIRMWARECONSTRAINTS` varchar(31) COLLATE utf8mb4_unicode_ci NOT NULL,
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `BUILD` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `CONTAINMENT` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `FIRMWARE_ID` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `FIRMWARECONSTRAINTS_ID` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `FW_VERSION` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `HW_VERSION` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `INSTANCENAME` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_FIRMWARECONSTRAINTS_01` (`FIRMWARECONSTRAINTS_ID`),
  UNIQUE KEY `UK_FIRMWARECONSTRAINTS_02` (`FIRMWARE_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE `firmware_history` (
  `IN_SEQ` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL,
  `ISSUE_DATE` varchar(14) COLLATE utf8mb4_unicode_ci NOT NULL,
  `TR_ID` bigint(20) NOT NULL,
  `EQUIP_ID` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `EQUIP_KIND` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `EQUIP_MODEL` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `EQUIP_TYPE` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `EQUIP_VENDOR` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ERROR_CODE` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `MCU_ID` int(11) DEFAULT NULL,
  `OTA_STATE` int(11) DEFAULT NULL,
  `OTA_STEP` int(11) DEFAULT NULL,
  `TRIGGER_CNT` int(11) DEFAULT NULL,
  `TRIGGER_HISTORY` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `TRIGGER_STATE` int(11) DEFAULT NULL,
  `TRIGGER_STEP` int(11) DEFAULT NULL,
  PRIMARY KEY (`IN_SEQ`,`ISSUE_DATE`,`TR_ID`),
  KEY `FK_FIRMWARE_HISTORY_MCU_ID` (`MCU_ID`),
  CONSTRAINT `FK_FIRMWARE_HISTORY_MCU_ID` FOREIGN KEY (`MCU_ID`) REFERENCES `mcu` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
