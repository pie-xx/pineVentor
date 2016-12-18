///<cat>System</cat>
TimerBuilder = function(pstr){
	var that = new NonDivBuilder(pstr);
	that.tid = null;

	that.setTimeout = function( tout ){
		if( that.tid ){
			that.stop();
		}
		that.tid = window.setTimeout( that.onTimer, tout );
	};
	that.setInterval = function( tout ){
		if( that.tid ){
			that.stop();
		}
		that.tid = window.setInterval( that.onTimer, tout );
	};
	that.onTimer = function(){ //P
	};
	that.stop = function(){
		clearTimeout(that.tid);
		clearInterval(that.tid);
		that.tid=null;
	}
	that.isRunning = function(){
		return that.tid!=null;
	}
	return that;
}
