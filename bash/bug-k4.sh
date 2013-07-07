#!/bin/bash
bash/cleanotserver.sh

FT_SILVER=yQGh0v
BOB_SILVER=O8uUtFN
ISSUER_SILVER=60kCK

OTJ="otj --server Tra --mypurse silver -d client"
OT1="opentxs --server Tra --mynym DY --mypurse silver --myacct $FT_SILVER"
OT2="opentxs --server Tra --mynym Hp --mypurse silver --myacct $BOB_SILVER"
OT3="opentxs --server Tra --mynym DY --mypurse silver --myacct $ISSUER_SILVER"

$OT1 refresh &>/dev/null
$OT1 acceptall &>/dev/null
OT1_START_BALANCE=$($OT1 balance 2>&1 | grep Balance)

$OT2 refresh &>/dev/null
$OT2 acceptall &>/dev/null
OT2_START_BALANCE=$($OT2 balance 2>&1 | grep Balance)

$OT3 refresh &>/dev/null
$OT3 acceptall &>/dev/null
OT3_START_BALANCE=$($OT3 balance 2>&1 | grep Balance)

for i in {1..5} ; do
	$OT1 transfer --hisacct $BOB_SILVER --args 'amount '$i' memo " " ' &>/dev/null
done

for i in {1..5} ; do
	$OT3 transfer --hisacct $BOB_SILVER --args 'amount '$i' memo " " ' &>/dev/null
done

for i in {1..5} ; do
	$OT3 transfer --hisacct $FT_SILVER --args 'amount '$i' memo " " ' &>/dev/null
done

$OT2 refresh &>/dev/null
$OT2 acceptall &>/dev/null

$OT1 refresh &>/dev/null
$OT1 acceptall &>/dev/null

$OT3 refresh &>/dev/null
$OT3 acceptall &>/dev/null

OT1_END_BALANCE=$($OT1 balance 2>&1 | grep Balance)
OT2_END_BALANCE=$($OT2 balance 2>&1 | grep Balance)
OT3_END_BALANCE=$($OT3 balance 2>&1 | grep Balance)

echo "OT1 Start "$OT1_START_BALANCE
echo "OT1 End   "$OT1_END_BALANCE
echo "OT2 Start "$OT2_START_BALANCE
echo "OT2 End   "$OT2_END_BALANCE
echo "OT3 Start "$OT3_START_BALANCE
echo "OT3 End   "$OT3_END_BALANCE

