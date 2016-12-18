///<cat>Web</cat>
SliderEditor = function(pstr){
	var that = new CompDiv(pstr);
	that.base = "Slider";
	that.methods.load(pstr);
	that.methods.defadd("","init","","","初期化",true);
	that.methods.defadd("change","onChange","","","値変更時",true);
	that.div.className = "noborderdiv";
	that.div.innerHTML = "<input type='range' value='"+that.id+"'/>";
	that.div.style.width = XML.tag2val("width",pstr);
	that.div.getElementsByTagName('input')[0].style.width = that.div.style.width;
	that.div.style.backgroundColor = "transparent";
	that.div.style.border = "none";

	that.onTouch = function(e){
		that.biasX = 0;
		that.biasY = 0;
		CBoardDiv.offFocus();
		that.focus( true );
	};

	that.getPropTab = function(){
		var Fix = that.strFix();
		var Top = parseCssValue(that.div.style.top) - TopHeight;
		return '<button onClick="CompEdit.ce.setFix()" id="PWfix" style="padding:0">'+Fix+'</button>'+
				'<button onClick="CompEdit.ce.setWidth()">Width</button> <span id="PWwidth">'+that.div.style.width+'</span>'+
				'<button onClick="CompEdit.setProp(\'Top\')">Top</button> <span id="PWtop">'+ Top +'</span>'+
				'<button onClick="CompEdit.setProp(\'Left\')">Left</button> <span id="PWleft">'+that.div.style.left+'</span><br/>';
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

	that.setWidth = function(){
		var width = that.div.style.width;
		var title = window.prompt("Widthを入力してください", width);
		if( title!=null ){
			PWwidth.innerHTML = title;
			that.div.getElementsByTagName('input')[0].style.width = title;
		}
	}
	
	that.toString = function(){
		return "<comp>"+
			XML.val2tag("id",that.id)+
			XML.val2tag("base",that.base)+
			XML.val2tag("width",parseCssValue(that.div.getElementsByTagName('input')[0].style.width))+
			XML.val2tag("top",parseCssValue(that.div.style.top)-TopHeight)+
			XML.val2tag("left",parseCssValue(that.div.style.left))+
			XML.val2tag("fix",that.fix)+
			that.methods.toString()+
			"</comp>";
	};

	that.clone = function() {
		var newId = CBoardDiv.getUniqId(that.base);
		var comp = new InputEditor("<id>"+newId+"</id>");
		comp.div.style.fontSize = parseCssInt(that.div.style.fontSize);
		comp.div.style.top = parseCssInt(that.div.style.top)+10;
		comp.div.style.left = parseCssInt(that.div.style.left)+10;
		comp.div.style.width = parseCssValue(that.div.style.width);
		comp.div.style.height = parseCssValue(that.div.style.height);
		comp.methods = that.methods.clone();
		return comp;
	};

	return that;
}
