///<cat>System</cat>
MusicBuilder = function(pstr){
	var that = new NonDivBuilder(pstr);
	that.base = "Music";
	that.methods.load(pstr);
	that.src = XML.tag2val("src",pstr);
	if( that.src != "" ){
		uug.setMedia(that.src);
	}

	that.set = function(src){
		uug.stopMedia();
		uug.setMedia(src);
	};
	that.play = function(){
		uug.playMedia();
	};
	that.stop = function(){
	//	uug.stopMedia();
		uug.pauseMedia();
		uug.seekMedia(0);
	};
	that.pause = function(){
		uug.pauseMedia();
	};
	that.seek = function(msec){
		uug.seekMedia(msec);
	};
	that.getDuration = function(){
		return uug.getMediaDuration();
	};
	that.getCurrentPosition = function(){
		return uug.getMediaPosition();
	};

	return that;
}
