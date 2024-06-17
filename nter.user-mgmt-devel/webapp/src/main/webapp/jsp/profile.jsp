<!DOCTYPE html>

	
	<%@ page import="org.nterlearning.usermgmt.webapp.NTERController" %>
	<%@ page import="java.text.MessageFormat" %>
		<%@ page import="java.util.ResourceBundle" %>
	<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
	
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <link rel="Shortcut Icon" href="http://static.nterlearning.org/favicon.ico" />
    <link rel="stylesheet" href="resources/loginstyle.css" type="text/css"/>
    <title><spring:message code="title.profile"/></title>
	<%@ include file="analytics.jsp" %>
</head>
<body aria-live="polite">

<%
// Get attributes
	Boolean isValidEmailAddressComplete = (Boolean) request.getAttribute(NTERController.IS_VALID_EMAIL_ADDRESS_COMPLETE);
	Boolean isPasswordConfirm = (Boolean) request.getAttribute(NTERController.IS_PASSWORD_CONFIRM);
	Boolean isPasswordValid = (Boolean) request.getAttribute(NTERController.IS_PASSWORD_VALID);
	Boolean isEmailInUse = (Boolean) request.getAttribute(NTERController.IS_EMAIL_IN_USE);
	String firstName = (String) request.getAttribute(NTERController.FIRST_NAME);
	String lastName = (String) request.getAttribute(NTERController.LAST_NAME);
	String userName = (String) request.getAttribute(NTERController.USER_NAME);
	String emailAddress = (String) request.getAttribute(NTERController.EMAIL_ADDRESS);
	String oldEmailAddress = (String) request.getAttribute(NTERController.OLD_EMAIL_ADDRESS);	
	String referer = (String) request.getAttribute(NTERController.REFERER);
	Boolean isPasswordForm = (Boolean) request.getAttribute(NTERController.IS_PASSWORD_FORM);
	Boolean isEditForm = (Boolean) request.getAttribute(NTERController.IS_EDIT_FORM);
	Boolean isFirstNameUpdated = (Boolean) request.getAttribute(NTERController.IS_FIRST_NAME_UPDATED);
	Boolean isLastNameUpdated = (Boolean) request.getAttribute(NTERController.IS_LAST_NAME_UPDATED);
	Boolean isEmailAddressUpdated = (Boolean) request.getAttribute(NTERController.IS_EMAIL_ADDRESS_UPDATED);
	Boolean isPasswordUpdated = (Boolean) request.getAttribute(NTERController.IS_PASSWORD_UPDATED);
	Boolean isPasswordChangeError = (Boolean) request.getAttribute(NTERController.IS_PASSWORD_CHANGE_ERROR);
	Boolean isOrgUpdated = (Boolean) request.getAttribute(NTERController.IS_ORG_UPDATED);
 	String validatorImpl = (String) request.getAttribute(NTERController.VALIDATOR);
	String messageBase = (String) request.getAttribute(NTERController.MESSAGE_BASE);
	String organization = (String)request.getAttribute(NTERController.ORGANIZATION);
	String passwordPattern = (String)request.getAttribute(NTERController.PASSWORD_PATTERN);
	
	// Default values if they are null
	if(isValidEmailAddressComplete == null) isValidEmailAddressComplete = true;
	if(isPasswordConfirm == null) isPasswordConfirm = true;
	if(isPasswordValid == null) isPasswordValid = true;
	if(isEmailInUse == null) isEmailInUse = false;
	if(firstName == null) firstName = "";
	if(lastName == null) lastName = "";
	if(userName == null) userName = "";
	if(emailAddress == null) emailAddress = "";
	if(referer == null) referer = "";
	if(oldEmailAddress == null) oldEmailAddress = "";
	if(isPasswordForm == null) isPasswordForm = false;
	if(isEditForm == null) isEditForm = false;
	if(isFirstNameUpdated  == null) isFirstNameUpdated = false;
	if(isLastNameUpdated == null) isLastNameUpdated = false;
	if(isEmailAddressUpdated == null) isEmailAddressUpdated = false;
	if(isPasswordUpdated == null) isPasswordUpdated = false;
	if(isPasswordChangeError == null) isPasswordChangeError = false;
	if (organization == null) organization = "";

	if(referer.equals(""))
	{
		referer = NTERController.getBaseURL(request.getHeader("Referer"));
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
// 	request.setAttribute("emailAddress",emailAddress);
// 	request.setAttribute("oldEmailAddress",oldEmailAddress);

%>
<spring:message code="confirm.password.match" var="errorPasswordConfirm"/>
<spring:message code="edit.account" var="editAccount"/>
<spring:message code="password.same" var="passwordSame"/>
<spring:message code="change.password.button" var="changePassword"/>
<spring:message code="confirm.msg" var="confirmMsg"/>
<spring:message code="return.nter" var="returnMsg"/>
<spring:message code="update.successfull" var="updateSuccess"/>
<spring:message code="signing.in" var="signingIn"/>

<div id="main">

<h1>
    <spring:message code="edit.details"/>
</h1>

<%
	if ((isEditForm) && (isFirstNameUpdated || isLastNameUpdated || isEmailAddressUpdated || isOrgUpdated)) 
	{
		%>
		<em class="error">
		<spring:message code="update.successfull"/>
		</em>
		<% 
		
		if(isEmailAddressUpdated)
		{
			%>
			<em class="error">
			<spring:message code="confirm.msg"/>
			</em>
			<% 
		}
		%>
		<a href="<%= referer %>"><spring:message code="return.nter"/></a>
		<% 
	}	
%>	

<form id="managePersonalForm" class="longform" action="<%= NTERController.ACTION_MANAGEMENT %>" method="POST">

	<input type="hidden" id="<%= NTERController.USER_NAME %>" name="<%= NTERController.USER_NAME %>" value="<%= userName  %>" class="textfield required" required>
	<input type="hidden" id="<%= NTERController.REFERER %>" name="<%= NTERController.REFERER %>"  value="<%= referer  %>" class="textfield required" required>

    <label>    
        <span>
            <spring:message code="first.name"/>
        </span>
        			       				        				
		<input type="text" id="<%= NTERController.FIRST_NAME %>" name="<%= NTERController.FIRST_NAME %>" value="<%= firstName %>" class="textfield required" required autofocus>
		
    </label>
	
    <label>
        <span>
            <spring:message code="last.name"/>
        </span>
		
		<input type="text" id="<%= NTERController.LAST_NAME %>" name="<%= NTERController.LAST_NAME %>" value="<%= lastName  %>" class="textfield required" required>
		
    </label>
	
    <label>
        <span>
            <spring:message code="email.address"/>
        </span>

	    <%
       	 	if (!isValidEmailAddressComplete ||
       	 			isEmailInUse) {
       	 		emailAddress = "";
       	 	}	
       	%>	
		
		<input type="email" id="<%= NTERController.EMAIL_ADDRESS %>" name="<%= NTERController.EMAIL_ADDRESS %>" value="<%= emailAddress  %>" class="textfield required email" required>
		
		<%
			if(isEmailAddressUpdated || (!oldEmailAddress.equals(emailAddress) && oldEmailAddress != ""))
			{
				%>
				<em class="error">
				<spring:message code="email.address.unconfirmed" arguments="${emailAddress},${oldEmailAddress}"/>
				 </em>
				<%				
			}
			else
			{
				%>
				<em class="help">
				<spring:message code="must.be.confirmed"/>
				</em>
				<% 
			}
		%>
			
	    <%
			if (!isValidEmailAddressComplete)
       	 	{
				%>
				<em class="error">
				<spring:message code="email.address.complete"/>
				</em>	   	
       	 		<%  		
       	 	}
       	 	else if (isEmailInUse) {
       	 		%>
				<em class="error">
       	 		<spring:message code="email.in.use"/>
       	 		</em>
       	 		<%
       	 	}	
       	%>	
       	
    </label>
    
    <label>	
        <span>
            <spring:message code="org"/>
        </span>

		<input type="text" id="<%= NTERController.ORGANIZATION %>" name="<%= NTERController.ORGANIZATION %>"  class="textfield" value="<%= organization  %>">
       	 	
    </label>


    	<input id="<%= NTERController.CMD_EDIT_ACCOUNT %>" name="<%= NTERController.CMD_EDIT_ACCOUNT %>" type="submit" class="submit" value="${editAccount}">
        <a href="<%= referer %>" class="secondary">
            <spring:message code="cancel"/>
        </a>
    
</form>

</div>

<br>

<div id="main">

<h1><spring:message code="change.password"/></h1>

<%
	if ((isPasswordForm) && (isPasswordUpdated)) 
	{
		%>
		<p>
			<spring:message code="password.updated"/>
			<a href="<%= referer %>"><spring:message code="return.nter"/></a>
		</p>
		<% 
	}	
%>	

<form id="managePasswordForm" class="longform" action="<%= NTERController.ACTION_MANAGEMENT %>" method="POST">	

	<input type="hidden" id="<%= NTERController.USER_NAME %>"  name="<%= NTERController.USER_NAME %>" value="<%= userName  %>" class="textfield required" required>
	<input type="hidden" id="<%= NTERController.REFERER %>" name="<%= NTERController.REFERER %>" value="<%= referer  %>" class="textfield required" required>
    
    <label>
        <span>
            <spring:message code="password"/>
        </span>
		
		<input type="password" id="<%= NTERController.PASSWORD %>" name="<%= NTERController.PASSWORD %>" title="${passwordGuidance}" class="textfield required password" pattern="<%= passwordPattern %>" required>
		
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
       	 		<em class="error"><spring:message code="confirm.password.match"/></em>
       	 		<% 	
       	 	}			
       	 	else if (isPasswordChangeError) {
	       	 	%>
	   	 		<em class="error"><spring:message code="password.change.error"/></em>
	   	 		<% 
       	 	}
		%>
    </label>

    <input id="<%= NTERController.CMD_PASSWORD_ACCOUNT %>" name="<%= NTERController.CMD_PASSWORD_ACCOUNT %>" type="submit" class="submit" value="${changePassword}">
    <a href="<%= referer %>" class="secondary">
            <spring:message code="cancel"/>
    </a>

    
</form>

</div>

</body>
</html>