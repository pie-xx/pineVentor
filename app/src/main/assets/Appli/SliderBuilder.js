///<cat>Web</cat>
SliderBuilder = function(pstr){
	var that = new BoxBuilder(pstr);
	that.div.className = "noborderdiv";
	that.div.innerHTML = "<input type='range' />";
	that.div.style.width = XML.tag2val("width",pstr);
	that.tag = that.div.getElementsByTagName('input')[0];
	that.tag.style.width = that.div.style.width;

	that.getVal = function(){
		return that.tag.value;
	};
	that.setVal = function(val){
		that.tag.value = val;
	};

	that.setMax = function(val){
		that.tag.max = val;
	};
	that.getMax = function(){
		return that.tag.max;
	};

	that.setMin = function(val){
		that.tag.min = val;
	};
	that.getMin = function(){
		return that.tag.min;
	};

	return that;
}
