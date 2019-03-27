package eu.cessda.cvmanager.service.manager;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.gesis.stardat.ddiflatdb.client.DDIStore;
import org.gesis.stardat.entity.CVConcept;
import org.gesis.stardat.entity.CVEditor;
import org.gesis.stardat.entity.CVScheme;
import org.gesis.stardat.entity.DDIElement;
import org.gesis.wts.domain.enumeration.Language;
import org.gesis.wts.security.SecurityUtils;
import org.gesis.wts.security.UserDetails;
import org.gesis.wts.service.dto.AgencyDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.vaadin.data.TreeData;
import com.vaadin.ui.Notification;

import eu.cessda.cvmanager.domain.Cv;
import eu.cessda.cvmanager.domain.CvCode;
import eu.cessda.cvmanager.domain.enumeration.ItemType;
import eu.cessda.cvmanager.domain.enumeration.Status;
import eu.cessda.cvmanager.service.CodeService;
import eu.cessda.cvmanager.service.ConceptService;
import eu.cessda.cvmanager.service.ConfigurationService;
import eu.cessda.cvmanager.service.ResolverService;
import eu.cessda.cvmanager.service.StardatDDIService;
import eu.cessda.cvmanager.service.VersionService;
import eu.cessda.cvmanager.service.VocabularyChangeService;
import eu.cessda.cvmanager.service.VocabularyService;
import eu.cessda.cvmanager.service.dto.CodeDTO;
import eu.cessda.cvmanager.service.dto.ConceptDTO;
import eu.cessda.cvmanager.service.dto.ResolverDTO;
import eu.cessda.cvmanager.service.dto.VersionDTO;
import eu.cessda.cvmanager.service.dto.VocabularyChangeDTO;
import eu.cessda.cvmanager.service.dto.VocabularyDTO;
import eu.cessda.cvmanager.service.mapper.CvMapper;
import eu.cessda.cvmanager.utils.CvCodeTreeUtils;
import eu.cessda.cvmanager.utils.WorkflowUtils;
import eu.cessda.cvmanager.service.mapper.CvCodeMapper;

@Service
public class VersioningManager {
	private static final Logger log = LoggerFactory.getLogger(VersioningManager.class);

	private static ConfigurationService configurationService;
	private static VocabularyService vocabularyService;
	private static VersionService versionService;
	private static CodeService codeService;
	private static ConceptService conceptService;
	private static CvMapper cvMapper;
	private static CvCodeMapper cvCodeMapper;
	private static StardatDDIService stardatDDIService;
	private static ResolverService resolverService;
	private static VocabularyChangeService vocabularyChangeService;
	
	public VersioningManager(VocabularyService vocabularySvc, VersionService versionSvc,
			CodeService codeSvc, ConceptService conceptSvc, CvMapper cvMpr,
			CvCodeMapper cvCodeMpr, StardatDDIService stardatDDIService,
			ResolverService resolverService, ConfigurationService configurationService,
			VocabularyChangeService vocabularyChangeService) {
		VersioningManager.vocabularyService = vocabularySvc;
		VersioningManager.versionService = versionSvc;
		VersioningManager.codeService = codeSvc;
		VersioningManager.conceptService = conceptSvc;
		VersioningManager.cvMapper = cvMpr;
		VersioningManager.cvCodeMapper = cvCodeMpr;
		VersioningManager.stardatDDIService = stardatDDIService;
		VersioningManager.resolverService = resolverService;
		VersioningManager.configurationService = configurationService;
		VersioningManager.vocabularyChangeService = vocabularyChangeService;
	}

	
}
