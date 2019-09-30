

CREATE TABLE `firmware_issue` (
  `ISSUEDATE` varchar(14) COLLATE utf8mb4_unicode_ci NOT NULL,
  `FIRMWAREID` bigint(20) NOT NULL,
  `LOCATIONID` int(11) NOT NULL,
  `COMMAND_TYPE` int(11) DEFAULT NULL,
  `name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `EXECUTE_TYPE` int(11) DEFAULT NULL,
  `RETRY_COUNT` int(11) DEFAULT NULL,
  `RETRY_CYCLE` int(11) DEFAULT NULL,
  `step1Count` int(11) DEFAULT NULL,
  `step2Count` int(11) DEFAULT NULL,
  `step3Count` int(11) DEFAULT NULL,
  `step4Count` int(11) DEFAULT NULL,
  `step5Count` int(11) DEFAULT NULL,
  `step6Count` int(11) DEFAULT NULL,
  `step7Count` int(11) DEFAULT NULL,
  `totalCount` int(11) DEFAULT NULL,
  PRIMARY KEY (`ISSUEDATE`,`FIRMWAREID`,`LOCATIONID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `firmware_issue_history` (
  `ISSUEDATE` varchar(14) COLLATE utf8mb4_unicode_ci NOT NULL,
  `DEVICEID` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `DEVICETYPE` int(11) NOT NULL,
  `FIRMWAREID` bigint(20) NOT NULL,
  `LOCATIONID` int(11) NOT NULL,
  `DCU_ID` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `REQUESTID` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `RESULT_STATUS` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `STEP` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `USE_BYPASS` bit(1) DEFAULT NULL,
  `UPDATEDATE` varchar(14) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`ISSUEDATE`,`DEVICEID`,`DEVICETYPE`,`FIRMWAREID`,`LOCATIONID`),
  CONSTRAINT `FK_FIRMWARE_ISSUE_HISTORY_01` FOREIGN KEY (`ISSUEDATE`, `FIRMWAREID`, `LOCATIONID`) REFERENCES `firmware_issue` (`ISSUEDATE`, `FIRMWAREID`, `LOCATIONID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;