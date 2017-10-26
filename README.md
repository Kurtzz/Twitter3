# Twitter3

## MySQL Settings
https://mathiasbynens.be/notes/mysql-utf8mb4

### add this to my.ini file:
```
[client]
default-character-set = utf8mb4

[mysql]
default-character-set = utf8mb4

[mysqld]
character-set-client-handshake = FALSE
character-set-server = utf8mb4
collation-server = utf8mb4_unicode_ci
```

## Run demo
`mvn clean install`

run: `pl.edu.agh.ed.twitter3.app.Main.main()` method