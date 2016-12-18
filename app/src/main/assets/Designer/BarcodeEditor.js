///<cat>Sensor</cat>
BarcodeEditor = function(pstr){
	var that = new CompNonDiv(pstr);
	that.base = "Barcode";
	that.methods.load(pstr);

	that.methods.defadd("","init","","","初期化",true);
	that.methods.defadd("","onSuccess","val","","読み出し成功",true);

	this.clone = function() {
		var newId = CBoardDiv.getUniqId(that.base);
		var comp = new BarcodeEditor("<id>"+newId+"</id>");
		comp.methods = that.methods.clone();
		return comp;
	};

	return that;
}
