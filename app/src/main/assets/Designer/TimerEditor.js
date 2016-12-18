///<cat>System</cat>
TimerEditor = function(pstr){
	var that = new CompNonDiv(pstr);
	that.base = "Timer";
	that.methods.load(pstr);
	that.methods.defadd("","init","","","初期化",true);
	that.methods.defadd("","onTimer","","","タイマー処理",true);

	this.clone = function() {
		var newId = CBoardDiv.getUniqId(that.base);
		var comp = new TimerEditor("<id>"+newId+"</id>");
		comp.methods = that.methods.clone();
		return comp;
	}
	return that;
}
