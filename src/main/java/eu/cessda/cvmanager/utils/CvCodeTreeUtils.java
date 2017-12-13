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
	private static List<String> conceptToBeRemoved = null;
	
	public static void buildCvConceptTree(CvManagerService cvManagerService, CVScheme cvScheme, TreeData<CVConcept> cvConceptTree ) {
		cvConceptTree.clear();
		Map<String, CVConcept> cvConceptMap = new HashMap<>();
		List<CVConcept> rootCvConcepts = new ArrayList<>();
		List<DDIStore> ddiConcepts = cvManagerService.findByIdAndElementType(cvScheme.getContainerId(), DDIElement.CVCONCEPT);
		ddiConcepts.forEach(ddiConcept -> {
			CVConcept concept = new CVConcept(ddiConcept);
			cvConceptMap.put(concept.getId(), concept);
		});
		// set root concepts - tree level 0
		cvScheme.getOrderedMemberList().forEach( item -> {
			CVConcept concept = cvConceptMap.get(item);
			if( concept != null ) {
				rootCvConcepts.add( concept );
				// assign Narrower if exist
				assignNarrower(cvConceptTree, cvConceptMap, concept, null);
				cvConceptMap.remove( item ); // remove map item
			}
		});
		cvConceptTree.addRootItems(rootCvConcepts);
		
		// set root concept child - tree level 1 
		rootCvConcepts.forEach( concept -> assignNarrower(cvConceptTree, cvConceptMap, concept, null) );

		// add narrower for level 2 and the rest
		do {
			conceptToBeRemoved = new ArrayList<>();
			cvConceptMap.forEach( (k, v) -> {
				assignNarrower(cvConceptTree, cvConceptMap, v, conceptToBeRemoved);
			});
			conceptToBeRemoved.forEach( i -> cvConceptMap.remove( i ));
		} while( conceptToBeRemoved.size() > 0 );
	}

	private static void assignNarrower(TreeData<CVConcept> cvConceptTree, Map<String, CVConcept> cvConceptMap,
			CVConcept concept, List<String> conceptToBeRemoved) {
		if( concept.getOrderedNarrowerList() != null && !concept.getOrderedNarrowerList().isEmpty() ) {
			concept.getOrderedNarrowerList().forEach( i -> {
				if(i == null || i.isEmpty())
					return;
				// check if parent node has a parent
				if( !cvConceptTree.contains(concept) )
					return;
				CVConcept narrowerCode = cvConceptMap.get(i);
				if( narrowerCode != null ) {
					cvConceptTree.addItem(concept, narrowerCode);
					if( conceptToBeRemoved != null)
						conceptToBeRemoved.add(concept.getId());
				}
			});
		}
	}
	
}
