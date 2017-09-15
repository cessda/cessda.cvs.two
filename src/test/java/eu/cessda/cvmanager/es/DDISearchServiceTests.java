package eu.cessda.cvmanager.es;

public class DDISearchServiceTests {

	// @Autowired
	// private ElasticsearchTemplate elasticsearchTemplate;
	//
	// private RestClient rClient = new
	// RestClient("http://localhost:8080/stardat-ddiflatdb");
	//
	// private String resourcePath = "src/main/resources/ddi-config";
	//
	// @Before
	// public void before() {
	//
	// elasticsearchTemplate.deleteIndex(DDIStoreES.class);
	// elasticsearchTemplate.createIndex(DDIStoreES.class);
	// elasticsearchTemplate.putMapping(DDIStoreES.class);
	// elasticsearchTemplate.refresh("ddistore_var");
	//
	// // loadDdiEntities(new File(resourcePath));
	//
	// elasticsearchTemplate.refresh("ddistore_var");
	// }
	//
	// @Test
	// public void indexVarHTMLTest() {
	// indexESCVConcepts();
	// indexESCVScheme();
	// }
	//
	// private void indexESCVScheme() {
	// List<DDIStore> questions = rClient.getStudyList("CVScheme");
	//
	// }
	//
	// private void indexESCVConcepts() {
	//
	// DDIStoreBuilder builder = new DDIStoreBuilder();
	// List<DDIStore> questions = rClient.getStudyList("CVConcept");
	//
	// for (DDIStore store : questions) {
	// CVConcept con = new CVConcept(store);
	// DDIStoreES result = new DDIStoreES(con.getId());
	// result.setElementId(con.getId());
	// result.setParentIdentifier(con.getParentId());
	// result.setStudy(store.getStudy());
	// result.setContent(store.getContent());
	//
	// IndexQuery indexQuery = builder.buildIndex(result);
	//
	// elasticsearchTemplate.index(indexQuery);
	// }
	//
	// }

}
