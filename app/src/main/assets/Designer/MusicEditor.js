///<cat>System</cat>
MusicEditor = function(pstr){
	var that = new CompNonDiv(pstr);
	that.base = "Music";
	that.src = XML.tag2val("src",pstr);
	that.methods.load(pstr);
	that.methods.defadd("","init","","","初期化",true);

	that.toString = function(){
		return "<comp>"+
			XML.val2tag("id",that.id)+
			XML.val2tag("base",that.base)+
			XML.val2tag("src",that.src)+
			that.methods.toString()+
			"</comp>";
	};

	that.getPropTab = function(){
		return '<button onClick="CompEdit.ce.setMusic()">music</button>'+
				'<button onClick="CompEdit.ce.pickFile()">pick file</button> <span id="PWimg">'+that.src+'</span><br/><br/>';
	}
	that.setMusic = function(){
		that.setPropVal("Music URLを入力してください","that.src","PWimg.innerHTML");
	}
	that.setPropVal = function(promtstr, propstr, panelstr ){
		var propval = eval(propstr);
		var title = window.prompt(promtstr, propval);
		if( title!=null ){
			eval(propstr+"='"+title+"'");
			eval(panelstr+"='"+title+"'");
		}
	}

	that.pickFile = function(){
		uug.pickFile("*/*","CompEdit.ce.onPicked");
	};
	that.onPicked = function(url){
		PWimg.innerText = url;
		that.src = url;
	};


	return that;
}
