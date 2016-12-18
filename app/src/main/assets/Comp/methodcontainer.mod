<script>
var MethodContainer = function(){
	var that = this;
	this.Methods = [];	//event,id,param,func
	this.length = function(){
		return that.Methods.length;
	};
	this.load = function(pstr){
		that.Methods = [];
		var mstrs = pstr.split("<method>");
		for( var n=1; n < mstrs.length; ++n ){
			that.add(XML.tag2val("event",mstrs[n]),XML.tag2val("fname",mstrs[n]),
						XML.tag2val("param",mstrs[n]),XML.tag2val("fbody",mstrs[n]));
		}
	}
	this.toString = function() {
		var methodstr = "<methods>";
		for( var n=0; n < that.Methods.length; ++n ){
			var param = "";
			if( that.Methods[n].param!=undefined ){
				param = XML.val2tag("param",that.Methods[n].param);
			}
			methodstr = methodstr + "<method>" +
				XML.val2tag("event",that.Methods[n].event)+
				XML.val2tag("fname",that.Methods[n].fname)+
				param+
				XML.val2tag("fbody",that.Methods[n].fbody)+
			"</method>";
		}
		return methodstr+"</methods>";
	}
	this.create = function(event,fname,param,fbody,desc,def){
		var method = {};
		method.event = event;
		method.fname = fname;
		method.param = param;
		method.fbody = fbody;
		method.desc = desc;
		method.def = def;
		that.Methods.push(method);
	};
	this.add = function(event,fname,param,fbody,desc,def){
		var method = that.getByName( fname );
		if( method ){
			method.event = event;
			method.param = param;
			method.fbody = fbody;
			method.desc = desc;
			method.def = def;
			return;
		}
		that.create(event,fname,param,fbody,desc,def);
	};
	this.defadd = function(event,fname,param,fbody,desc,def){
		if( that.getByName(fname)==null ){
			that.add(event,fname,param,fbody,desc,true);
		}
	};
	this.getByName = function(fname){
		for( var n=0; n < that.Methods.length; ++n ){
			if(that.Methods[n].fname==fname){
				return that.Methods[n];
			}
		}
		return null;
	};
	this.get = function(n){
		return that.Methods[n];
	};
	this.remove = function(n){
		that.Methods.splice(n, 1);
	};
	this.getXMLlist = function(){
		var methodstr = "<methods>";
		for( var n=0; n < that.Methods.length; ++n ){
			methodstr = methodstr + "<L2>" +
				XML.val2tag("event",that.Methods[n].event)+
				XML.val2tag("id",that.Methods[n].fname+
					"("+that.Methods[n].param+")" )+
				XML.val2tag("desc",that.Methods[n].desc)+
			"</L2>";
		}
		return methodstr+"</methods>";
	};

	this.clone = function(){
		var clone = new MethodContainer();
		for( var n=0; n < that.Methods.length; ++n ){
			var method = that.Methods[n];
			var event = method.event;
			var fname = method.fname;
			var param = method.param;
			var fbody = method.fbody;
			var desc = method.desc;
			var def = method.def;
			clone.create(event,fname,param,fbody,desc,def);
		}
		return clone;
	};
};
</script>
