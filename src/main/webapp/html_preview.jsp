<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <base href="./frontend/"></base>
  <script type="text/javascript" src="../js/syntaxhighlighter/shCore.js"></script>
  <script type="text/javascript" src="../js/syntaxhighlighter/shAutoloader.js"></script>
  <script type="text/javascript" src="../js/syntaxhighlighter/shBrushAppleScript.js"></script>
  <script type="text/javascript" src="../js/syntaxhighlighter/shBrushAS3.js"></script>
  <script type="text/javascript" src="../js/syntaxhighlighter/shBrushBash.js"></script>
  <script type="text/javascript" src="../js/syntaxhighlighter/shBrushColdFusion.js"></script>
  <script type="text/javascript" src="../js/syntaxhighlighter/shBrushCpp.js"></script>
  <script type="text/javascript" src="../js/syntaxhighlighter/shBrushCSharp.js"></script>
  <script type="text/javascript" src="../js/syntaxhighlighter/shBrushDelphi.js"></script>
  <script type="text/javascript" src="../js/syntaxhighlighter/shBrushDiff.js"></script>
  <script type="text/javascript" src="../js/syntaxhighlighter/shBrushErlang.js"></script>
  <script type="text/javascript" src="../js/syntaxhighlighter/shBrushGroovy.js"></script>
  <script type="text/javascript" src="../js/syntaxhighlighter/shBrushJava.js"></script>
  <script type="text/javascript" src="../js/syntaxhighlighter/shBrushJavaFX.js"></script>
  <script type="text/javascript" src="../js/syntaxhighlighter/shBrushJScript.js"></script>
  <script type="text/javascript" src="../js/syntaxhighlighter/shBrushPerl.js"></script>
  <script type="text/javascript" src="../js/syntaxhighlighter/shBrushPhp.js"></script>
  <script type="text/javascript" src="../js/syntaxhighlighter/shBrushPlain.js"></script>
  <script type="text/javascript" src="../js/syntaxhighlighter/shBrushPython.js"></script>
  <script type="text/javascript" src="../js/syntaxhighlighter/shBrushRuby.js"></script>
  <script type="text/javascript" src="../js/syntaxhighlighter/shBrushSass.js"></script>
  <script type="text/javascript" src="../js/syntaxhighlighter/shBrushScala.js"></script>
  <script type="text/javascript" src="../js/syntaxhighlighter/shBrushSql.js"></script>
  <script type="text/javascript" src="../js/syntaxhighlighter/shBrushVb.js"></script>
  <script type="text/javascript" src="../js/syntaxhighlighter/shBrushXml.js"></script>
  <link type="text/css" rel="stylesheet" href="../css/syntaxhighlighter/${cssCore}"/>
  <link type="text/css" rel="stylesheet" href="../css/syntaxhighlighter/${cssTheme}"/>
  <link type="text/css" rel="stylesheet" href="../style/extension/htmlEditor"/>
  <script type="text/javascript">SyntaxHighlighter.all();</script>
</head>
<body>
  ${content}
</body>
</html>