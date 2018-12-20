#!/bin/bash
if [ "$TRAVIS_PULL_REQUEST" == "false" ] && [ $TRAVIS_BRANCH == "master" ]; then
  echo "Release"
else 
  mvn clean verify jacoco:report coveralls:report -Pitests sonar:sonar -DrepoToken=$COVERALLS_TOKEN -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn
fi
