create user if not exists sa identified by 'sa';
grant all on fftest.* to sa; 
drop database if exists fftest; 
create database fftest default charset utf8;