package eu.cessda.cvmanager.ui.view;

import javax.annotation.PostConstruct;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

@SpringView(name = ErrorView.VIEW_NAME)
@org.springframework.stereotype.Component(value = ErrorView.VIEW_NAME)
public class ErrorView extends VerticalLayout implements View {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String VIEW_NAME = "Error";
	private Label errorLabel;
	private Button back;

	private VerticalLayout mainLayout = new VerticalLayout();

	@PostConstruct
	void init() {

	}

	@Override
	public void enter(ViewChangeEvent event) {
		mainLayout.setWidth(60, Unit.PERCENTAGE);
		mainLayout.setSpacing(true);
		mainLayout.setResponsive(true);

		mainLayout.setStyleName("mainlayout");
		this.errorLabel = new Label("<br><br><h3>" + String.format("No such view: %s", event.getViewName()) + "</h3>",
				ContentMode.HTML);
		this.mainLayout.addComponent(this.errorLabel);
		this.mainLayout.setComponentAlignment(this.errorLabel, Alignment.BOTTOM_LEFT);
		this.back = new Button("Back");
		this.back.addClickListener(e -> {
			this.getUI().getNavigator().navigateTo(EditorView.VIEW_NAME);
		});
		this.mainLayout.addComponent(this.back);
		addComponent(mainLayout);

		setComponentAlignment(mainLayout, Alignment.MIDDLE_CENTER);

	}

}