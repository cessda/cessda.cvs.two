package eu.cessda.cvmanager.entity;

import java.util.ArrayList;
import java.util.List;

import org.gesis.stardat.ddiflatdb.client.DDIStore;
import org.gesis.stardat.ddiflatdb.client.RestClient;
import org.gesis.stardat.entity.CVConcept;
import org.gesis.stardat.entity.DDIElement;
import org.junit.Before;
import org.junit.Test;

public class EntityTest {

	private RestClient restClient;

	@Before
	public void before() {
		// NOTE: stardat-ddiflatdb instance must run on given URI
		restClient = new RestClient("http://localhost:8080/stardat-ddiflatdb");
	}

	@Test
	public void CVTest() {
		List<DDIStore> ddiConcepts = restClient.getElementList("thesoz", DDIElement.CVCONCEPT);
		List<CVConcept> concepts = new ArrayList<CVConcept>();

		for (DDIStore store : ddiConcepts) {
			CVConcept con = new CVConcept(store);
			System.out.println("PrefLabel: " + con.getPrefLabelByLanguage("en"));
			System.out.println("Description: " + con.getDescriptionByLanguage("en"));

			concepts.add(con);
		}

		for (CVConcept con : concepts) {

			con.setPrefLabelByLanguage("en", "Peter");
			con.setDescriptionByLanguage("en",
					"Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.");
			con.save();
			restClient.saveElement(con.ddiStore, "Peter", "TEst");
		}

	}

}
