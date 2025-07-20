#!/bin/bash
source ./docker.properties
export PROFILE=docker
export COMPOSE_PROFILES=test
export PREFIX="${IMAGE_PREFIX}"
export ALLURE_DOCKER_API=http://allure:5050/
export HEAD_COMMIT_MESSAGE="local-build"
export ARCH=$(uname -m)
export EXECUTION_TYPE="local docker"

docker compose down

docker_containers=$(docker ps -a -q)

if [ ! -z "$docker_containers" ]; then
  echo "### Stop containers: $docker_containers ###"
  docker stop $docker_containers
  docker rm $docker_containers
fi

if [ "$1" = "firefox" ]; then
  echo "### Run on FIREFOX ###"
  export BROWSER=firefox
  docker pull selenoid/vnc_firefox:125.0
fi

images=("postgres" "zookeeper" "kafka" "rococo-auth-docker" "rococo-userdata-docker" "rococo-gateway-docker"
 "rococo-museum-docker" "rococo-artist-docker" "rococo-client-docker" "rococo-painting-docker" "selenoid" "selenoid-ui"
 "allure-docker-service" "allure-docker-service-ui")
build_needed=false
for image in "${images[@]}"; do
  if ! docker images --format "{{.Repository}}" | grep -q "${image}"; then
    echo "### Image not found: '${image}'. Build needed ###"
    build_needed=true
  fi
done

E2E_IMAGE=$(docker images --format "{{.Repository}}:{{.Tag}}" | grep "rococo-e-2-e-tests")
docker rmi "$E2E_IMAGE"
echo "### $E2E_IMAGE removed ###"

if $build_needed; then
  echo "### Build all images except $E2E_IMAGE ###"
  bash ./gradlew clean
  bash ./gradlew jibDockerBuild -x :rococo-e-2-e-tests:build -x :rococo-e-2-e-tests:test
else
  echo "### All images exist except $E2E_IMAGE. No build needed ###"
fi

echo "### Pull chrome images for Selenoid ###"
docker pull erolatex/selenoid_chromium:137.0

docker compose up -d
docker ps -a
