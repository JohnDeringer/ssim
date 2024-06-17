<!DOCTYPE html>

	
	<%@ page import="org.nterlearning.usermgmt.webapp.NTERController" %>
	<%@ page import="java.util.ResourceBundle" %>
	<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
	
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <link rel="Shortcut Icon" href="http://static.nterlearning.org/favicon.ico" />
    <link rel="stylesheet" href="resources/loginstyle.css" type="text/css"/>
    <title><spring:message code="title.reset.password"/></title>
	<%@ include file="analytics.jsp" %>
</head>
<body aria-live="polite">

<%
// Get attributes
	Boolean isPasswordValid = (Boolean) request.getAttribute(NTERController.IS_PASSWORD_VALID);
	Boolean isPasswordConfirm = (Boolean) request.getAttribute(NTERController.IS_PASSWORD_CONFIRM);
	Boolean isValidToken = (Boolean) request.getAttribute(NTERController.IS_VALID_TOKEN);
	Boolean isPasswordChangeError = (Boolean) request.getAttribute(NTERController.IS_PASSWORD_CHANGE_ERROR);
	String token = (String) request.getAttribute(NTERController.TOKEN);
	String referer = (String) request.getAttribute(NTERController.REFERER);
	String defaultReferer = (String) request.getAttribute(NTERController.DEFAULT_REFERER);
	String messageBase = (String) request.getAttribute(NTERController.MESSAGE_BASE);
	String validatorImpl = (String) request.getAttribute(NTERController.VALIDATOR);

	// Default values if they are null
	if(isPasswordValid == null) isPasswordValid = true;
	if(isPasswordConfirm == null) isPasswordConfirm = true;
	if(isValidToken == null) isValidToken = false;
	if (isPasswordChangeError == null) isPasswordChangeError = false;
	if(token == null) token = "";
	if(referer == null) referer = "";

	if(referer.isEmpty())
	{
		referer = NTERController.getBaseURL(request.getHeader("Referer"));
	}
	else {
		referer=defaultReferer;
	}
	
	ResourceBundle rb = ResourceBundle.getBundle(messageBase,request.getLocale());
	
	// get the password guidance text, based on locale
	String msgKeyBase = "password.invalid.error.msg";
	String validatorMsgKey=msgKeyBase;

	if (!validatorImpl.trim().isEmpty()) {
		validatorMsgKey = validatorImpl+"."+msgKeyBase;
	}

	String guidance = rb.getString(validatorMsgKey);
	request.setAttribute("passwordGuidance",guidance);
	
	
%>
<spring:message code="password.invalid.error.msg" var="errorPasswordValid"/>
<spring:message code="password.same" var="passwordSame"/>

<div id="main">

<h1>
    <spring:message code="reset.password"/>
</h1>

<form id="resetPasswordForm" action="<%= NTERController.ACTION_RESET_PASSWORD %>" method="POST">

	<%
    	if(isValidToken)
    	{
    		out.println("<input type=\"hidden\" id=\""+NTERController.TOKEN+"\" name=\""+NTERController.TOKEN+"\" value=\"" + token + "\">");
    	}
    %>
    <label>
        <span>
            <spring:message code="password"/>
        </span>
		
		<input type="password" id="<%= NTERController.PASSWORD %>" name="<%= NTERController.PASSWORD %>" title="${errorPasswordValid}"  class="textfield required password" required>
		
		<%
		String type = isPasswordValid ? "help":"error";
		%>
		<em class="<%= type %>">
		${passwordGuidance}
		</em>
    </label>

	<label>	
        <span>
            <spring:message code="confirm.password"/>
        </span>
		
		<input type="password" id="<%= NTERController.CONFIRM_PASSWORD %>" name="<%= NTERController.CONFIRM_PASSWORD %>" title="${passwordSame}" class="textfield" data-equal-to="password">
       	 	
       	<%	
       	 	if (!isPasswordConfirm && isPasswordValid) {
       	 		%>
       	 		<em class="error">
       	 		<spring:message code="confirm.password.match"/>
       	 		</em>
       	 		<%
       	 	}
       	 else if (isPasswordChangeError) {
	       	 	%>
	   	 		<em class="error"><spring:message code="password.change.error"/></em>
	   	 		<% 
    	 	}
       	 			
		%>
    </label>


    <input id="<%= NTERController.CMD_RESET_PASSWORD %>" name="<%= NTERController.CMD_RESET_PASSWORD %>" type="submit" class="submit" value="Change Password">
    <span class="forgotpw-return">
        <a href="<%= NTERController.buildSSOLink(referer) %>">
            <spring:message code="sign.in"/>
        </a>
    </span>

    
</form>

</div>

</body>
</html>