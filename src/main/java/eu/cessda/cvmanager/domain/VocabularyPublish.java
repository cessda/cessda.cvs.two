package eu.cessda.cvmanager.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.data.elasticsearch.annotations.Document;

@Entity
@Table(name = "vocabulary-publish")
@Document(indexName = "vocabulary-publish2")
public class VocabularyPublish extends VocabularyBase{

}
