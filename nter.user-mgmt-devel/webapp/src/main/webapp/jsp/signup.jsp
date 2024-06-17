<!DOCTYPE html>

	
	<%@ page import="org.nterlearning.usermgmt.webapp.NTERController" %>
	<%@ page import="org.nterlearning.usermgmt.common.UserMgmtUtils" %>
	<%@ page import="java.util.ResourceBundle" %>
	<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
<head>


    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <link rel="Shortcut Icon" href="http://static.nterlearning.org/favicon.ico" />
    
    <link rel="stylesheet" href="resources/loginstyle.css" type="text/css"/>
    
    <title><spring:message code="title.signup"/></title>
	<%@ include file="analytics.jsp" %>
</head>
<body aria-live="polite">

<%
// Get attributes
	Boolean isEmailAddressComplete = (Boolean) request.getAttribute(NTERController.IS_EMAIL_ADDRESS_COMPLETE);
	Boolean isValidEmailAddressComplete = (Boolean) request.getAttribute(NTERController.IS_VALID_EMAIL_ADDRESS_COMPLETE);
	Boolean isFirstNameComplete = (Boolean) request.getAttribute(NTERController.IS_FIRST_NAME_COMPLETE);
	Boolean isLastNameComplete = (Boolean) request.getAttribute(NTERController.IS_LAST_NAME_COMPLETE);
	Boolean isPasswordConfirm = (Boolean) request.getAttribute(NTERController.IS_PASSWORD_CONFIRM);
	Boolean isPasswordValid = (Boolean) request.getAttribute(NTERController.IS_PASSWORD_VALID);
	Boolean isEmailNotUsed = (Boolean) request.getAttribute(NTERController.EMAIL_NOT_USED);
	Boolean userSignupSuccess = (Boolean) request.getAttribute(NTERController.USER_SIGNUP_SUCCESS);
	String firstName = (String) request.getAttribute(NTERController.FIRST_NAME);
	String lastName = (String) request.getAttribute(NTERController.LAST_NAME);
	String userName = (String) request.getAttribute(NTERController.USER_NAME);
	String emailAddress = (String) request.getAttribute(NTERController.EMAIL_ADDRESS);
	String referer = (String) request.getAttribute(NTERController.REFERER);
	String loginURL = (String) request.getAttribute(NTERController.LOGIN_URL);
	String validatorImpl = (String) request.getAttribute(NTERController.VALIDATOR);
	String messageBase = (String) request.getAttribute(NTERController.MESSAGE_BASE);
	Boolean isTermsAndConditions = (Boolean) request.getAttribute(NTERController.IS_TERMS_AND_CONDITIONS);
	Boolean createError = (Boolean)request.getAttribute(NTERController.CREATE_ERROR);
	String supportEmail = (String)request.getAttribute(NTERController.SUPPORT_EMAIL);
	String organization = (String)request.getAttribute(NTERController.ORGANIZATION);
	String passwordPattern = (String)request.getAttribute(NTERController.PASSWORD_PATTERN);
	
	Boolean isEmailUsed = (isEmailNotUsed == null) ? false:!isEmailNotUsed;
	
	// Default values if they are null
	if(isEmailAddressComplete == null) isEmailAddressComplete = true;
	if(isValidEmailAddressComplete == null) isValidEmailAddressComplete = true;
	if(isFirstNameComplete == null) isFirstNameComplete = true;
	if(isLastNameComplete == null) isLastNameComplete = true;
	if(isPasswordConfirm == null) isPasswordConfirm = true;
	if(isPasswordValid == null) isPasswordValid = true;
	

	if(userSignupSuccess == null) userSignupSuccess = false;
	if(firstName == null) firstName = "";
	if(lastName == null) lastName = "";
	if(userName == null) userName = "";
	if(emailAddress == null) emailAddress = "";
	if(referer == null) referer = "";
	if(createError == null) createError = false;
	if (organization == null) organization = "";
	

	if(referer.isEmpty())
	{
		referer = NTERController.getBaseURL(request.getHeader("Referer"));
	}
	
	// resolve the terms and conditions based on the Locale
	String termsAndConditions = "/"+UserMgmtUtils.resolveRemoteResource(loginURL,"TermsAndConditions",request.getLocale(),".html");
	
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

<spring:message code="password.same" var="passwordSame"/>
<spring:message code="create.account" var="createAccount"/>
<spring:message code="signing.in" var="signingIn"/>
<spring:message code="resend.confirmation" var="resendConf"/>

<div id="main">

<h1>
    <spring:message code="create.account"/>
</h1>

<form id="createAccountForm" action="<%= NTERController.ACTION_SIGNUP %>" method="POST">

	<input type="hidden" id="<%= NTERController.REFERER %>" name="<%= NTERController.REFERER %>" value="<%= referer %>">
        
        <% 
        
        	if (createError) {
        		String mailLink = "<a href=\"mailto:"+supportEmail+"?Subject=Problem%20creating%20NTER%20account\">NTER Support</a>";
        		pageContext.setAttribute("supportEmail",mailLink);
        		%>
        		<em class="error">
	   	 			<spring:message code="signup.error" arguments="${supportEmail}"/>
	   	 		</em>
	   	 		<% 
        	}
        %>
        
    <label>    
        <span>
            <spring:message code="first.name"/>
        </span>
        				
		<%
			// Should already be true, but ...
       	 	if (!isFirstNameComplete) {
       	 		firstName = "";
       	 	}	
		%>        				
        				
		<input type="text" id="<%= NTERController.FIRST_NAME %>" name="<%= NTERController.FIRST_NAME %>" value="<%= firstName %>" class="textfield required" required autofocus>

    	<%
	   	 	if (!isFirstNameComplete) {
	   	 		%>
	   	 		<em class="error">
	   	 		<spring:message code="first.name.required"/>
	   	 		</em>
		<%	
	   	 	}
       	%>
		
    </label>
	
    <label>
        <span>
            <spring:message code="last.name"/>
        </span>

		
		<%
			// Should already be true, but ...
       	 	if (!isLastNameComplete) {
       	 		lastName = "";
       	 	}	
		%>
		
		<input type="text" id="<%= NTERController.LAST_NAME %>" name="<%= NTERController.LAST_NAME %>" value="<%= lastName  %>" class="textfield required" required>
		
    	<%
	   	 	if (!isLastNameComplete) {
	   	 		%>
	   	 		<em class="error">
	   	 			<spring:message code="last.name.required"/>
	   	 		</em>
	   	 		<% 
	   	 	}
       	%>
		
    </label>
	
    <label>
        <span>
            <spring:message code="email.address"/>
        </span>

	    <%
	    /*
       	 	if (!isEmailAddressComplete || !isValidEmailAddressComplete || isEmailUsed) {
       	 		emailAddress = "";
       	 	}
	    */
       	%>	
		
		<input type="email" id="<%= NTERController.EMAIL_ADDRESS %>" name="<%= NTERController.EMAIL_ADDRESS %>" value="<%= emailAddress  %>" class="textfield required email" required>
		
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
       	 	else if (isEmailUsed) {
       	 	
       	 		String signinLink="<a href=\""+NTERController.buildSSOLink(referer)+"\">"+pageContext.getAttribute("signingIn")+"</a>";
       	 		String resendLink="<a href=\"/nter-user-mgmt-webapp/signup?emailAddress="+ emailAddress+"\">"+pageContext.getAttribute("resendConf")+"</a>";
       	 		pageContext.setAttribute("sLink",signinLink);
       	 		pageContext.setAttribute("rLink",resendLink);

       	 		%>
       	 		<em class="error">
       	 			<spring:message code="email.address.used" arguments="${sLink},${rLink}"/>
       	 		</em>	
       	 		<% 
       	 	}	
       	%>	
       	
    </label>
    
    <label>	
        <span>
            <spring:message code="org"/>
        </span>

		<input type="text" id="<%= NTERController.ORGANIZATION %>" name="<%= NTERController.ORGANIZATION %>" title="${organization}" value="<%= organization  %>" class="textfield">
       	 	
    </label>
	
    <label>
        <span>
            <spring:message code='password'/>
        </span>
		
		<input type="password" id="<%= NTERController.PASSWORD %>" name="<%= NTERController.PASSWORD %>" title="${passwordGuidance}"  class="textfield required password" pattern="<%= passwordPattern %>" required>
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
       	 			
		%>
    </label>
    
    <label>
    <input type="checkbox" id="<%= NTERController.TERMS_AND_CONDITIONS %>" name="<%= NTERController.TERMS_AND_CONDITIONS %>" value="<%= NTERController.AGREED %>" required >
      <strong><spring:message code="i.agree"/> <a target="_blank" id="TosLink" href=<%= termsAndConditions %>><spring:message code="terms.and.conditions"/></a></strong><br>

       	<%	
       	 	if (!isTermsAndConditions) {
       	 		%>
       	 		<em class="error">
       	 		<spring:message code="error.terms.conditions"/>
       	 		</em>
  				<% 
       	 	}
       	 		
		%>

	</label>
    <input type="submit" class="submit" value="${createAccount}">
    <span class="signup">
        <spring:message code="already.member"/>
        <a href="<%= NTERController.buildSSOLink(referer) %>">
            <spring:message code="sign.in"/>
        </a>
    </span>

    
</form>

</div>

</body>
</html>