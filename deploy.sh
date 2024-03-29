#!/bin/bash

./mvnw package -Pprod verify -DskipTests jib:dockerBuild
docker-compose -f src/main/docker/app-prod.yml up

docker network disconnect lawfirm-network docker_lawfirm-app_1
docker network disconnect lawfirm-network docker_lawfirm-postgresql_1

docker network rm lawfirm-network
docker network create --subnet 172.20.0.0/16 lawfirm-network

docker network connect --ip 172.20.0.3 lawfirm-network docker_lawfirm-postgresql_1
docker network connect --ip 172.20.0.4 lawfirm-network docker_lawfirm-app_1

docker network inspect lawfirm-network

