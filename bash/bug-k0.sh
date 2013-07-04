#!/bin/bash
bash/cleanotserver.sh

OTJ="otj --server Tra --mypurse silver -d client"
OT1="opentxs --server Tra --mynym DY --mypurse silver --myacct silver"
OT2="opentxs --server Tra --mynym Hp --mypurse silver --myacct Bob"

$OTJ -x acceptall
OTJ_ACC_ID=$(cat client/account.id)
$OT1 transfer --hisacct $OTJ_ACC_ID --args 'amount 20 memo " " '
$OTJ acceptall
$OT1 acceptall

$OTJ transfer --hisacct Bob --args 'amount 15 memo " " '
$OT2 acceptall

$OT2 transfer --hisacct $OTJ_ACC_ID --args 'amount 5 memo " " '
$OT2 transfer --hisacct $OTJ_ACC_ID --args 'amount 5 memo " " '

$OTJ acceptall
$OT2 acceptall

$OT2 transfer --hisacct $OTJ_ACC_ID --args 'amount 3 memo " " '
$OTJ acceptall
$OT2 acceptall

bash/killotserver.sh
