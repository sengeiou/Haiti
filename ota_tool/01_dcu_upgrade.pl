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

$telnet -> print('shell "cd /app/sw"');
print $telnet->waitfor('/AIMIR>*$/i');

for ($index = 0; $index <= 6; $index++) {
        $telnet->print('shell "wget -c http://172.16.10.180:8085/aimir-web/uploadImg/fw/mcu/NDC_I531-v4.3-5577.tar.gz&"');
 
        print $telnet->waitfor('/AIMIR>*$/i');
}
 
$telnet -> print('shell "(cd /app/sw; gzip -d NDC_I531-v4.3-5577.tar.gz)&"');
print $telnet->waitfor('/AIMIR>*$/i');
 
$telnet -> print('shell "(cd /app/sw; tar xf NDC_I531-v4.3-5577.tar)&"');
print $telnet->waitfor('/AIMIR>*$/i');

$telnet -> print('shell "(cd /app/sw; ./install)&"');
print $telnet->waitfor('/AIMIR>*$/i');
 
$telnet -> print('reset system');
$telnet -> print('y');
 
$telnet -> print("logout");
