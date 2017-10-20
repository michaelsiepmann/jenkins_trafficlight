#!/usr/bin/perl

use strict;
use warnings;

my $STATE_UNKNOWN = -1;
my $STATE_RED = 0;
my $STATE_YELLOW = 1;
my $STATE_BLUE = 2;

my $URL = 'url'; # Add your Url to your Jenkins here!

my $state = "R Y G";
my $worstState;
my $lastState = -2;

while (1 == 1) {
   $worstState = $STATE_UNKNOWN;
   open FILE, "curl " . $URL . " |" or die $!;
   while(<FILE>) {
      while(/<color>([^<]*)<\/color>/ig) {
         if($1 =~ /yellow/) {
            if($worstState != $STATE_RED) {
               $worstState = $STATE_YELLOW;
            }
         }
         elsif($1 =~ /blue/) {
            if($worstState != $STATE_RED && $worstState != $STATE_YELLOW) {
               $worstState = $STATE_BLUE;
            }
         }
         elsif($1 =~ /disabled/ || $1 =~ /canceled/ || $1 =~ /aborted/) { # do nothing
         }
         else {
            $worstState = $STATE_RED;
         }
      }
   }
   close FILE;

   if ($lastState != $worstState) {
   if ($worstState == $STATE_RED) {
      system("./usbswitchcmd.sh R");
   }
   elsif ($worstState == $STATE_YELLOW) {
      system("./usbswitchcmd.sh Y");
   }
   elsif ($worstState == $STATE_BLUE) {
      system("./usbswitchcmd.sh G");
   }
   else {
      system("./usbswitchcmd.sh R Y G");
   }
   $lastState = $worstState;
   }
   sleep 5;
}
