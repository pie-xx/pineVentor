///<cat>System</cat>
SystemBuilder = function(pstr){
	var that = new NonDivBuilder(pstr);
	that.base = "System";
	that.img = XML.tag2val("img",pstr);

	that.setImage = function(url){
		document.body.style.backgroundImage = "url("+url+")";
		document.body.style.backgroundRepeat = "no-repeat";
		document.body.style.backgroundSize="100% 100%";
	};
	that.toast = function(msg){
		uug.toast(msg);
	};

	that.share = function( datauri, extravalue ){
		uug.intentSend( 'android.intent.action.SEND', 'text/plain', datauri, 'android.intent.extra.TEXT', extravalue);
	};
	that.show = function( datauri ){
		uug.intentSend( 'android.intent.action.VIEW', '', datauri, '', '');
	};

	that.getIcon = function(icno){
		return uug.getIcon(icno);
	};
	that.run = function(app){
		var appbody = uug.getItem("#App/"+app);
		uug.setItem("#DesignComps", appbody);
		uug.setItem("#appname","#App/"+app);
		uug.go("/Appli/start.html");
	}

	that.setOnKeyVDown = function( fname ){
		uug.setOnKeyVDown(fname);
	};
	that.setOnKeyVUp = function( fname ){
		uug.setOnKeyVUp(fname);
	};
	that.setOnKeySearch = function( fname ){
		uug.setOnKeySearch(fname);
	};

	that.getOrientation = function(){
		return uug.getOrientation();
	}
	that.setOrientation = function(dir){
		uug.setOrientation(dir);
	}
	
	return that;
}
