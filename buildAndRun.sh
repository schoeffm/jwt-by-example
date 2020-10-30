#!/bin/sh
mvn clean package 
docker-compose rm -f && docker-compose build && docker-compose up -d && docker-compose logs -f
