#!/bin/bash
bash/cleanotserver.sh

FT_SILVER=yQGh0v
BOB_SILVER=O8uUtFN

OTJ="otj --server Tra --mypurse silver -d client"
OT1="opentxs --server Tra --mynym DY --mypurse silver --myacct $FT_SILVER"
OT2="opentxs --server Tra --mynym Hp --mypurse silver --myacct $BOB_SILVER"

$OT1 refresh
$OT1 acceptall
OT1_START_BALANCE=$($OT1 balance 2>&1 | grep Balance)

$OT2 refresh
$OT2 acceptall
OT2_START_BALANCE=$($OT2 balance 2>&1 | grep Balance)

$OT1 transfer --hisacct $BOB_SILVER --args 'amount 20 memo " " '

$OT2 refresh
$OT2 acceptall
$OT2 transfer --hisacct $FT_SILVER --args 'amount 5 memo " " '
$OT2 transfer --hisacct $FT_SILVER --args 'amount 6 memo "c" '

$OT1 refresh
$OT1 acceptall
$OT1 transfer --hisacct $BOB_SILVER --args 'amount 10 memo " " '

$OT2 refresh
$OT2 acceptall
$OT2 transfer --hisacct $FT_SILVER --args 'amount 4 memo " " '

$OT1 refresh
$OT1 acceptall

$OT2 refresh
$OT2 acceptall

OT1_END_BALANCE=$($OT1 balance 2>&1 | grep Balance)
OT2_END_BALANCE=$($OT2 balance 2>&1 | grep Balance)

echo "OT1 Start "$OT1_START_BALANCE
echo "OT1 End   "$OT1_END_BALANCE
echo "OT2 Start "$OT2_START_BALANCE
echo "OT2 End   "$OT2_END_BALANCE

exit
# ignore 


$OT1 refresh
$OT1 acceptall
$OT1 transfer --hisacct $BOB_SILVER --args 'amount 3 memo " " '

$OT2 refresh
$OT2 acceptall
