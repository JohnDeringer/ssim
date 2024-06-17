<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:include page="head.jsp">
	<jsp:param name="title" value="Search for Encounters"/>
</jsp:include>

<body data-templates="<c:url value="/static/templates/search-templates.html" />">
	<div class="wrapper">
		<jsp:include page="nav.jsp">
			<jsp:param name="page" value="search"/>
		</jsp:include>
		
		<h1>Search for Encounters</h1>
		<form id="search-form">
			<section class="page search-main">
				<ul class="search-list">
					<li>
						<button type="button" class="add-more search-filename">by file name...</button>
					</li>
					<li>
						<button type="button" class="add-more search-transcripts">by transcript contents...</button>
					</li>
					<li>
						<button type="button" class="add-more search-encounter-metadata">by encounter metadata...</button>
					</li>
					<li>
						<button type="button" class="add-more search-participant-metadata">by participants...</button>
					</li>
				</ul>

				<div class="actions">
					<button type="submit">Search</button>
				</div>
			</section>
		</form>
	</div>
</body>
</html>