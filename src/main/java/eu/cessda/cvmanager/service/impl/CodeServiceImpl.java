package eu.cessda.cvmanager.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.cessda.cvmanager.domain.Code;
import eu.cessda.cvmanager.repository.CodeRepository;
import eu.cessda.cvmanager.repository.search.CodeSearchRepository;
import eu.cessda.cvmanager.service.CodeService;
import eu.cessda.cvmanager.service.dto.CodeDTO;
import eu.cessda.cvmanager.service.mapper.CodeMapper;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing Code.
 */
@Service
@Transactional
public class CodeServiceImpl implements CodeService {

    private final Logger log = LoggerFactory.getLogger(CodeServiceImpl.class);

    private final CodeRepository codeRepository;

    private final CodeMapper codeMapper;

    private final CodeSearchRepository codeSearchRepository;

    public CodeServiceImpl(CodeRepository codeRepository, CodeMapper codeMapper, CodeSearchRepository codeSearchRepository) {
        this.codeRepository = codeRepository;
        this.codeMapper = codeMapper;
        this.codeSearchRepository = codeSearchRepository;
    }

    /**
     * Save a code.
     *
     * @param codeDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public CodeDTO save(CodeDTO codeDTO) {
        log.debug("Request to save Code : {}", codeDTO);
        Code code = codeMapper.toEntity(codeDTO);
        code = codeRepository.save(code);
        CodeDTO result = codeMapper.toDto(code);
        codeSearchRepository.save(code);
        return result;
    }

    /**
     * Get all the codes.
     *
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public List<CodeDTO> findAll() {
        log.debug("Request to get all Codes");
        return codeRepository.findAll().stream()
            .map(codeMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }
    
    /**
     * Get all the codes.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<CodeDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Codes");
        return codeRepository.findAll(pageable)
            .map(codeMapper::toDto);
    }

    /**
     * Get one code by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public CodeDTO findOne(Long id) {
        log.debug("Request to get Code : {}", id);
        Code code = codeRepository.getOne(id);
        return codeMapper.toDto(code);
    }

    /**
     * Delete the code by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Code : {}", id);
        codeRepository.deleteById(id);
        codeSearchRepository.deleteById(id);
    }

    /**
     * Search for the code corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<CodeDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Codes for query {}", query);
        Page<Code> result = codeSearchRepository.search(queryStringQuery(query), pageable);
        return result.map(codeMapper::toDto);
    }
}
