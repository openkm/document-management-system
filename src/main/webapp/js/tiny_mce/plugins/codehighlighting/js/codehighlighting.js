function Save_Button_onclick() {
	var lang = document.getElementById("ProgrammingLanguages").value;
	var code =  WrapCode(lang);
	code = code + document.getElementById("CodeArea").value;
	code = code + "</pre>";
	
	if (document.getElementById("CodeArea").value == '') {
		tinyMCEPopup.close();
		return false;
	}
	
	tinyMCEPopup.execCommand('mceInsertContent', false, code);
	tinyMCEPopup.close();
}

function WrapCode(lang) {
	var options = "";
	
	if (document.getElementById("nogutter").checked == true)
		options = ":nogutter";
	
	if (document.getElementById("collapse").checked == true)
		options = options + ":collapse";
	
	if (document.getElementById("nocontrols").checked == true)
		options = options + ":nocontrols";
	
	if (document.getElementById("showcolumns").checked == true)
		options = options + ":showcolumns";
	
	return "<pre name='code' class='brush:" + lang + options + "; toolbar: false;'>";
}

function Cancel_Button_onclick() {
	tinyMCEPopup.close();
	return false;
}
