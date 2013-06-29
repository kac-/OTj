*otj* app- transfer tool with command-line syntax compatible to [opentxs](https://github.com/FellowTraveler/Open-Transactions/wiki/opentxs)
========

#### supported commands: ####

 * `transfer --hisacct ACCOUNT_ID --args "amount 100 memo \" \""` send 100 to ACCOUNT_ID
 * `acceptall` processInbox 
 * `balance` print account balance
 * `procnym` processNymbox
 * `reload` reload trans# from our server nymfile
 
#### supported options: ####
 * `--server` select server by id or name, *Transactions.com*(localhost) and *vancouver btc* available
 * `--mypurse` specify asset type to use, can be `--mypurse silver` or `--mypurse d2Af13...`
 * `--hisacct` specify recipient asset account id 
 * `--args` args for `transfer` command 
 * `--clean` refresh otj by deleting `./client` folder
 * `--new` create fresh asset account 
 * `--dir` specify otj client directory, default is `./client`
 
You can see how to use it looking at [bash/](bash/) scripts
e.g. [transfer0.sh](bash/transfer0.sh) receives 100 from *opentxs*, sends him back *10* to check balances finally

If you see `Error: Could not find or load main class com.kactech.otj.examples.App_otj` while running bin/otj.sh you first have to do `mvn compile` because *otj* makes use of unpacked classes from `./target` directory

