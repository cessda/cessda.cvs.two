package eu.cessda.cvmanager.export.utils;

import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;

import java.io.IOException;

/**
 * This specializes {@link FileDownloader} in a way, such that both the file name and content can be
 * determined on-demand, i.e. when the user has clicked the component.
 *
 * @see <a href="https://vaadin.com/docs/v8/framework/articles/LettingTheUserDownloadAFile.html">
 * https://vaadin.com/docs/v8/framework/articles/LettingTheUserDownloadAFile.html</a>
 */
public class OnDemandFileDownloader extends FileDownloader
{

	private static final long serialVersionUID = -4338072152891476750L;

	/**
	 * Provide both the {@link StreamSource} and the filename in an on-demand way.
	 */
	public interface OnDemandStreamResource extends StreamSource
	{
		String getFilename();
	}

	private final OnDemandStreamResource onDemandStreamResource;

	public OnDemandFileDownloader( OnDemandStreamResource onDemandStreamResource )
	{
		super( new StreamResource( onDemandStreamResource, "" ) );
		this.onDemandStreamResource = onDemandStreamResource;
	}

	@Override
	public boolean handleConnectorRequest( VaadinRequest request, VaadinResponse response, String path )
	{
		getResource().setFilename( onDemandStreamResource.getFilename() );
		try
		{
			return super.handleConnectorRequest( request, response, path );
		}
		catch ( final IOException ignored )
		{
			return true;
		}
	}

	private StreamResource getResource()
	{
		return (StreamResource) this.getResource( "dl" );
	}

	@Override
	public boolean equals( Object obj )
	{
		return super.equals( obj );
	}

	@Override
	public int hashCode()
	{
		return super.hashCode();
	}
}