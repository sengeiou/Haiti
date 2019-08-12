#!/bin/bash

. /home/aimir/.bashrc



# 1. location
#   - 대상 모뎀의 Location.
#   - 옵션 : 필수
# 2. targetFWVersion
#   - 대상 모뎀의 펌웨어버전
#   - 옵션 : 선택 (default : all)
# 3. lastLinkTimeRange
#   - 대상 모뎀의 통신시간 범위. (Day)
#     EX) 1 : 1일 이내 통신이력있는 대상
#         2 : 2일 이내 통신이력있는 대상
#         3 : 3일 이내 통신이력있는 대상
#   - 옵션 : 선택 (default : 3)
# 4. fwFileName  
#   - 적용할 펌웨어 파일명
#   - 옵션 : 필수
# 5. fwFileVersion
#   - 적용할 펌웨어 파일 버전
#   - 옵션 : 필수
# 6. reTryCount
#   - 재시도 횟수
#   - 옵션 : 선택 (default : 2)
# 7. reTryInterval
#   - 재시도 간격 (Time)
#     ex) 1 : 1 시간 간격으로 실행
#         2 : 2 시간 간격으로 실행
#         3 : 3 시간 간격으로 실행
#   - 옵션 : 선택 (default : 3)

### Properties ####
location=1F
targetFWVersion=0.1
lastLinkTimeRange=3
fwFileName=MBB_V0131_TEST_111
fwFileVersion=0.02
reTryCount=1
reTryInterval=5
####################

CHK_PARAM=(`which grep | wc`)
if [ $CHK_PARAM -ge 2 ]
then
    LOC_GREP=(`which --skip-alias grep`)
    LOC_AWK=(`which --skip-alias awk`)
else
    LOC_GREP=(`which grep`)
    LOC_AWK=(`which awk`)
fi
MVNREPOSITORY=(`$LOC_GREP localRepository ~/.m2/settings.xml | $LOC_AWK -F "[><]" '{print $3}'`)
if [ "$MVNREPOSITORY" == "" ]
then
    MVNREPOSITORY=~/.m2/repository
fi


echo ""
echo "##########################################################"
echo "###  SORIA MBB Modem OTA batch Tool                     ##"
echo "##########################################################"
echo ""

nohup mvn -e -f pom-SoriaMBBModemOTABatch.xml antrun:run -DtaskName=SoriaMBBModemOTABatch -Dlocation=$location -DtargetFWVersion=$targetFWVersion -DlastLinkTimeRange=$lastLinkTimeRange -DfwFileName=$fwFileName -DfwFileVersion=$fwFileVersion -DreTryCount=$reTryCount -DreTryInterval=$reTryInterval -Dspring.instrument.path=$MVNREPOSITORY/org/springframework/spring-instrument/4.2.5.RELEASE > /dev/null 2>&1 &
