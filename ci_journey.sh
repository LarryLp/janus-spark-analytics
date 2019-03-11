#!/bin/bash

set -eou pipefail

function main() {
  echo_green "Clean up from previous runs..."
  docker-compose down 
  
  echo_green "Build analytics service..."
  echo "Note that the maven build process includes running the unit tests"
  docker-compose build analytics 
  
  echo_green "Start up dependencies..."
  docker-compose up --force-recreate -d 
  
  echo_green "Import required test data"
  go run tools/dev/demo-data/importer.go
}

function echo_green() {
  green='\033[0;32m'
  nc='\033[0m' 
  echo -e "${green}${*}${nc}"
}

main "$@"
