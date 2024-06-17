<!DOCTYPE html>

	
	<%@ page import="org.nterlearning.usermgmt.webapp.NTERController" %>
	<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <link rel="Shortcut Icon" href="http://static.nterlearning.org/favicon.ico" />
    <link rel="stylesheet" href="resources/loginstyle.css" type="text/css"/>
    <title><spring:message code="title.success.signup"/></title>
	<%@ include file="analytics.jsp" %>
</head>
	
<body>
<%
	//Get attributes
	String emailAddress = (String) request.getAttribute("emailAddress");
	String referer = (String) request.getAttribute("referer");
	String defaultReferer = (String) request.getAttribute("defaultReferer");
	
	if(emailAddress == null) emailAddress = "";
	if(referer == null) referer = "";
	
	if(referer.isEmpty())
	{
		referer = NTERController.getBaseURL(request.getHeader("Referer"));
	}
	else
	{
		referer=defaultReferer;
	}

%>

	<h1><spring:message code="resend.email"/></h1>
	<p><spring:message code="email.sent.to" arguments="<%= emailAddress %>"/></p>
	
    <span class="forgotpw-return">
        <a href="<%= NTERController.buildSSOLink(referer) %>">
            <spring:message code="sign.in"/>
        </a>
		<a href="/nter-user-mgmt-webapp/signup?emailAddress=<%= emailAddress %>">
            <spring:message code="send.email.again"/>
        </a>
    </span>
	
</body>
</html>