var CBoardCtl = function(div){
	var that = this;
	this.div = div;

	this.Members= [];
	this.offFocusTimer = null;

	this.assign = function(comp){
		that.Members.push(comp);
	};

	this.offFocus= function(){
		if( that.offFocusTimer ){
			clearTimeout(that.offFocusTimer);
		}
		that.offFocusTimer=null;
		for( var n=0; n < that.Members.length; ++n ){
			that.Members[n].focus( false );
		}
	};

	this.setOffFocusTimer = function(){
		if( that.offFocusTimer ){
			clearTimeout(that.offFocusTimer);
		}
		that.offFocusTimer = setTimeout(
								function(){
									that.offFocus();
								},10000);
	};
	
	this.getMemberNum= function(){
		return that.Members.length;
	};

	this.getComp= function( n ){
		return that.Members[ n ];
	};
	this.getCompById = function( id ){
		for( var n = 0; n < that.Members.length; ++n ){
			if(that.getComp(n).id==id){
				return that.getComp(n);
			}
		}
		return null;
	};

	this.isUniqId = function(id){
		for( var n=0; n < that.Members.length; ++n ){
			if( that.Members[n].id == id )
				return false;
		}
		return true;
	};

	this.getUniqId = function(comptype) {
		var charz = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
		for( var n=0; n < charz.length; ++n ){
			var testid = "c"+comptype+charz.substr(n,1);
			if( that.isUniqId(testid) )
				return testid;
		}
		return "";
	}

	this.onAddComp = function(ctype){
		var newId = that.getUniqId(ctype);
		newId = prompt("新しいcomponent名を入力してください",newId);
		if( newId != null ){
			if( !that.isUniqId(newId) ){
				if( newId.charAt(0)=='c'){
					newId = newId.substr(1);
				}
				newId = that.getUniqId(newId);
			}
			var createStr = XML.val2tag("id", newId)+XML.val2tag("base", ctype)+XML.val2tag("title", newId);
			var ce = that.createComp( createStr );
			that.assign(ce);
			if(ce.div){
				DBoard.appendChild( ce.div );
				ce.div.addEventListener("touchstart", ce.onTouch, true);
				ce.div.addEventListener("touchmove", ce.onMove, true);
			}else
			if(ce.btn){
				NonDivBoard.appendChild( ce.btn );
				ce.btn.addEventListener("click", ce.onClick, false);
			}
			that.save();
			that.offFocus();
			ce.focus( true );
			CompWnd.style.display="none";
		}
	};

	this.selectCompType = function(){
		CompWnd.style.display = "block";
		that.offFocus();
	}

	this.getCompNum = function(delid){
		for( var n=0; n < that.Members.length; ++n ){
			if( delid==that.Members[n].id ){
				return n;
			}
		}
		return -1;
	};

	this.onDelComp = function(delid){
		var fn = that.getCompNum(delid);
		if( fn != -1 ){
			if( that.Members[fn].div ) {
				that.Members[fn].div.parentNode.removeChild(that.Members[fn].div);
			}
			if( that.Members[fn].btn ) {
				that.Members[fn].btn.parentNode.removeChild(that.Members[fn].btn);
			}
			that.Members.splice(fn, 1);
			that.save();
			return;
		}
	};

	this.save = function(){
		var savestr = "<comps>";
		for( var n=0; n < that.Members.length; ++n ){
			savestr = savestr + (that.Members[n].toString());
		}
		savestr = savestr + "</comps>";
		uug.setItem(SaveDB,savestr);
	};

	this.load = function(){
		that.Members= [];
		var sstr = uug.getItem(SaveDB);
		var compstrs = sstr.split("<comp>");
		var sysexist = false;
		for( var n=1; n < compstrs.length; ++n ){
			var ce = that.createComp(compstrs[n]);
			that.assign( ce );
			if( ce.base == "System" ){
				sysexist = true;
			}
		}
		if( !sysexist ){
			that.assign( new SystemEditor("<id>cSys</id>")  );
		}

	//	Comp.load( that.getCompXml(), "L1", "id", "id" );
	};
	
	this.createComp = function(str){
		var base = XML.tag2val("base",str);
		return eval('new '+base+'Editor(str)');
	};
	
	this.getCompXml = function(){
		var xml = "<comps>";
		for( var n=0; n < that.Members.length; ++n ){
			var baselist = "";
			var funcs = RefComps[ that.Members[n].base ];
			if( funcs ){
				baselist = funcs; 
			}
			xml = xml + "<L1>"+XML.val2tag("id",that.Members[n].id)+
					that.Members[n].methods.getXMLlist()+baselist+
			"</L1>";
		}
		return xml +"</comps>";
	};
	this.getCompXml2 = function(){
		var xml = "<comps>";
		for( var n=0; n < that.Members.length; ++n ){
			xml = xml + "<L1>"+XML.val2tag("id",that.Members[n].id)+
					that.Members[n].methods.getXMLlist()+
			"</L1>";
		}
		return xml +"</comps>";
	};
	
}
