package eu.cessda.cvmanager.ui.layout;

import java.util.Locale;

import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.EventScope;
import org.vaadin.spring.events.EventBus.UIEventBus;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;
import org.vaadin.spring.i18n.I18N;
import org.vaadin.spring.i18n.support.Translatable;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MVerticalLayout;

import com.vaadin.server.Sizeable.Unit;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class AdvertiserPanelLayout extends MVerticalLayout implements Translatable{

	private static final long serialVersionUID = 1L;
	private I18N i18n;
	private Locale locale = UI.getCurrent().getLocale();
	private EventBus eventBus;
	
	private String fieldName;
	private Integer ComponentPositionx;
	private VerticalLayout cl = new VerticalLayout();
	private Label blank = new Label( "&nbsp;", ContentMode.HTML );
	private Label helpText = new Label( "" );
	
	public AdvertiserPanelLayout(  UIEventBus eventBus, I18N i ) {
		this.i18n = i;
		this.eventBus = eventBus;
		eventBus.subscribe( this );

		addComponent( cl );
		
		//apply i18n
		updateMessageStrings(locale);
    }
	
	@EventBusListenerMethod( scope = EventScope.UI )
	public void onTooltip( Label ht )
	{

		setHelpText( ht );
		getCl().removeAllComponents();
		// if ( getComponentPositionx() != null )
		// helppanel.getComponent( ss ).setHeight( getComponentPositionx() / 100, Unit.PERCENTAGE );

	}

	@EventBusListenerMethod( scope = EventScope.UI )
	public void onTooltip( Integer positionx )
	{

		setComponentPositionx( positionx );
		getCl().removeAllComponents();

		getCl().addComponent( getBlank() );
		getCl().addComponent( getHelpText() );
		getHelpText().setStyleName( "activehelptext" );
		getCl().setSizeFull();
		getCl().setComponentAlignment( getHelpText(), Alignment.BOTTOM_LEFT );

		if ( getComponentPositionx() != null )
		{

			getBlank().setHeight( getComponentPositionx(), Unit.PIXELS );

		}

	}

	public String getFieldName()
	{
		return fieldName;
	}

	public void setFieldName( String fieldName )
	{
		this.fieldName = fieldName;
	}

	public Integer getComponentPositionx()
	{
		return ComponentPositionx;
	}

	public void setComponentPositionx( Integer componentPositionx )
	{
		ComponentPositionx = componentPositionx - 10;
	}

	public final VerticalLayout getCl()
	{
		return cl;
	}

	public final void setCl( VerticalLayout cl )
	{
		this.cl = cl;
	}

	public final Label getBlank()
	{
		return blank;
	}

	public final void setBlank( Label blank )
	{
		this.blank = blank;
	}

	public Label getHelpText()
	{
		return helpText;
	}

	public void setHelpText( Label helpText )
	{

		this.helpText = new Label( "<span>" + helpText.getValue() + "</span>", ContentMode.HTML );;
	}

	@Override
	public void updateMessageStrings(Locale locale) {
	}

}