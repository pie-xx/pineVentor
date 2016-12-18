///<cat>Web</cat>
SelectEditor = function(pstr){
	var that = new CompDiv(pstr);
	that.base = "Select";
	that.methods.load(pstr);
	that.methods.defadd("","init","","","初期化",true);
	that.methods.defadd("change","onChange","","","選択変更時",true);

	that.div.className = "noborderdiv";
	that.div.innerHTML = "";

	var select = document.createElement('select');
	that.div.appendChild(select);
	select.disabled = true;

	that.mkOption = function(title,value){
		var option = document.createElement('option');
		option.innerHTML = title;
		option.value = value;
		return option;
	}
	that.addOption = function(title,value){
		var option = that.mkOption(title,value);
		that.div.getElementsByTagName("select")[0].appendChild(option);
	};
	that.delOption = function(title){
		var selecter = that.div.getElementsByTagName("select")[0];

		for( var n = 0; n < selecter.options.length; ++n ){
			if( selecter.options[n].value==title ){
				selecter.options[n] = null;
				document.getElementById(that.id+"_sel").options[n] = null;
				return;
			}
		}
	};

	var options = pstr.split(/<option>/);
	for( var n=1; n < options.length; ++n ){
		that.addOption(XML.tag2val("title",options[n]),XML.tag2val("value",options[n]));
	}

	that.onTouch = function(e){
		that.biasX = e.touches[0].pageX - parseInt(e.target.style.left) ;
		that.biasY = e.touches[0].pageY - parseInt(e.target.style.top) ;
		CBoardDiv.offFocus();
		that.focus( true );
	};

	that.toString = function(){
		var selecter = that.div.getElementsByTagName("select")[0];
		var optionstr = "<select>";
		for( var n=0; n < selecter.options.length; ++n ){
			optionstr = optionstr + "<option>"+XML.val2tag("title",selecter.options[n].text)+
												XML.val2tag("value",selecter.options[n].value)+"</option>";
		}
		optionstr = optionstr + "</select>";
		return "<comp>"+
			XML.val2tag("id",that.id)+
			XML.val2tag("base",that.base)+
			XML.val2tag("title","")+
			XML.val2tag("fontsize",that.div.style.fontSize)+
			XML.val2tag("width",that.div.style.width)+
			XML.val2tag("height",that.div.style.height)+
			XML.val2tag("top",parseCssValue(that.div.style.top)-TopHeight)+
			XML.val2tag("left",that.div.style.left)+
			XML.val2tag("radius",that.div.style.webkitBorderRadius)+
			XML.val2tag("img",that.div.style.backgroundImage)+
			that.methods.toString()+optionstr+
			"</comp>";
	};
	that.getPropTab = function(){
		var Fix = that.strFix();
		var Top = parseCssValue(that.div.style.top) - TopHeight;
		var selecter = that.div.getElementsByTagName("select")[0];
		var selstr = "<select id='"+that.id+"_sel' onChange='CompEdit.ce.chgOptionMenu()'>";
		for( var n = 0; n < selecter.options.length; ++n ){
			selstr = selstr + "<option>" + selecter.options[n].text + "</option>";
		}
		selstr = selstr + "</select>";
		return '<button onClick="CompEdit.ce.delOptionMenu()">del</button>'+
				selstr+
				'<button onClick="CompEdit.ce.addOptionMenu()">add</button> '+
				'<button onClick="CompEdit.setProp(\'FontSize\')">FontSize</button> <span id="PWfontsize">'+that.div.style.fontSize+'</span><br/>'+
				'<button onClick="CompEdit.ce.setFix()" id="PWfix" style="padding:0">'+Fix+'</button>'+
				'<button onClick="CompEdit.setProp(\'Top\')">Top</button> <span id="PWtop">'+Top+'</span>'+
				'<button onClick="CompEdit.setProp(\'Left\')">Left</button> <span id="PWleft">'+that.div.style.left+'</span><br/>';
	}
	this.strFix = function(){
		var str = "<img src='file:///android_asset/icon/ic_lock_open_black_24dp.png' height='24'/>";
		if( that.fix != 0 ){
			str = "<img src='file:///android_asset/icon/ic_lock_black_24dp.png' height='24'/>";
		}
		return str;
	}
	this.setFix = function(){
		if( that.fix == 0 ){
			that.fix = 1;
		}else{
			that.fix = 0;
		}
		PWfix.innerHTML = that.strFix();
	}
	that.addOptionMenu = function(){
		var title = prompt("追加するoption名を入力してください","");
		if(title!=null){
			that.addOption(title,title);
			var option = that.mkOption(title,title);
			document.getElementById(that.id+"_sel").appendChild(option);;
		}
	};
	that.delOptionMenu = function(){
		var selecter = document.getElementById(that.id+"_sel");
		var title = selecter.options[selecter.selectedIndex].value;
		for( var n = 0; n < selecter.options.length; ++n ){
			if( selecter.options[n].value==title ){
				selecter.options[n] = null;
				that.div.getElementsByTagName("select")[0].options[n] = null;
				return;
			}
		}
	};
	that.chgOptionMenu = function(){
		var selecter = document.getElementById(that.id+"_sel");
		that.div.getElementsByTagName("select")[0].selectedIndex = selecter.selectedIndex;
	};

	that.clone = function() {
		var newId = CBoardDiv.getUniqId(that.base);
		var comp = new SelectEditor("<id>"+newId+"</id>");
		comp.div.style.fontSize = parseCssInt(that.div.style.fontSize);
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
