#!/bin/bash
. bash/common.sh
#runCleanServer
runServer

rm -f debug.txt log.txt

OTJ="otj --server Tra --mypurse silver -d client"
OT1="opentxs --server Tra --mynym DY --mypurse silver --myacct silver"
OT2="opentxs --server Tra --mynym Hp --mypurse silver --myacct Bob"

$OTJ -x acceptall &>/dev/null
OTJ_ACC_ID=$(cat client/account.id)

$OT1 refresh &>/dev/null
$OT1 acceptall &>/dev/null
$OT1 transfer --hisacct $OTJ_ACC_ID --args 'amount 20 memo " " ' &>/dev/null

$OTJ acceptall &>/dev/null

$OT1 refresh &>/dev/null
$OT1 acceptall &>/dev/null

$OTJ transfer --hisacct Bob --args 'amount 10 memo " " ' 
#INVALID_ACCOUNT=jnt2ifBF3X7NJT6ntM6oHYM0xkmEB2SEYaKo63QgYpa
#$OTJ transfer --hisacct $INVALID_ACCOUNT --args 'amount 10 memo " " ' 

$OT2 refresh &>/dev/null
$OT2 acceptall &>/dev/null

$OTJ acceptall &>/dev/null

$OTJ balance | grep "Balance"

