
SERVER_PID=~/.ot/server_data/ot.pid

function checkHome(){
	([ 'A'$OT_HOME'A' != 'AA' ] && [ -e $OT_HOME/sample-data/ot-sample-data ]) || (echo "ERROR: please specify valid OT_HOME variable" && exit 1)
}

function killServer(){
	PID=$(cat $SERVER_PID 2>/dev/null) && kill $PID 2>/dev/null && echo "server killed"
	rm -f $SERVER_PID 
}

function cleanSample(){
	checkHome || exit 1
	killServer
	rm ~/.ot/ -rf 
	mkdir ~/.ot 
	cp -r $OT_HOME/sample-data/ot-sample-data/* ~/.ot/
	echo "samples copied"
}

function runServer(){
	killServer
	otserver &>/dev/null &
	sleep 1
	echo "server running"
}

function runCleanServer(){
	cleanSample || exit 1
	runServer
}
