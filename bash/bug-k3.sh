#!/bin/bash
bash/cleanotserver.sh

OTJ="otj --server Tra --mypurse silver -d client"
OT1="opentxs --server Tra --mynym DY --mypurse silver --myacct silver"
OT2="opentxs --server Tra --mynym Hp --mypurse silver --myacct Bob"

$OT1 refresh

$OT1 transfer --hisacct Bob --args 'amount 5 memo " " '
$OT1 transfer --hisacct Bob --args 'amount 5 memo " " '

$OT2 refresh
$OT2 acceptall
$OT2 transfer --hisacct silver --args 'amount 10 memo " " '

$OT1 refresh
$OT1 acceptall
$OT2 refresh
$OT2 acceptall
