-- Run this file if the production database has to be reconfigured.

create database 'smartrfid';
create user 'admin1' identified by 'a1b2c3d4e5';
grant trigger on smartrfid.* to admin1 identified by 'a1b2c3d4e5';
