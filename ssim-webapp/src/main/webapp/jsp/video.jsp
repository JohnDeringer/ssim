<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:choose>
	<c:when test="${!empty video}">
		<c:set var="page" value="" />
		<c:set var="title" value="Edit a Videoed Encounter" />
	</c:when>
	<c:otherwise>
		<c:set var="page" value="video" />
		<c:set var="title" value="Add a Videoed Encounter" />
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
		<form id="upload" class="page upload-form validate">
			<div class="left-col">
				<section>
					<h2>Video</h2>
					<div class="form-row">
						<label for="original-video">Original Video</label>
						<input type="file" name="original-video" id="original-video" class="main-file" />
					</div>
					<div class="form-row">
						<label for="converted-video">Converted Video</label>
						<input type="file" name="converted-video" id="converted-video" class="main-file" />
					</div>
					<div class="form-row">
						<label for="video-audio">Audio Channel</label>
						<input type="file" name="video-audio" id="video-audio" />
					</div>
					<div class="form-row">
						<label for="alternate-audio">Alternate Audio Source</label>
						<input type="file" name="alternate-audio" id="alternate-audio" />
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
						<input type="date" name="collection-date" id="collection-date" class="date" placeholder="MM/DD/YYYY" />
					</div>
					<div class="form-row">
						<label for="collection-time">Collection Time</label>
						<input type="text" name="collection-time" id="collection-time" class="time" placeholder="HH:MM:SS AM/PM" />
					</div>
					<div class="form-row">
						<label for="general-collection-time">Daylight Conditions</label>
						<select name="general-collection-time" id="general-collection-time">
							<option value=""></option>
							<option value="light">Light</option>
							<option value="dark">Dark</option>
							<option value="dusk">Dusk</option>
						</select>
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
						<label for="video-quality">Video Quality</label>
						<select name="video-quality" id="video-quality">
							<option value=""></option>
							<option value="good">Good</option>
							<option value="ok">Okay</option>
							<option value="partly-usable">Partly Usable</option>
							<option value="bad">Bad</option>
						</select>
					</div>
					<div class="form-row">
						<label for="audio-quality">Audio Quality</label>
						<select name="audio-quality" id="audio-quality">
							<option value=""></option>
							<option value="good">Good</option>
							<option value="ok">Okay</option>
							<option value="partly-usable">Partly Usable</option>
							<option value="bad">Bad</option>
						</select>
					</div>
					<div class="form-row">
						<label for="collector">Collector</label>
						<input type="text" class="autocomplete" data-autocomplete-source="/ssim-repository-service-1.0.0/rest/encounterAPI/collectors" data-autocomplete-save="/ssim-repository-service-1.0.0/rest/encounterAPI/collector" data-autocomplete-param="collectorEntity" name="collector" id="collector" />
					</div>
					<input type="hidden" name="data-descriptor" value="video" />
				</section>

				<section>
					<h2>Encounter Metadata</h2>
					<div class="list-row">
						<ul id="encounter-list" class="subitem-list"></ul>
						<button type="button" class="add-encounter-metadata add-more no-encounters">Enter encounter metadata...</button>
						<button type="button" class="add-encounter-metadata add-more has-encounters" style="display:none;">Add another encounter for this video...</button>
						<input type="hidden" id="encounter-validation-placeholder" class="encounter-placeholder" />
					</div>
				</section>
			</div>

			<div class="right-col">
				<section>
					<h2>Transcripts</h2>
					<h3>Orthographic</h3>
					<div class="form-row">
						<label for="elan-transcript-orthographic">Native ELAN</label>
						<input type="file" name="elan-transcript-orthographic" id="elan-transcript-orthographic" class="main-file" />
					</div>
					<div class="form-row">
						<label for="traditional-transcript-orthographic">Traditional</label>
						<input type="file" name="traditional-transcript-orthographic" id="traditional-transcript-orthographic" class="main-file" />
					</div>
					<div class="form-row">
						<label for="tab-delimited-transcript-orthographic">Tab-Delimited</label>
						<input type="file" name="tab-delimited-transcript-orthographic" id="tab-delimited-transcript-orthographic" class="main-file" />
					</div>

					<h3>CA-Style</h3>
					<div class="form-row">
						<label for="elan-transcript-ca">Native ELAN</label>
						<input type="file" name="elan-transcript-ca" id="elan-transcript-ca" class="main-file" />
					</div>
					<div class="form-row">
						<label for="traditional-transcript-ca">Traditional</label>
						<input type="file" name="traditional-transcript-ca" id="traditional-transcript-ca" class="main-file" />
					</div>
					<div class="form-row">
						<label for="tab-delimited-transcript-ca">Tab-Delimited</label>
						<input type="file" name="tab-delimited-transcript-ca" id="tab-delimited-transcript-ca" class="main-file" />
					</div>
					<div class="form-row">
						<label for="tab-delimited-transcript-ca">Other Format</label>
						<input type="file" name="other-transcript-ca" id="other-transcript-ca" class="main-file" />
					</div>
				</section>

				<section>
					<h2>Stimulated Recall Interview Transcript/Notes</h2>
					<div class="multi-section" data-multi-template="stimulated-recall-interview-template" data-grouped-multi-name="stimulated-recall-interviews">
						<div class="multi">
							<div class="form-row">
								<label for="stimulated-recall-transcript-0">File</label>
								<input type="file" name="stimulated-recall-transcript-0" id="stimulated-recall-transcript-0" />
							</div>
							<div class="form-row">
								<label for="stimulated-recall-description-0">Description</label>
								<textarea name="stimulated-recall-description-0" id="stimulated-recall-description-0"></textarea>
							</div>
						</div>
						<button type="button" id="add-stimulated-recall-interview" class="add-more">Add another file</button>
					</div>
				</section>

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