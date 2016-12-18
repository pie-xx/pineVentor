///<cat>Comminucation</cat>
HttpBuilder = function(pstr){
	var that = new NonDivBuilder(pstr);
	that.url = "";
	that.header = "";
	that.resheader = "";
	that.enc = "";
	that.value="";

	that.open = function( url ){
		uug.sendHttpContent("GET", url, "", that.header, that.id+".Callback('"+that.id+"')",that.id,that.enc,"");
		that.url=url;
	};
	that.download = function( url ){
		uug.sendHttpContent("GET", url, "", that.header, that.id+".Callback('"+that.id+"')",that.id,that.enc,"down");
		that.url=url;
	};


	that.get = function( url ){
		that.open(url);
	};
	that.post = function( url, entitiy ){
		uug.sendHttpContent("POST", url, entitiy, that.header, that.id+".Callback('"+that.id+"')",that.id,that.enc,"");
		that.url=url;
	};
	that.put = function( url, entitiy ){
		uug.sendHttpContent("PUT", url, entitiy, that.header, that.id+".Callback('"+that.id+"')",that.id,that.enc,"");
		that.url=url;
	};
	that.delete = function( url ){
		uug.sendHttpContent("DELETE", url, "", that.header, that.id+".Callback('"+that.id+"')",that.id,that.enc,"");
		that.url=url;
	};
	that.Callback = function (eid){
		that.value = uug.getCache(eid);
		that.onSuccess( that.value );
	};
	that.getStatus = function(){
		that.resheader = that.pick(that.value, "<status>", "</status>");
		return that.resheader;
	};
	that.getHeader = function(){
		that.resheader = that.pick(that.value, "<header>", "</header>");
		return that.resheader;
	};
	that.getEntity = function(){
		that.entity = that.pick(that.value, "<entity><![CDATA[", "]]></entity>");
		return that.entity;
	};
	that.getJson = function(){
		eval("that.json="+that.getEntity() );
		return that.json;
	};
	
	that.setHeader = function(header){
		that.header = header;
	};
	that.setEncode = function(enc){
		that.enc = enc;
	};

	that.onSuccess = function(val){
	};
	that.pick = function (target,from,to){
		var p;
		p = target.indexOf(from);
		if( p==-1 ){
			return "";
		}
		target=target.substring(p);
		p = target.indexOf(to);
		if( p==-1 ){
			return "";
		}
		return target.substring(from.length,p);
	};

	that.auth = function( authurl, tokenurl, redirecturl, clientid, clientsecret, scope ){
		uug.auth( that.id+".onOAuthtoken", authurl, tokenurl, redirecturl, clientid, clientsecret, scope );
	};
	that.onOAuthtoken = function(token){
		alert(token);
	};
	
	return that;
}
