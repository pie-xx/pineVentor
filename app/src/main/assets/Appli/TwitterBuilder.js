///<cat>Comminucation</cat>
TwitterBuilder = function(pstr){
	var that = new NonDivBuilder(pstr);

	that.SinceId="0";
	that.MaxId="0";
	that.MoreMaxId="9999999999999999999";

	that.LastCmd="";
	that.LastPrm="";
	that.Title= "";

	//// TimeLine
	that.getTL= function(){
		that.iniparam("tl","");
		uug.twTimeline( 0, 0, that.id+".Back()",that.id+".Error()");
	};
	that.getForwardTL= function (since){
		uug.twTimeline( since, 0, that.id+".FBack()",that.id+".Error()");
	};
	that.getBackwardTL= function (max){
		uug.twTimeline( 0, max, that.id+".BBack()",that.id+".Error()");
	};
	that.getMoreTL= function (more){
		uug.twTimeline( 0, more, that.id+".MBack()",that.id+".Error()");
	};

	//// Search
	that.getSearch= function(queryword){
		that.iniparam("search",queryword);
		uug.twQuerry(queryword, 0, 0, that.id+".Back()",that.id+".Error()");
	};	
	that.getForwardSearch= function (queryword, since){
		uug.twQuerry(queryword, since, 0, that.id+".FBack()",that.id+".Error()");
	};
	that.getBackwardSearch= function (queryword, max){
		uug.twQuerry(queryword, 0, max, that.id+".BBack()",that.id+".Error()");
	};
	that.getMoreSearch= function (queryword, more){
		uug.twQuerry(queryword, 0, more, that.id+".MBack()",that.id+".Error()");
	};

	//// List
	that.getList= function(lid){
		that.iniparam("list",lid);
		uug.twLists( lid, 0, 0, that.id+".Back()",that.id+".Error()");
	};	
	that.getForwardList= function (lid,since){
		uug.twLists( lid, since, 0, that.id+".FBack()",that.id+".Error()");
	};
	that.getBackwardList= function (lid,max){
		uug.twLists( lid, 0, max, that.id+".BBack()",that.id+".Error()");
	};
	that.getMoreList= function (lid,more){
		uug.twLists( lid, 0, more, that.id+".MBack()",that.id+".Error()");
	};

	//// Mention
	that.getMention= function(){
		that.iniparam("mention","");
		uug.twMentions( 0, 0,that.id+".Back()",that.id+".Error()");
	};
	that.getForwardMention= function (since){
		uug.twMentions( since, 0, that.id+".FBack()",that.id+".Error()");
	};
	that.getBackwardMention= function (max){
		uug.twMentions( 0, max, that.id+".BBack()",that.id+".Error()");
	};
	that.getMoreMention= function (more){
		uug.twMentions( 0, more, that.id+".MBack()",that.id+".Error()");
	};

	//// Favorites
	that.getFavorites= function(){
		that.iniparam("favorites","");
		uug.twFavorites( 0, 0,that.id+".Back()",that.id+".Error()");
	};
	that.getForwardFavorites= function (since){
		uug.twFavorites( since, 0, that.id+".FBack()",that.id+".Error()");
	};
	that.getBackwardFavorites= function (max){
		uug.twFavorites( 0, max, that.id+".BBack()",that.id+".Error()");
	};
	that.getMoreFavorites= function (more){
		uug.twFavorites( 0, more, that.id+".MBack()",that.id+".Error()");
	};

	//// Usertweets
	that.getUsertweets= function(uid){
		that.iniparam("usertweet",uid);
		uug.twUserTimeline( uid, 0, 0,that.id+".Back()",that.id+".Error()");
	};
	that.getForwardUsertweets= function (uid, since){
		uug.twUserTimeline( uid, since, 0, that.id+".FBack()",that.id+".Error()");
	};
	that.getBackwardUsertweets= function(uid, max){
		uug.twUserTimeline( uid, 0, max, that.id+".BBack()",that.id+".Error()");
	};
	that.getMoreUsertweets= function (uid, more){
		uug.twUserTimeline( uid, 0, more, that.id+".MBack()",that.id+".Error()");
	};

	//// ListMembers
	that.getListMembers= function(){
		uug.twListLists( that.id+".ListBackP()",that.id+".Error()");
	};
	that.ListBackP= function (){
		var tweets = uug.getCache("#twitter");
		uug.setItem("#listmembers",tweets);		
		that.onGetLM(tweets);
	};
	that.onGetLM = function (tweets){
	}

	//// SavedSearch
	that.getSavedSearch= function(){
		uug.twSavedSearch( that.id+".SavedSearchBack()",that.id+".Error()");
	};
	that.SavedSearchBack= function (){
		var tweets = uug.getCache("#twitter");
		uug.setItem("#savedsearch",tweets);
		that.onGetSS(tweets);
	};
	that.onGetSS = function (tweets){
	}

/////////////////////////////////////////////////////////////////////////////
	that.getForward = function(){
		if(that.LastCmd=="tl"){
			that.getForwardTL(that.SinceId);
			return;
		}
		if(that.LastCmd=="search"){
			that.getForwardSearch(that.LastPrm,that.SinceId);
			return;
		}
		if(that.LastCmd=="list"){
			that.getForwardList(that.LastPrm,that.SinceId);
			return;
		}
		if(that.LastCmd=="mention"){
			that.getForwardMention(that.LastPrm,that.SinceId);
			return;
		}
		if(that.LastCmd=="favorites"){
			that.getForwardFavorites(that.SinceId);
			return;
		}
		if(that.LastCmd=="usertweet"){
			that.getForwardUsertweets(that.LastPrm,that.SinceId);
			return;
		}
	}

	that.getBackward = function(){
		if(that.LastCmd=="tl"){
			that.getBackwardTL(that.MaxId);
			return;
		}
		if(that.LastCmd=="search"){
			that.getBackwardSearch(that.LastPrm,that.MaxId);
			return;
		}
		if(that.LastCmd=="list"){
			that.getBackwardList(that.LastPrm,that.MaxId);
			return;
		}
		if(that.LastCmd=="mention"){
			that.getBackwardMention(that.LastPrm,that.MaxId);
			return;
		}
		if(that.LastCmd=="favorites"){
			that.getBackwardFavorites(that.MaxId);
			return;
		}
		if(that.LastCmd=="usertweet"){
			that.getBackwardUsertweets(that.LastPrm,that.MaxId);
			return;
		}
	}

	that.getMore = function(){
		if(that.LastCmd=="tl"){
			that.getMoreTL(that.MoreMaxId);
			return;
		}
		if(that.LastCmd=="search"){
			that.getMoreSearch(that.LastPrm,that.MoreMaxId);
			return;
		}
		if(that.LastCmd=="list"){
			that.getMoreList(that.LastPrm,that.MoreMaxId);
			return;
		}
		if(that.LastCmd=="mention"){
			that.getMoreMention(that.LastPrm,that.MoreMaxId);
			return;
		}
		if(that.LastCmd=="favorites"){
			that.getMoreFavorites(that.MoreMaxId);
			return;
		}
		if(that.LastCmd=="usertweet"){
			that.getMoreUsertweets(that.LastPrm,that.MoreMaxId);
			return;
		}
	}

/////////////////////////////////////////////////////////////////////////////
	that.iniparam= function(cmd,prm){
		that.SinceId = "0";
		that.MoreMaxId = "0";
		that.MaxId = "9999999999999999999";
		that.LastCmd = cmd;
		that.LastPrm = prm;
		that.Title = "";
	};
	that.setMaxSince= function(fsttw,lasttw){
		var twtop = that.tag2value("tid",fsttw);
		var twbottom = that.tag2value("tid",lasttw);
		if( that.SinceId < twtop ){
			that.SinceId = twtop;
		}
		if( that.MaxId > twbottom ){
			that.MaxId = twbottom;
		}
		that.MoreMaxId = twbottom;
	};
	that.tag2value= function ( tag, str ){
		if( str==undefined )
			return "";
		var tags = str.split("<"+tag+">");
		if( tags.length < 2 ){
			return "";
		}
		var its = tags[1].split("</"+tag+">");
		if(its.length < 2 ){
			return "";
		}
		return its[0];
	};

	// Callbacks
	that.Back= function (){
		var tweets = uug.getCache("#twitter");
		that.onGetNew(tweets);
		var tl = tweets.split(/<tweet>/mg,2);
		that.setMaxSince(tl[1],tl[tl.length-1]);
		/*
		var twobjs = new Array();
		for( var n = 1; n < tl.length; ++n ){
			twobjs.push("<tweet>"+tl[n]);
		}
		that.onGetNew(twobjs);
		*/
	};
	that.gotNew= function (twpacs){
		alert(twpacs.length+"tweets.");
	};

	that.FBack= function (){
		var tweets = uug.getCache("#twitter");
		that.onGetFwd(tweets);
		var tl = tweets.split(/<tweet>/mg,2);
		that.setMaxSince(tl[1],tl[tl.length-1]);
		/*
		var twobjs = new Array();
		for( var n = 1; n < tl.length; ++n ){
			twobjs.push("<tweet>"+tl[n]);
		}
		that.onGetFwd(twobjs);
		*/
	};
	that.gotFwd= function (twpacs){
		alert(twpacs.length+"tweets.");
	};

	that.BBack= function (){
		var tweets = uug.getCache("#twitter");
		that.onGetBwd(tweets);
		var tl = tweets.split(/<tweet>/mg,2);
		that.setMaxSince(tl[1],tl[tl.length-1]);
		/*
		var twobjs = new Array();
		for( var n = 1; n < tl.length; ++n ){
			twobjs.push("<tweet>"+tl[n]);
		}
		that.onGetBwd(twobjs);
		*/
	};
	that.gotBwd= function (twpacs){
		alert(twpacs.length+"tweets.");
	};

	that.MBack= function (){
		var tweets = uug.getCache("#twitter");
		that.onGetMore(tweets);
		var tl = tweets.split(/<tweet>/mg,2);
		that.setMaxSince(tl[1],tl[tl.length-1]);
		/*
		var twobjs = new Array();
		for( var n = 1; n < tl.length; ++n ){
			twobjs.push("<tweet>"+tl[n]);
		}
		that.onGetMore(twobjs);
		*/
	};
	that.onGetMore= function (twpacs){
		alert(twpacs.length+"tweets.");
	};

	that.pacVal = function( tag, twpac ){
		return that.tag2value(tag,twpac);
	};

	that.Error= function (){
	};
	
	return that;
}