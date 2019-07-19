#!/bin/bash

CURRENT_DATE=`date +"%Y%m%d"`

cat euiidlist.txt | while read euiid pc
do
    ./addvpninfo.sh /etc/ipsec.conf /etc/ipsec.secrets nuri $euiid 2>&1 |tee addvpninfo_${CURRENT_DATE}.log
done