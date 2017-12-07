package eu.cessda.cvmanager.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gesis.stardat.ddiflatdb.client.DDIStore;
import org.gesis.stardat.entity.CVConcept;
import org.gesis.stardat.entity.CVScheme;
import org.gesis.stardat.entity.DDIElement;

import com.vaadin.data.TreeData;

import eu.cessda.cvmanager.service.CvManagerService;

public class CvCodeTreeUtils{
	
	public static void buildCvConceptTree(CvManagerService cvManagerService, CVScheme cvScheme, TreeData<CVConcept> cvConceptTree ) {
		cvConceptTree.clear();
		Map<String, CVConcept> cvConceptMap = new HashMap<>();
		List<CVConcept> rootCvConcepts = new ArrayList<>();
		List<DDIStore> ddiConcepts = cvManagerService.findByIdAndElementType(cvScheme.getContainerId(), DDIElement.CVCONCEPT);
		ddiConcepts.forEach(ddiConcept -> {
			CVConcept concept = new CVConcept(ddiConcept);
			cvConceptMap.put(concept.getId(), concept);
		});
		// get root concepts
		cvScheme.getOrderedMemberList().forEach( item -> {
			CVConcept concept = cvConceptMap.get(item);
			if( concept != null ) {
				rootCvConcepts.add( concept );
			}
		});
		cvConceptTree.addRootItems(rootCvConcepts);
		
		// TODO: add broader in each concept if needed

		// add narrower in each concept if exist
		cvConceptMap.forEach( (k, v) -> {
			if( v.getOrderedNarrowerList() != null && !v.getOrderedNarrowerList().isEmpty() ) {
				v.getOrderedNarrowerList().forEach( i -> {
					CVConcept narrowerCode = cvConceptMap.get(i);
					if( narrowerCode != null )
						cvConceptTree.addItem(v, narrowerCode);
				});
			}
		});
	}
}
