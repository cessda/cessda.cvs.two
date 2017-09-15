package eu.cessda.cvmanager.ui.view;

import org.gesis.stardat.entity.CVScheme;

import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

/**
 * This class represents that panel that will be showed when clicking on a row
 * of the results grid.
 */
public class DetailsPanel extends Panel {

	private static final long serialVersionUID = 1L;

	private VerticalLayout mainContainer = new VerticalLayout();

	/**
	 * Constructor. @param hit: the corresponding hit of the clicked
	 * row. @throws
	 */
	public DetailsPanel(CVScheme hit) {

		super();

		this.setStyleName(ValoTheme.PANEL_BORDERLESS);

		this.mainContainer.setSizeFull();
		this.mainContainer.setSpacing(true);
		this.mainContainer.setMargin(true);

		VerticalLayout layout = new VerticalLayout();
		layout.setSizeFull();

		Label label = new Label();
		// if (hit.highlightFields().get("FOO") == null)
		// label = new Label("<h2>Description</h2><p>" + (String)
		// hit.getSource().get("FOO") + "</p>",
		// ContentMode.HTML);
		// else
		// label = new Label(
		// "<h2>Description</h2><p>" +
		// hit.highlightFields().get("FOO").getFragments()[0].string() + "</p>",
		// ContentMode.HTML);
		label.setSizeFull();

		layout.setSpacing(true);
		layout.setMargin(true);
		layout.setSizeFull();
		layout.addComponents(label);

		this.setContent(layout);

	}
}
