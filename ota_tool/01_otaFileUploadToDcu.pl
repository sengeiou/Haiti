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
 
for ($index = 0; $index <= 1; $index++) {
        $telnet->print('shell "wget -c http://172.16.10.180:8085/aimir-web/uploadImg/fw/modem/NAMR-P226SR_OTA_TEST_V10.ebl&"');
 
        print $telnet->waitfor('/AIMIR>*$/i');
}
 
$telnet -> print("logout");
 
$telnet -> close;
