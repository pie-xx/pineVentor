<script>
var $name$ = new (function(){
	var that = this;
	that.sf = {};

	that.onKeyBack = function (){
	};

	that.onKeyVup = function (){
	};

	that.onKeyVdown = function (){
	};

	that.onKeySearch = function (){
	};

	that.push = function(f){
		that.sf = that.onKeyBack;
		that.onKeyBack = f;
	};

	that.pop = function(){
		that.onKeyBack = that.sf;
	}
})();
</script>