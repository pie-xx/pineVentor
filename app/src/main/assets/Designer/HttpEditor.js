///<cat>Comminucation</cat>
HttpEditor = function(pstr){
	var that = new CompNonDiv(pstr);
	that.base = "Http";
	that.methods.load(pstr);
	that.methods.defadd("","init","","","初期化",true);
	that.methods.defadd("","onSuccess","val","","通信成功",true);
	that.methods.defadd("","onOAuthtoken","token","","OAuth成功",true);

	this.clone = function() {
		var newId = CBoardDiv.getUniqId(that.base);
		var comp = new HttpEditor("<id>"+newId+"</id>");
		comp.methods = that.methods.clone();
		return comp;
	}

	return that;
}
