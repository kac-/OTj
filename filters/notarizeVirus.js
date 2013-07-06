type = 'com.kactech.otj.MSG.NotarizeTransactions';
mask = 1;
priority = 0;

filter = function(req, client) {
	var tx = req.getAccountLedger().getTransactions().get(0);
	var balance = tx.getItems().get(tx.getItems().size()-1);
	var v = balance.getAmount().intValue();
	println("ACTUAL BALANCE "+v);
	balance.setAmount(v+1);
	println("VIRUS BALANCE "+balance.getAmount());
	return req;
}

if (typeof toFilter != 'undefined')
	filtered = filter(toFilter, client);
