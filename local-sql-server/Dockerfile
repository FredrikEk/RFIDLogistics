# Some code taken from http://txt.fliglio.com/2013/11/creating-a-mysql-docker-container/

FROM debian:latest

RUN apt-get update
RUN apt-get install -y mysql-server

RUN sed -i "s/\(^bind.*$\)/#\1/" /etc/mysql/my.cnf

RUN /usr/sbin/mysqld & \
    sleep 10s &&\
    echo "GRANT ALL ON *.* TO admin@'%' IDENTIFIED BY 'password' WITH GRANT OPTION; FLUSH PRIVILEGES" | mysql &&\
    echo "CREATE DATABASE kandidat" | mysql
