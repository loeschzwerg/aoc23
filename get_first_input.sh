#!/bin/bash
if [ -z "$1" ] ; then
  echo "Usage:"
  echo "  $0 <day>"
  exit 1
elif [ -z "$(cat .cookie)" ]; then
  echo "Set a session \`.cookie\` file before execution"
  exit 2
fi
curl https://adventofcode.com/2023/day/$1/input --cookie session="$(cat .cookie)" -o $1.txt
