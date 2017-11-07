function copyTextToClipboard(ToCopy) {
	var textArea = document.createElement('textarea');

	textArea.style.position = 'fixed';
	textArea.style.top = 0;
	textArea.style.left = 0;
	textArea.style.width = '2em';
	textArea.style.height = '2em';
	textArea.style.padding = 0;
	textArea.style.border = 'none';
	textArea.style.outline = 'none';
	textArea.style.boxShadow = 'none';
	textArea.style.background = 'transparent';
	textArea.value = ToCopy;

	document.body.appendChild(textArea);
	textArea.select();

	var successful = document.execCommand('copy');
	document.body.removeChild(textArea);
}
