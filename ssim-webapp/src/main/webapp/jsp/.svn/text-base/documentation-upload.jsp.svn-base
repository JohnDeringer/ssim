<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:include page="head.jsp">
	<jsp:param name="title" value="Upload Documentation"/>
</jsp:include>

<body>
	<div class="wrapper">
		<jsp:include page="nav.jsp">
			<jsp:param name="page" value="documentation-upload"/>
		</jsp:include>

		<h1>Upload Documentation</h1>

		<form action="documentation" method="post">
			<div class="form-row">
				<label for="document">File</label>
				<input type="file" name="document" id="document" />
			</div>

			<div class="form-row">
				<label for="description">Description</label>
				<textarea name="description" id="description"></textarea>
			</div>

			<div class="actions">
				<button type="submit">Upload</button>
				<button type="button">Cancel</button>
			</div>
		</form>
	</div>
</body>
</html>