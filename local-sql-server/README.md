# Local MySQL server

Docker is a tool for creating and running virtual, isolated Linux containers on
a Linux system.

## Build

To create a Docker image with mysql-server installed within, run:

    $ sudo docker build -t rfid/db .

This will create a Docker image from [Dockerfile](Dockerfile).

## Run

Start it with the port 3306 published to the host machine:

    $ sudo docker run -i -t -p 3306:3306 rfid/db bash

3306 is MySQL's default port.

Then inside the container, start mysqld with:

    $ service mysql start

## Test getting data from the database on the outside

    $ virtualenv -p /usr/bin/python3 env
    $ source env/bin/activate
    $ pip install pymysql
    $ python3 test.py

## Connect to the database from the outside of the container

    $ mysql -h 127.0.0.1 -u admin -ppassword kandidat

## Configure Play

Open `conf/application.conf` and set the following settings:

    db.default.driver=com.mysql.jdbc.Driver
    db.default.url="jdbc:mysql://localhost/kandidat?characterEncoding=UTF-8"
    db.default.user="admin"
    db.default.password="password"
