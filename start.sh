#!/bin/bash

cd ./frontend/
lein package
cd ..
docker-compose -f docker-compose.yml up -d
