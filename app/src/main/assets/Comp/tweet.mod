<script>
var $name$ = {
	SinceId:"0",
	MaxId:"0",
	MoreMaxId:"9999999999999999999",

	LastCmd:"",
	LastPrm:"",
	Title: "",

	//// TimeLine
	NewTL: function (){
		this.iniparam("tl","");
		uug.twTimeline( 0, 0, "$name$.BackP()","$name$.Error()");
	},
	
	ForwardTL: function(since){
		uug.twTimeline( since, 0, "$name$.FBack()","$name$.Error()");
	},
	BackwardTL: function(max){
		uug.twTimeline( 0, max, "$name$.BBack()","$name$.Error()");
	},
	MoreTL: function(more){
		uug.twTimeline( 0, more, "$name$.MBack()","$name$.Error()");
	},

	//// Search
	NewSearch: function(queryword){
		this.iniparam("search",queryword);
		uug.twQuerry(queryword, 0, 0, "$name$.Back()","$name$.Error()");
	},
	
	ForwardSearch: function(queryword, since){
		uug.twQuerry(queryword, since, 0, "$name$.FBack()","$name$.Error()");
	},
	BackwardSearch: function(queryword, max){
		uug.twQuerry(queryword, 0, max, "$name$.BBack()","$name$.Error()");
	},
	MoreSearch: function(queryword, more){
		uug.twQuerry(queryword, 0, more, "$name$.MBack()","$name$.Error()");
	},

	//// List
	NewList: function(lid){
		this.iniparam("list",lid);
		uug.twLists( lid, 0, 0, "$name$.Back()","$name$.Error()");
	},
	
	ForwardList: function(lid,since){
		uug.twLists( lid, since, 0, "$name$.FBack()","$name$.Error()");
	},
	BackwardList: function(lid,max){
		uug.twLists( lid, 0, max, "$name$.BBack()","$name$.Error()");
	},
	MoreList: function(lid,more){
		uug.twLists( lid, 0, more, "$name$.MBack()","$name$.Error()");
	},

	//// Mention
	NewMention: function(){
		this.iniparam("mention","");
		uug.twMentions( 0, 0,"$name$.Back()","$name$.Error()");
	},

	ForwardMention: function(since){
		uug.twMentions( since, 0, "$name$.FBack()","$name$.Error()");
	},
	BackwardMention: function(max){
		uug.twMentions( 0, max, "$name$.BBack()","$name$.Error()");
	},
	MoreMention: function(more){
		uug.twMentions( 0, more, "$name$.MBack()","$name$.Error()");
	},

	//// Favorites
	NewFavorites: function(){
		this.iniparam("favorites","");
		uug.twFavorites( 0, 0,"$name$.Back()","$name$.Error()");
	},

	ForwardFavorites: function(since){
		uug.twFavorites( since, 0, "$name$.FBack()","$name$.Error()");
	},
	BackwardFavorites: function(max){
		uug.twFavorites( 0, max, "$name$.BBack()","$name$.Error()");
	},
	MoreFavorites: function(more){
		uug.twFavorites( 0, more, "$name$.MBack()","$name$.Error()");
	},

	//// Usertweets
	NewUsertweets: function(uid){
		this.iniparam("usertweet","");
		uug.twUserTimeline( uid, 0, 0,"$name$.Back()","$name$.Error()");
	},

	ForwardUsertweets: function(uid, since){
		uug.twUserTimeline( uid, since, 0, "$name$.FBack()","$name$.Error()");
	},
	BackwardUsertweets: function(uid, max){
		uug.twUserTimeline( uid, 0, max, "$name$.BBack()","$name$.Error()");
	},
	MoreUsertweets: function(uid, more){
		uug.twUserTimeline( uid, 0, more, "$name$.MBack()","$name$.Error()");
	},

	//// ListMembers
	ListMembers: function(){
		uug.twListLists( "$name$.ListBackP()","$name$.Error()");
	},
	ListBackP: function(){
		var tweets = uug.getCache("#twitter");
		uug.setItem("#listmembers",tweets);		
		$name$.ListBack(tweets);
	},

	//// SavedSearch
	SavedSearch: function(){
		uug.twSavedSearch( "$name$.SavedSearchBack()","$name$.Error()");
	},
	SavedSearchBack: function(){
		var tweets = uug.getCache("#twitter");
		uug.setItem("#savedsearch",tweets);
//		SCH.load(tweets,"tweet","name","name");
	},



/////////////////////////////////////////////////////////////////////////////
	iniparam: function(cmd,prm,title){
		this.SinceId = "0";
		this.MoreMaxId = "0";
		this.MaxId = "9999999999999999999";
		this.LastCmd = cmd;
		this.LastPrm = prm;
		this.Title = title;
	},
	setMaxSince: function(fsttw,lasttw){
		var twtop = this.tag2value("tid",fsttw);
		var twbottom = this.tag2value("tid",lasttw);
		if( this.SinceId < twtop ){
			this.SinceId = twtop;
		}
		if( this.MaxId > twbottom ){
			this.MaxId = twbottom;
		}
		this.MoreMaxId = twbottom;
	},
	tag2value: function ( tag, str ){
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
	},

	Name: function( twpac ){
		return this.tag2value("name",twpac);
	},
	Text: function( twpac ){
		return this.tag2value("text",twpac);
	},
	BackP: function(){
		var tweets = uug.getCache("#twitter");
		var tl = tweets.split(/<tweet>/mg);
		this.setMaxSince(tl[1],tl[tl.length-1]);
		var twobjs = new Array();
		for( var n = 1; n < tl.length; ++n ){
			twobjs.push("<tweet>"+tl[n]);
		}
		this.Back(twobjs);
	},
	Back: function(twpacs){
	},

	Error: function(){
	}
}
</script>