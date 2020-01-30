package eu.cessda.cvmanager.service;

import eu.cessda.cvmanager.domain.enumeration.ItemType;
import eu.cessda.cvmanager.service.dto.ConceptDTO;
import eu.cessda.cvmanager.service.dto.LicenceDTO;
import eu.cessda.cvmanager.service.dto.VersionDTO;
import eu.cessda.cvmanager.service.dto.VocabularyDTO;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExportService {

	private final VersionService versionService;
	private final VocabularyService vocabularService;
	private final CodeService codeService;
	private final TemplateEngine templateEngine;
	private final LicenceService licenceService;

	public ExportService(VocabularyService vocabularService, VersionService versionService, CodeService codeService,
			TemplateEngine templateEngine, LicenceService licenceService) {
		this.versionService = versionService;
		this.vocabularService = vocabularService;
		this.codeService = codeService;
		this.templateEngine = templateEngine;
		this.licenceService = licenceService;
	}

	public String getGeneratedSkosRdf(String cvCode, String languageIso, String version) {
		VocabularyDTO vocabulary = vocabularService.getByNotation(cvCode);
		VersionDTO versionDTO = versionService.findOneByNotationLangVersion(cvCode, languageIso, version);
		VersionDTO slVersion = versionDTO;
		List<LicenceDTO> licenses = licenceService.findAll();

		// always get one SL version
		if ( versionDTO.getItemType().equals( ItemType.TL.toString() ) )
		{
			Optional<VersionDTO> theVersionDTO = vocabulary.getVersionByUri( versionDTO.getUriSl() );
			if ( theVersionDTO.isPresent() )
				slVersion = theVersionDTO.get();
		}

		Map<String, Object> map = new HashMap<>();
		String cvUrn;
		String docId;
		String docVersionOf;
		String docVersion;
		String docLicense = null;

		for ( ConceptDTO concept : versionDTO.getConcepts() )
		{
			if ( concept.getPosition() == null )
				concept.setPosition( 999 );
		}
		versionDTO.setConcepts(
				versionDTO.getConcepts().stream().sorted( Comparator.comparing( ConceptDTO::getPosition ) )
						.collect( Collectors.toCollection( LinkedHashSet::new ) ) );

		int index = versionDTO.getCanonicalUri().lastIndexOf( ':' );
		cvUrn = versionDTO.getCanonicalUri().substring( 0, index );
		docId = versionDTO.getSkosUri();
		index = docId.lastIndexOf( '_' );
		docVersionOf = docId.substring( 0, index );
		docVersion = docId.substring( index + 1 );
		Optional<LicenceDTO> dLicence = licenses.stream().filter( l -> l.getId().equals( versionDTO.getLicenseId() ) )
				.findFirst();
		if ( dLicence.isPresent() )
			docLicense = dLicence.get().getName();

		map.put( "docId", docId );
		map.put( "docNotation", versionDTO.getNotation() );
		map.put( "docVersionOf", docVersionOf );
		map.put( "docVersion", docVersion );
		map.put( "docLicense", docLicense );
		map.put( "docVersionNotes", slVersion.getVersionNotes() );
		map.put( "docVersionChanges", slVersion.getVersionChanges() );
		map.put( "docRight",
				"Copyright © " + vocabulary.getAgencyName() + " " + versionDTO.getPublicationDate().getYear() );
		map.put( "codes", codeService.findByVocabularyAndVersion( vocabulary.getId(), slVersion.getId() ) );

		Context ctx = new Context();

		for ( Map.Entry<String, Object> stringObjectEntry : map.entrySet() )
		{
			@SuppressWarnings( "rawtypes" )
			Map.Entry pair = stringObjectEntry;
			ctx.setVariable( pair.getKey().toString(), pair.getValue() );
		}

		return templateEngine.process( "xml/" + "export_rdf", ctx ).replaceAll( "(?m)^[ \t]*\r?\n", "" );
	}
}
