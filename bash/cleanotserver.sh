#!/bin/bash
killall -q otserver
./bash/cleansample.sh
otserver 2>/dev/null &
sleep 1
