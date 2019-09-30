CREATE TABLE `eventalert` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `autoClosed` int(11) DEFAULT NULL,
  `descr` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `EVENTALERT_TYPE` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `MONITOR_TYPE` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `msgPattern` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `oid` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `SEVERITY_TYPE` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `timeout` int(11) DEFAULT NULL,
  `troubleAdvice` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_EVENTALERT_01` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `eventalertlog` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `activatorId` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `activatorIp` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ACTIVATOR_TYPE` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `closeTime` varchar(14) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `duration` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `EVENTALERT_ID` int(11) DEFAULT NULL,
  `LOCATION_ID` int(11) DEFAULT NULL,
  `message` varchar(1024) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `occurCnt` int(11) DEFAULT NULL,
  `openTime` varchar(14) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `SEVERITY_TYPE` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `STATUS` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `supplier_id` int(11) DEFAULT NULL,
  `writeTime` varchar(14) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;



CREATE TABLE `profile` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `activatorId` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ACTIVATOR_TYPE` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `eventAlertType` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `popup` bit(1) DEFAULT NULL,
  `popupCnt` int(11) DEFAULT NULL,
  `SEVERITY_TYPE` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `sound` bit(1) DEFAULT NULL,
  `STATUS` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `EVENTALERT_ID` int(11) DEFAULT NULL,
  `LOCATION_ID` int(11) DEFAULT NULL,
  `METEREVENT_ID` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `OPERATOR_ID` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_PROFILE_OPERATOR_ID` (`OPERATOR_ID`),
  KEY `FK_PROFILE_LOCATION_ID` (`LOCATION_ID`),
  KEY `FK_PROFILE_EVENTALERT_ID` (`EVENTALERT_ID`),
  KEY `FK_PROFILE_METEREVENT_ID` (`METEREVENT_ID`),
  CONSTRAINT `FK_PROFILE_EVENTALERT_ID` FOREIGN KEY (`EVENTALERT_ID`) REFERENCES `eventalert` (`id`),
  CONSTRAINT `FK_PROFILE_LOCATION_ID` FOREIGN KEY (`LOCATION_ID`) REFERENCES `location` (`id`),
  CONSTRAINT `FK_PROFILE_METEREVENT_ID` FOREIGN KEY (`METEREVENT_ID`) REFERENCES `meterevent` (`id`),
  CONSTRAINT `FK_PROFILE_OPERATOR_ID` FOREIGN KEY (`OPERATOR_ID`) REFERENCES `operator` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;