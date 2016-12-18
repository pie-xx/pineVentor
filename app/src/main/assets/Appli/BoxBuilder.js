///<cat>Web</cat>
BoxBuilder = function(pstr){
	var that = this;
	that.isTab = true;
	that.ctype = "div";
	that.methods = new MethodContainer();	//event,id,param,func
	that.methods.load(pstr);

	var nwtws = document.createElement('div');
	nwtws.className = "compdiv";
	that.div = nwtws;

	that.id = XML.tag2val("id",pstr);
	that.div.innerHTML = XML.tag2val("title",pstr);
	that.div.style.fontSize = XML.tag2val("fontsize",pstr);
	that.div.style.top = XML.tag2val("top",pstr);
	that.div.style.left = XML.tag2val("left",pstr);
	that.div.style.width = XML.tag2val("width",pstr);
	that.div.style.height = XML.tag2val("height",pstr);
	that.div.style.backgroundRepeat = "no-repeat";
	that.div.style.backgroundSize="100% 100%";
	that.div.style.backgroundImage = "url("+XML.tag2val("img",pstr)+")";
	
	that.getText = function(){
		return that.div.innerText;
	};
	that.setText = function(text){
		that.div.innerText = text;
	};
	that.getHTML = function(){
		return that.div.innerHTML;
	};
	that.setHTML = function(html){
		that.div.innerHTML = html;
	};
	that.setBackgroundColor = function(color){
		that.div.style.backgroundColor = color;
	};
	that.setImage = function(url){
		that.div.style.backgroundImage = "url("+url+")";
		that.div.style.backgroundRepeat = "no-repeat";
		that.div.style.backgroundSize="100% 100%";
	};
	that.setCss = function(key,val){
		that.div.style[key] = val;
	};
	that.getCss = function(key){
		return that.div.style[key];
	};
	that.addTail = function(box){
		var div = box.div;
		if( div==undefined ){
			div = box;
		}
		div.style.top = 0;
		div.style.left = 0;
		div.style.position = "static";
		that.div.appendChild(div);
	};
	that.addHead = function(box) {
		var div = box.div;
		if( div==undefined ){
			div = box;
		}
		div.style.top = 0;
		div.style.left = 0;
		div.style.position = "static";

		var topdivs = that.div.getElementsByTagName('div');
		if( topdivs.length == 0 ){
			that.div.appendChild(div);
			return;
		}

		that.div.insertBefore(div,topdivs[0]);
	};
	
	return that;
}
