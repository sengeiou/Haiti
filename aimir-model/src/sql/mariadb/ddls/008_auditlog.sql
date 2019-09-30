CREATE TABLE `auditlog` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `action` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `createdDate` datetime(6) DEFAULT NULL,
  `currentState` varchar(1024) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `entityId` bigint(20) NOT NULL,
  `entityName` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `instanceName` varchar(1024) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `loginId` varchar(1024) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `previousState` varchar(1024) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `propertyName` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


