#!/bin/bash

# setup apps
OT="opentxs --mynym FT --myacct Silver" 
# run otj with pomrunner
OTJ=bin/otj.sh
# run otj using app .jar
#OTJ="java -jar "`ls target/OTj-app-*.jar | tail -n -1`

# get initial OT-side balance
OT_START_BALANCE=$($OT balance 2>&1 | grep Balance)

# -x: remove 'client' folder; --mypurse silver: create silver account at init()
$OTJ -x --mypurse silver
# read OTJ new accountID from client/account.id 
OTJ_ID=$(cat client/account.id )
# send 100 to OTJ
$OT  transfer --args "amount 100 memo \" \" " --hisacct $OTJ_ID
# accept 100
$OTJ acceptall
# get accept 100
$OT  acceptall
# send 10 to OT
$OTJ transfer --hisacct Silver --args "amount 10 memo \" \" "
# accept 10
$OT  acceptall
# needs second one, why?
$OT  acceptall
# get accept 10
$OTJ acceptall
# get balances
OT_BALANCE=$($OT balance 2>&1 | grep Balance)
OTJ_BALANCE=$($OTJ balance | grep Balance)
echo "OT Start"$OT_START_BALANCE
echo "OT"$OT_BALANCE
echo "OTJ "$OTJ_BALANCE

