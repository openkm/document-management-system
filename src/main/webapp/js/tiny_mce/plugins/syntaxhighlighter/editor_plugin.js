tinymce.create('tinymce.plugins.SyntaxHighlighterPlugin', {
    createControl: function(n, cm) {
        switch (n) {
            case 'syntaxhighlighter':
                var mlb = cm.createListBox('syntaxhighlighter', {
                    title: 'Code',
                    onselect: function(v) {
                        var content = new String(tinyMCE.activeEditor.selection.getContent());
                        content = content.replace(/<(p)([^>]*)>/g, '');
                        content = content.replace(/<\/(p)>/g, '');
                        //content = content.replace(/\/(&nbsp;)/g, '\n\r');
                        
                        tinyMCE.activeEditor.selection.setContent('<pre id=\'okm_code\' name=\'okm_code\' class=\'brush: ' + v + '; toolbar: false;\'>' + content + '</pre>');
                    }
                });
                
                // Add some values to the list box
                mlb.add('ActionScript3', 'actionscript3');
                mlb.add('Bash/shell', 'shell');
                mlb.add('ColdFusion', 'coldfusion');
                mlb.add('C#', 'csharp');
                mlb.add('C++', 'c');
                mlb.add('CSS', 'css');
                mlb.add('Delphi', 'delphi');
                mlb.add('Diff', 'diff');
                mlb.add('Erlang', 'erlang');
				mlb.add('Groovy', 'groovy');
				mlb.add('JavaScript', 'javascript');
				mlb.add('Java', 'java');
				mlb.add('JavaFX', 'javafx');
				mlb.add('Perl', 'perl');
				mlb.add('PHP', 'php');
				mlb.add('Plain Text', 'text');
				mlb.add('PowerShell', 'powershell');
				mlb.add('Python', 'python');
				mlb.add('Ruby', 'ruby');
				mlb.add('Scala', 'scala');
				mlb.add('SQL', 'sql');
				mlb.add('Visual Basic', 'vbnet');
				mlb.add('XML', 'xml');
				
                // Return the new listbox instance
                return mlb;
        }
        
        return null;
    },
    
    getInfo: function() {
        return {
            longname: 'Syntax Highlighter',
            author: 'Josep Llort Tella',
            authorurl: 'http://www.openkm.com',
            infourl: 'http://www.openkm.com',
            version: '1.0'
        };
    }
});

// Register plugin with a short name
tinymce.PluginManager.add('syntaxhighlighter', tinymce.plugins.SyntaxHighlighterPlugin);
