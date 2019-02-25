package eu.cessda.cvmanager.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.gesis.wts.service.dto.AgencyDTO;

import eu.cessda.cvmanager.domain.Cv;
import eu.cessda.cvmanager.service.ConfigurationService;
import eu.cessda.cvmanager.service.dto.VersionDTO;
import eu.cessda.cvmanager.ui.view.PublicationDetailsView;

public final class WorkflowUtils {
	private WorkflowUtils() {}
	
	public static String generateAgencyBaseUri( String agencyUri) {
		String cvUriLink = agencyUri;
		if(cvUriLink == null )
			cvUriLink = ConfigurationService.DEFAULT_CV_LINK;
		if(!cvUriLink.endsWith("/"))
			cvUriLink += "/";
		return cvUriLink;
	}
	
	public static String generateCvUri( String agencyUri, String code, String languageIso) {
		return generateAgencyBaseUri(agencyUri) + code + "/" + languageIso;
	}
	
	public static String generateCodeUri( String versionUri, String versionNotation,
			String notation, String languageIso) {
		String uri = versionUri;
		int lastIndex = uri.lastIndexOf("/");
		if( lastIndex == -1) {
			uri = ConfigurationService.DEFAULT_CV_LINK;
			if(!uri.endsWith("/"))
				uri += "/";
			uri += versionNotation;
		} else {
			uri = uri.substring(0, lastIndex);
		}
		return uri + "#" + notation + "/" + languageIso;
	}
	
	/**
	 * Generate CV version URN
	 * @param agency
	 * @param version
	 */
	public static String generateVersionCanonicalURI(AgencyDTO agency, VersionDTO version) {
		String urn =  agency.getCanonicalUri();
		if(urn == null) {
			urn = "urn:" + agency.getName().replace(" ", "") + "-cv:";
		}
		return urn + version.getNotation() + ":" + version.getNumber() + "-" + version.getLanguage();
	}
	
	public static String generateCvDetailUrl(ConfigurationService configService, String cvNotation, String uri) {
		String detailUrl = configService.getServerBaseUrl() + configService.getServerContextPath() + "/#!" + PublicationDetailsView.VIEW_NAME + "/" + cvNotation + "?url=";
		try {
			detailUrl += URLEncoder.encode( uri, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			detailUrl += uri;
			e.printStackTrace();
		}
		return detailUrl;
	}
	
	
	public static String generateResolverUri(ConfigurationService configService, String uri) {
		String resolverUri = configService.getServerBaseUrl() + configService.getServerContextPath() + "/urn/";
		resolverUri += uri;

		return resolverUri;
	}
	
}
