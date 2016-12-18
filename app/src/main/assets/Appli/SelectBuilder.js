///<cat>Web</cat>
SelectBuilder = function(pstr){
	var that = new BoxBuilder(pstr);
	that.div.className = "noborderdiv";
	
	that.addOption = function(title,value){
		var option = document.createElement('option');
		option.innerHTML = title;
		option.value = value;
		that.div.getElementsByTagName("select")[0].appendChild(option);
	};
	that.delOption = function(title){
		var selecter = that.div.getElementsByTagName("select")[0];

		for( var n = 0; n < selecter.options.length; ++n ){
			if( selecter.options[n]==title ){
				selecter.options[n] = null;
				return;
			}
		}
	};

	that.tag = document.createElement('select');
	that.div.appendChild(that.tag);

	var options = pstr.split(/<option>/);
	for( var n=1; n < options.length; ++n ){
		uug.logmsg("#debug",options[n]);
		that.addOption(XML.tag2val("title",options[n]),XML.tag2val("value",options[n]));
	}
	
	that.clear = function(){
		that.tag.innerHTML = "";
	};

	that.getText = function(){
		var inx =  that.tag.selectedIndex;
		return that.tag.options[inx].text;
	}

	that.getIndex = function(){
		var inx =  that.tag.selectedIndex;
		return inx;
	}
	that.setIndex = function(n){
		that.tag.selectedIndex = n;
	}
	that.getSize = function(){
		return that.tag.options.length;
	}
	
	that.getCurTitle = function(){
		var inx =  that.tag.selectedIndex;
		return that.tag.options[inx].text;
	}
	that.getTitle = function(n){
		return that.tag.options[n].text;
	}
	that.setTitle = function(n,title){
		that.tag.options[n].text = title;
	};

	that.getCurValue = function(){
		var inx =  that.tag.selectedIndex;
		return that.tag.options[inx].value;
	}
	that.getValue = function(n){
		return that.tag.options[n].value;
	}
	that.setValue = function(n,value){
		that.tag.options[n].value = value;
	};


	that.indexOf = function(str){
		for(var n=0; n < that.tag.options.length; ++n ){
			if(that.tag.options[n].text==str){
				return n;
			}
		}
		return -1;
	};

	
	return that;
}
