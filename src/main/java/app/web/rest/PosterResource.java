package app.web.rest;

import com.codahale.metrics.annotation.Timed;
import app.domain.Poster;

import app.repository.PosterRepository;
import app.repository.search.PosterSearchRepository;
import app.web.rest.errors.BadRequestAlertException;
import app.web.rest.util.HeaderUtil;
import app.web.rest.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Poster.
 */
@RestController
@RequestMapping("/api")
public class PosterResource {

    private final Logger log = LoggerFactory.getLogger(PosterResource.class);

    private static final String ENTITY_NAME = "poster";

    private final PosterRepository posterRepository;

    private final PosterSearchRepository posterSearchRepository;

    public PosterResource(PosterRepository posterRepository, PosterSearchRepository posterSearchRepository) {
        this.posterRepository = posterRepository;
        this.posterSearchRepository = posterSearchRepository;
    }

    /**
     * POST  /posters : Create a new poster.
     *
     * @param poster the poster to create
     * @return the ResponseEntity with status 201 (Created) and with body the new poster, or with status 400 (Bad Request) if the poster has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/posters")
    @Timed
    public ResponseEntity<Poster> createPoster(@Valid @RequestBody Poster poster) throws URISyntaxException {
        log.debug("REST request to save Poster : {}", poster);
        if (poster.getId() != null) {
            throw new BadRequestAlertException("A new poster cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Poster result = posterRepository.save(poster);
        posterSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/posters/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /posters : Updates an existing poster.
     *
     * @param poster the poster to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated poster,
     * or with status 400 (Bad Request) if the poster is not valid,
     * or with status 500 (Internal Server Error) if the poster couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/posters")
    @Timed
    public ResponseEntity<Poster> updatePoster(@Valid @RequestBody Poster poster) throws URISyntaxException {
        log.debug("REST request to update Poster : {}", poster);
        if (poster.getId() == null) {
            return createPoster(poster);
        }
        Poster result = posterRepository.save(poster);
        posterSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, poster.getId().toString()))
            .body(result);
    }

    /**
     * GET  /posters : get all the posters.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of posters in body
     */
    @GetMapping("/posters")
    @Timed
    public ResponseEntity<List<Poster>> getAllPosters(Pageable pageable) {
        log.debug("REST request to get a page of Posters");
        Page<Poster> page = posterRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/posters");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /posters/:id : get the "id" poster.
     *
     * @param id the id of the poster to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the poster, or with status 404 (Not Found)
     */
    @GetMapping("/posters/{id}")
    @Timed
    public ResponseEntity<Poster> getPoster(@PathVariable Long id) {
        log.debug("REST request to get Poster : {}", id);
        Poster poster = posterRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(poster));
    }

    /**
     * DELETE  /posters/:id : delete the "id" poster.
     *
     * @param id the id of the poster to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/posters/{id}")
    @Timed
    public ResponseEntity<Void> deletePoster(@PathVariable Long id) {
        log.debug("REST request to delete Poster : {}", id);
        posterRepository.delete(id);
        posterSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/posters?query=:query : search for the poster corresponding
     * to the query.
     *
     * @param query the query of the poster search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/posters")
    @Timed
    public ResponseEntity<List<Poster>> searchPosters(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Posters for query {}", query);
        Page<Poster> page = posterSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/posters");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * 
     */
    @GetMapping("/poster/findiddesc")
    @Timed
    public List<Poster> findiddesc(){
        return posterRepository.findAllByOrderByIdDesc();
    }
    
}
