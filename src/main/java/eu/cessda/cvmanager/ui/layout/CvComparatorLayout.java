package eu.cessda.cvmanager.ui.layout;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.fop.pdf.CCFFilter;
import org.vaadin.spring.i18n.I18N;
import org.vaadin.spring.i18n.support.Translatable;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.grid.MGrid;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MCssLayout;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.renderers.ComponentRenderer;
import com.vaadin.ui.themes.ValoTheme;

import eu.cessda.cvmanager.service.ConceptService;
import eu.cessda.cvmanager.service.dto.ConceptDTO;
import eu.cessda.cvmanager.service.dto.VersionDTO;

public class CvComparatorLayout extends MCssLayout implements Translatable {
	
	private static final long serialVersionUID = 1L;
	private final ConceptService conceptService;
	private MLabel titleLayout = new MLabel().withContentMode( ContentMode.HTML ).withFullWidth();
	
	private MCssLayout rowCompareHead = new MCssLayout().withFullWidth().withStyleName("row-compare-head");
	private MLabel headLabel1 = new MLabel( "<strong>Versions</strong>" ).withContentMode( ContentMode.HTML).withStyleName("col-compare-small", "col-compare-head");
	private MLabel headLabel2 = new MLabel().withContentMode( ContentMode.HTML).withStyleName("col-compare-extra-large","col-compare-previous", "col-compare-head");
	private MLabel headLabel3 = new MLabel().withContentMode( ContentMode.HTML).withStyleName("col-compare-extra-large","col-compare-current", "col-compare-head");
	
	private MCssLayout rowCompareTitle = new MCssLayout().withFullWidth().withStyleName("row-compare");
	private MLabel titleLabel = new MLabel( "<strong>CV name</strong>" ).withContentMode( ContentMode.HTML).withStyleName("col-compare-small");
	private MLabel titlePrev = new MLabel().withContentMode( ContentMode.HTML).withStyleName("col-compare-extra-large","word-brake-normal","col-compare-previous");
	private MLabel titleCurrent = new MLabel().withContentMode( ContentMode.HTML).withStyleName("col-compare-extra-large","word-brake-normal","col-compare-current");
	
	private MCssLayout rowCompareDefinition = new MCssLayout().withFullWidth().withStyleName("row-compare-end");
	private MLabel definitionLabel = new MLabel( "<strong>CV definition</strong>" ).withContentMode( ContentMode.HTML).withStyleName("col-compare-small");
	private MLabel definitionPrev = new MLabel().withContentMode( ContentMode.HTML).withStyleName("col-compare-extra-large","word-brake-normal","col-compare-previous");
	private MLabel definitionCurrent = new MLabel().withContentMode( ContentMode.HTML).withStyleName("col-compare-extra-large","word-brake-normal","col-compare-current");
	
	private MCssLayout codeLayout = new MCssLayout().withFullWidth();
//	private MGrid<ConceptCompare> conceptGrid = new MGrid<>( ConceptCompare.class ).withFullWidth();
	
	private MCssLayout buttonLayout = new MCssLayout().withFullWidth();
	private MButton showOnlyChanges = new MButton( "Show all codes" ).withStyleName("pulll-left");
	
	private List<ConceptCompare> allComparation = new ArrayList<>();
	private List<ConceptCompare> changesComparation = new ArrayList<>();

	private StringBuilder changeLogsCv = new StringBuilder();
	private StringBuilder changeLogs = new StringBuilder();
	private boolean versionCompared;
	
	private VersionDTO versionOld;
	private VersionDTO versionCurrent;
	
	private boolean showAllCode = false;
	
	private MLabel changesLogHead = new MLabel("<strong>Changes Logs:</strong>").withContentMode( ContentMode.HTML).withFullWidth();
	private MLabel changesLogLabel = new MLabel().withContentMode( ContentMode.HTML).withFullWidth();
	
	private boolean isForPublication = false;
	
	public CvComparatorLayout(ConceptService conceptService) {
		super();
		this.conceptService = conceptService;
		
		init();
	}

	private void init() {
		rowCompareHead
			.add( headLabel1, headLabel2, headLabel3);
		
		rowCompareTitle
			.add(titleLabel, titlePrev, titleCurrent);
		
		rowCompareDefinition
			.add( definitionLabel, definitionPrev, definitionCurrent);
		
		buttonLayout
			.add( showOnlyChanges );
		
		showOnlyChanges.addClickListener( e->{
			if( showAllCode ) {
				updateGrid( false , isForPublication);
				showOnlyChanges.setCaption("Show all codes");
				showAllCode =  false;
			} else {
				updateGrid( true , isForPublication);
				showOnlyChanges.setCaption("Show only code changes");
				showAllCode =  true;
			}
		});
		
//		conceptGrid
//			.withStyleName(ValoTheme.TABLE_BORDERLESS, "undefined-height")
//			.withFullSize()
//			.setSelectionMode(SelectionMode.NONE);
//		conceptGrid.setHeaderVisible(false);
		
//		conceptGrid
//			.withStyleName("export-grid")
//			.withFullSize()
//			.withHeight("500px")
//			.setSelectionMode(SelectionMode.NONE);
//		conceptGrid.setHeaderVisible(false);
		
//		gridLayout
//			.withHeight("100%")
//			.add(conceptGrid);
		
		this
			.withFullWidth()
			.add(
				titleLayout,
				rowCompareHead,
				rowCompareTitle,
				rowCompareDefinition,
				buttonLayout,
				codeLayout,
				changesLogHead,
				changesLogLabel
//				conceptGrid,
				
			);
	}
	
	public void compareVersion( VersionDTO version1, VersionDTO version2) {
		compareVersion(version1, version2, false);
	}
	
	public void compareVersion( VersionDTO version1, VersionDTO version2, boolean isForPublication) {
		versionCompared = true;
		versionOld = version1;
		versionCurrent = version2;
		
		String rightConpareLabel = "(Current)";
		if( isForPublication ) {
			showOnlyChanges.setVisible( false );
			rightConpareLabel = versionCurrent.getNumber();
		}
		headLabel2.setValue("<strong>" + version1.getNumber() + "</strong>");
		headLabel3.setValue("<strong>" + rightConpareLabel + "</strong>");
		
		titlePrev.setValue( "<span>" + version1.getTitle() + "</span>" );
		titleCurrent.setValue( "<span>" + version2.getTitle() + "</span>" );
		
		definitionPrev.setValue( "<span>" + version1.getDefinition() + "</span>" );
		definitionCurrent.setValue( "<span>" + version2.getDefinition() + "</span>" );
		
		if( !version1.getTitle().equals( version2.getTitle() )) {
			titlePrev.addStyleName("label-highlight");
			titleCurrent.addStyleName("label-highlight");
			changeLogsCv.append( "CV long name changed: " ).append( version2.getTitle() ).append( "\n" );
		}
		
		if( !version1.getDefinition().equals( version2.getDefinition() )) {
			definitionPrev.addStyleName("label-highlight");
			definitionCurrent.addStyleName("label-highlight");
			changeLogsCv.append( "CV definition changed: " ).append( version2.getTitle() ).append( "\n" );
		}
		
		// get both concepts
		Set<ConceptDTO> prevConcepts = new HashSet<>(conceptService.findByVersion( version1.getId()));
		List<ConceptDTO> currentConcepts = version2.getSortedConcepts();
		
		for(ConceptDTO currentConcept : currentConcepts) {
			ConceptCompare conceptCompare = null;
			if( currentConcept.getPreviousConcept() == null ) { // new concept
				conceptCompare = new ConceptCompare( null , currentConcept);
			}
			else { // unchanged or edited concept
				ConceptDTO prevConcept = ConceptDTO.getConceptById(prevConcepts, currentConcept.getPreviousConcept());
				if( prevConcept == null ) {
					conceptCompare = new ConceptCompare( null , currentConcept);
				}
				else {
					conceptCompare = new ConceptCompare( prevConcept , currentConcept);
					prevConcepts.remove( prevConcept );
				}
			}
			allComparation.add(conceptCompare);
			if(conceptCompare.isChangeExist())
				changesComparation.add(conceptCompare);
		}
		
		// deleted concept 
		for(ConceptDTO prevConcept : prevConcepts) {
			ConceptCompare conceptCompare = new ConceptCompare(prevConcept, null);
			allComparation.add(conceptCompare);
			if(conceptCompare.isChangeExist())
				changesComparation.add(conceptCompare);
		}
		
		changesComparation.forEach( cc -> changeLogs.append( cc.getChangesLog()) );
		
		// set the grid
		updateGrid( false, isForPublication );
		
	}
	
	
	public void updateGrid( boolean showAll, boolean isForPublication) {
		codeLayout.removeAllComponents();
		changeLogs.setLength(0);
		
		String rightConpareLabel = "(Current)";
		if( isForPublication )
			rightConpareLabel = versionCurrent.getNumber();
		
		// set second table header
		codeLayout
			.add(
				new MCssLayout().withFullWidth().withStyleName("row-compare-head")
				.add(
					new MLabel( "Code " + versionOld.getNumber() ).withStyleName( "col-compare-head","col-compare-small", "col-compare-previous"),
					new MLabel( "Code " + rightConpareLabel ).withStyleName( "col-compare-head","col-compare-small", "col-compare-current"),
					new MLabel( "Term " + versionOld.getNumber() ).withStyleName( "col-compare-head","col-compare-small", "col-compare-previous"),
					new MLabel( "Term " + rightConpareLabel ).withStyleName( "col-compare-head","col-compare-small", "col-compare-current"),
					new MLabel( "Definition " + versionOld.getNumber() ).withStyleName( "col-compare-head","col-compare-large", "col-compare-previous"),
					new MLabel( "Definition " + rightConpareLabel ).withStyleName( "col-compare-head","col-compare-large", "col-compare-current")
				)
			);
				
		if( showAll ) {
//			conceptGrid.setItems(allComparation);
			int i=0;
			for( ConceptCompare cc : allComparation) {
				i++;
				if(i == allComparation.size())
					codeLayout.add( generateCodeRow(cc).withStyleName("row-compare-end"));
				else
					codeLayout.add( generateCodeRow(cc));
				changeLogs.append( cc.getChangesLog() );
			}
		} else {
			int i=0;
			for( ConceptCompare cc : changesComparation) {
				i++;
				if(i == changesComparation.size())
					codeLayout.add( generateCodeRow(cc).withStyleName("row-compare-end"));
				else
					codeLayout.add( generateCodeRow(cc));
				changeLogs.append( cc.getChangesLog() );
			}
//			conceptGrid.setItems(changesComparation);
		}
		
//		conceptGrid.removeAllColumns();
//		conceptGrid.addColumn( cc -> cc.getCodePrevious(), new ComponentRenderer() ).setId("codePrevious");
//		conceptGrid.addColumn( cc -> cc.getCodeCurrent(), new ComponentRenderer() ).setId("codeCurrent");
//		conceptGrid.addColumn( cc -> cc.getTermPrevious(), new ComponentRenderer() ).setId("termPrevious");
//		conceptGrid.addColumn( cc -> cc.getTermCurrent(), new ComponentRenderer() ).setId("ternCurrent");
//		conceptGrid.addColumn( cc -> cc.getDefinitionPrevious(), new ComponentRenderer() ).setId("definitionPrevious");
//		conceptGrid.addColumn( cc -> cc.getDefinitionCurrent(), new ComponentRenderer() ).setId("definitionCurrent");
		
		changesLogLabel.setValue( changeLogsCv.toString().replaceAll("(\r\n|\n)", "<br />") + changeLogs.toString().replaceAll("(\r\n|\n)", "<br />"));
	}
	
	private MCssLayout generateCodeRow( ConceptCompare cc) {
		MCssLayout layout = new MCssLayout().withFullWidth().withStyleName("row-compare");
		layout.add(
				cc.getCodePrevious().withStyleName("break-word"),
				cc.getCodeCurrent().withStyleName("break-word"),
				cc.getTermPrevious(),
				cc.getTermCurrent(),
				cc.getDefinitionPrevious(),
				cc.getDefinitionCurrent()
			);
		return layout;
	}

	
	@Override
	public void updateMessageStrings(Locale locale) {
	
	}
	
	static class ConceptCompare{
		private MLabel codePrevious = new MLabel().withContentMode( ContentMode.HTML).withStyleName( "word-brake-normal","col-compare-small", "col-compare-previous");
		private MLabel codeCurrent = new MLabel().withContentMode( ContentMode.HTML).withStyleName( "word-brake-normal" ,"col-compare-small", "col-compare-current");
		private MLabel termPrevious = new MLabel().withContentMode( ContentMode.HTML).withStyleName( "word-brake-normal","col-compare-small", "col-compare-previous");
		private MLabel termCurrent = new MLabel().withContentMode( ContentMode.HTML).withStyleName( "word-brake-normal" ,"col-compare-small", "col-compare-current");
		private MLabel definitionPrevious = new MLabel().withContentMode( ContentMode.HTML).withStyleName( "word-brake-normal" ,"col-compare-large", "col-compare-previous");
		private MLabel definitionCurrent = new MLabel().withContentMode( ContentMode.HTML).withStyleName( "word-brake-normal" ,"col-compare-large", "col-compare-current");
		private boolean changeExist;
		private String changesLog;
		public ConceptCompare( ConceptDTO conceptPrevious, ConceptDTO conceptCurrent) {
			if( conceptPrevious == null && conceptCurrent == null)
				throw new IllegalArgumentException("conceptPrevious and conceptCurrent is null!");
			
			if ( conceptPrevious == null )
				changesLog = "Code added: " + conceptCurrent.getNotation() + "\n";
			else if ( conceptCurrent == null )
				changesLog = "Code deleted: " + conceptPrevious.getNotation() + "\n";
			
			if( conceptPrevious != null ) {
				codePrevious.setValue( "<span>" + conceptPrevious.getNotation() + "</span>" );
				termPrevious.setValue( "<span>" + conceptPrevious.getTitle() + "</span>" );
				definitionPrevious.setValue( "<span>" + conceptPrevious.getDefinition() + "</span>" );
			}
			else {
				codePrevious.setValue( "<span>&nbsp</span>" );
				termPrevious.setValue( "<span>&nbsp</span>" );
				definitionPrevious.setValue( "<span>&nbsp</span>" );
			}
			
			if( conceptCurrent != null ) {
				codeCurrent.setValue( "<span>" + conceptCurrent.getNotation() + "</span>" );
				termCurrent.setValue( "<span>" + conceptCurrent.getTitle() + "</span>" );
				definitionCurrent.setValue( "<span>" + conceptCurrent.getDefinition() + "</span>" );
			}else {
				codeCurrent.setValue( "<span>&nbsp</span>" );
				termCurrent.setValue( "<span>&nbsp</span>" );
				definitionCurrent.setValue( "<span>&nbsp</span>" );
			}
			
			// compare each other
			if( !codePrevious.getValue().equals( codeCurrent.getValue())) {
				codePrevious.addStyleName("label-highlight");
				codeCurrent.addStyleName("label-highlight");
				changeExist = true;
				if (conceptPrevious != null && conceptCurrent != null)
					changesLog = "Code value changed: " + conceptCurrent.getNotation() + "\n";
			}
			
			if( !termPrevious.getValue().equals( termCurrent.getValue())) {
				termPrevious.addStyleName("label-highlight");
				termCurrent.addStyleName("label-highlight");
				changeExist = true;
				if (conceptPrevious != null && conceptCurrent != null)
					changesLog = "Code descriptive term changed: " + conceptCurrent.getNotation() + "\n";
			}
			
			if( !definitionPrevious.getValue().equals( definitionCurrent.getValue())) {
				definitionPrevious.addStyleName("label-highlight");
				definitionCurrent.addStyleName("label-highlight");
				changeExist = true;
				if (conceptPrevious != null && conceptCurrent != null)
					changesLog = "Code definition changed: " + conceptCurrent.getNotation() + "\n";
			}
		}
		public MLabel getCodePrevious() {
			return codePrevious;
		}
		public MLabel getCodeCurrent() {
			return codeCurrent;
		}
		public MLabel getTermPrevious() {
			return termPrevious;
		}
		public MLabel getTermCurrent() {
			return termCurrent;
		}
		public MLabel getDefinitionPrevious() {
			return definitionPrevious;
		}
		public MLabel getDefinitionCurrent() {
			return definitionCurrent;
		}
		public boolean isChangeExist() {
			return changeExist;
		}
		public String getChangesLog() {
			return changesLog == null ? "": changesLog;
		}
		
	}

	public boolean isVersionCompared() {
		return versionCompared;
	}
	
	public String getChangesLogs() {
		return changeLogs.toString();
	}

	public void showChangeLog(boolean b) {
		changesLogHead.setVisible( b );
		changesLogLabel.setVisible( b );
	}

}
