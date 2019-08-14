package eu.cessda.cvmanager.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import eu.cessda.cvmanager.CvGuiApplication;
import eu.cessda.cvmanager.domain.Vocabulary;
import eu.cessda.cvmanager.repository.VocabularyRepository;
import eu.cessda.cvmanager.service.mapper.VocabularyMapper;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = CvGuiApplication.class)
public class VocabularyServiceTests {

	@Autowired
	private VocabularyService vocabularyService;
	
	@MockBean
	private VocabularyRepository vocabularyRepository;
	
	@Autowired
    private VocabularyMapper vocabularyMapper;
	
	@Test
	public void getVocabulariesTest() {
		when(vocabularyRepository.findAll()).thenReturn(
			Stream.of(new Vocabulary()).collect(Collectors.toList())
		);
		assertEquals(1, vocabularyService.findAll().size());
	}
}
