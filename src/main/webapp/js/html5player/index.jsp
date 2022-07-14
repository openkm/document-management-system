<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<?xml version="1.0" encoding="UTF-8" ?>
<html>
<head>
    <meta charset="utf-8">
    <base href="./preview/html5player/"/>
    <script type="text/javascript" src="js/jquery-1.9.1.min.js"></script>
    <script type="text/javascript"  src="js/mediaelement-and-player.min.js"></script>
    <link rel="stylesheet" href="css/mediaelementplayer.min.css" />
    <link rel="stylesheet" href="css/mejs-skins.css" />

</head>

<body style="margin:0px !important">
    <c:if test="${mediaProvider == 'video'}">
      <video width="${width}" height="${height}" id="playerVideo" controls="controls" preload="auto">
        <source src="${mediaUrl}" type="video/webm"> 
        <source src="${mediaUrl}" type="video/ogg"> 
        <source src="${mediaUrl}" type="video/mp4">
        <source src="${mediaUrl}" type="video/3gp">
      </video>
    </c:if>
    <c:if test="${mediaProvider == 'sound'}">
      <audio id="playerSound">
        <source src="${mediaUrl}" type="audio/mpeg"> 
        <source src="${mediaUrl}" type="audio/ogg"> 
        <source src="${mediaUrl}" type="audio/wav">
      </audio>
    </c:if>
    <script>
    jQuery(document).ready(function($) {
        $('#playerVideo').mediaelementplayer();
        $('#playerSound').mediaelementplayer();
    });
    </script>
</body>
</html>