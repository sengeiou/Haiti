#!/bin/bash

function addDcuSerialIpSecConf()
{
    count=`fgrep $3 $1 | wc -l`
    if [ $count -ge 1 ]; then
        echo "$3 is already registered in $1"
        #return
    fi
    #echo -e "conn $2-$3\n        rightsubnet=fd${3:6:2}:${3:8:4}:${3:12:4}::/64\n        rightid=@$3"
    echo -e "conn $2-$3\n        rightsubnet=fd${3:6:2}:${3:8:4}:${3:12:4}::/64\n        rightid=@$3" >> $1
    #echo "$1 $3 $count"
}
function addDcuSerialIpSecSecrets()
{
    count=`fgrep $2 $1 | wc -l`
    if [ $count -ge 1 ]; then
        echo "$2 is already registered in $1"
        #return
    fi
    #echo -e "@nuri-vpn-01 @$2 : PSK 0x0e022d550bb8cfd40928aca428f08035"
    echo -e "@nuri-vpn-01 @$2 : PSK 0x0e022d550bb8cfd40928aca428f08035" >> $1
    #echo "$1 $2 $count"
}

if [ $# -ne 4 ]; then
  echo "$0 /etc/ipsec.conf /etc/ipsec.secrets Training 000B12000000AABB"
  exit
fi

if [ -f $1 ]; then
  if [ -f $2 ]; then
    addDcuSerialIpSecConf $1 $3 $4
    addDcuSerialIpSecSecrets $2 $4
  else
    echo "$2 file not found."
  fi
else
  echo "$1 file not found."
fi