#!/bin/bash

# full path to the clewarecontrol utility
# this can be downloaded from http://www.vanheusden.com/clewarecontrol/ (see http://www.cleware.net/linux.html)
CONTROLCMD="sudo /home/pi/ampel/clewarecontrol"

RED=0
YELLOW=0
GREEN=0

for color in $@; do
        if [ "$color" == "R" ]; then
                RED=1
        elif [ "$color" == "Y" ]; then
                YELLOW=1
        elif [ "$color" == "G" ]; then
                GREEN=1
        fi
done

$CONTROLCMD -c 1 -as 0 $RED -as 1 $YELLOW -as 2 $GREEN
