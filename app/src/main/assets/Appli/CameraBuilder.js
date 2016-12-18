///<cat>Sensor</cat>
CameraBuilder = function(pstr){
	var that = new NonDivBuilder(pstr);

	uug.setOrientation(uug.getOrientation());

	that.open = function(timer){
		uug.goCamera(timer, that.id+".onSuccess");
	};

	return that;
}
