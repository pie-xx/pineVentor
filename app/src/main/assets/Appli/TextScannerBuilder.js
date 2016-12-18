///<cat>Comminucation</cat>
TextScannerBuilder = function(pstr){
	var that = new NonDivBuilder(pstr);
	that.tid = null;

	that.value = "";
	that.target = "";
	that.items = [];
	that.itx = 0;

	that.open = function(val){
		that.value = val;
		that.reset();
	}
	that.reset = function(){
		that.target = that.value;
		that.items = [];
		that.itx = 0;
	};
	that.pick = function(from,to){
		var sp;
		if(from==""){
			sp = 0;
		}else{
			sp = that.target.indexOf(from);
		}
		if( sp==-1 ){
			return "";
		}

		var tstr=that.target.substring(sp+from.length);
		var ep;
		if(to==""){
			ep = tstr.length;
		}else{
			ep = tstr.indexOf(to);
		}
		if( ep==-1 ){
			return "";
		}
		return tstr.substring(0,ep);
	};

	that.skipTo = function( str ){
		var p = that.target.indexOf(str);
		if( p == -1 ){
			that.target = "";
		}else{
			that.target = that.target.substring( p + str.length );
		}
	};
	that.setItems = function( dlm ){
		that.items = that.target.split(dlm);
		that.itx = 0;
	};
	that.nextItem = function(){
		that.itx = that.itx + 1;
		if( that.itx >= that.items.length ){
			return false;
		}
		that.target = that.items[that.itx];
		return true;
	};
	that.ItemSize = function(){
		return that.items.length;
	};
	that.getItem = function( n ){
		that.itx = n;
		that.target = that.items[that.itx];
		return that.items[that.itx];
	};

	return that;
}
