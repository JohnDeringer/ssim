<!DOCTYPE html>
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
	// Get attributes
	String emailAddress = (String) request.getAttribute("emailAddress");
%>

<div id="main">
	<h1><spring:message code="forgot.password"/></h1>
	<spring:message code="your.email.address" var="generic.email"/>
	<p><spring:message code="sent.email" arguments="<%= emailAddress %>"/></p>
</div>
</body>
</html>