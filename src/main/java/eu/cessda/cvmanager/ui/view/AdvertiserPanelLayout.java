package eu.cessda.cvmanager.ui.view;

import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.EventBus.UIEventBus;
import org.vaadin.spring.events.EventScope;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class AdvertiserPanelLayout extends VerticalLayout {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private EventBus eventBus;
	private String fieldName;
	private Integer ComponentPositionx;
	private VerticalLayout cl = new VerticalLayout();
	private Label blank = new Label("&nbsp;", ContentMode.HTML);
	private Label helpText = new Label("");

	// private CustomLayout helppanel = new CustomLayout( "aboutProject" );

	public AdvertiserPanelLayout(UIEventBus eventBus) {
		this.eventBus = eventBus;
		this.eventBus.subscribe(this);

		addComponent(cl);

	}

	@EventBusListenerMethod(scope = EventScope.UI)
	public void onTooltip(Label ht) {

		setHelpText(ht);
		getCl().removeAllComponents();

	}

	@EventBusListenerMethod(scope = EventScope.UI)
	public void onTooltip(Integer positionx) {

		if (!positionx.equals(-1)) {
			setComponentPositionx(positionx);
			getCl().removeAllComponents();

			getCl().addComponent(getBlank());
			getCl().addComponent(getHelpText());
			getHelpText().setStyleName("activehelptext");
			getCl().setSizeFull();
			getCl().setComponentAlignment(getHelpText(), Alignment.BOTTOM_LEFT);

			if (getComponentPositionx() != null) {

				getBlank().setHeight(getComponentPositionx(), Unit.PIXELS);

			}
		} else {
			getHelpText().removeStyleName("activehelptext");
		}

	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public Integer getComponentPositionx() {
		return ComponentPositionx;
	}

	public void setComponentPositionx(Integer componentPositionx) {
		ComponentPositionx = componentPositionx - 10;
	}

	public final VerticalLayout getCl() {
		return cl;
	}

	public final void setCl(VerticalLayout cl) {
		this.cl = cl;
	}

	public final Label getBlank() {
		return blank;
	}

	public final void setBlank(Label blank) {
		this.blank = blank;
	}

	public Label getHelpText() {
		return helpText;
	}

	public void setHelpText(Label helpText) {

		this.helpText = new Label("<span>" + helpText.getValue() + "</span>", ContentMode.HTML);
		;
	}

}