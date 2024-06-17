<!DOCTYPE html>

	<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
	<%@ page import="org.nterlearning.usermgmt.webapp.NTERController" %>

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <link rel="Shortcut Icon" href="http://static.nterlearning.org/favicon.ico" />
    <link rel="stylesheet" href="../resources/loginstyle.css" type="text/css"/>
    <title><spring:message code="title.validate"/></title>
	<%@ include file="analytics.jsp" %>
</head>
<body>

<%
	// Get attributes
	String referralURL = (String) request.getAttribute("referralURL");

// Default values if they are null
	if(referralURL == null) referralURL = "";
%>

<div id="main">
		<h1><spring:message code="account.created"/></h1>
		<p><spring:message code="email.verified.already"/></p>
		<p><a href="<%= NTERController.buildSSOLink(referralURL) %>"><spring:message code="continue.nter"/></a></p>
</div>
</body>
</html>