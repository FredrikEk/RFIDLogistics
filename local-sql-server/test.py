#!/usr/bin/env python3

import pymysql

db = pymysql.connect(host="localhost", user="admin", passwd="password",
                     db="kandidat")
cur = db.cursor()
cur.execute("SELECT * FROM pallet")
for row in cur.fetchall():
    print(row[0])
