#!/bin/bash

set -eou pipefail

# Jump to root directory
cd "$( dirname "${BASH_SOURCE[0]}" )"/../../..

function replace_newline() {
  tr '\n' ' ' | tr '"' '\\"'
}

function request_body() {
  echo "{\"query\": \"$(echo "$1" | replace_newline  )\"}" 
}

function main() {
  mgmnt=$(cat ./tools/dev/demo-data/graph-management.groovy)
  body=$(request_body "$mgmnt")
  echo $body
  curl -X POST -d"$body" localhost:6182
}


main "$@"
