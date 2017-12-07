package eu.cessda.cvmanager.service.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

import java.util.ArrayList;
import java.util.List;

import org.gesis.stardat.ddiflatdb.client.DDIStore;
import org.gesis.stardat.ddiflatdb.client.RestClient;
import org.gesis.stardat.entity.CVConcept;
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
	@RequestMapping(value = "/suggest/{dataset}/{language}/{query}/{limit}", produces = APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.GET)
	@ApiOperation(value = "Get list of suggestions by query")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "No code found") })

	public List<Code> getSuggestionByQuery(@PathVariable String dataset, @PathVariable String language,
			@PathVariable String query, @PathVariable Integer limit) {

		restClient = new RestClient(configService.getDdiflatdbRestUrl());
		List<DDIStore> searchResult = restClient.getElementListByContent(DDIElement.CVCONCEPT, query);

		int count = limit;
		List<Code> list = new ArrayList<Code>();
		for (DDIStore store : searchResult) {
			if (count <= 0)
				break;

			CVConcept concept = new CVConcept(store);

			if (concept.getPrefLabelByLanguage("en").contains(query)) {
				Code code = new Code();

				code.setLanguage(language);
				code.setCode(concept.getPrefLabelByLanguage("en"));
				code.setPrefLabel(concept.getPrefLabelByLanguage("en"));
				code.setLanguagePrefLabel(concept.getPrefLabelByLanguage(language));
				code.setDescription(concept.getDescriptionByLanguage("en"));
				list.add(code);
				count--;
			}
		}

		return list;
		// } else {
		// throw new RestRepsonseException(HttpStatus.NOT_FOUND,
		// "No suggestions found in dataset: " + dataset + " wrt query: " + query);
		// }
	}

}
