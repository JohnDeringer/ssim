<%@ taglib uri="urn:mace:shibboleth:2.0:idp:ui" prefix="idpui" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0.1 Transitional//EN"
	"http://www.w3.org/TR/html4/loose.dtd">
	
<html>
  <head>
    <title>Sign In</title>
	<link rel="Shortcut Icon" href="http://static.nterlearning.org/favicon.ico" />
    <link rel="stylesheet" type="text/css" href="/nter-user-mgmt-webapp/resources/loginstyle.css"/>
	<meta charset="utf-8" />

	<script type="text/javascript">

	var _gaq = _gaq || [];
	_gaq.push(['_setAccount', 'UA-24904955-1']);
	_gaq.push(['_setDomainName', '.nterlearning.org']);
	_gaq.push(['_trackPageview']);

	(function() { var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true; ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js'; var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s); })();

	</script>
  </head>

  <body>

<%   
	if(request.getAttribute("ShibbolethIdPSession") != null)
	{
		%>  <script type="text/javascript">window.history.back();</script>   <%
	}
%>  
  
	<section>
		<h1>Sign In</h1>
		<h2>You've been asked to sign in to <idpui:serviceName/></h2>
		
           <% if ("true".equals(request.getAttribute("loginFailed"))) { %>
              <p><font color="red"> Credentials not recognized. </font> </p>
           <% } %>
           <% if(request.getAttribute("actionUrl") != null){ %>
             <form action="<%=request.getAttribute("actionUrl")%>" method="post">
           <% }else{ %>
             <form action="j_security_check" method="post">
           <% } %>
		   
			<label for="IDToken1">
				<span>Email:</span>
				<input id="email" name="j_username" type="text" class="textfield" autofocus required />
			</label>
			<label for="IDToken1">
				<span>Password:</span>
				<input name="j_password" type="password" value="" class="textfield"/>
			</label>
			<!-- <button type="submit" value="Login" >Log In</button> -->
			<input type="submit" class="submit" value="Log In">
			
			<a class="forgotpw" href="/nter-user-mgmt-webapp/password?referer=https://<idpui:serviceName/>">Forgot password?</a>
			<span class="signup">Not yet a member? <a href="/nter-user-mgmt-webapp/signup?referer=https://<idpui:serviceName/>">Sign up now!</a></span>
		</form>
	</section>
	
	<!--
	<section>
		<h1>Or sign in with your account from one of our partners:</h1>
		<a href="#">How does this work?</a>
		<ul class="openid-options">
			<li><a href="#"><img src="/nter-user-mgmt-webapp/resources/images/open-id-icons/aol.png" alt="AOL" /></a></li>
			<li><a href="#"><img src="/nter-user-mgmt-webapp/resources/images/open-id-icons/google.png" alt="Google" /></a></li>
			<li><a href="#"><img src="/nter-user-mgmt-webapp/resources/images/open-id-icons/paypal.png" alt="PayPal" /></a></li>
			<li><a href="#"><img src="/nter-user-mgmt-webapp/resources/images/open-id-icons/verisign.png" alt="Verisign" /></a></li>
			<li><a href="#"><img src="/nter-user-mgmt-webapp/resources/images/open-id-icons/yahoo.png" alt="Yahoo" /></a></li>
		</ul>
	</section>
	-->
 	
	<script>
		document.createElement('section');
		if(!("autofocus" in document.createElement('input'))){document.getElementById('email').focus();}
	</script>
	
  </body>
</html>

