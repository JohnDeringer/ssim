<!DOCTYPE html>

	
	<%@ page import="org.nterlearning.usermgmt.webapp.NTERController" %>
	<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <link rel="Shortcut Icon" href="http://static.nterlearning.org/favicon.ico" />
    <link rel="stylesheet" href="resources/loginstyle.css" type="text/css"/>
    <title><spring:message code="title.success.password.reset"/></title>
	<%@ include file="analytics.jsp" %>
</head>
<body>

<%

	//Get attributes
	String referer = (String) request.getAttribute("referer");
	
	// Default values if they are null
	if(referer == null) referer = "";
	
	if(referer.isEmpty())
	{
		referer = NTERController.getBaseURL(request.getHeader("Referer"));
	}
%>

<div id="main">
	<h1><spring:message code="password.reset"/></h1>
	<p><spring:message code="password.reset.msg"/></p>
    <a href="<%= NTERController.buildSSOLink(referer) %>">
            <spring:message code="sign.in"/>
    </a>
</div>
</body>
</html>