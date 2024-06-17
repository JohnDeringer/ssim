<!DOCTYPE html>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <link rel="Shortcut Icon" href="http://static.nterlearning.org/favicon.ico" />
    <link rel="stylesheet" href="resources/loginstyle.css" type="text/css"/>
    <title><spring:message code="title.success"/></title>
	<%@ include file="analytics.jsp" %>
</head>
	
<body>
<%
// Get attributes
	String emailAddress = (String) request.getAttribute("emailAddress");
%>
	<h1><spring:message code="verify.email"/></h1>
	<p><spring:message code="confirm.email" arguments="<%= emailAddress %>"/></p>
	<spring:message code="resend.link" var="resendLink"/>
	
	
	<% 
		String link = "<a href=\"/nter-user-mgmt-webapp/signup?emailAddress="+ emailAddress+"\">"+pageContext.getAttribute("resendLink")+"</a>";
	%>
	<p><spring:message code="spam.email" arguments="<%= link %>"/></p>
	
</body>
</html>