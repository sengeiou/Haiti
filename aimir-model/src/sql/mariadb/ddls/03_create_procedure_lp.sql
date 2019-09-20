DROP PROCEDURE IF EXISTS AIMIR.LP_EXTERNAL_MERGE;

DELIMITER //

CREATE PROCEDURE AIMIR.LP_EXTERNAL_MERGE(IN THREAD_NUM DOUBLE, OUT P_RESULT VARCHAR(4000))
 BEGIN         
        
DECLARE STMT VARCHAR(5000);     
DECLARE START_TIME DOUBLE;     
DECLARE E_TIME DOUBLE;     
        
  DECLARE EXIT HANDLER FOR SQLEXCEPTION BEGIN      
P_RESULT := CONCAT('ERROR [' , IFNULL(SQLERRM, ''),']');
 END;     
   
START_TIME := DBMS_UTILITY.GET_TIME;     
STMT := CONCAT('MERGE INTO AIMIR.LP_EM LP_TOBE USING AIMIR.LP_EM_EXT_',IFNULL(THREAD_NUM, ''),' LP_TOBE_EXT ');   
STMT := CONCAT(IFNULL(STMT, ''),'ON (LP_TOBE.MDEV_ID = LP_TOBE_EXT.MDEV_ID AND LP_TOBE.YYYYMMDDHHMISS = LP_TOBE_EXT.YYYYMMDDHHMISS AND LP_TOBE.CHANNEL = LP_TOBE_EXT.CHANNEL AND LP_TOBE.MDEV_TYPE = LP_TOBE_EXT.MDEV_TYPE AND LP_TOBE.DST = LP_TOBE_EXT.DST) ');   
STMT := CONCAT(IFNULL(STMT, ''),'WHEN MATCHED THEN UPDATE SET LP_TOBE.DEVICE_ID = LP_TOBE_EXT.DEVICE_ID, LP_TOBE.DEVICE_TYPE = LP_TOBE_EXT.DEVICE_TYPE, LP_TOBE.METERINGTYPE = LP_TOBE_EXT.METERINGTYPE, LP_TOBE.DEVICE_SERIAL = LP_TOBE_EXT.DEVICE_SERIAL, LP_TOBE.LP_STATUS = LP_TOBE_EXT.LP_STATUS, LP_TOBE.INTERVAL_YN = LP_TOBE_EXT.INTERVAL_YN, LP_TOBE.VALUE = LP_TOBE_EXT.VALUE, LP_TOBE.WRITEDATE = LP_TOBE_EXT.WRITEDATE, LP_TOBE.CONTRACT_ID = LP_TOBE_EXT.CONTRACT_ID, LP_TOBE.MODEM_TIME = LP_TOBE_EXT.MODEM_TIME, LP_TOBE.DCU_TIME = LP_TOBE_EXT.DCU_TIME ');   
STMT := CONCAT(IFNULL(STMT, ''),'WHEN NOT MATCHED THEN INSERT VALUES (LP_TOBE_EXT.MDEV_ID, LP_TOBE_EXT.YYYYMMDDHHMISS, LP_TOBE_EXT.CHANNEL, LP_TOBE_EXT.MDEV_TYPE, LP_TOBE_EXT.DST, LP_TOBE_EXT.DEVICE_ID, LP_TOBE_EXT.DEVICE_TYPE, LP_TOBE_EXT.METERINGTYPE, LP_TOBE_EXT.DEVICE_SERIAL, LP_TOBE_EXT.LP_STATUS, LP_TOBE_EXT.INTERVAL_YN, LP_TOBE_EXT.VALUE, LP_TOBE_EXT.WRITEDATE, LP_TOBE_EXT.CONTRACT_ID, LP_TOBE_EXT.MODEM_TIME, LP_TOBE_EXT.DCU_TIME) ');    
EXECUTE IMMEDIATE STMT;    
     
E_TIME := DBMS_UTILITY.GET_TIME - START_TIME;     
      
P_RESULT := CONCAT('SUCESS [ELAPSED_TIME : ',IFNULL(E_TIME, ''),']');      
      
                  
END;
//

DELIMITER ;

