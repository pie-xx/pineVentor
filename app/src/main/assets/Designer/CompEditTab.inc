<div id="CompEditWnd" >
	<button id="MenuBtn" onClick="CompEdit.setTab('menu')">≡</button>
	<span id="PWid"></span>
	<button id="MethodBtn" onClick="CompEdit.setTab('method')">Method</button>
	<button id="PropBtn" onClick="CompEdit.setTab('prop')">Property</button>
	<button onclick='CompEdit.close()' style="float:right;">x</button>

	<div id="MenuTab" >
		Base Component <span id="PWbase"></span><br/>
		<button onClick="CompEdit.delComp()" id='MenuDelete'><img src='file:///android_asset/icon/ic_delete_black_24dp.png' height='32'/> delete</button>
		<button onClick="CompEdit.setID()" id='MenuChgID'><img src='file:///android_asset/icon/ic_flag_black_24dp.png' height='32'/> change ID</button> 
		<button onClick="CompEdit.copyComp()" id='MenuCopy'><img src='file:///android_asset/icon/ic_content_copy_black_24dp.png' height='32'/> copy</button> 
	</div>
	<div id="PropTab" >
	</div>
	<div id="MethodTab" >
		<button onClick="CompEdit.addMethod()">new</button><br/>
		<div id="MethodList">
		</div>
	</div>
</div>
<div id="MethodEditWnd" >
	<button onclick='CompEdit.methodEditClose()' style="float:right;">x</button>
	<div id="MethodEditTab">
		<table>
		<tr><td>Event</td>		<td><span id="MEevent"></span></td></tr>
		<tr><td><button onclick="CompEdit.setMEfname()">Method name</button></td>	<td><span id="MEfname"></span></td></tr>
		<tr><td><button onclick="CompEdit.setMEparam()">Parameters</button></td>	<td><span id="MEparam"></span></td></tr>
		</table>
		<comp type="textinputNw" name="FBedit"/>
		<script>
		FBedit.setText = function(fbody){
			CompEdit.ebackMEfbody(fbody);
			CBoardDiv.save();
		};
		</script>
		<br/>
		<button onclick="CompEdit.removeMethod()" id="MethodRemoveBtn">remove</button>
	</div>
</div>

<script src="CompEditBuilder.js"></script>
