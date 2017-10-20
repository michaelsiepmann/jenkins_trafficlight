#!/bin/sh

killall ampel.pl

cd /home/pi/ampel

nohup ./ampel.pl > /dev/null &
