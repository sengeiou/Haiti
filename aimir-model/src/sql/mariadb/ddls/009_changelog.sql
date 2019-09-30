
CREATE TABLE `changelog` (
  `id` bigint(20) NOT NULL  AUTO_INCREMENT,
  `changeDate` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `changeTime` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `currentVal` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `operationCode` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `operationLogId` bigint(20) DEFAULT NULL,
  `operatorId` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `operatorTypeCode` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `previousVal` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `property` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `seq` int(11) DEFAULT NULL,
  `target` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `targetTypeCode` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `totalChangeCnt` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `changelogsetting` (
  `id` bigint(20) NOT NULL  AUTO_INCREMENT,
  `isLoggingCode` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `labelCode` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `supplierId` bigint(20) DEFAULT NULL,
  `targetCode` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;