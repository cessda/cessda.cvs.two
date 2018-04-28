package eu.cessda.cvmanager.ui.view.publication;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.vaadin.spring.i18n.I18N;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.layouts.MHorizontalLayout;

import com.vaadin.data.validator.IntegerRangeValidator;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Label;

public class PaginationBar extends MHorizontalLayout {

	private static final long serialVersionUID = 7799263034212965499L;
	
	@FunctionalInterface
    public interface PagingListener {
		void pageRequested(int page, int pageSize);
    }
	
    private PagingListener listener;
    private int noOfRows = 5;
    private int totalPages;
    private int currentPage;
    private Button first, last, next, previous,status;
	 
	private CustomLayout pagin = new CustomLayout("pagination");
	private ComboBox perpage = new ComboBox();
	
    private final ClickListener handler = event -> {
        if (event.getButton() == first) {
            currentPage = 0;
        } else if (event.getButton() == last) {
            currentPage = totalPages - 1;
        } else if (event.getButton() == next) {
            currentPage++;
        } else if (event.getButton() == previous) {
            currentPage--;
        }
        listener.pageRequested( currentPage, noOfRows);
    };

    public PaginationBar(PagingListener listener, I18N i18n) {
        this.listener = listener;
//        setSize(size);
//        this.pageSize = pageSize;
//        //System.out.println("pageSize"+getPageSize());
//        pages = (int) Math.ceil((float)getSize() / getPageSize());
//        setPages();

		Integer[] perPages={ 10, 20,30,50,100};
		perpage.setWidth(7, Unit.EM);
		perpage.setItems( perPages );
		perpage.setValue( perPages[0] );
		perpage.setTextInputAllowed( false );
		perpage.addValueChangeListener( event -> {
			 noOfRows = (int)event.getValue();
	         listener.pageRequested(0, noOfRows);		
		});
		
        initButtons();

        pagin.addComponent(first,"firstpage");
        pagin.addComponent(last,"lastpage");
        pagin.addComponent(previous,"prevpage");
        pagin.addComponent(next,"nextpage");
        pagin.addComponent(status,"status");
        
        pagin.addComponent(perpage,"pagesize");
        
        addComponents(pagin);
        
        setSizeFull();
    }
    
    public void updateState(EsQueryResultDetail esQueryResultDetail) {
    	currentPage = esQueryResultDetail.getPage().getPageNumber();
        final boolean hasPrev = currentPage > 0;
        first.setEnabled(hasPrev);
        previous.setEnabled(hasPrev);
        totalPages = esQueryResultDetail.getVocabularies().getTotalPages();
        final boolean hasNext = currentPage < esQueryResultDetail.getVocabularies().getTotalPages() - 1;
        last.setEnabled(hasNext);
        next.setEnabled(hasNext);
        if( esQueryResultDetail.getVocabularies().getTotalPages() > 0)
        	status.setCaption("page "+(currentPage + 1)+" of "+ esQueryResultDetail.getVocabularies().getTotalPages() );
        else
        	status.setCaption("page 0 of 0" );
    }

    private void initButtons() {
        first = new MButton(FontAwesome.FAST_BACKWARD, handler).withStyleName("xeconbutton");
        last = new MButton(FontAwesome.FAST_FORWARD, handler).withStyleName("xeconbutton");
        next = new MButton(FontAwesome.FORWARD, handler).withStyleName("xeconbutton");
        previous = new MButton(FontAwesome.BACKWARD, handler).withStyleName("xeconbutton");
        status = new MButton("", handler).withStyleName("xeconbutton_status");
        status.setEnabled(false);
    }
    
    public void reset() {
    	currentPage = 0;
    }

    public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	
	public int getNoOfRows() {
		return (int)perpage.getValue();
	}
	
    public PagingListener getListener() {
        return listener;
    }

    public void setListener(PagingListener listener) {
        this.listener = listener;
    }

}

