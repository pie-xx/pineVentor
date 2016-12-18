///<cat>Web</cat>
VideoEditor = function(pstr){
	var that = new CompDiv(pstr);
	that.src = "";
	that.base = "Video";
	that.methods.load(pstr);
	that.methods.defadd("","init","","","初期化",true);
	that.methods.defadd("ended","onEnded","","","ビデオ終了時",true);

	that.div.className = "compdiv";
	that.div.innerHTML = "<img src='file:///android_asset/icon/ic_local_movies_black_24dp.png'/>";
	that.tag = that.div.getElementsByTagName('img')[0];
	that.div.style.width = XML.tag2val("width",pstr);
	if(that.div.style.width==""){
		that.div.style.width = 320;
	}
	that.tag.style.width = that.div.style.width;
	that.div.style.height = XML.tag2val("height",pstr);
	if(that.div.style.height==""){
		that.div.style.height = 240;
	}
	that.tag.style.height = that.div.style.height;
	that.src = XML.tag2val("src",pstr);

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
				'<button onClick="CompEdit.ce.setHeight()">Height</button> <span id="PWheight">'+that.div.style.height+'</span>'+
				'<button onClick="CompEdit.setProp(\'Top\')">Top</button> <span id="PWtop">'+ Top +'</span>'+
				'<button onClick="CompEdit.setProp(\'Left\')">Left</button> <span id="PWleft">'+that.div.style.left+'</span><br/>'+
				'<button onClick="CompEdit.ce.setSrc()">src</button> '+
				'<button onClick="CompEdit.ce.pickVFile()">pick file</button> <span id="PWimg">'+that.src+'</span><br/><br/><br/><br/>';
	}
	that.setSrc = function(){
		var src = that.src;
		var title = window.prompt("Srcを入力してください", src);
		if( title!=null ){
			that.src = title;
			PWimg.innerHTML = title;
		}
	}
	that.pickVFile = function(){
		uug.pickFile("*/*","CompEdit.ce.onVPicked");
	};
	that.onVPicked = function(url){
		PWimg.innerText = url;
		that.src = url;
		uug.logmsg("#debug",that.src);
	};
	
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
	
	that.toString = function(){
		return "<comp>"+
			XML.val2tag("id",that.id)+
			XML.val2tag("base",that.base)+
			XML.val2tag("width",parseCssValue(that.div.style.width))+
			XML.val2tag("height",parseCssValue(that.div.style.height))+
			XML.val2tag("top",parseCssValue(that.div.style.top)-TopHeight)+
			XML.val2tag("left",parseCssValue(that.div.style.left))+
			XML.val2tag("fix",that.fix)+
			XML.val2tag("src",that.src)+
			that.methods.toString()+
			"</comp>";
	};

	that.clone = function() {
		var newId = CBoardDiv.getUniqId(that.base);
		var comp = new VideoEditor("<id>"+newId+"</id>");
		comp.div.style.fontSize = parseCssInt(that.div.style.fontSize);
		comp.div.style.top = parseCssInt(that.div.style.top)+10;
		comp.div.style.left = parseCssInt(that.div.style.left)+10;
		comp.div.style.width = parseCssValue(that.div.style.width);
		comp.div.style.height = parseCssValue(that.div.style.height);
		comp.src = that.src;
		comp.methods = that.methods.clone();
		return comp;
	};


	return that;
}
