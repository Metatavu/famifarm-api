mysql -u root --password=$MYSQL_ROOT_PASS < scripts/setup-db.sql && mvn clean verify -Pitests
kill -9 $(ps aux|grep wild|sed -r 's/([a-zA-Z +]*)([0-9]{1,}).*/\2/')