# LMS Volumen

```
$ psql -d postgres -U postgres

postgres=# create database volumendev1;
CREATE DATABASE
postgres=# create user volumendev1 with password 'password';
postgres=# grant all privileges on database volumendev1 to volumendev1;
GRANT
postgres=# \connect volumendev1;
Вы подключены к базе данных "volumendev1" как пользователь "postgres".
volumendev1=# GRANT ALL ON SCHEMA public TO volumendev1;
GRANT
volumendev1=# 
```

