///<cat>System</cat>
DBBuilder = function(pstr){
	var that = new NonDivBuilder(pstr);
	that.base = "DB";
	that.methods.load(pstr);

	that.get = function(key){
		return uug.getItem(that.regulizeKey(key));
	};
	that.set = function(key, value){
		uug.setItem(that.regulizeKey(key), value);
	};
	that.list = function(){
		return uug.listItems("title like '"+AppName+"/%'").replace("<name>"+AppName+"/", "<name>");
	};
	that.query = function(str){
		return uug.listItems("title like '"+AppName+"/%' and body like '%"+str+"%'").replace("<name>"+AppName+"/", "<name>");
	};
	that.remove = function(key){
		return uug.removeItem(that.regulizeKey(key));
	};

	that.regulizeKey = function(key){
		if( key.charAt(0)!="/" ){
			key = AppName+"/"+key;
		}else{
			key = key.substring(1);
		}
		return key;
	};
	return that;
}
