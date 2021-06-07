#!/bin/bash

case "$1" in

"producer" | "consumer")
  pkill -f "$1"
  ;;

"")
  echo "stopping all"
  pkill -f producer
  pkill -f consumer
  ;;

*)
  echo "bad option"
  ;;

esac