///<cat>Web</cat>
InputBuilder = function(pstr){
	var that = new BoxBuilder(pstr);
	that.div.className = "noborderdiv";
	that.div.innerHTML = "<textarea/>";
	that.div.style.width=XML.tag2val("width",pstr);
	that.div.style.height=XML.tag2val("height",pstr);
	if(that.div.style.height==""){
		that.div.style.height=24;
	}
	that.tag = that.div.getElementsByTagName('textarea')[0];
	that.tag.style.width = that.div.style.width;
	that.tag.style.height = that.div.style.height;

	that.getText = function(){
		return that.tag.value;
	};
	that.setText = function(str){
		that.tag.value = str;
	};

	return that;
}
