<script>
var $name$ = {
	tag2val: function ( tag, str ){
		if( str===undefined )
			return "";
		var tags = str.split("<"+tag+">");
		if( tags.length < 2 ){
			return "";
		}
		var its = tags[1].split("</"+tag+">");
		if(its.length < 2 ){
			return "";
		}
		return its[0].replace(/&gt;/gm,'>').replace(/&lt;/gm,'<').replace(/&amp;/gm,'&');
	},
	val2tag: function( tag, value ){
		if( value==undefined || value==null ){
			return "<"+tag+"></"+tag+">";
		}
		if( !value.replace ){
			return "<"+tag+">"+value+"</"+tag+">";
		}
		value = value.replace(/&/gm,'&amp;');
		value = value.replace(/</gm,'&lt;');
		value = value.replace(/>/gm,'&gt;');
		return "<"+tag+">"+value+"</"+tag+">";
	},
};
</script>