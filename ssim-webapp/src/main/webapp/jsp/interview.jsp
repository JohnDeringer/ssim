<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:choose>
	<c:when test="${!empty interview}">
		<c:set var="page" value="" />
		<c:set var="title" value="Edit an Interview" />
	</c:when>
	<c:otherwise>
		<c:set var="page" value="interview" />
		<c:set var="title" value="Add an Interview" />
	</c:otherwise>
</c:choose>
<jsp:include page="head.jsp">
	<jsp:param name="title" value="${title}"/>
</jsp:include>

<body data-templates="<c:url value="/static/templates/upload-templates.html" />">
	<div class="wrapper">
		<jsp:include page="nav.jsp">
			<jsp:param name="page" value="${page}"/>
		</jsp:include>

		<h1>${title}</h1>
		<form id="upload" class="page upload-form">
			<div class="left-col">
				<section>
					<h2>Interview</h2>
					<div class="form-row">
						<fieldset class="multi-section" data-multi-template="upload-template">
							<legend>Transcript or Notes</legend>
							<div class="rows">
								<div class="multi">
									<input type="file" name="interview-transcript-0" id="interview-transcript-0" class="main-file" />
								</div>
								<button id="add-stimulated-recall-interview" type="button" class="add-file add-more">Add another file</button>
							</div>
						</fieldset>
					</div>

					<div class="form-row">
						<fieldset>
							<legend>Type of Interview</legend>
							<div class="rows">
								<div class="radio-row">
									<input type="radio" name="data-descriptor" id="data-descriptor-cta" value="cta" />
									<label for="data-descriptor-cta">Cognitive Task Analysis</label>
								</div>
								<div class="radio-row">
									<input type="radio" name="data-descriptor" id="data-descriptor-ethnographic" value="ethnographic" />
									<label for="data-descriptor-ethnographic">Ethnographic</label>
								</div>
							</div>
						</fieldset>
					</div>
					
					<div class="form-row">
						<label for="corpus">Corpus</label>
						<input type="text" class="autocomplete" data-autocomplete-source="/ssim-repository-service-1.0.0/rest/encounterAPI/corpus" data-autocomplete-save="/ssim-repository-service-1.0.0/rest/encounterAPI/corpus" data-autocomplete-param="corpus" name="corpus" id="corpus" />
					</div>

					<h3>Related to previously uploaded video:</h3>
					<ul id="related-video-display" class="subitem-list"></ul>
					<button type="button" id="related-video-search" class="add-more">Find a video...</button>
				</section>

				<section>
					<h2>Collection Metadata</h2>
					<div class="form-row">
						<label for="collection-date">Collection Date</label>
						<input type="date" name="collection-date" id="collection-date" placeholder="MM/DD/YYYY" class="date" />
					</div>
					<div class="form-row">
						<label for="collection-time">Collection Time</label>
						<input type="text" name="collection-time" id="collection-time" placeholder="HH:MM:SS AM/PM" class="time" />
					</div>
					<div class="form-row">
						<label for="geographic-location">Geographic Location</label>
						<input type="text" class="autocomplete" data-autocomplete-source="/ssim-repository-service-1.0.0/rest/encounterAPI/geoLocations" data-autocomplete-save="/ssim-repository-service-1.0.0/rest/encounterAPI/geoLocation" data-autocomplete-param="geographicLocation" name="geographic-location" id="geographic-location" />
					</div>
					<div class="form-row">
						<label for="site-location">Site</label>
						<input type="text" class="autocomplete" data-autocomplete-source="/ssim-repository-service-1.0.0/rest/encounterAPI/sites" data-autocomplete-save="/ssim-repository-service-1.0.0/rest/encounterAPI/site" data-autocomplete-param="site" name="site-location" id="site-location" />
					</div>
					<div class="form-row">
						<label for="collector">Collector</label>
						<input type="text" class="autocomplete" data-autocomplete-source="/ssim-repository-service-1.0.0/rest/encounterAPI/collectors" data-autocomplete-save="/ssim-repository-service-1.0.0/rest/encounterAPI/collector" data-autocomplete-param="collectorEntity" name="collector" id="collector" />
					</div>
				</section>

				<section>
					<h2>Encounter Metadata</h2>
					<div class="list-row">
						<ul id="encounter-list" class="subitem-list"></ul>
						<button type="button" class="add-encounter-metadata add-more no-encounters">Enter encounter metadata...</button>
						<button type="button" class="add-encounter-metadata add-more has-encounters" style="display:none;">Add another encounter for this interview...</button>
						<input type="hidden" id="encounter-validation-placeholder" class="encounter-placeholder" value="0" />
					</div>
				</section>
			</div>

			<div class="right-col">
				<section>
					<h2><label for="comments">Comments</label></h2>
					<textarea name="comments" id="comments" class="no-label"></textarea>
				</section>
			</div>

			<div class="actions">
				<button type="submit">Save</button>
			</div>
		</form>
	</div>
</body>
</html>