package eu.cessda.cvmanager.ui.view;

import java.util.List;

import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

/**
 * A pagination bar 
 * @author Karam
 *
 * @param <T>: the type of the elements in the list to be paged.
 */
public class GesisPagination<T> extends HorizontalLayout{

	private static final long serialVersionUID = 1L;
	
	/**
	 * The grid to paginate
	 */
	private Grid<T> grid;
	
	/**
	 * The item list of the grid
	 */
	private List<T> elements;
	
	//buttons
	/**
	 * "next page"
	 */
	private Button nextButton = new Button(VaadinIcons.CARET_RIGHT);
	/**
	 * "previous page"
	 */
	private Button previousButton = new Button(VaadinIcons.CARET_LEFT);
	/**
	 * "first page"
	 */
	private Button firstButton = new Button(VaadinIcons.BACKWARDS);
	/**
	 * "last page"
	 */
	private Button lastButton = new Button(VaadinIcons.FORWARD);
	
	//text field
	/**
	 * "page number"
	 */
	private TextField currentPageTextField = new TextField();
	
	//data
	/**
	 * Total number of items to be paged
	 */
	private int total;
	/**
	 * number of items per page
	 */
	private int itemsPerPage;
	/**
	 * the number of current page
	 */
	private int currentPage;
	/**
	 * how many pages
	 */
	private int numberOfPages;
	
	/**
	 * Build a pagination bar.
	 * @param grid: the grid graphical component to be paginated
	 * @param elements: the list of elements to be distributed on pages
	 * @param itemsPerPage: number of items per page
	 * @param currentPage: the page to be showed first (normally = 1)
	 */
	public GesisPagination(Grid<T> grid, List<T> elements, int itemsPerPage, int currentPage){
		
		super();
		
		this.setSpacing(true);
		this.setMargin(false);
		
		this.grid = grid;
		this.elements = elements;
		this.total = elements.size();
		this.itemsPerPage = itemsPerPage;
		this.currentPage = currentPage;
		this.numberOfPages = this.getNumberOfPages();
		
		//first page
		this.firstButton.addClickListener(e->{
			this.grid.setItems(this.elements.subList(0, Math.min(this.itemsPerPage, this.total)));
			this.currentPage = 1;
			this.currentPageTextField.setValue("" + this.currentPage);
		});
		this.addComponent(this.firstButton);
		
		//previous page
		this.previousButton.addClickListener(e->{
			//if not first page
			if(this.currentPage > 1){
				this.grid.setItems(this.elements.subList((this.currentPage-2)*this.itemsPerPage, (this.currentPage-1)*this.itemsPerPage));
				this.currentPage--;
				this.currentPageTextField.setValue("" + this.currentPage);
			}
		});
		this.addComponent(this.previousButton);
		
		Label l1 = new Label("<b>Page</b>", ContentMode.HTML);
		this.addComponent(l1);
		
		this.currentPageTextField.setValue("" + Math.min(this.currentPage, this.numberOfPages));
		this.currentPageTextField.addShortcutListener(new ShortcutListener("", ShortcutAction.KeyCode.ENTER, null) {

			private static final long serialVersionUID = 1L;

			@Override
			public void handleAction(Object sender, Object target) {
				String value = currentPageTextField.getValue();
				try{
					int i = Integer.parseInt(value);
					if( i>=1 && i<=numberOfPages ){
						grid.setItems(elements.subList((i-1)*itemsPerPage, Math.min(i*itemsPerPage, total)));
						setCurrentPage(i);
					}
					currentPageTextField.setValue("" + getCurrentPage());
				}
				catch(Exception ex){
					currentPageTextField.setValue("" + getCurrentPage());
				}
			}
		});
		this.addComponent(this.currentPageTextField);
		
		Label l2 = new Label("<b>of " + this.numberOfPages + "</b>", ContentMode.HTML);
		this.addComponents(l2);
		
		//next page
		this.nextButton.addClickListener(e->{
			//if not last page
			if(this.currentPage < this.numberOfPages){
				this.grid.setItems(this.elements.subList((this.currentPage)*this.itemsPerPage, Math.min((this.currentPage+1)*this.itemsPerPage, this.total)));
				this.currentPage++;
				this.currentPageTextField.setValue("" + this.currentPage);
			}
		});
		this.addComponent(this.nextButton);
		
		//last page
		this.lastButton.addClickListener(e->{
			if(this.numberOfPages > 0){
				this.grid.setItems(this.elements.subList((this.numberOfPages-1)*this.itemsPerPage, this.total));
				this.currentPage = this.numberOfPages;
				this.currentPageTextField.setValue("" + this.currentPage);
			}
		});
		this.addComponent(this.lastButton);
		
		this.grid.setItems(this.elements.subList((this.currentPage-1)*this.itemsPerPage, Math.min(this.currentPage*this.itemsPerPage, this.total)));
	}
	
	/**
	 * calculate the number of needed pages given the total number of elements and the number of elements per page 
	 * @return the needed number of pages
	 */
	private int getNumberOfPages(){
		int div = this.total / this.itemsPerPage;
		int mod = this.total % this.itemsPerPage;
		if( mod>0 ) return div+1;
		else return div;
	}
	
	private int getCurrentPage() {
		return currentPage;
	}

	private void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

}
