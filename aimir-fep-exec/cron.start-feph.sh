. /home/aimir/.bashrc

CHK_ALIVE=(`netstat -ano|grep :::8000 |grep LISTEN | wc -l`)
    if [ $CHK_ALIVE -ge 1 ]
    then
    echo "feph is working"
    CONNCNT=`netstat -na | grep 8000 | grep EST |wc -l`
    if [ $CONNCNT -ge 8000 ]
    then
        echo "restarting"
        PID=`/bin/ps -eaf | /bin/grep java | /bin/grep FEP1 | /bin/grep feph | /bin/awk '{print $2}'`
        for pid in $PID
        do            
            kill $pid
        done
        sleep 5
	cd /home/aimir/aimiramm/aimir-fep-exec
        ./start-feph.sh
    fi
    
    CLOSE_CNT=`netstat -na | grep CLOSE |wc -l`        
    if [ $CLOSE_CNT -ge 50 ]
    then

        PID=`/bin/ps -eaf | /bin/grep java | /bin/grep FEP1 | /bin/grep feph | /bin/awk '{print $2}'`
        for pid in $PID
        do  
            kill $pid
        done
        sleep 5
	cd /home/aimir/aimiramm/aimir-fep-exec
        ./start-feph.sh
        
        echo "$timestamp - Feph complated(CLOSE_WAIT Cnt : $CLOSE_CNT)" 
    fi
else
    cd /home/aimir/aimiramm/aimir-fep-exec
    ./start-feph.sh
fi

