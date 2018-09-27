package eu.cessda.cvmanager.service.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

import java.util.List;

import org.gesis.stardat.ddiflatdb.client.DDIStore;
import org.gesis.stardat.ddiflatdb.client.RestClient;
import org.gesis.stardat.entity.CVConcept;
import org.gesis.stardat.entity.CVScheme;
import org.gesis.stardat.entity.DDIElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import eu.cessda.cvmanager.service.ConfigurationService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
public class CVController {

	@Autowired
	ConfigurationService configService;

	private RestClient restClient;

	@ResponseStatus(code = HttpStatus.OK)
	@RequestMapping(value = "/v1/suggest/Vocabulary/{vocabulary}/version/{version}/language/{language}/limit/{limit}/query/{query}", produces = APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.GET)
	@ApiOperation(value = "Get list of suggestions by query")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "No code found") })

	public CodeList getSuggestionByQuery(@PathVariable String vocabulary, @PathVariable String version,
			@PathVariable String language, @PathVariable String query, @PathVariable Integer limit) {

		restClient = new RestClient(configService.getDdiflatdbRestUrl());
		List<DDIStore> searchResult = restClient.getElementListByContent(DDIElement.CVCONCEPT, query);

		int count = limit;
		CodeList codeList = new CodeList();

		for (DDIStore store : searchResult) {
			if (count <= 0)
				break;

			CVConcept concept = new CVConcept(store);

			// TODO needs to be code?
			if (concept.getPrefLabelByLanguage("en").toLowerCase().contains(query.toLowerCase())) {
				Code code = new Code();

				ContentType url = new ContentType(ContentType.TYPE_URI);
				url.setValue("http://lod.cessda.eu/" + store.getElementId());
				code.setUrl(url);

				ContentType theCode = new ContentType(ContentType.TYPE_LITERAL);
				theCode.setValue(concept.getNotation());
				code.setCode(theCode);

				theCode = new ContentType(ContentType.TYPE_LITERAL);
				theCode.setValue(concept.getPrefLabelByLanguage("en"));
				code.setPrefLabel(theCode);

				theCode = new ContentType(ContentType.TYPE_LITERAL);
				theCode.setValue(concept.getPrefLabelByLanguage(language));
				code.setLanguagePrefLabel(theCode);

				theCode = new ContentType(ContentType.TYPE_LITERAL);
				theCode.setValue(concept.getDescriptionByLanguage("en"));
				code.setDescription(theCode);

				theCode = new ContentType(ContentType.TYPE_LITERAL);
				theCode.setValue(language);
				code.setLanguage(theCode);

				codeList.getListOfCodes().add(code);
				count--;
			}
		}

		return codeList;

	}

	@ResponseStatus(code = HttpStatus.OK)
	@RequestMapping(value = "/v1/Vocabularies/agency/{agency}/version/{version}/language/{language}", produces = APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.GET)
	@ApiOperation(value = "Get list of suggestions by agency, version or/and language")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "No cv  found") })

	public CVSchemeList getVocabularies(@PathVariable String agency, @PathVariable String version,
			@PathVariable String language) {

		restClient = new RestClient(configService.getDdiflatdbRestUrl());
		List<DDIStore> searchResult = restClient.getStudyList(DDIElement.CVSCHEME);
		CVSchemeList schemeList = new CVSchemeList();

		for (DDIStore store : searchResult) {
			CVScheme scheme = new CVScheme(store);
			schemeList.getListOfCVSchemes().add(scheme);
		}

		return schemeList;
	}

	@ResponseStatus(code = HttpStatus.OK)
	@RequestMapping(value = "/v1/Codes/vocabulary/{vocabulary}/version/{version}/language/{language}", produces = APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.GET)
	@ApiOperation(value = "Get list of suggestions by agency, version or/and language")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "No cv  found") })

	public CodeList getCodes(@PathVariable String vocabulary, @PathVariable String version,
			@PathVariable String language) {

		restClient = new RestClient(configService.getDdiflatdbRestUrl());
		List<DDIStore> searchResult = restClient.getStudyList(DDIElement.CVCONCEPT);
		CodeList codeList = new CodeList();

		for (DDIStore store : searchResult) {

			CVConcept concept = new CVConcept(store);

			Code code = new Code();

			ContentType url = new ContentType(ContentType.TYPE_URI);
			url.setValue("http://lod.cessda.eu/" + store.getElementId());
			code.setUrl(url);

			ContentType theCode = new ContentType(ContentType.TYPE_LITERAL);
			theCode.setValue(concept.getNotation());
			code.setCode(theCode);

			theCode = new ContentType(ContentType.TYPE_LITERAL);
			theCode.setValue(concept.getPrefLabelByLanguage("en"));
			code.setPrefLabel(theCode);

			theCode = new ContentType(ContentType.TYPE_LITERAL);
			theCode.setValue(concept.getPrefLabelByLanguage(language));
			code.setLanguagePrefLabel(theCode);

			theCode = new ContentType(ContentType.TYPE_LITERAL);
			theCode.setValue(concept.getDescriptionByLanguage("en"));
			code.setDescription(theCode);

			theCode = new ContentType(ContentType.TYPE_LITERAL);
			theCode.setValue(language);
			code.setLanguage(theCode);

			codeList.getListOfCodes().add(code);

		}

		return codeList;
	}

}
