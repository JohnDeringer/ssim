package com.sri.ssim.service;

import com.sri.ssim.persistence.*;
import com.sri.ssim.rest.ArtifactsType;
import com.sri.ssim.rest.EncounterResponse;
import com.sri.ssim.rest.FileRequest;
import com.sri.ssim.rest.SearchType;
import com.sri.ssim.schema.EncounterFile;

import org.apache.cxf.jaxrs.ext.multipart.Attachment;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 8/28/12
 */
public interface EncounterRestInterface {

    /**
     * Creates a new User
     * @param map map containing user credentials
     * @return javax.ws.rs.core.Response object
     */
    @POST
    @Path("/createUser")
    @Consumes({ MediaType.APPLICATION_FORM_URLENCODED })
    @Produces({ MediaType.APPLICATION_FORM_URLENCODED })
    Response createUser(MultivaluedMap<String, String> map);

    /**
     * Handles login
     * @param map containing name/value attributes of the form
     * @return javax.ws.rs.core.Response object
     */
    @POST
    @Path("/login")
    @Consumes({ MediaType.APPLICATION_FORM_URLENCODED })
    @Produces({ MediaType.APPLICATION_FORM_URLENCODED })
    Response login(MultivaluedMap<String, String> map);

    /**
     * Handles file-upload
     * @param attachments File(s) to be uploaded
     * @param request HttpServletRequest object
     * @return javax.ws.rs.core.Response object
     */
    @POST
    @Path("/uploadFile")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    Response uploadFile(List<Attachment> attachments,
                        @Context HttpServletRequest request);

    /**
     * Add a collection of Encounters
     * @param artifacts A group of objects representing one or more encounters
     * @return javax.ws.rs.core.Response object
     */
    @POST
    @Path("/createEncounters")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Produces({ MediaType.APPLICATION_FORM_URLENCODED })
    Response createEncounters(ArtifactsType artifacts);

    /**
     * Retrieve all Collectors
     * @return A List of Collectors
     */
    @GET
    @Path("/allCollectors")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    List<Collector> getCollectors();

    /**
     * Retrieve all Collectors that match the 'LIKE*' comparison
     * @param root A String to use for a 'LIKE%' search
     * @return A List of Collector objects
     */
    @GET
    @Path("/collectors")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    List<Collector> getCollectors(@QueryParam("root") String root);

    /**
     * Add a new collector entity
     * @param collectorEntity, Populate the value attribute, id will be populated by the database
     * @return The newly created Collector Entity
     */
    @POST
    @Path("/collector")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    Collector addCollector(Collector collectorEntity);

    /**
     * Retrieve an Encounter by name
     * @param name The encounter name
     * @return An ArtifactsType object
     */
    @GET
    @Path("/encounter")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    ArtifactsType getEncounter(@QueryParam("name") String name);

    /**
     * Retrieve an Encounter by name
     * @param filename The filename
     * @return An ArtifactsType object
     */
    @GET
    @Path("/encounters")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    ArtifactsType getEncounters(@QueryParam("name") String filename);

    /**
     * Retrieve all EncounterQuality entities
     * @return A List of EncounterQuality objects
     */
    @GET
    @Path("/allEncounterQualities")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    List<EncounterQuality> getEncounterQualities();

    /**
     * Retrieve all EncounterQuality that match the 'LIKE*' comparison
     * @param root A String to use for a 'LIKE%' search
     * @return A List of EncounterQuality objects
     */
    @GET
    @Path("/encounterQualities")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    List<EncounterQuality> getEncounterQualities(@QueryParam("root") String root);

    /**
     * Add a new EncounterQuality entity
     * @param encounterQuality, Populate the value attribute, id will be populated by the database
     * @return The newly created EncounterQuality Entity
     */
    @POST
    @Path("/encounterQuality")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    EncounterQuality addEncounterQuality(EncounterQuality encounterQuality);

    /**
     * Retrieve all EncounterReasons
     * @return A List of EncounterReasons
     */
    @GET
    @Path("/allEncounterReasons")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    List<EncounterReason> getEncounterReasons();

    /**
     * Retrieve all EncounterReasons that match the 'LIKE*' comparison
     * @param root A String to use for a 'LIKE%' search
     * @return A List of EncounterReason objects
     */
    @GET
    @Path("/encounterReasons")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    List<EncounterReason> getEncounterReasons(@QueryParam("root") String root);

    /**
     * Add a new EncounterReason entity
     * @param encounterReason, Populate the value attribute, id will be populated by the database
     * @return The newly created EncounterReason Entity
     */
    @POST
    @Path("/encounterReason")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    EncounterReason addEncounterReason(EncounterReason encounterReason);

    /**
     * Retrieve all Ethnicities
     * @return A List of Ethnicity entity objects
     */
    @GET
    @Path("/allEthnicities")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    List<Ethnicity> getEthnicities();

    /**
     * Retrieve all Ethnicities that match the 'LIKE*' comparison
     * @param root A String to use for a 'LIKE%' search
     * @return A List of Ethnicity objects
     */
    @GET
    @Path("/ethnicities")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    List<Ethnicity> getEthnicities(@QueryParam("root") String root);

    /**
     * Add a new Ethnicity entity
     * @param ethnicity, Populate the value attribute, id will be populated by the database
     * @return The newly created Ethnicity Entity
     */
    @POST
    @Path("/ethnicity")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    Ethnicity addEthnicityEntity(Ethnicity ethnicity);

    /**
     * Retrieve all GeneralTime entities
     * @return A List of GeneralTime objects
     */
    @GET
    @Path("/allGeneralTimes")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    List<GeneralTime> getGeneralTimes();

    /**
     * Retrieve all GeneralTime entities that match the 'LIKE*' comparison
     * @param root A String to use for a 'LIKE%' search
     * @return A List of GeneralTime objects
     */
    @GET
    @Path("/generalTimes")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    List<GeneralTime> getGeneralTimes(@QueryParam("root") String root);

    /**
     * Add a new generalTime entity
     * @param generalTime, Populate the value attribute, id will be populated by the database
     * @return The newly created GeneralTime Entity
     */
    @POST
    @Path("/generalTime")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    GeneralTime addGeneralTime(GeneralTime generalTime);

    /**
     * Retrieve all GeographicLocation entities
     * @return A List of GeographicLocation objects
     */
    @GET
    @Path("/allGeoLocations")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    List<GeographicLocation> getGeoLocations();

    /**
     * Retrieve all GeographicLocation entities that match the 'LIKE*' comparison
     * @param root A String to use for a 'LIKE%' search
     * @return A List of GeographicLocation objects
     */
    @GET
    @Path("/geoLocations")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    List<GeographicLocation> getGeoLocations(@QueryParam("root") String root);

    /**
     * Add a new GeographicLocation entity
     * @param geographicLocation, Populate the value attribute, id will be populated by the database
     * @return The newly created GeographicLocation Entity
     */
    @POST
    @Path("/geoLocation")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    GeographicLocation addGeoLocation(GeographicLocation geographicLocation);

    /**
     * Retrieve an Interview by name
     * @param name The interview name
     * @return An Interview object
     */
    @GET
    @Path("/interview")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    Interview getInterview(@QueryParam("name") String name);

    /**
     * Retrieve all languages
     * @return A List of Language objects
     */
    @GET
    @Path("/allLanguages")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    List<Language> getLanguages();

    /**
     * Retrieve all Languages that match the 'LIKE*' comparison
     * @param root A String to use for a 'LIKE%' search
     * @return A List of Language objects
     */
    @GET
    @Path("/languages")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    List<Language> getLanguages(@QueryParam("root") String root);

    /**
     * Add a new Language entity
     * @param language, Populate the value attribute, id will be populated by the database
     * @return The newly created Language Entity
     */
    @POST
    @Path("/language")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    Language addLanguage(Language language);

    /**
     * Retrieve all Ranks
     * @return A List of Rank objects
     */
    @GET
    @Path("/allRanks")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    List<Rank> getRanks();

    /**
     * Retrieve all Ranks that match the 'LIKE*' comparison
     * @param root A String to use for a 'LIKE%' search
     * @return A List of Rank objects
     */
    @GET
    @Path("/ranks")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    List<Rank> getRanks(@QueryParam("root") String root);

    /**
     * Add a new rank entity
     * @param rankEntity, Populate the value attribute, id will be populated by the database
     * @return The newly created Rank Entity
     */
    @POST
    @Path("/rank")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    Rank addRank(Rank rankEntity);

    /**
     * Retrieve all Roles
     * @return A List of Role objects
     */
    @GET
    @Path("/allRoles")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    List<Role> getRoles();

    /**
     * Retrieve all Roles that match the 'LIKE*' comparison
     * @param root A String to use for a 'LIKE%' search
     * @return A List of Role objects
     */
    @GET
    @Path("/roles")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    List<Role> getRoles(@QueryParam("root") String root);

    /**
     * Add a new role entity
     * @param role, Populate the value attribute, id will be populated by the database
     * @return The newly created Role Entity
     */
    @POST
    @Path("/role")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    Role addRole(Role role);

    /**
     * Retrieve all Sites
     * @return A List of Site objects
     */
    @GET
    @Path("/allSites")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    List<Site> getSites();

    /**
     * Retrieve all Sites that match the 'LIKE*' comparison
     * @param root A String to use for a 'LIKE%' search
     * @return A List of Site objects
     */
    @GET
    @Path("/sites")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    List<Site> getSites(@QueryParam("root") String root);

    /**
     * Add a new site entity
     * @param site, Populate the value attribute, id will be populated by the database
     * @return The newly created Site Entity
     */
    @POST
    @Path("/site")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    Site addSite(Site site);

    /**
     * Retrieve all Corpus
     * @return A List of Corpus objects
     */
    @GET
    @Path("/allCorpus")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    List<Corpus> getCorpus();

    /**
     * Retrieve all Corpus that match the 'LIKE*' comparison
     * @param root A String to use for a 'LIKE%' search
     * @return A List of Corpus objects
     */
    @GET
    @Path("/corpus")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    List<Corpus> getCorpus(@QueryParam("root") String root);

    /**
     * Add a new Corpus entity
     * @param corpus, Populate the value attribute, id will be populated by the database
     * @return The newly created Corpus Entity
     */
    @POST
    @Path("/corpus")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    Corpus addCorpus(Corpus corpus);

    /**
     * Retrieve a list of fileName that matches a *like* query in the name parameter
     * @param name A String to use for a '%LIKE%' search
     * @param limit An integer value that will limit the number of items returned
     * @return A List of String objects containing fileNames
     */
    @GET
    @Path("/fileNames")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    List<EncounterFile> getFileNames(
            @QueryParam("name") String name, @QueryParam("limit") int limit);

    /**
     * Search for Encounters
     * @param search, Search criteria object
     * @return A list of encounter objects
     */
    @POST
    @Path("/searchEncounters")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    Set<EncounterResponse> searchEncounters(SearchType search);

    @POST
    @Path("/fileRequest")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response fileRequest(FileRequest fileRequest);

    @GET
    @Path("/encFileRequest")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response encFileRequest(@QueryParam("file") String[] fileNames,
                                   @QueryParam("format") String[] formats);

    /**
     * Update a collection of Encounters
     * @param artifacts A group of objects representing one or more encounters
     * @return javax.ws.rs.core.Response object
     */
     @POST
     @Path("/updateEncounters")
     @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
     @Produces({ MediaType.APPLICATION_FORM_URLENCODED })
     Response updateEncounters(ArtifactsType artifacts);

    /**
     * Retrieve all AudioQuality entities
     * @return A List of AudioQuality objects
     */
    @GET
    @Path("/allAudioQuality")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    List<AudioQuality> getAudioQuality();

    /**
     * Retrieve all VideoQuality entities
     * @return A List of VideoQuality objects
     */
    @GET
    @Path("/allVideoQuality")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    List<VideoQuality> getVideoQuality();


}
