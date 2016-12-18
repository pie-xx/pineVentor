<script>
var $name$ ={
	open: function(title,text,ref){
		uug.getText(title, text, '$name$.callbackP',ref);
	},
	callbackP: function(key){
		this.setText(uug.getCache(key));
	},
	setText: function(text){
		alert(text);
	}
}
</script>