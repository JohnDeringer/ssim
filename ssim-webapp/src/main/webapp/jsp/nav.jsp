<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<header>
	<div class="logo">SSIM Data Repository</div>
	<nav>
		<ul class="nav">
			<c:choose>
				<c:when test="${param.page == 'search'}">
					<li class="current"><a>Search</a></li>
				</c:when>
				<c:otherwise>
					<li><a href="search">Search</a></li>
				</c:otherwise>
			</c:choose>
			<c:choose>
				<c:when test="${param.page == 'video'}">
					<li class="current"><a>Upload Video</a></li>
				</c:when>
				<c:otherwise>
					<li><a href="video">Upload Video</a></li>
				</c:otherwise>
			</c:choose>
			<c:choose>
				<c:when test="${param.page == 'interview'}">
					<li class="current"><a>Upload Interview</a></li>
				</c:when>
				<c:otherwise>
					<li><a href="interview">Upload Interview</a></li>
				</c:otherwise>
			</c:choose>
			<c:choose>
				<c:when test="${param.page == 'documentation'}">
					<li class="current"><a>Documentation</a></li>
				</c:when>
				<c:otherwise>
					<li><a href="documentation">Documentation</a></li>
				</c:otherwise>
			</c:choose>
		</ul>
	</nav>
</header>