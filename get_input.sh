#!/bin/bash

year=$1
day=$2

if [[ -z "$year" || -z "$day" ]]; then
	year=$(date +%Y)
	day=$(date +%d)
fi

curl -o inputs/Day"${day}".txt https://adventofcode.com/"${year}"/day/"${day##0}"/input --cookie "session=${AOC_SESSION_COOKIE}"
