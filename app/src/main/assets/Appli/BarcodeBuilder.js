///<cat>Sensor</cat>
BarcodeBuilder = function(pstr){
	var that = new NonDivBuilder(pstr);
	
	uug.setOrientation(uug.getOrientation());

	that.open = function(){
		uug.goBarcode( that.id+".onSuccess");
	};
	that.open2 = function (){
		uug.intentCall('com.google.zxing.client.android.SCAN', 
				"", "", "", 'SCAN_RESULT', that.id+'.scanback');
	};
	that.scanback = function (extra){
		setTimeout(that.id+".scanback2('"+extra+"')",500);
	};
	that.scanback2 = function (extra){
		that.onSuccess( uug.getCache(extra) );
	};
	that.onSuccess = function(val){
		alert(val);
	};

	that.encode = function(str) {
		return uug.getQRcode(str);
	};

	return that;
}
