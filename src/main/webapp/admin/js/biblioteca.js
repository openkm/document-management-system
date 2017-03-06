/**
 * 
 */ 
function disabledFormFields() {
	for (var i=0;i<document.forms[0].elements.length;i++) {
		var field = document.forms[0].elements[i];
		if (field.type == 'text' || field.type == 'textarea' ||
				field.type == 'file' || field.type == 'select-one' ||
				field.type == 'select-multiple' || field.type == 'radio' ||
				field.type == 'checkbox' || field.type == 'password') {
			field.readOnly = true;
		}
	}
}

/**
 * 
 */
function move(srcStr, dstStr) {
	var src = document.getElementById(srcStr);
	var dst = document.getElementById(dstStr);
	if (src.selectedIndex > -1) {
		var text = src.options[src.selectedIndex].text;
		var value = src.options[src.selectedIndex].value;
		dst.options[dst.options.length] = new Option(text, value, false, false);
		src.remove(src.selectedIndex);
  }
}

/**
 * 
 */
function up(selStr) {
	var sel = document.getElementById(selStr);
	if (sel.selectedIndex > 0) {
		var idx = sel.selectedIndex;
		var tmp = new Option(sel.options[idx-1].text, sel.options[idx-1].value);
		sel.options[idx-1] = new Option(sel.options[idx].text, sel.options[idx].value, false, true);
		sel.options[idx] = tmp;
  }
}

/**
 * 
 */
function down(selStr) {
	var sel = document.getElementById(selStr);
	if (sel.selectedIndex < sel.options.length-1) {
		var idx = sel.selectedIndex;
		var tmp = new Option(sel.options[idx+1].text, sel.options[idx+1].value);
		sel.options[idx+1] = new Option(sel.options[idx].text, sel.options[idx].value, false, true);
		sel.options[idx] = tmp;
    }
}

/**
 * 
 */
function selectAllOptions(selStr) {
	var sel = document.getElementById(selStr);
	for (var i=0; i<sel.options.length; i++) {
		sel.options[i].selected = true;
	}
}

/**
 * window.scroll(0, document.height);
 * window.scroll(0, document.body.offsetHeight);
 */
function scrollToBottom() {
	var bottom = document.body.scrollHeight;
	var current = window.innerHeight + document.body.scrollTop;
	if (bottom-current > 0) {
		window.scrollTo(0, bottom);
		setTimeout('scrollToBottom()', 1000);
	}
}
