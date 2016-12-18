<script>
var $name$ ={
	open: function(addr,sub,body){
		uug.intentCall('android.intent.action.VIEW', 
			"mailto:"+addr+"?subject="+sub+"&body="+body,
			"", "", "", '');
	}
}
</script>
