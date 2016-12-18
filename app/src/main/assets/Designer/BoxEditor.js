///<cat>Web</cat>
BoxEditor = function(pstr){
	var that = new CompDiv(pstr);
	that.base = "Box";
	that.methods.load(pstr);
	that.methods.defadd("","init","","","初期化",true);
	that.methods.defadd("touchstart","onTouch","te","","タッチ処理",true);

	return that;
}
