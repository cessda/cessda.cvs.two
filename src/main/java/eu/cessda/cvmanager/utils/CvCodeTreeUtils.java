package eu.cessda.cvmanager.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.gesis.stardat.ddiflatdb.client.DDIStore;
import org.gesis.stardat.entity.CVConcept;
import org.gesis.stardat.entity.CVScheme;
import org.gesis.stardat.entity.DDIElement;
import org.gesis.wts.domain.enumeration.Language;

import com.vaadin.data.TreeData;

import eu.cessda.cvmanager.service.StardatDDIService;
import eu.cessda.cvmanager.service.dto.CodeDTO;
import eu.cessda.cvmanager.service.dto.ConceptDTO;

public class CvCodeTreeUtils{
	private static List<String> conceptToBeRemoved = null;
	
	/**
	 * Generate a tree from codes. The codes should have been sorted correctly
	 * based on position. Otherwise the tree will be failed to be built
	 * @param codes ~ sorted codes by position
	 * @return
	 */
	public static TreeData<CodeDTO> getTreeDataByCodes( List<CodeDTO> codes){
		return getTreeDataByCodes(codes, null);
	}
	
	public static TreeData<CodeDTO> getTreeDataByCodes( List<CodeDTO> codes, TreeData<CodeDTO> codeTree){
		if( codeTree == null)
			codeTree = new TreeData<>();
		else
			codeTree.clear();
		
		Map<String, CodeDTO> codeMap = new HashMap<>();
		for( CodeDTO code: codes) {
			if( code.getParent() == null ) {
				codeTree.addRootItems( code );
				codeMap.put( code.getUri(), code);
			} else {
				// determine parent
				codeTree.addItem( codeMap.get( code.getParent()), code);
			}
		}
		return codeTree;
	}
	
	public static List<CodeDTO> getCodeDTOByCodeTree( TreeData<CodeDTO> codeTree){
		List<CodeDTO> codes = new ArrayList<>();
		int position = 0;
		for(CodeDTO codeRoot: codeTree.getRootItems()) {
			codeRoot.setPosition(position);
			position++;
			codes.add( codeRoot );
			if( !codeTree.getChildren( codeRoot).isEmpty())
				position = traverseChildCode( codes, codeTree, codeRoot, codeTree.getChildren(codeRoot), position );
		};
		return codes;
	}
	
	private static int traverseChildCode(List<CodeDTO> codes, TreeData<CodeDTO> codeTree,
			CodeDTO codeParent, List<CodeDTO> codesChild, Integer position) {
		for(CodeDTO code: codesChild) {
			code.setPosition(position);
			position++;
			codes.add(code);
			if( !codeTree.getChildren( code ).isEmpty())
				position = traverseChildCode( codes, codeTree, code, codeTree.getChildren( code ), position );
		};
		return position;
	}
	
	
	
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
			code.setParent( cvConceptParent.getNotation());
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
		// put list of concept into map, with the id as the key
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
	
	/**
	 * Generate a tree from concepts by sorting based on position first
	 */
	public static void buildConceptTree(Set<ConceptDTO> concepts, TreeData<ConceptDTO> cvCodeTree) {
		cvCodeTree.clear();
		
		// sort concept first
		List<ConceptDTO> sortedConcepts = concepts.stream()
				.sorted( ( c1, c2) -> c1.getPosition().compareTo( c2.getPosition() ))
				.collect( Collectors.toList());
	
		Map<String, ConceptDTO> conceptMaps = new HashMap<>();
		for(ConceptDTO concept : sortedConcepts) {
			if( concept.getParent() == null ) { // if root concept
				cvCodeTree.addRootItems( concept );
			} else { // if child
				// find parent concept
				ConceptDTO parentCode = conceptMaps.get( concept.getParent() );
				cvCodeTree.addItem(parentCode, concept);
			}
			conceptMaps.put( concept.getNotation(), concept);
		}
	}

	/**
	 * the tree position needs to be correct
	 * @param codeDTOs
	 * @param cvCodeTree
	 */
	public static void buildCvConceptTree(List<CodeDTO> codeDTOs, TreeData<CodeDTO> cvCodeTree) {
		cvCodeTree.clear();
	
		Map<String, CodeDTO> codeMaps = new HashMap<>();
		for(CodeDTO code : codeDTOs) {
			if( code.getParent() == null ) { // if root concept
				cvCodeTree.addRootItems( code );
			} else { // if child
				// find parent concept
				CodeDTO parentCode = codeMaps.get( code.getParent() );
				cvCodeTree.addItem(parentCode, code);
			}
			codeMaps.put( code.getNotation(), code);
		}
	}
	
	public static TreeData<CVConcept> generateCVConceptTreeFromCodeTree(TreeData<CodeDTO> codeTree, CVScheme cvScheme) {
		TreeData<CVConcept> cvConceptTree = new TreeData<>();
		String baseUri = cvScheme.getContainerId().substring(0, cvScheme.getContainerId().lastIndexOf("/"));
		String versionNumber = cvScheme.getContainerId().substring(cvScheme.getContainerId().lastIndexOf("/"),cvScheme.getContainerId().length());
		baseUri = baseUri.substring(0, baseUri.lastIndexOf("/"));
		
		
		for(CodeDTO topCode : codeTree.getRootItems()) {
			
			CVConcept topCVConcept = new CVConcept();
			topCVConcept.loadSkeleton(topCVConcept.getDefaultDialect());
			topCVConcept.setId( baseUri + "#" + topCode.getNotation() + versionNumber);
			topCVConcept.setContainerId( cvScheme.getContainerId());
			topCVConcept.setNotation( topCode.getNotation() );
			
			for( String langString: topCode.getLanguages()) {
				Language lang = Language.getEnum(langString);
				topCVConcept.setPrefLabelByLanguage(lang.toString(), topCode.getTitleByLanguage(lang));
				topCVConcept.setDescriptionByLanguage(lang.toString(), topCode.getDefinitionByLanguage(lang));
			}
			// add root entry
			topCVConcept.save();
			cvConceptTree.addRootItems(topCVConcept);
			
			for(CodeDTO childCode : codeTree.getChildren(topCode)) {
				generateCVConceptTreeNarrowerFromCodeTree(cvConceptTree, codeTree, cvScheme, topCVConcept, childCode, baseUri ,versionNumber);
			}
		}
	
		return cvConceptTree;
	}
	
	private static void generateCVConceptTreeNarrowerFromCodeTree(TreeData<CVConcept> cvConceptTree, TreeData<CodeDTO> codeTree, CVScheme cvScheme, CVConcept parentCVConcept, CodeDTO childCode, String baseUri, String versionNumber) {
		
		CVConcept newCVConcept = new CVConcept();
		newCVConcept.loadSkeleton(newCVConcept.getDefaultDialect());
		newCVConcept.setId( baseUri + "#" + childCode.getNotation() + versionNumber);
		newCVConcept.setContainerId( cvScheme.getContainerId());
		newCVConcept.setNotation( childCode.getNotation() );
		
		for( String langString: childCode.getLanguages()) {
			Language lang = Language.getEnum(langString);
			newCVConcept.setPrefLabelByLanguage(lang.toString(), childCode.getTitleByLanguage(lang));
			newCVConcept.setDescriptionByLanguage(lang.toString(), childCode.getDefinitionByLanguage(lang));
		}
		// add narrower entry
		newCVConcept.save();
		cvConceptTree.addItem(parentCVConcept, newCVConcept);
		
		for(CodeDTO code : codeTree.getChildren( childCode )) {
			generateCVConceptTreeNarrowerFromCodeTree(cvConceptTree, codeTree, cvScheme, newCVConcept, code, baseUri, versionNumber);
		}
		
	}
	
}
