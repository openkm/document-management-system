<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<script type="text/javascript" src="js/syntaxhighlighter/shCore.js"></script>
<script type="text/javascript" src="js/syntaxhighlighter/${brush}"></script>
<link type="text/css" rel="stylesheet" href="css/syntaxhighlighter/${cssCore}"/>
<link type="text/css" rel="stylesheet" href="css/syntaxhighlighter/${cssTheme}"/>
<script type="text/javascript">SyntaxHighlighter.all();</script>
</head>
<body style="background: white;">
  <pre class="brush: ${brushType}; toolbar: false;">${content}</pre>
</body>
</html>