--------------------------------
-- TinyMCE jQuery package
http://www.tinymce.com/download/download.php
Utilizando Version 4.1.7 (2014-11-27)
Se ha modicado el plugin Spellchecker para evitar añadir &nbsp;
En el fichero tinymce4/plugins/spellcheker --> function showSuggestions: 
change editor.insertContent(editor.dom.encode(suggestion)); to editor.execCommand('mceInsertRawHTML', false, editor.dom.encode(suggestion));