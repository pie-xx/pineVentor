///<cat>Web</cat>
CanvasBuilder = function(pstr){
	var that = new BoxBuilder(pstr);
	that.div.className = "noborderdiv";
	that.div.innerHTML = "<canvas />";
	that.div.style.width = XML.tag2val("width",pstr);
	that.div.getElementsByTagName('canvas')[0].style.width = that.div.style.width;
	that.div.style.height = XML.tag2val("height",pstr);
	that.div.getElementsByTagName('canvas')[0].style.height = that.div.style.height;
	that.tag = that.div.getElementsByTagName('canvas')[0];
	that.context = that.tag.getContext('2d');

	that.drawImage = function(image, sx, sy, sw, sh, dx, dy, dw, dh){
		that.context.drawImage(image, sx, sy, sw, sh, dx, dy, dw, dh);
	};

	return that;
}
