<%@page import="edu.internet2.middleware.shibboleth.common.profile.AbstractErrorHandler"%>
<%@ taglib uri="urn:mace:shibboleth:2.0:idp:ui" prefix="idpui" %>

<html>
<body>

    <%
        Throwable error = (Throwable) request.getAttribute(AbstractErrorHandler.ERROR_KEY);
 
		if(error != null && error.getMessage() == "Error decoding authentication request message")
		{  
			%>  <script type="text/javascript">window.history.back();</script>   <%
		}   
		else
		{ 
			%>
			
			<h3>Shibboleth ERROR</h3>
			<p>
				An error occurred while processing your request.  Please contact your helpdesk or
				user ID office for assistance.
			</p>
			<p>
			   This service requires cookies.  Please ensure that they are enabled and try your
			   going back to your desired resource and trying to login again.
			</p>
			<p>
			   Use of your browser's back button may cause specific errors that can be resolved by
			   going back to your desired resource and trying to login again.
			</p>
			<p>
			   If you think you were sent here in error,
			   please contact <idpui:serviceContact>technical support</idpui:serviceContact>
			</p>
								
			<%
			if(error != null){
				org.owasp.esapi.Encoder esapiEncoder = org.owasp.esapi.ESAPI.encoder();
				%>	<strong>Error Message: <%= esapiEncoder.encodeForHTML(error.getMessage()) %></strong>

			<%
			}
		}
   %>


</body>
</html>
