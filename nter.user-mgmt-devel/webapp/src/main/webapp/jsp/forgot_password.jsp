<%@ page import="java.util.*" %>
<!DOCTYPE html>

	
	<%@ page import="org.nterlearning.usermgmt.webapp.NTERController" %>
	<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
	
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <link rel="Shortcut Icon" href="http://static.nterlearning.org/favicon.ico" />
    <link rel="stylesheet" href="resources/loginstyle.css" type="text/css"/>
    <title><spring:message code="title.forgot.password"/></title>
	<%@ include file="analytics.jsp" %>
</head>
<body>

<%
//Get attributes
String emailAddress = (String) request.getAttribute(NTERController.EMAIL_ADDRESS);
Boolean isEmailAddressComplete = (Boolean) request.getAttribute(NTERController.IS_EMAIL_ADDRESS_COMPLETE);
Boolean isValidEmailAddressComplete = (Boolean) request.getAttribute(NTERController.IS_VALID_EMAIL_ADDRESS_COMPLETE);
Boolean isUserWithEmail = (Boolean) request.getAttribute(NTERController.IS_USER_WITH_EMAIL);
String referer = (String) request.getAttribute(NTERController.REFERER);
String defaultURL = (String) request.getAttribute(NTERController.DEFAULT_REFERER);

if(emailAddress == null) emailAddress = "";
if(isEmailAddressComplete == null) isEmailAddressComplete = true;
if(isValidEmailAddressComplete == null) isValidEmailAddressComplete = true;
if(isUserWithEmail == null) isUserWithEmail = true;
if(referer == null) referer = "";

if(referer.isEmpty())
{
	referer = request.getHeader("Referer");
	if (referer != null) {
		referer = NTERController.getBaseURL(referer);
	}
	else referer = defaultURL;
}

// Set a cookie for the referer ... if needed.  It tried to hook
// it onto the end of the login request, but it wouldn't go through.
// It goes through when pasted directly into the url though
Cookie cookie = new Cookie("referer",referer);
response.addCookie(cookie);


%>

<div id="main">

<h1>
    <spring:message code="forgot.password"/>
</h1>

<p><spring:message code="enter.email.msg"/></p>

<form id="forgotPasswordForm" action="<%= NTERController.ACTION_FORGOT_PASSWORD %>" method="POST">
	
   <input type="hidden" id="<%= NTERController.REFERER %>" name="<%= NTERController.REFERER %>" value="<%= referer  %>" class="textfield required" required>	
	
   <label>
        <span>
            <spring:message code="email.address"/>
        </span>
		
		<input type="email" id="<%= NTERController.EMAIL_ADDRESS %>" name="<%= NTERController.EMAIL_ADDRESS %>" value="" class="textfield required email" required>	

		<%
	       	 	if (!isEmailAddressComplete) {
	       	 		%>
	       	 		<em class="error">
	       	 		<spring:message code="email.address.required"/>
	       	 		</em>
	       	 		<% 
	       	 	}
	       	 	else if (!isValidEmailAddressComplete)
	       	 	{
	       	 	%>
       	 		<em class="error">
       	 		<spring:message code="email.address.complete"/>
       	 		</em>
       	 		<% 	   	 		
	       	 	}
	       	 	else if (!isUserWithEmail) {
	       	 	%>
       	 		<em class="error">
       	 		<spring:message code="email.address.valid"/>
       	 		</em>
       	 		<% 
	
	       	 	}	
	    %>	

    </label>

    <input id="<%= NTERController.CMD_FORGOT_PASSWORD %>" name="<%= NTERController.CMD_FORGOT_PASSWORD %>" type="submit" class="submit" value="Reset Password"/>
    <span class="forgotpw-return">
        <a href="<%= NTERController.buildSSOLink(referer) %>">
            <spring:message code="sign.in"/>
        </a>
		<a href="/nter-user-mgmt-webapp/signup?referer=<%= referer %>">
            <spring:message code="create.account"/>
        </a>
    </span>

    
</form>

</div>

</body>
</html>