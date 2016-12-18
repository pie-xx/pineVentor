<default btntitle="reload" btndisp=""/>
<script>
var $name$ ={
	load: function( listxml, itemtag, keytag, valtag ){
		var items = listxml.split("<"+itemtag+">");
		var select = $tag$w.getElementsByTagName('div')[0];
		select.innerHTML = "";
		for( var i=1; i < items.length; ++i ){
			var option = document.createElement("div");
			option.innerHTML = this.tag2value(keytag,items[i]);
			option.style.borderBottom = "solid 1px";
			option.onclick = $name$.onSelectP;
			option.value = this.tag2value(valtag,items[i]);
			select.appendChild(option);
		}
		if(this.tag2value("selectedText",listxml)==""){
			$tag$i.innerHTML = this.tag2value(keytag,items[1]);
			$name$.selectedText = $tag$i.innerHTML;
			$name$.selectedValue = this.tag2value(valtag,items[1]);
		}else{
			$tag$i.innerHTML = this.tag2value("selectedText",listxml);
			$name$.selectedText = $tag$i.innerHTML;
			$name$.selectedValue = this.tag2value("selectedValue",listxml);
		}
		this.itemtag = itemtag;
		this.keytag = keytag;
		this.valtag = valtag;
		if("$btndisp$"!=""){
			$tag$i.innerHTML = "$btndisp$";
		}
	},
	save: function(){
		var seldiv = document.getElementById("$tag$w");
		var opts = seldiv.getElementsByTagName('div');
		var savestr = "";
		for( var n=1; n < opts.length; ++n ){
			savestr = savestr + this.value2tagstr( this.itemtag, 
									this.value2tagstr( this.keytag, opts[n].innerHTML )+
									this.value2tagstr( this.valtag, opts[n].value ) );
		}
		savestr = savestr + this.value2tagstr("selectedText", this.selectedText );
		savestr = savestr + this.value2tagstr("selectedValue", this.selectedValue );
		return savestr;
	},
	itemtag:"",
	keytag:"",
	valtag:"",
	selectedText:"",
	selectedValue:0,
	onSelectP: function(){
		$name$.disp( false );
		$name$.set( event.target.innerHTML, event.target.value );
	},
	onSelectS: function(){
		$name$.onSelect($name$.selectedValue);
	},
	onSelect: function(iv){
		alert(iv);
	},
	set: function( text, value ){
		$name$.selectedText = text;
		$name$.selectedValue = value;
		$tag$i.innerHTML = text.substr(0,18) ;
		$name$.onSelect($name$.selectedValue);
	},
	close: function(){
		$name$.disp( false );
	},
	print: function(msg){
		$tag$w.getElementsByTagName('div')[0].innerHTML = msg;
	},
	disp: function( sw ){
		if( sw ){
			$tag$w.style.display = "block";
			$tag$t.style.display = "block";
		}else{
			$tag$w.style.display = "none";
			$tag$t.style.display = "none";
		}
	},
	open: function(){
		$name$.disp(true);
	},
	//////////////////////////////////////////////////////////////////////////
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
	value2tagstr: function( tag, value ){
		return "<"+tag+">"+value+"</"+tag+">";
	}
}
</script>
<button id="$tag$i" onclick='$name$.onSelectS()' style="background-color:White;color:black;">
$btndisp$
</button>
<button onclick='$name$.open()'>↓</button>
<div id="$tag$t" style="height:40px;display:none;position:fixed;width:80%;top:100;left:10%;z-index:10000;background-color:pink;border:1px solid #666;padding:4px;">
	<button onclick='$name$.refresh()' >$btntitle$</button>
	<span id="$tag$title"></span>
	<button onclick='$name$.close()' style="float:right;">x</button>
</div>
<div id="$tag$w" style="height:320px;overflow:scroll;display:none;position:fixed;width:80%;top:140;left:10%;z-index:10000;background-color:White;border:1px solid #666;padding:4px;">
	<div style="color:black;font-size:x-large;"></div>
</div>
