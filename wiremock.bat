@echo off
REM Windows batch script for running Wiremock in Docker

echo Pulling latest Wiremock Docker image...
docker pull wiremock/wiremock:2.35.0

echo Stopping and removing existing container if it exists...
docker stop rococo-mock 2>nul
docker rm rococo-mock 2>nul

echo Starting Wiremock container...
docker run --name rococo-mock -p 8080:8080 -v %cd%/wiremock/rest:/home/wiremock -d wiremock/wiremock:2.35.0 --global-response-templating --enable-stub-cors

echo Wiremock is running on http://localhost:8080
