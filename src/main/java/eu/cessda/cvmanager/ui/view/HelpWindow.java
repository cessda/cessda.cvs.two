package eu.cessda.cvmanager.ui.view;

import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

/**
 * This class represents the window that must be showed when user needs some help on how to formulate a query. 
 */
public class HelpWindow extends Window {

	private static final long serialVersionUID = 1L;
	
	/**
	 * The caption of this window
	 */
	private static final String CAPTION = "Search Help";

	/**
	 * the main content region of this window
	 */
	private Panel content;
	
	public HelpWindow(){
		
		super(HelpWindow.CAPTION);
		
		this.center();
		this.setModal(true);
		
		this.content = new Panel();
		this.content.setStyleName(ValoTheme.PANEL_WELL);
		
		Label label = new Label("TO BE DONE ...");
		label.setStyleName(ValoTheme.LABEL_H2);
		
		this.content.setContent(label);
		
		this.setContent(this.content);
	}
}
