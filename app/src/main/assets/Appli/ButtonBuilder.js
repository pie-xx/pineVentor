///<cat>Web</cat>
ButtonBuilder = function(pstr){
	var that = new BoxBuilder(pstr);
	that.div.className = "buttondiv";

	that.setText = function(text){
		that.div.innerText = text;
	};
	that.setImage = function(url){
		that.div.style.backgroundImage = "url("+url+")";
		that.div.style.backgroundRepeat = "no-repeat";
		that.div.style.backgroundSize="100% 100%";
	};
	that.setCss = function(key,val){
		that.div.style[key] = val;
	};
	return that;
}
