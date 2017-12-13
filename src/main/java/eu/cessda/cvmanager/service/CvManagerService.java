package eu.cessda.cvmanager.service;

import java.util.List;

import org.gesis.stardat.ddiflatdb.client.DDIStore;
import org.gesis.stardat.ddiflatdb.client.RestClient;
import org.gesis.stardat.entity.CVConcept;
import org.gesis.stardat.entity.CVScheme;
import org.gesis.stardat.entity.DDIElement;
import org.springframework.stereotype.Service;

import com.vaadin.data.TreeData;

@Service
public class CvManagerService {
	public final ConfigurationService configService;
	
	public final RestClient restClient;

	public CvManagerService(ConfigurationService configService) {
		this.configService = configService;
		
		this.restClient = new RestClient( configService.getDdiflatdbRestUrl() );
	}
	
	public List<DDIStore> findByIdAndElementType(String id, String elementType){
		return restClient.getElementList(id, elementType);
	}
	
	public List<DDIStore> findByContentAndElementType(String content, String elementType){
		return restClient.getElementListByContent( elementType, content);
	}
	
	public List<DDIStore> findStudyByElementType(String elementType){
		return restClient.getStudyList( elementType );
	}	

	public DDIStore saveElement(DDIStore ddiStore, String username, String comment) {
		return restClient.saveElement( ddiStore, username, comment);
	}
	
	public Long deleteById(Long id, String username, String comment){
		return restClient.deleteElement(id, username, comment);
	}
	
	public Long deleteById(final String studyId, final String elementType, String userName, String comment){
		return restClient.deleteElementList(studyId, elementType, userName, comment);
	}
	
	public void deleteConceptTree(TreeData<CVConcept> treeData, CVConcept targetConcept) {
		deleteById(targetConcept.ddiStore.getPrimaryKey(), "peter", "delete concept");
		treeData.getChildren(targetConcept).forEach( childConcept -> {
			deleteConceptTree(treeData, childConcept);
		});
	}
	
	public DDIStore storeTopConcept( CVScheme cvScheme, List<CVConcept> topConcepts ) {
		List<String> topConceptIds = cvScheme.getOrderedMemberList();
		topConceptIds.clear();
		topConcepts.forEach( item -> topConceptIds.add( item.ddiStore.getElementId() ));
		cvScheme.save();
		return saveElement(cvScheme.ddiStore, "Peter", "store top concept");
	}

	public DDIStore storeNarrowerConcept(CVConcept cvConcept, List<CVConcept> narrowerConcepts) {
		List<String> narrowerConceptIds = cvConcept.getOrderedNarrowerList();
		narrowerConceptIds.clear();
		narrowerConcepts.forEach( item -> narrowerConceptIds.add( item.ddiStore.getElementId() ));
		cvConcept.save();
		return saveElement(cvConcept.ddiStore, "Peter", "store narrower concept");
	}
}
