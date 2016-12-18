<script>
var $name$ ={
	open: function(content){
		this.print("<img src='"+content+"'/>");
		this.disp( true );
	},
	close: function(){
		this.disp( false );
	},
	print: function(msg){
		document.getElementById('$tag$c').innerHTML = msg;
	},
	disp: function( sw ){
		if( sw ){
			$tag$.style.display = "block";
		}else{
			$tag$.style.display = "none";
		}
	}
}
</script>
<div id="$tag$" style="height:320px;display:none;position:fixed;width:80%;top:100;left:10%;z-index:10000;background-color:White;border:1px solid #666;padding:4px;">
	<button onclick='$name$.close()' style="float:right;">x</button>
	<div id="$tag$c" style="color:black;font-size:x-large;">test</div>
</div>