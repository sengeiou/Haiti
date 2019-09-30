CREATE TABLE `async_command_log` (
  `mcuId` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `trId` bigint(20) NOT NULL,
  `command` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `createTime` varchar(14) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `curNice` int(11) DEFAULT NULL,
  `curTry` int(11) DEFAULT NULL,
  `day` int(11) DEFAULT NULL,
  `deviceId` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `deviceType` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `errorCode` int(11) DEFAULT NULL,
  `eventType` int(11) DEFAULT NULL,
  `initNice` int(11) DEFAULT NULL,
  `initTry` int(11) DEFAULT NULL,
  `lastTime` varchar(14) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `operator` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `queue` int(11) DEFAULT NULL,
  `requestTime` varchar(14) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `resultCnt` int(11) DEFAULT NULL,
  `state` int(11) DEFAULT NULL,
  `trOption` int(11) DEFAULT NULL,
  PRIMARY KEY (`mcuId`,`trId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `async_command_param` (
  `mcuId` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `num` int(11) NOT NULL,
  `trId` bigint(20) NOT NULL,
  `paramType` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `paramValue` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `TR_TYPE` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`mcuId`,`num`,`trId`),
  KEY `FK_ASYNC_COMMAND_PARAM_mcuId` (`mcuId`,`trId`),
  CONSTRAINT `FK_ASYNC_COMMAND_PARAM_mcuId` FOREIGN KEY (`mcuId`, `trId`) REFERENCES `async_command_log` (`mcuId`, `trId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `async_command_result` (
  `mcuId` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `num` int(11) NOT NULL,
  `trId` bigint(20) NOT NULL,
  `data` blob DEFAULT NULL,
  `length` bigint(20) DEFAULT NULL,
  `oid` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `resultType` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `resultValue` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `TR_TYPE` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`mcuId`,`num`,`trId`),
  KEY `FK_ASYNC_COMMAND_RESULT_mcuId` (`mcuId`,`trId`),
  CONSTRAINT `FK_ASYNC_COMMAND_RESULT_mcuId` FOREIGN KEY (`mcuId`, `trId`) REFERENCES `async_command_log` (`mcuId`, `trId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
