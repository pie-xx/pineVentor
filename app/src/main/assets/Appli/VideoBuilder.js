///<cat>Web</cat>
VideoBuilder = function(pstr){
	var that = new BoxBuilder(pstr);
	that.div.className = "compdiv";
	that.div.innerHTML = "<video />";
	that.tag = that.div.getElementsByTagName("video")[0];
	that.div.style.width = XML.tag2val("width",pstr);
	that.tag.style.width = that.div.style.width;
	that.div.style.height = XML.tag2val("height",pstr);
	that.tag.style.height = that.div.style.height;
	that.tag.poster='file:///android_asset/icon/ic_local_movies_black_24dp.png';
	that.tag.autoplay = false;
	that.tag.controls = false;
	that.tag.loop = false;
	that.tag.src=XML.tag2val("src",pstr);
	if(that.tag.src!=""){
		that.tag.poster="";
	}

	that.getTag = function(){
		return that.tag;
	};
	that.setSrc = function(src){
		that.tag.src = src;
		that.tag.poster="";
	};
	that.play = function(){
		that.tag.play();
	};
	that.pause = function(){
		that.tag.pause();
	};
	that.setLoop = function(of){
		that.tag.loop = of;
	};
	that.setControls = function(of){
		that.tag.controls = of;
	};
	that.setAutoplay = function(of){
		that.tag.autoplay = of;
	};

	return that;
}
