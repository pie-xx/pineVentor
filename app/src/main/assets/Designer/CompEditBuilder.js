var CompEditBuilder = function(pdiv){
	var that = this;
	this.pdiv = pdiv;
	this.ce = null;

	this.open = function(comp){
		that.ce = comp;
		PWid.innerHTML = comp.id;
		PWbase.innerHTML = comp.base;

		PropTab.innerHTML = comp.getPropTab();
		that.pdiv.style.top = window.innerHeight - 200;
		that.setTab('prop');
		that.disp( true );
	};

	this.close = function(){
		CompEdit.disp( false );
		CBoardDiv.offFocus();
	};

	this.disp = function( sw ){
		if( sw ){
			that.pdiv.style.display = "block";
		}else{
			that.pdiv.style.display = "none";
		}
	};
	this.isPropEvent= function(x,y){
		if( CompEditWnd.style.display === "none" ){
			return false;
		}
		if( y > parseCssInt(CompEditWnd.style.top) ){
			return true;
		}
		return false;
	};

	this.delComp = function(){
		if( window.confirm("Delete '"+that.ce.id+"'?") ){
			CBoardDiv.onDelComp(that.ce.id);
			that.close();
		}
	}

	this.copyComp = function(){
		var ce = that.ce.clone();

		if( ce ){	
			CBoardDiv.assign(ce);
			if(ce.div){
				DBoard.appendChild( ce.div );
				ce.div.addEventListener("touchstart", ce.onTouch, true);
				ce.div.addEventListener("touchmove", ce.onMove, true);
			}
			CBoardDiv.save();
		}
	}

	this.setTab = function(tab){
		PropTab.style.display = "none";
		MethodTab.style.display = "none";
		MenuTab.style.display = "none";
		if( tab == "prop" ){
			PropTab.style.display = "block";
			return;
		}
		CBoardDiv.offFocus();
		if( tab == "menu" ){
			MenuTab.style.display = "block";
			MenuDelete.style.display = "block";
			MenuDelete.style.float = "left";
			MenuChgID.style.display = "block";
			MenuChgID.style.float = "left";
			MenuCopy.style.display = "block";
			MenuCopy.style.float = "left";
			if( ! that.ce.div ){
				MenuCopy.style.display = "none";
				if( that.ce.id == "cSys" ){
					MenuChgID.style.display = "none";
					MenuDelete.style.display = "none";
				}
			}
			return;
		}
		that.makeMethodList();
		MethodTab.style.display = "block";
	}
///////////////Prop
	this.setBase = function(){
		that.setProp("baseを入力してください","that.ce.base","PWbase.innerHTML");
	};
	this.setID = function(){
		if( that.ce != null ) {
			var id = window.prompt("IDを入力してください", that.ce.id);
			if( id!=null ){
				if( CBoardDiv.isUniqId(id)){
					that.ce.id=id;
					PWid.innerHTML=id;
				}else{
					alert(id+"は、すでに使われています。別の名前にしてください。");
				}
			}
		}
	};
	this.setProp = function(prop){
		that.ce.setProp(prop);
	};
///////////////Method
	this.addMethod = function(){
		var newm = prompt("method名を入力してください","newm");
		if( newm!=null ){
			that.ce.methods.add("",newm,"","","",false);
			that.makeMethodList();
		}
	};
	this.makeMethodList = function(){
		var mlist = "";
		for( var n=0; n < that.ce.methods.length(); ++n ){
			var method = that.ce.methods.get(n);
			mlist = mlist + "<div><button onclick='CompEdit.methodEditOpen("+n+");'>...</button> "+
					"<span onclick='CompEdit.setMEfbody("+n+");'>"+this.toMethodStr(method)+"</span></div>";
		}
		MethodList.innerHTML = mlist;
	};
	this.toMethodStr= function(method){
		return "<span class='Mname'>"+method.fname+"("+method.param+")</span>  <span class='Mevent'>["+method.event+"]</span>";
	};
	this.makeMethodRef = function(){
		var mlist = "<L1>"+XML.val2tag("id","my") ;
		for( var n=0; n < that.ce.methods.length(); ++n ){
			var method = that.ce.methods.get(n);
			mlist = mlist + "<L2><id>" + method.fname + "(" + method.param + ")</id></L2>";
		}
		return mlist + RefComps[ that.ce.base ] + "</L1>";
	}
	
	this.CurMethod = {};
	this.CurMethodNum = -1;

	this.methodEditOpen = function(n){
		that.CurMethodNum = n;
		that.CurMethod = that.ce.methods.get(n);
		MEevent.innerHTML = that.CurMethod.event;
		MEfname.innerHTML = that.CurMethod.fname;
		if(that.CurMethod.param==undefined){
			MEparam.innerHTML = "";
		}else{
			MEparam.innerHTML = that.CurMethod.param;
		}
		MethodRemoveBtn.style.display="block";
		MethodEditWnd.style.display = "block";
	};
	this.methodEditClose = function(){
		MethodEditWnd.style.display = "none";
	};
	this.removeMethod = function(){
		if( confirm(that.CurMethod.fname+"() を削除しますか？") ){
			if(that.CurMethod.def){
				that.CurMethod.fbody="";
			}else{
				that.ce.methods.remove(that.CurMethodNum);
			}
			that.methodEditClose();
			that.close();
		}
	};
	
	this.setMEevent = function(){
		that.setMethodProp("eventを入力してください","that.CurMethod.event","MEevent.innerHTML");
	};
	this.setMEfname = function(){
		that.setMethodProp("method nameを入力してください","that.CurMethod.fname","MEfname.innerHTML");
	};
	this.setMEparam = function(){
		that.setMethodProp("paramを入力してください","that.CurMethod.param","MEparam.innerHTML");
	};

	this.setMEfbody = function(n){
		that.CurMethodNum = n;
		that.CurMethod = that.ce.methods.get(n);

		FBedit.open(PWid.innerHTML+"."+that.CurMethod.fname+"("+that.CurMethod.param+")",
					that.CurMethod.fbody, 
					that.makeMethodRef() + CBoardDiv.getCompXml() + uug.getItem("Designer/FuncRef.xml"));
	};
	this.ebackMEfbody = function(fbody){
		if(fbody.lastIndexOf("\n")!=fbody.length-1 ){
			fbody=fbody+"\n";
		}
		that.CurMethod.fbody = fbody;
	}

	this.setMethodProp = function(promtstr, propstr, panelstr ){
		var propval = eval(propstr);
		var title = window.prompt(promtstr, propval);
		if( title!=null ){
			eval(propstr+"='"+title+"'");
			eval(panelstr+"='"+title+"'");
		}
		that.makeMethodList();
	}
};
