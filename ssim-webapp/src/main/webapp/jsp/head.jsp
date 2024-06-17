<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!doctype html>
<html>
<head>
	<meta charset="UTF-8">
	<title><c:out value="${param.title}" /></title>
	<link rel="stylesheet" href="<c:url value="/static/css/jquery-ui-custom/jquery-ui-1.8.23.custom.css" />" />
	<link rel="stylesheet" href="<c:url value="/static/css/layout.css" />" />
	<link rel="stylesheet" href="<c:url value="/static/css/style.css" />" />
	<link href='http://fonts.googleapis.com/css?family=Yanone+Kaffeesatz:400,700' rel='stylesheet' type='text/css'>
	<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.8.1/jquery.min.js"></script>
	<script src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.23/jquery-ui.min.js"></script>
	<script src="<c:url value="/static/js/jquery.fileDownload/jquery.fileDownload.js" />"></script>
	<script src="<c:url value="/static/js/uploadify/jquery.uploadify.min.js" />"></script>
	<script src="<c:url value="/static/js/validation/dist/jquery.validate.min.js" />"></script>
	<script src="<c:url value="/static/js/interaction.js" />"></script>
</head>