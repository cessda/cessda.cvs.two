package eu.cessda.cvmanager.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import eu.cessda.cvmanager.domain.enumeration.ItemType;
import eu.cessda.cvmanager.service.dto.ConceptDTO;
import eu.cessda.cvmanager.service.dto.LicenceDTO;
import eu.cessda.cvmanager.service.dto.VersionDTO;
import eu.cessda.cvmanager.service.dto.VocabularyDTO;

@Service
public class ExportService {

	private final VersionService versionService;
	private final VocabularyService vocabularService;
	private final CodeService codeService;
	private final TemplateEngine templateEngine;
	private final LicenceService licenceService;
	
	public ExportService( VocabularyService vocabularService, VersionService versionService, CodeService codeService,
			TemplateEngine templateEngine, LicenceService licenceService) {
		this.versionService = versionService;
		this.vocabularService = vocabularService;
		this.codeService = codeService;
		this.templateEngine = templateEngine;
		this.licenceService = licenceService;
	}
	
	public String getGeneratedSkosRdf(String cvCode, String languageIso, String version) {
		VocabularyDTO vocabulary = vocabularService.getByNotation(cvCode);
    	VersionDTO versionDTO = versionService.findOneByNotationLangVersion( cvCode, languageIso, version );
    	VersionDTO slVersion = versionDTO;
    	List<LicenceDTO> licenses = licenceService.findAll();
    	
    	// always get one SL version
		if( versionDTO.getItemType().equals( ItemType.TL.toString())) {
			if(vocabulary.getVersionByUri( versionDTO.getUriSl()).isPresent())
				slVersion = vocabulary.getVersionByUri( versionDTO.getUriSl()).get();
		}
    	
        Map<String, Object> map = new HashMap<>();
		String cvUrn = null;
		String docId = null;
		String docVersionOf = null;
		String docVersion = null;
		String docLicense = null;
		
		for( ConceptDTO concept : versionDTO.getConcepts()) {
			if( concept.getPosition() == null)
				concept.setPosition(999);
		}
		versionDTO.setConcepts( 
				versionDTO.getConcepts().stream()
				.sorted((c1,c2) -> c1.getPosition().compareTo(c2.getPosition()))
				.collect(Collectors.toCollection(LinkedHashSet::new)));
		
		int index = versionDTO.getCanonicalUri().lastIndexOf(":");
		cvUrn = versionDTO.getCanonicalUri().substring(0, index);
		docId = versionDTO.getSkosUri();
		index = docId.lastIndexOf("_");
		docVersionOf = docId.substring(0, index);
		docVersion = docId.substring(index + 1);
		Optional<LicenceDTO> dLicence = licenses.stream().filter( l -> l.getId().equals( versionDTO.getLicenseId())).findFirst();
		if(dLicence.isPresent())
			docLicense = dLicence.get().getName();
		
		map.put("docId", docId);
		map.put("docNotation", versionDTO.getNotation());
		map.put("docVersionOf", docVersionOf);
		map.put("docVersion", docVersion);
		map.put("docLicense", docLicense);
		map.put("docVersionNotes", slVersion.getVersionNotes());
		map.put("docVersionChanges", slVersion.getVersionChanges());
		map.put("docRight", "Copyright © " + vocabulary.getAgencyName() + " " + versionDTO.getPublicationDate().getYear());
		map.put("codes", codeService.findByVocabularyAndVersion(vocabulary.getId(), slVersion.getId()));
		
		Context ctx = new Context();
	    Iterator<?> itMap = map.entrySet().iterator();
	    while (itMap.hasNext()) {
	    	 @SuppressWarnings("rawtypes")
	    	 Map.Entry pair = (Map.Entry) itMap.next();
	    	 ctx.setVariable(pair.getKey().toString(), pair.getValue());
		}
		
        return templateEngine.process("xml/" +"export_rdf" , ctx).replaceAll("(?m)^[ \t]*\r?\n", "");
	}
}
