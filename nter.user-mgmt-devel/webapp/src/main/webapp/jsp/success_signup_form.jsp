<!DOCTYPE html>


<%@ page import="org.nterlearning.usermgmt.webapp.NTERController" %>
	<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <link rel="Shortcut Icon" href="http://static.nterlearning.org/favicon.ico" />
    <link rel="stylesheet" href="resources/loginstyle.css" type="text/css"/>
    <title><spring:message code="title.resend.email"/></title>
	<%@ include file="analytics.jsp" %>
</head>
	
<body>
<%
// Get attributes
	String emailAddress = (String) request.getAttribute(NTERController.EMAIL_ADDRESS);
	Boolean isEmailAddressComplete = (Boolean) request.getAttribute(NTERController.IS_EMAIL_ADDRESS_COMPLETE);
	Boolean isValidEmailAddressComplete = (Boolean) request.getAttribute(NTERController.IS_VALID_EMAIL_ADDRESS_COMPLETE);
	Boolean isEmailWaiting = (Boolean) request.getAttribute(NTERController.IS_EMAIL_WAITING);
	String referer = (String) request.getAttribute(NTERController.REFERER);
	
	if(emailAddress == null) emailAddress = "";
	if(isEmailAddressComplete == null) isEmailAddressComplete = true;
	if(isValidEmailAddressComplete == null) isValidEmailAddressComplete = true;
	if(isEmailWaiting == null) isEmailWaiting = true;
	if(referer == null) referer = "";
	
	if(referer.isEmpty())
	{
		referer = NTERController.getBaseURL(request.getHeader("Referer"));
	}

%>
<spring:message code="resend.email" var="resendEmailMsg"/>

	<h1><spring:message code="resend.verification.email"/></h1>
	<p><spring:message code="check.email.msg"/></p>

	<form id="createAccountForm" action="<%= NTERController.ACTION_VERIFY %>" method="POST">
	
	    <label>
	        <span>
	            <spring:message code="email.address"/>
	        </span>
	        
			<input type="email" id="<%= NTERController.EMAIL_ADDRESS %>" name="<%= NTERController.EMAIL_ADDRESS %>" value="<%= emailAddress  %>" class="textfield required email" required>

		    <%
	       	 	if (!isEmailAddressComplete) {
	       	 %>
	       	 		<em class="error">
	       	 		<spring:message code="email.address.required"/>
	       	 		</em>"
			<% 	
	       	 	}
	       	 	else if (!isValidEmailAddressComplete)
	       	 	{
	       	 	 %>
	       	 		<em class="error">
	       	 		<spring:message code="email.address.complete"/>
	       	 		</em>"
			<%    	 		
	       	 	}
	       	 	else if (!isEmailWaiting) {
	       	 	%>
       	 		<em class="error">
       	 		<spring:message code="email.not.waiting"/>
       	 		</em>
		<%   
	       	 	}	
	       	%>	
			
	    </label>

		<input id="<%= NTERController.CMD_RESUBMIT %>" name="<%= NTERController.CMD_RESUBMIT %>" type="submit" class="submit" value="${resendEmailMsg}">
		<input type="hidden" id="<%= NTERController.ORIG_EMAIL %>" name="<%= NTERController.ORIG_EMAIL %>" value="<%= emailAddress  %>" class="textfield required" required>

	</form>

	
</body>
</html>