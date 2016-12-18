//

SystemEditor = function(pstr){
	var that = new CompNonDiv(pstr);
	that.base = "System";
	that.methods.load(pstr);

	document.body.style.backgroundRepeat = "no-repeat";
	document.body.style.backgroundPosition="0 "+TopHeight;
	document.body.style.backgroundSize="100% "+window.innerHeight;
	document.body.style.backgroundImage = "url("+XML.tag2val("img",pstr)+")";

	that.methods.defadd("","init","","","",true);
	if( that.id=="cSys" ){
		that.methods.defadd("","onKeyBack","","uug.go('/Designer/start.html');","後退",true);
	}
	that.clone = function() {
		var newId = CBoardDiv.getUniqId(that.base);
		var comp = new SystemEditor("<id>"+newId+"</id>");
		comp.methods = that.methods.clone();
		return comp;
	}
	that.pickFile = function(){
		uug.pickFile("*/*","CompEdit.ce.onPicked");
	};
	that.getPropTab = function(){
		var img = getImgProp(document.body);
		
		return '<button onClick="CompEdit.ce.setProp(\'Img\')">image</button> '+
				'<button onClick="CompEdit.ce.pickFile()">pick file</button> <span id="PWimg">'+img+'</span>';
	}
	that.onPicked = function(url){
		document.body.style.backgroundRepeat = "no-repeat";
		document.body.style.backgroundSize="100% 100%";
		document.body.style.backgroundImage = "url("+url+")";
		PWimg.innerText = url;
	};

	that.toString = function(){
		return "<comp>"+
			XML.val2tag("id",that.id)+
			XML.val2tag("base",that.base)+
			XML.val2tag("img",getImgProp(document.body))+
			that.methods.toString()+
			"</comp>";
	};
	this.setProp = function(pname){
		if( pname=='Img' ){
			that.setPropVal("Img URLを入力してください","that.div.style.backgroundImage","PWimg.innerHTML");
			return;
		}

		alert("BoxEditor.set"+pname);
	}
	this.setPropVal = function(promtstr, propstr, panelstr ){
		var propval = eval(propstr);
		var title = window.prompt(promtstr, propval);
		if( title!=null ){
			eval(propstr+"='"+title+"'");
			eval(panelstr+"='"+title+"'");
		}
	}
	return that;
}

