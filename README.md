# LMS Volumen

Используются:

* Spring Boot 3
* Spring Data JPA
* PostgreSQL
* ORM
* JPQL
* Thymeleaf
* REST контроллеры + JavaScript + XMLHttpRequest
* Загрузка файлов через XMLHttpRequest/MultipartFile


```
$ psql -d postgres -U postgres

postgres=# create database volumendev1;
postgres=# create user volumendev1 with password 'password';
postgres=# grant all privileges on database volumendev1 to volumendev1;
postgres=# \connect volumendev1;
volumendev1=# GRANT ALL ON SCHEMA public TO volumendev1;
```

