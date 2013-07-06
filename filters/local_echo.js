type = 'com.kactech.otj.MSG.GetNymboxResp';
mask = 1;
priority = 0;
filter = function(req, client) {
	println('LOCAL_ECHO_FILTER: ' + req);
	return req;
}
if (typeof toFilter != 'undefined')
	filtered = filter(toFilter, client);
