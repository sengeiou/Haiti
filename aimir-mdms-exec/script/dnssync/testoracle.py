#!/usr/bin/python

import cx_Oracle

dsn_tns = cx_Oracle.makedsn('187.1.10.20', 1521, 'orcl')
connection = cx_Oracle.connect("AIMIR33", "aimir33", dsn_tns)
#connection = cx_Oracle.connect('aimir33/aimir33@187.1.10.20AIMIR33')

cursor = connection.cursor()
cursor.execute('select sysdate from dual')

rows = cursor.fetchall()

for row in rows:
    print row[0]

connection.close()
