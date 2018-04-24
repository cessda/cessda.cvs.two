package eu.cessda.cvmanager.es;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;

public class AggregatedPageImplTest {

	@Test
	@Ignore
	public void constructFacetedPageWithPageable() {
		Page<String> page = new AggregatedPageImpl<>(Arrays.asList("Test", "Test 2"), PageRequest.of(0, 2), 10);

		assertEquals(10, page.getTotalElements());
		assertEquals(2, page.getNumberOfElements());
		assertEquals(2, page.getSize());
		assertEquals(5, page.getTotalPages());
	}

}
