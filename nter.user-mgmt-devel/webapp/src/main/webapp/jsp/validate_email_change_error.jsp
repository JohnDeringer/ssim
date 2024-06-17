<!DOCTYPE html>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <link rel="Shortcut Icon" href="http://static.nterlearning.org/favicon.ico" />
    <link rel="stylesheet" href="../resources/loginstyle.css" type="text/css"/>
    <title><spring:message code="title.validate.error"/></title>
	<%@ include file="analytics.jsp" %>
</head>
<body>

<div id="main">

		<h1><spring:message code="email.not.validated"/></h1>
		<p><spring:message code="email.validate.error"/></p>
</div>
</body>
</html>