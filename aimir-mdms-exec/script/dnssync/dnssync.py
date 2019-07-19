#!/usr/bin/env python
'''
Created on 2016. 6. 9.

@author: lucky
'''
from ConfigParser import ConfigParser
import MySQLdb
import cx_Oracle
import sys
import os
import logging

Config = ConfigParser()
Config.read('dnssync.cfg')

def get_config_str(section, str):
    return Config.get(section, str)
def get_config_int(str):
    return Config.getint(section, str)

def applied_changed_record_to_dns(dnsConnection, aimirConnection, aimirrecords, domain):
    dnsCursor = dnsConnection.cursor()
    logging.debug("select id from domains where name='{0}'".format(domain))
    dnsCursor.execute("select id from domains where name='{0}'".format(domain))
    rows = dnsCursor.fetchall()
    domainid = 0
    if(len(rows) == 0):
        return False
    else:
        domainid = rows[0][0]

    for aimirrec in aimirrecords:
        if (aimirrec['LOGTYPE'] == 'DELETE' ):
            logging.debug("""delete from records where domain_id={0} and name='{1}'
            """.format(domainid, aimirrec['DOMAINNAME']))
            dnsCursor.execute("""
            delete from records where domain_id={0} and name='{1}'
            """.format(domainid, aimirrec['DOMAINNAME']))
        else:
            if (aimirrec['IPV4'] != None):
                logging.debug("""
                select count(*) from records where domain_id={0} and name='{1}' and type='A'
                """.format(domainid, aimirrec['DOMAINNAME']))
                dnsCursor.execute("""
                select count(*) from records where domain_id={0} and name='{1}' and type='A'
                """.format(domainid, aimirrec['DOMAINNAME']))
                rows = dnsCursor.fetchall()
                if(rows[0][0] > 0):
                    logging.debug("""
                    update records set content='{0}' where domain_id={1} and name='{2}' and type='A'
                    """.format(aimirrec['IPV4'], domainid, aimirrec['DOMAINNAME']))
                    dnsCursor.execute("""
                    update records set content='{0}' where domain_id={1} and name='{2}' and type='A'
                    """.format(aimirrec['IPV4'], domainid, aimirrec['DOMAINNAME']))
                else:
                    logging.debug("""
                    insert into records(domain_id, name, content, type,ttl,prio)
                    values({0}, '{1}', '{2}', 'A', 86400, NULL)
                    """.format(domainid, aimirrec['DOMAINNAME'], aimirrec['IPV4']))
                    dnsCursor.execute("""
                    insert into records(domain_id, name, content, type,ttl,prio)
                    values({0}, '{1}', '{2}', 'A', 86400, NULL)
                    """.format(domainid, aimirrec['DOMAINNAME'], aimirrec['IPV4']))
            elif (aimirrec['IPV6'] != None):
                logging.debug("""
                select count(*) c from records where domain_id={0} and name='{1}' and type='AAAA'
                """.format(domainid, aimirrec['DOMAINNAME']))
                dnsCursor.execute("""
                select count(*) c from records where domain_id={0} and name='{1}' and type='AAAA'
                """.format(domainid, aimirrec['DOMAINNAME']))
                rows = dnsCursor.fetchall()
                if(rows[0][0] > 0):
                    logging.debug("""
                    update records set content='{0}' where domain_id={1} and name='{2}' and type='AAAA'
                    """.format(aimirrec['IPV6'], domainid, aimirrec['DOMAINNAME']))
                    dnsCursor.execute("""
                    update records set content='{0}' where domain_id={1} and name='{2}' and type='AAAA'
                    """.format(aimirrec['IPV6'], domainid, aimirrec['DOMAINNAME']))
                else:
                    logging.debug("""
                    insert into records(domain_id, name, content, type,ttl,prio)
                    values({0}, '{1}', '{2}', 'AAAA', 86400, NULL)
                    """.format(domainid, aimirrec['DOMAINNAME'], aimirrec['IPV6']))
                    dnsCursor.execute("""
                    insert into records(domain_id, name, content, type,ttl,prio)
                    values({0}, '{1}', '{2}', 'AAAA', 86400, NULL)
                    """.format(domainid, aimirrec['DOMAINNAME'], aimirrec['IPV6']))

        aimirCursor = aimirConnection.cursor()
        logging.debug("delete from ip_dnssync_log where rowid='{0}'".format(aimirrec['DELETEFORROWID']))
        aimirCursor.execute("delete from ip_dnssync_log where rowid='{0}'".format(aimirrec['DELETEFORROWID']))

    return True

def get_changed_dns_record_from_aimir(connection):
    cursor = connection.cursor()
    logging.debug("""
    select rowid as deleteforrowid,logtype,domainname,ipv4,ipv6
    from ip_dnssync_log
    order by createdtime""")
    cursor.execute("""
    select rowid as deleteforrowid,logtype,domainname,ipv4,ipv6
    from ip_dnssync_log
    order by createdtime""")

    columns = [i[0] for i in cursor.description]
    return [dict(zip(columns, row)) for row in cursor]

def main():
    loglevel = getattr(logging, get_config_str('LOG', 'level').upper(), None)
    logging.basicConfig(level=loglevel, format='%(asctime)s %(levelname)s : %(message)s')

    logging.info("Start.")
    domain = get_config_str('CONFIG', 'domain')

    dnshost = get_config_str('DB', 'dns.db.ip')
    dnsuser = get_config_str('DB', 'dns.db.user')
    dnspass = get_config_str('DB', 'dns.db.passwd')
    dnsdbname = get_config_str('DB', 'dns.db.name')

    aimiruser = get_config_str('DB', 'aimir.db.user')
    aimirpasswd = get_config_str('DB', 'aimir.db.passwd')
    aimirdburl = get_config_str('DB', 'aimir.db.url')

    
    # Open database connection
    aimirConnection = cx_Oracle.connect(aimiruser, aimirpasswd, aimirdburl)
    dnsConnection = MySQLdb.connect(dnshost, dnsuser, dnspass, dnsdbname)
    aimirConnection.begin()
    dnsConnection.begin()
    # aimirConnection = cx_Oracle.connect('aimir33','aimir33','187.1.10.20:1521/orcl')
    # dnsConnection = MySQLdb.connect("localhost","dnstest","dnstest","DNSTEST" )

    aimirrecords = get_changed_dns_record_from_aimir(aimirConnection)
    logging.debug("aimirrecords count:{0}".format(len(aimirrecords)))
    if (len(aimirrecords) == 0):
        aimirConnection.close()
        logging.info("Finished.")
        return

    try:
        for aimirrec in aimirrecords:
            logging.debug('AIMIR Record : {0}'.format(aimirrec))
    
        isFinished = applied_changed_record_to_dns(dnsConnection, aimirConnection, aimirrecords, domain)
        
        if(isFinished == True):
            dnsConnection.commit()
            aimirConnection.commit()
        else:
            dnsConnection.rollback()
            aimirConnection.rollback()
    except Exception,e:
        logging.error(e)
        dnsConnection.rollback()
        aimirConnection.rollback()
    
    dnsConnection.close()
    aimirConnection.close()

    logging.info("{0} count processed : {1}".format(len(aimirrecords), isFinished))
    logging.info("Finished.")

if __name__ == '__main__':
    main()
