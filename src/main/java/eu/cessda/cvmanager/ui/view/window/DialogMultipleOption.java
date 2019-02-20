package eu.cessda.cvmanager.ui.view.window;

import java.io.Serializable;
import java.util.List;

import org.gesis.stardat.ddiflatdb.client.DDIStore;
import org.gesis.stardat.entity.CVConcept;
import org.gesis.stardat.entity.CVScheme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.dialogs.ConfirmDialog;
import org.vaadin.dialogs.ConfirmDialog.Listener;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.EventScope;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MCssLayout;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;
import org.vaadin.viritin.layouts.MWindow;

import com.vaadin.data.Binder;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

import eu.cessda.cvmanager.event.CvManagerEvent;
import eu.cessda.cvmanager.event.CvManagerEvent.EventType;
import eu.cessda.cvmanager.service.StardatDDIService;
import eu.cessda.cvmanager.ui.view.PublicationDetailsView;

public class DialogMultipleOption extends MWindow {

	private static final long serialVersionUID = -2960064213533383226L;
	private static final Logger log = LoggerFactory.getLogger(DialogMultipleOption.class);

	private MVerticalLayout layout = new MVerticalLayout();
	private MCssLayout buttonLayout = new MCssLayout();
	private Integer selectedOptionNumber;
	private Listener optionChosedListener;
	
	public interface Listener extends Serializable {
	        void onClose( DialogMultipleOption dialogMultipleOption );
	}

	public DialogMultipleOption(String windowTitle, String htmlContent, List<Button> optionButtons, final Listener listener ) {
		super( windowTitle );
		optionChosedListener = listener;
		
		this
			.withHeight("240px")
			.withWidth("600px")
			.withResizable(false)
			.withCenter()
			.withModal(true)
			.withContent(layout)
			.addCloseListener( e -> {
				if( selectedOptionNumber == null ) {
					this.getOptionChosedListener().onClose( this );
				}
			});

		buttonLayout
			.withFullWidth()
			.withStyleName("alignTextRight");
		
		Button cancelButton = new Button("Cancel", e -> this.close());
		cancelButton.addStyleName( "spacing-left-10px" );
		
		optionButtons.forEach( button -> {
			buttonLayout.add(button);
			button.addStyleName("spacing-right-10px");
			button.addClickListener( e -> {
				selectedOptionNumber = buttonLayout.getComponentIndex( e.getButton() );
				this.getOptionChosedListener().onClose( this );
				this.close();
			});
		});
		
		buttonLayout.add(cancelButton);
		
		layout
			.withHeight("95%")
			.withStyleName("dialog-content")
			.add( 
				new MHorizontalLayout()
					.withFullWidth()
					.add(
						new MLabel( htmlContent )
							.withContentMode( ContentMode.HTML )
				),
				buttonLayout
			)
			.withExpand(layout.getComponent(0), 0.4f)
			.withExpand(layout.getComponent(1), 0.06f)
			.withAlign(layout.getComponent(1), Alignment.BOTTOM_RIGHT);

	}

	public Integer getSelectedOptionNumber() {
		return selectedOptionNumber;
	}

	public Listener getOptionChosedListener() {
		return optionChosedListener;
	}
	
	

}
