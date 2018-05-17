package eu.cessda.cvmanager.ui.view;

import javax.annotation.PostConstruct;

import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.layouts.MVerticalLayout;
import org.vaadin.viritin.navigator.MView;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.UI;

import eu.cessda.cvmanager.service.controller.CVManagerSuggestionProvider;
import eu.maxschuster.vaadin.autocompletetextfield.AutocompleteTextField;

@UIScope
@SpringView(name = EditorTestView.VIEW_NAME)
public class EditorTestView extends MVerticalLayout implements MView {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1478897532833428925L;

	public static final String VIEW_NAME = "EditorTest";

	private View oldView;

	CVManagerSuggestionProvider suggestionProvider = new CVManagerSuggestionProvider();

	@PostConstruct
	public void init() {
		MButton enter = new MButton(VaadinIcons.BACKWARDS, this::back);
		AutocompleteTextField affiliation = new AutocompleteTextField();
		affiliation.withSizeFull().withCaption("CV Code").withSuggestionProvider(suggestionProvider).withMinChars(3)
				.withSuggestionLimit(20);

		addComponents(enter, affiliation);
	}

	private void back(ClickEvent clickEvent) {

		UI.getCurrent().getNavigator().navigateTo(SearchView.VIEW_NAME);
		if (getOldView().getClass().getSimpleName().equals("HomeView")) {
			UI.getCurrent().getNavigator().navigateTo(SearchView.VIEW_NAME);
		} else {
			UI.getCurrent().getNavigator().navigateTo(SearchView.VIEW_NAME);
		}

	}

	@Override
	public void enter(ViewChangeEvent event) {
		// locale = UI.getCurrent().getLocale();
		setOldView(event.getOldView());

	}

	@Override
	public boolean beforeViewChange(ViewChangeEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void afterViewChange(ViewChangeEvent event) {
		// TODO Auto-generated method stub

	}

	public View getOldView() {
		return oldView;
	}

	public void setOldView(View oldView) {
		this.oldView = oldView;
	}

}
