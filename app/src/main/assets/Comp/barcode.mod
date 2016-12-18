<script>
var $name$ ={
	open: function(){
		uug.intentCall('com.google.zxing.client.android.SCAN', "", "", "", 'SCAN_RESULT', '$name$.scanback');
	},
	scanback: function(extra){
		var pic = uug.getCache(extra);
		alert(pic);
	}
}
</script>
