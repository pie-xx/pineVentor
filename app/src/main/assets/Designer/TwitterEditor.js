///<cat>Comminucation</cat>
TwitterEditor = function(pstr){
	var that = new CompNonDiv(pstr);
	that.base = "Twitter";
	that.methods.load(pstr);
	that.methods.defadd("","init","","","初期化",true);
	that.methods.defadd("","onGetNew","twpacs","alert(twpacs.length+'tweets.');","コールバック",true);
	that.methods.defadd("","onGetFwd","twpacs","alert(twpacs.length+'tweets.');","コールバック",true);
	that.methods.defadd("","onGetBwd","twpacs","alert(twpacs.length+'tweets.');","コールバック",true);
	that.methods.defadd("","onGetMore","twpacs","alert(twpacs.length+'tweets.');","コールバック",true);
	that.methods.defadd("","onGetSS","twpacs","alert(twpacs.length+'tweets.');","コールバック",true);
	that.methods.defadd("","onGetLM","twpacs","alert(twpacs.length+'tweets.');","コールバック",true);

	this.clone = function() {
		var newId = CBoardDiv.getUniqId(that.base);
		var comp = new HttpEditor("<id>"+newId+"</id>");
		comp.methods = that.methods.clone();
		return comp;
	}
	
	that.getPropTab = function(){
		return 	'<button onClick="CompEdit.ce.setKey()">consumerKey</button> <br/>'+
				'<button onClick="CompEdit.ce.setSecret()">consumerSecret</button>';
	}

	that.setKey = function(){
		var key = prompt('Input consumerKey.', "");
		if( key != null ){
			uug.storeConsumerKey(key);
		}
	};
	that.setSecret = function(){
		var secr = prompt('Input consumerSecret.', "");
		if( secr != null ){
			uug.storeConsumerSecret(secr);
		}
	};
	that.move = function(posX,posY){};
	return that;
}
