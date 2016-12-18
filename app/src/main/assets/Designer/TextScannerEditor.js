///<cat>Comminucation</cat>
TextScannerEditor = function(pstr){
	var that = new CompNonDiv(pstr);
	that.base = "TextScanner";
	that.methods.load(pstr);
	that.methods.defadd("","init","","","初期化",true);

	return that;
}
