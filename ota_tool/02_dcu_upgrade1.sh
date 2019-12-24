#!/bin/bash
 
cat dcu_upgrade_list_1.txt | while read dcuip pc
do
iplen=`expr length $dcuip+1`
        echo "# Length of input str -> $iplen"
 
        if [ $iplen -ge 9 ]; then
                perl 01_dcu_upgrade.pl $dcuip 2>&1
        fi
done
