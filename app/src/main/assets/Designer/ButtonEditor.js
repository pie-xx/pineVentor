///<cat>Web</cat>
ButtonEditor = function(pstr){
	var that = new CompDiv(pstr);
	that.base = "Button";
	that.methods.load(pstr);
	that.methods.defadd("","init","","","初期化",true);
	that.methods.defadd("click","onClick","te","","タッチ処理",true);
	that.div.className = "buttondiv";

	that.clone = function() {
		var newId = CBoardDiv.getUniqId(that.base);
		var comp = new ButtonEditor("<id>"+newId+"</id>");
		comp.div.innerHTML = splitBra(that.div.innerHTML)+"("+newId+")";
		comp.div.style.top = parseCssInt(that.div.style.top)+10;
		comp.div.style.left = parseCssInt(that.div.style.left)+10;
		comp.div.style.width = parseCssValue(that.div.style.width);
		comp.div.style.height = parseCssValue(that.div.style.height);
		comp.div.style.backgroundImage = that.div.style.backgroundImage;
		comp.methods = that.methods.clone();
		return comp;
	}

	return that;
}
