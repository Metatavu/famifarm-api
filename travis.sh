#!/bin/bash
export MAVEN_OPTS=-Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn
if [ "$TRAVIS_PULL_REQUEST" == "false" ] && [ $TRAVIS_BRANCH == "master" ]; then
  echo "Release"
else 
  echo "[mysqld]" > $HOME/.my.cnf
  echo "lower-case-table-names = 1" >> $HOME/.my.cnf
  sudo service mysql restart
  mysql -u root < scripts/setup-db.sql
  mvn clean verify jacoco:report coveralls:report -Pitests sonar:sonar -DrepoToken=$COVERALLS_TOKEN --batch-mode -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn
fi
