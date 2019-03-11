#!/bin/bash

set -eou pipefail

function main() {
  echo_green "Clean up from previous runs..."
  docker-compose down 
  
  echo_green "Build analytics service..."
  echo "Note that the maven build process includes running the unit tests"
  docker-compose build analytics goenv
  
  echo_green "Start up dependencies..."
  docker-compose up --force-recreate -d 
  
  echo_green "Import required test data..."
  docker-compose run goenv sh -c 'go run tools/dev/demo-data/importer.go'

  echo_green "Run journey tests..."
  docker-compose run goenv sh -c 'go test -v ./tools/dev/journey-test/'

  echo_green ""
  echo_green "All tests passed!"
}

function echo_green() {
  green='\033[0;32m'
  nc='\033[0m' 
  echo -e "${green}${*}${nc}"
}

main "$@"
