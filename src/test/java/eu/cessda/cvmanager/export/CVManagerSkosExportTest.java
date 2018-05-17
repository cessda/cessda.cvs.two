package eu.cessda.cvmanager.export;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.gesis.stardat.ddiflatdb.client.DDIStore;
import org.gesis.stardat.ddiflatdb.client.RestClient;
import org.gesis.stardat.entity.CVScheme;
import org.gesis.stardat.entity.DDIElement;
import org.junit.Before;
import org.junit.Test;

import eu.cessda.cvmanager.export.utils.SaxParserUtils;

public class CVManagerSkosExportTest {

	public RestClient restClient;
	
	@Before
	public void init() {
		this.restClient = new RestClient( "http://localhost:8080/stardat-ddiflatdb" );
	}
	
	@Test
	public void test() {
		List<DDIStore> ddiSchemes = restClient.getElementList( "thesoz", DDIElement.CVSCHEME);
		CVScheme cvScheme = null;
		if (ddiSchemes != null && !ddiSchemes.isEmpty())
			cvScheme = new CVScheme(ddiSchemes.get(0));
		
		Set<String> filterTag = new HashSet<>( Arrays.asList("skos:canonicalUri","skos:publishedVersionsURI","skos:versionNotes"));
		Set<String> filterLanguage = new HashSet<>( Arrays.asList("fr","ru"));
		
		System.out.println(SaxParserUtils.filterSkosDoc(filterTag, filterLanguage, cvScheme.getContent()));
	}

}
