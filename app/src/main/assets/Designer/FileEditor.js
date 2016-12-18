///<cat>System</cat>
FileEditor = function(pstr){
	var that = new CompNonDiv(pstr);
	that.base = "File";
	that.methods.load(pstr);
	that.methods.defadd("","init","","","初期化",true);
	that.methods.defadd("","onPicked","url","","ファイル選択完了",true);

	return that;
}
