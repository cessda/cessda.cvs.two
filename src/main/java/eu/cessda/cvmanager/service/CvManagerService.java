package eu.cessda.cvmanager.service;

import java.util.List;

import org.gesis.stardat.ddiflatdb.client.DDIStore;
import org.gesis.stardat.ddiflatdb.client.RestClient;
import org.springframework.stereotype.Service;

@Service
public class CvManagerService {
	public final ConfigurationService configService;
	
	public final RestClient restClient;

	public CvManagerService(ConfigurationService configService) {
		this.configService = configService;
		
		this.restClient = new RestClient( configService.getDdiflatdbRestUrl() );
	}
	
	public List<DDIStore> findByIdAndElementType(String id, String elementType){
		return restClient.getElementList(id, elementType);
	}
	
	public List<DDIStore> findByContentAndElementType(String content, String elementType){
		return restClient.getElementListByContent( elementType, content);
	}
	
	public List<DDIStore> findStudyByElementType(String elementType){
		return restClient.getStudyList( elementType );
	}	

	public DDIStore saveElement(DDIStore ddiStore, String username, String comment) {
		return restClient.saveElement( ddiStore, username, comment);
	}
}
