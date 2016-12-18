///<cat>Sensor</cat>
CameraEditor = function(pstr){
	var that = new CompNonDiv(pstr);
	that.base = "Camera";
	that.methods.load(pstr);

	that.methods.defadd("","init","","","初期化",true);
	that.methods.defadd("","onSuccess","url","","撮影成功",true);

	return that;
}
