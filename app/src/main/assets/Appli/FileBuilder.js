///<cat>System</cat>
FileBuilder = function(pstr){
	var that = new NonDivBuilder(pstr);
	that.base = "File";
	that.methods.load(pstr);

	that.pickImage = function (){
		uug.pickImage(that.id+'.onPicked');
	};
	that.pickFile = function(){
		uug.pickFile("*/*",that.id+'.onPicked');
	};
	that.read = function(file){
		return uug.fread(file);
	};
	that.write = function(file, value){
		uug.fwrite(file, value);
	};
	that.fname2realpath = function(fname){
		return uug.fname2realpath(fname);
	};
	that.onPicked = function(url){
	};
	return that;
}
