///<cat>System</cat>
DBEditor = function(pstr){
	var that = new CompNonDiv(pstr);
	that.base = "DB";
	that.methods.load(pstr);
	that.methods.defadd("","init","","","初期化",true);

	return that;
}
