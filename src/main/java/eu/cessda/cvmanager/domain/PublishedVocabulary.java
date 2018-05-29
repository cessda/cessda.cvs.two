package eu.cessda.cvmanager.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.data.elasticsearch.annotations.Document;

@Entity
@Table(name = "vocabulary-publish")
@Document(indexName = "vocabulary-publish")
public class PublishedVocabulary extends BaseVocabulary implements Serializable {

	private static final long serialVersionUID = 1L;

}
