# LMS Volumen - Система управления обучением

Учебное приложение в целях изучения Spring Framework.

Используются:

* Spring Boot 3
* Spring Data JPA
* Spring Security
* PostgreSQL
* ORM
* JPQL
* Thymeleaf
* Интернационализация
* REST контроллеры + динамические элементы с JavaScript + XMLHttpRequest
* Загрузка файлов через XMLHttpRequest/MultipartFile

Для запуска понадобится база данных:

```
$ psql -d postgres -U postgres

postgres=# create database volumendev1;
postgres=# create user volumendev1 with password 'password';
postgres=# grant all privileges on database volumendev1 to volumendev1;
postgres=# \connect volumendev1;
volumendev1=# GRANT ALL ON SCHEMA public TO volumendev1;
```

