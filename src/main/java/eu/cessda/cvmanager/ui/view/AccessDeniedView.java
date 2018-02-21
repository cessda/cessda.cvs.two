package eu.cessda.cvmanager.ui.view;

import org.springframework.stereotype.Component;

import com.vaadin.annotations.Theme;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

/**
 * The view that will be navigated to if a user try to access a non-authorized view.
 * 
 * @author Karam
 */
@Component (value = AccessDeniedView.NAME) // No SpringView annotation because this view can not be navigated to
@Theme("valo")
@UIScope
public class AccessDeniedView extends VerticalLayout implements View {
	
	private static final long serialVersionUID = -6690385676534540187L;

	/**
	 * The Bean and view name of the current view.
	 */
	public final static String NAME = "denied";
	
	/**
	 * This is the Bean name of the view that you must navigate to after the current view.
	 * The default value is: "" (an empty string).
	 * This value must be set according to the needs of your system.
	 * More precisely, you should set this value before navigating to this view.
	 */
	public static String NAVIGATETO_VIEWNAME = "";
	
	private Label l;
	private Button back;

    public AccessDeniedView() {
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
//    	((MainUI) getUI()).resetSearchBox();
    	
    	this.setSpacing(true);
    	this.setMargin(true);
    	
    	this.removeAllComponents();
    	
    	this.l = new Label("You don't have access to this view.");
    	this.l.addStyleName(ValoTheme.LABEL_FAILURE);
        addComponent(this.l);
        
        this.back = new Button("Back");
        this.back.addClickListener( e -> {
        	this.getUI().getNavigator().navigateTo(AccessDeniedView.NAVIGATETO_VIEWNAME);
        });
        this.addComponent(this.back);
    }
}

