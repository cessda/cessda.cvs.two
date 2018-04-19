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

import eu.cessda.cvmanager.service.StardatDDIService;
import eu.cessda.cvmanager.service.dto.CodeDTO;

public class CvCodeTreeUtils{
	private static List<String> conceptToBeRemoved = null;
	
	public static List<CodeDTO> getCodeDTOByConceptTree( TreeData<CVConcept> conceptTree){
		List<CodeDTO> codes = new ArrayList<>();
		int position = 0;
		for(CVConcept cvConceptRoot: conceptTree.getRootItems()) {
			CodeDTO codeRoot = CodeDTO.generateFromCVConcept(cvConceptRoot);
			codeRoot.setPosition(position);
			position++;
			codes.add( codeRoot );
			if( !conceptTree.getChildren(cvConceptRoot).isEmpty())
				position = traverseChildCvConcept( codes, conceptTree, cvConceptRoot, conceptTree.getChildren(cvConceptRoot), position );
		};
		return codes;
	}
	
	private static int traverseChildCvConcept(List<CodeDTO> codes, TreeData<CVConcept> conceptTree,
			CVConcept cvConceptParent, List<CVConcept> cvConcepts, Integer position) {
		for(CVConcept cvConcept: cvConcepts) {
			CodeDTO code = CodeDTO.generateFromCVConcept(cvConcept);
			code.setPosition(position);
			code.setParent( cvConceptParent.getContainerId());
			position++;
			codes.add(code);
			if( !conceptTree.getChildren(cvConcept).isEmpty())
				position = traverseChildCvConcept( codes, conceptTree, cvConcept, conceptTree.getChildren(cvConcept), position );
		};
		return position;
	}

	public static TreeData<CVConcept> buildCvConceptTree(List<DDIStore> ddiConcepts, CVScheme cvScheme) {
		TreeData<CVConcept> cvConceptTree = new TreeData<CVConcept>();
		buildCvConceptTree(ddiConcepts, cvScheme, cvConceptTree);
		return cvConceptTree;
	}
	
	public static void buildCvConceptTree(List<DDIStore> ddiConcepts, CVScheme cvScheme, TreeData<CVConcept> cvConceptTree ) {
		cvConceptTree.clear();
		Map<String, CVConcept> cvConceptMap = new HashMap<>();
		List<CVConcept> rootCvConcepts = new ArrayList<>();
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
