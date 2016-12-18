///<cat>Web</cat>
InputEditor = function(pstr){
	var that = new CompDiv(pstr);
	that.base = "Input";
	that.methods.load(pstr);
	that.methods.defadd("","init","","","初期化",true);
	that.div.className = "compdiv";
	that.div.style.padding="0px";
	that.div.style.margin="0px";
	that.div.style.width=XML.tag2val("width",pstr);
	that.div.style.height=XML.tag2val("height",pstr);
	that.div.innerHTML = "<div style='border-style:double;margin'>"+that.id+"</div>";
	that.tag = that.div.getElementsByTagName('div')[0];
	that.tag.style.width = that.div.style.width;
	that.tag.style.height = that.div.style.height;

	that.getPropTab = function(){
		var Fix = that.strFix();
		var Top = parseCssValue(that.div.style.top) - TopHeight;
		return '<button onClick="CompEdit.setProp(\'FontSize\')">FontSize</button> <span id="PWfontsize">'+that.div.style.fontSize+'</span><br/>'+
				'<button onClick="CompEdit.ce.setFix()" id="PWfix" style="padding:0">'+Fix+'</button>'+
				'<button onClick="CompEdit.ce.setWidth(\'Width\')">Width</button> <span id="PWwidth">'+that.div.style.width+'</span>'+
				'<button onClick="CompEdit.ce.setHeight(\'Height\')">Height</button> <span id="PWheight">'+that.div.style.height+'</span>'+
				'<button onClick="CompEdit.setProp(\'Top\')">Top</button> <span id="PWtop">'+ Top +'</span>'+
				'<button onClick="CompEdit.setProp(\'Left\')">Left</button> <span id="PWleft">'+that.div.style.left+'</span><br/>';
	}
	that.setWidth = function(){
		var width = that.div.style.width;
		var title = window.prompt("Widthを入力してください", width);
		if( title!=null ){
			PWwidth.innerHTML = title;
			that.div.style.width = title;
			that.tag.style.width = title;
		}
	}
	that.setHeight = function(){
		var width = that.div.style.height;
		var title = window.prompt("Heightを入力してください", width);
		if( title!=null ){
			PWheight.innerHTML = title;
			that.div.style.height = title;
			that.tag.style.height = title;
		}
	}
	
	that.strFix = function(){
		var str = "<img src='file:///android_asset/icon/ic_lock_open_black_24dp.png' height='24'/>";
		if( that.fix != 0 ){
			str = "<img src='file:///android_asset/icon/ic_lock_black_24dp.png' height='24'/>";
		}
		return str;
	}
	
	that.setFix = function(){
		if( that.fix == 0 ){
			that.fix = 1;
		}else{
			that.fix = 0;
		}
		PWfix.innerHTML = that.strFix();
	}
	

	that.clone = function() {
		var newId = CBoardDiv.getUniqId(that.base);
		var comp = new InputEditor("<id>"+newId+"</id>");
		comp.div.style.fontSize = that.div.style.fontSize;
		comp.div.style.top = parseCssInt(that.div.style.top)+10;
		comp.div.style.left = parseCssInt(that.div.style.left)+10;
		comp.div.style.width = parseCssValue(that.div.style.width);
		comp.div.style.height = parseCssValue(that.div.style.height);
		comp.div.style.backgroundImage = that.div.style.backgroundImage;
		comp.methods = that.methods.clone();
		return comp;
	};

	return that;
}
