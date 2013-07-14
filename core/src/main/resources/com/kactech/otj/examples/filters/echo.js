type = 'java.lang.Object';
mask = 1;
priority = 0;
filter = function(req, client) {
	println('ECHO_FILTER: ' + req);
	return req;
}
if (typeof toFilter != 'undefined')
	filtered = filter(toFilter, client);