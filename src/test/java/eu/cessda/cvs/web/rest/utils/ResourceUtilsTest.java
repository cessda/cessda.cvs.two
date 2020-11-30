package eu.cessda.cvs.web.rest.utils;

import eu.cessda.cvs.domain.Vocabulary;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

class ResourceUtilsTest {

    private static final String DEFAULT_URI = "AAAAA";

    private static final String DEFAULT_STATUS = "DRAFT";

    private static final String DEFAULT_NOTATION = "AAAAA";

    private static final String DEFAULT_VERSION_NUMBER = "AAAAA";

    private static final Long DEFAULT_INITIAL_PUBLICATION = 1L;

    private static final Long DEFAULT_PREVIOUS_PUBLICATION = 1L;

    private static final Boolean DEFAULT_ARCHIVED = false;

    private static final Boolean DEFAULT_WITHDRAWN = false;

    private static final Boolean DEFAULT_DISCOVERABLE = false;

    private static final String DEFAULT_SOURCE_LANGUAGE = "en";

    private static final Long DEFAULT_AGENCY_ID = 1L;

    private static final String DEFAULT_AGENCY_NAME = "AAAAA";

    private static final String DEFAULT_AGENCY_LOGO = "AAAAA";

    private static final LocalDate DEFAULT_PUBLICATION_DATE = LocalDate.ofEpochDay(0L);

    private static final ZonedDateTime DEFAULT_LAST_MODIFIED = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);

    private static final String DEFAULT_NOTES = "AAAAA";

    private static final String DEFAULT_VERSION_SQ = "AAAAA";

    private static final String DEFAULT_TITLE_SQ = "AAAAA";

    private static final String DEFAULT_DEFINITION_SQ = "AAAAA";

    private static final String DEFAULT_VERSION_BS = "AAAAA";

    private static final String DEFAULT_TITLE_BS = "AAAAA";

    private static final String DEFAULT_DEFINITION_BS = "AAAAA";

    private static final String DEFAULT_VERSION_BG = "AAAAA";

    private static final String DEFAULT_TITLE_BG = "AAAAA";

    private static final String DEFAULT_DEFINITION_BG = "AAAAA";

    private static final String DEFAULT_VERSION_HR = "AAAAA";

    private static final String DEFAULT_TITLE_HR = "AAAAA";

    private static final String DEFAULT_DEFINITION_HR = "AAAAA";

    private static final String DEFAULT_VERSION_CS = "AAAAA";

    private static final String DEFAULT_TITLE_CS = "AAAAA";

    private static final String DEFAULT_DEFINITION_CS = "AAAAA";

    private static final String DEFAULT_VERSION_DA = "AAAAA";

    private static final String DEFAULT_TITLE_DA = "AAAAA";

    private static final String DEFAULT_DEFINITION_DA = "AAAAA";

    private static final String DEFAULT_VERSION_NL = "AAAAA";

    private static final String DEFAULT_TITLE_NL = "AAAAA";

    private static final String DEFAULT_DEFINITION_NL = "AAAAA";

    private static final String DEFAULT_VERSION_EN = "AAAAA";

    private static final String DEFAULT_TITLE_EN = "AAAAA";

    private static final String DEFAULT_DEFINITION_EN = "AAAAA";

    private static final String DEFAULT_VERSION_ET = "AAAAA";

    private static final String DEFAULT_TITLE_ET = "AAAAA";

    private static final String DEFAULT_DEFINITION_ET = "AAAAA";

    private static final String DEFAULT_VERSION_FI = "AAAAA";

    private static final String DEFAULT_TITLE_FI = "AAAAA";

    private static final String DEFAULT_DEFINITION_FI = "AAAAA";

    private static final String DEFAULT_VERSION_FR = "AAAAA";

    private static final String DEFAULT_TITLE_FR = "AAAAA";

    private static final String DEFAULT_DEFINITION_FR = "AAAAA";

    private static final String DEFAULT_VERSION_DE = "AAAAA";

    private static final String DEFAULT_TITLE_DE = "AAAAA";

    private static final String DEFAULT_DEFINITION_DE = "AAAAA";

    private static final String DEFAULT_VERSION_EL = "AAAAA";

    private static final String DEFAULT_TITLE_EL = "AAAAA";

    private static final String DEFAULT_DEFINITION_EL = "AAAAA";

    private static final String DEFAULT_VERSION_HU = "AAAAA";

    private static final String DEFAULT_TITLE_HU = "AAAAA";

    private static final String DEFAULT_DEFINITION_HU = "AAAAA";

    private static final String DEFAULT_VERSION_IT = "AAAAA";

    private static final String DEFAULT_TITLE_IT = "AAAAA";

    private static final String DEFAULT_DEFINITION_IT = "AAAAA";

    private static final String DEFAULT_VERSION_JA = "AAAAA";

    private static final String DEFAULT_TITLE_JA = "AAAAA";

    private static final String DEFAULT_DEFINITION_JA = "AAAAA";

    private static final String DEFAULT_VERSION_LT = "AAAAA";

    private static final String DEFAULT_TITLE_LT = "AAAAA";

    private static final String DEFAULT_DEFINITION_LT = "AAAAA";

    private static final String DEFAULT_VERSION_MK = "AAAAA";

    private static final String DEFAULT_TITLE_MK = "AAAAA";

    private static final String DEFAULT_DEFINITION_MK = "AAAAA";

    private static final String DEFAULT_VERSION_NO = "AAAAA";

    private static final String DEFAULT_TITLE_NO = "AAAAA";

    private static final String DEFAULT_DEFINITION_NO = "AAAAA";

    private static final String DEFAULT_VERSION_PL = "AAAAA";

    private static final String DEFAULT_TITLE_PL = "AAAAA";

    private static final String DEFAULT_DEFINITION_PL = "AAAAA";

    private static final String DEFAULT_VERSION_PT = "AAAAA";

    private static final String DEFAULT_TITLE_PT = "AAAAA";

    private static final String DEFAULT_DEFINITION_PT = "AAAAA";

    private static final String DEFAULT_VERSION_RO = "AAAAA";

    private static final String DEFAULT_TITLE_RO = "AAAAA";

    private static final String DEFAULT_DEFINITION_RO = "AAAAA";

    private static final String DEFAULT_VERSION_RU = "AAAAA";

    private static final String DEFAULT_TITLE_RU = "AAAAA";

    private static final String DEFAULT_DEFINITION_RU = "AAAAA";

    private static final String DEFAULT_VERSION_SR = "AAAAA";

    private static final String DEFAULT_TITLE_SR = "AAAAA";

    private static final String DEFAULT_DEFINITION_SR = "AAAAA";

    private static final String DEFAULT_VERSION_SK = "AAAAA";

    private static final String DEFAULT_TITLE_SK = "AAAAA";

    private static final String DEFAULT_DEFINITION_SK = "AAAAA";

    private static final String DEFAULT_VERSION_SL = "AAAAA";

    private static final String DEFAULT_TITLE_SL = "AAAAA";

    private static final String DEFAULT_DEFINITION_SL = "AAAAA";

    private static final String DEFAULT_VERSION_ES = "AAAAA";

    private static final String DEFAULT_TITLE_ES = "AAAAA";

    private static final String DEFAULT_DEFINITION_ES = "AAAAA";

    private static final String DEFAULT_VERSION_SV = "AAAAA";

    private static final String DEFAULT_TITLE_SV = "AAAAA";

    private static final String DEFAULT_DEFINITION_SV = "AAAAA";

    private String jwt;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Vocabulary createEntity(EntityManager em) {
        Vocabulary vocabulary = new Vocabulary();
        vocabulary.status(DEFAULT_STATUS);
        vocabulary.uri(DEFAULT_URI);
        vocabulary.notation(DEFAULT_NOTATION);
        vocabulary.versionNumber(DEFAULT_VERSION_NUMBER);
        vocabulary.initialPublication(DEFAULT_INITIAL_PUBLICATION);
        vocabulary.previousPublication(DEFAULT_PREVIOUS_PUBLICATION);
        vocabulary.archived(DEFAULT_ARCHIVED);
        vocabulary.withdrawn(DEFAULT_WITHDRAWN);
        vocabulary.discoverable(DEFAULT_DISCOVERABLE);
        vocabulary.sourceLanguage(DEFAULT_SOURCE_LANGUAGE);
        vocabulary.agencyId(DEFAULT_AGENCY_ID);
        vocabulary.agencyName(DEFAULT_AGENCY_NAME);
        vocabulary.agencyLogo(DEFAULT_AGENCY_LOGO );
        vocabulary.publicationDate(DEFAULT_PUBLICATION_DATE);
        vocabulary.lastModified(DEFAULT_LAST_MODIFIED);
        vocabulary.notes(DEFAULT_NOTES);
        vocabulary.versionSq(DEFAULT_VERSION_SQ);
        vocabulary.titleSq(DEFAULT_TITLE_SQ);
        vocabulary.definitionSq(DEFAULT_DEFINITION_SQ);
        vocabulary.versionBs(DEFAULT_VERSION_BS);
        vocabulary.titleBs(DEFAULT_TITLE_BS);
        vocabulary.definitionBs(DEFAULT_DEFINITION_BS);
        vocabulary.versionBg(DEFAULT_VERSION_BG);
        vocabulary.titleBg(DEFAULT_TITLE_BG);
        vocabulary.definitionBg(DEFAULT_DEFINITION_BG);
        vocabulary.versionHr(DEFAULT_VERSION_HR);
        vocabulary.titleHr(DEFAULT_TITLE_HR);
        vocabulary.definitionHr(DEFAULT_DEFINITION_HR);
        vocabulary.versionCs(DEFAULT_VERSION_CS);
        vocabulary.titleCs(DEFAULT_TITLE_CS);
        vocabulary.definitionCs(DEFAULT_DEFINITION_CS);
        vocabulary.versionDa(DEFAULT_VERSION_DA);
        vocabulary.titleDa(DEFAULT_TITLE_DA);
        vocabulary.definitionDa(DEFAULT_DEFINITION_DA);
        vocabulary.versionNl(DEFAULT_VERSION_NL);
        vocabulary.titleNl(DEFAULT_TITLE_NL);
        vocabulary.definitionNl(DEFAULT_DEFINITION_NL);
        vocabulary.versionEn(DEFAULT_VERSION_EN);
        vocabulary.titleEn(DEFAULT_TITLE_EN);
        vocabulary.definitionEn(DEFAULT_DEFINITION_EN);
        vocabulary.versionEt(DEFAULT_VERSION_ET);
        vocabulary.titleEt(DEFAULT_TITLE_ET);
        vocabulary.definitionEt(DEFAULT_DEFINITION_ET);
        vocabulary.versionFi(DEFAULT_VERSION_FI);
        vocabulary.titleFi(DEFAULT_TITLE_FI);
        vocabulary.definitionFi(DEFAULT_DEFINITION_FI);
        vocabulary.versionFr(DEFAULT_VERSION_FR);
        vocabulary.titleFr(DEFAULT_TITLE_FR);
        vocabulary.definitionFr(DEFAULT_DEFINITION_FR);
        vocabulary.versionDe(DEFAULT_VERSION_DE);
        vocabulary.titleDe(DEFAULT_TITLE_DE);
        vocabulary.definitionDe(DEFAULT_DEFINITION_DE);
        vocabulary.versionEl(DEFAULT_VERSION_EL);
        vocabulary.titleEl(DEFAULT_TITLE_EL);
        vocabulary.definitionEl(DEFAULT_DEFINITION_EL);
        vocabulary.versionHu(DEFAULT_VERSION_HU);
        vocabulary.titleHu(DEFAULT_TITLE_HU);
        vocabulary.definitionHu(DEFAULT_DEFINITION_HU);
        vocabulary.versionIt(DEFAULT_VERSION_IT);
        vocabulary.titleIt(DEFAULT_TITLE_IT);
        vocabulary.definitionIt(DEFAULT_DEFINITION_IT);
        vocabulary.versionJa(DEFAULT_VERSION_JA);
        vocabulary.titleJa(DEFAULT_TITLE_JA);
        vocabulary.definitionJa(DEFAULT_DEFINITION_JA);
        vocabulary.versionLt(DEFAULT_VERSION_LT);
        vocabulary.titleLt(DEFAULT_TITLE_LT);
        vocabulary.definitionLt(DEFAULT_DEFINITION_LT);
        vocabulary.versionMk(DEFAULT_VERSION_MK);
        vocabulary.titleMk(DEFAULT_TITLE_MK);
        vocabulary.definitionMk(DEFAULT_DEFINITION_MK);
        vocabulary.versionNo(DEFAULT_VERSION_NO);
        vocabulary.titleNo(DEFAULT_TITLE_NO);
        vocabulary.definitionNo(DEFAULT_DEFINITION_NO);
        vocabulary.versionPl(DEFAULT_VERSION_PL);
        vocabulary.titlePl(DEFAULT_TITLE_PL);
        vocabulary.definitionPl(DEFAULT_DEFINITION_PL);
        vocabulary.versionPt(DEFAULT_VERSION_PT);
        vocabulary.titlePt(DEFAULT_TITLE_PT);
        vocabulary.definitionPt(DEFAULT_DEFINITION_PT);
        vocabulary.versionRo(DEFAULT_VERSION_RO);
        vocabulary.titleRo(DEFAULT_TITLE_RO);
        vocabulary.definitionRo(DEFAULT_DEFINITION_RO);
        vocabulary.versionRu(DEFAULT_VERSION_RU);
        vocabulary.titleRu(DEFAULT_TITLE_RU);
        vocabulary.definitionRu(DEFAULT_DEFINITION_RU);
        vocabulary.versionSr(DEFAULT_VERSION_SR);
        vocabulary.titleSr(DEFAULT_TITLE_SR);
        vocabulary.definitionSr(DEFAULT_DEFINITION_SR);
        vocabulary.versionSk(DEFAULT_VERSION_SK);
        vocabulary.titleSk(DEFAULT_TITLE_SK);
        vocabulary.definitionSk(DEFAULT_DEFINITION_SK);
        vocabulary.versionSl(DEFAULT_VERSION_SL);
        vocabulary.titleSl(DEFAULT_TITLE_SL);
        vocabulary.definitionSl(DEFAULT_DEFINITION_SL);
        vocabulary.versionEs(DEFAULT_VERSION_ES);
        vocabulary.titleEs(DEFAULT_TITLE_ES);
        vocabulary.definitionEs(DEFAULT_DEFINITION_ES);
        vocabulary.versionSv(DEFAULT_VERSION_SV);
        vocabulary.titleSv(DEFAULT_TITLE_SV);
        vocabulary.definitionSv(DEFAULT_DEFINITION_SV);
        return vocabulary;
    }

    @Test
    void convertVocabularyDtoToJsonLdSkosMos() {
    }

    @Test
    void convertVocabulariesToJsonLd() {
    }

    @Test
    void convertVocabularyDtoToJsonLd() {
    }

    @Test
    void getDocIdFromVersionOrCode() {
    }
}
