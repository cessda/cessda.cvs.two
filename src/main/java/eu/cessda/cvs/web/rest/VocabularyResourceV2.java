package eu.cessda.cvs.web.rest;

import eu.cessda.cvs.domain.Vocabulary;
import eu.cessda.cvs.service.ExportService;
import eu.cessda.cvs.service.VocabularyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;


/**
 * REST controller for managing {@link Vocabulary}.
 */
@RestController
@RequestMapping("/v2")
public class VocabularyResourceV2 {

    public static final String ATTACHMENT_FILENAME = "attachment; filename=";
    private final Logger log = LoggerFactory.getLogger(VocabularyResourceV2.class);

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final VocabularyService vocabularyService;

    public VocabularyResourceV2(VocabularyService vocabularyService) {
        this.vocabularyService = vocabularyService;
    }

    /**
     *  get a PDF file of vocabulary {cv} with version {v} with included versions {lv}
     *
     * @param cv controlled vocabulary
     * @param v controlled vocabulary version
     * @param lv included version to be exported with format language_version e.g en-1.0_de-1.0.1
     * @return
     * @throws IOException
     */
    @GetMapping("/download/pdf/{cv}/{v}")
    public ResponseEntity<Resource> getVocabularyInPdf(
        HttpServletRequest request,
        @PathVariable String cv,
        @PathVariable String v,
        @RequestParam(name = "lv", required = true) String lv
    ) throws IOException {
        log.debug("REST request to get a PDF file of vocabulary {} with version {} with included versions {}", cv, v, lv);
        File pdfFile = vocabularyService.generateVocabularyPublishFileDownload( cv, v, lv, ExportService.DownloadType.PDF, request );

        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(pdfFile.toPath()) );
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT_FILENAME + pdfFile.getName() );
        return ResponseEntity.ok()
            .headers(headers)
            .contentType(MediaType.parseMediaType("application/pdf"))
            .body(resource);
    }

    /**
     *  get a DOCX file of vocabulary {cv} with version {v} with included versions {lv}
     *
     * @param cv controlled vocabulary
     * @param v controlled vocabulary version
     * @param lv included version to be exported with format language_version e.g en-1.0_de-1.0.1
     * @return
     * @throws IOException
     */
    @GetMapping("/download/docx/{cv}/{v}")
    public ResponseEntity<Resource> getVocabularyInDocx(
        HttpServletRequest request,
        @PathVariable String cv,
        @PathVariable String v,
        @RequestParam(name = "lv", required = true) String lv
    ) throws IOException {
        log.debug("REST request to get a WORD-DOCX file of vocabulary {} with version {} with included versions {}", cv, v, lv);
        File wordFile = vocabularyService.generateVocabularyPublishFileDownload( cv, v, lv, ExportService.DownloadType.WORD, request );

        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(wordFile.toPath()) );
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT_FILENAME + wordFile.getName() );
        return ResponseEntity.ok()
            .headers(headers)
            .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
            .body(resource);
    }

    /**
     *  get a HTML file of vocabulary {cv} with version {v} with included versions {lv}
     *
     * @param cv controlled vocabulary
     * @param v controlled vocabulary version
     * @param lv included version to be exported with format language_version e.g en-1.0_de-1.0.1
     * @return
     * @throws IOException
     */
    @GetMapping("/download/html/{cv}/{v}")
    public ResponseEntity<Resource> getVocabularyInHtml(
        HttpServletRequest request,
        @PathVariable String cv,
        @PathVariable String v,
        @RequestParam(name = "lv", required = true) String lv
    ) throws IOException {
        log.debug("REST request to get a HTML file of vocabulary {} with version {} with included versions {}", cv, v, lv);
        File htmlFile = vocabularyService.generateVocabularyPublishFileDownload( cv, v, lv, ExportService.DownloadType.HTML, request );

        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(htmlFile.toPath()) );
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT_FILENAME + htmlFile.getName() );
        return ResponseEntity.ok()
            .headers(headers)
            .contentType(MediaType.parseMediaType("text/html"))
            .body(resource);
    }

    /**
     *  get a SKOS file of vocabulary {cv} with version {v} with included versions {lv}
     *
     * @param cv controlled vocabulary
     * @param v controlled vocabulary version
     * @param lv included version to be exported with format language_version e.g en-1.0_de-1.0.1
     * @return
     * @throws IOException
     */
    @GetMapping("/download/rdf/{cv}/{v}")
    public ResponseEntity<Resource> getVocabularyInSkos(
        HttpServletRequest request,
        @PathVariable String cv,
        @PathVariable String v,
        @RequestParam(name = "lv", required = true) String lv
    ) throws IOException {
        log.debug("REST request to get a SKOS-RDF file of vocabulary {} with version {} with included versions {}", cv, v, lv);
        File rdfFile = vocabularyService.generateVocabularyPublishFileDownload( cv, v, lv, ExportService.DownloadType.SKOS, request );

        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(rdfFile.toPath()) );
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT_FILENAME + rdfFile.getName() );
        return ResponseEntity.ok()
            .headers(headers)
            .contentType(MediaType.parseMediaType("text/xml"))
            .body(resource);
    }

}
