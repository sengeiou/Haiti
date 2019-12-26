#!/usr/bin/perl
use Net::Telnet;
print "\n===========mcu option setting[";
print "$ARGV[0]";
print "] start========================================\n";
$telnet = new Net::Telnet ( Timeout=>500, Errmode=>'die' );
 
$telnet->open($ARGV[0]);
print $telnet->waitfor('/user[: ]*$/i');
 
$telnet->print("aimir");
print $telnet->waitfor('/password[: ]*$/i');
 
$telnet->print("aimir");
$line = $telnet->getline;
print $line;
 
print $telnet->waitfor('/AIMIR>*$/i');
 
$telnet->print("upgrade sensor ansi 1.0 1.0 6 NAMR-P226SR_OTA_TEST_V10.ebl\n");
$telnet->print("y");
 
print $telnet->waitfor('/AIMIR>*$/i');
 
$telnet -> print("logout");
 
$telnet -> close;
