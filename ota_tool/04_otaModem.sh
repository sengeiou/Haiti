#!/bin/bash
 
cat dcu_list_1.txt | while read dcuip pc
do
iplen=`expr length $dcuip+1`
        echo "# Length of input str -> $iplen"
 
        if [ $iplen -ge 9 ]; then
                perl 03_otaModem.pl $dcuip 2>&1
        fi
done
