package app.web.rest;

import app.VtravelApp;

import app.domain.Poster;
import app.repository.PosterRepository;
import app.repository.search.PosterSearchRepository;
import app.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;

import javax.persistence.EntityManager;
import java.util.List;

import static app.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the PosterResource REST controller.
 *
 * @see PosterResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = VtravelApp.class)
public class PosterResourceIntTest {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final byte[] DEFAULT_IMAGE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_IMAGE = TestUtil.createByteArray(2, "1");
    private static final String DEFAULT_IMAGE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_IMAGE_CONTENT_TYPE = "image/png";

    private static final String DEFAULT_RECOMMEND = "AAAAAAAAAA";
    private static final String UPDATED_RECOMMEND = "BBBBBBBBBB";

    @Autowired
    private PosterRepository posterRepository;

    @Autowired
    private PosterSearchRepository posterSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restPosterMockMvc;

    private Poster poster;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final PosterResource posterResource = new PosterResource(posterRepository, posterSearchRepository);
        this.restPosterMockMvc = MockMvcBuilders.standaloneSetup(posterResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Poster createEntity(EntityManager em) {
        Poster poster = new Poster()
            .title(DEFAULT_TITLE)
            .image(DEFAULT_IMAGE)
            .imageContentType(DEFAULT_IMAGE_CONTENT_TYPE)
            .recommend(DEFAULT_RECOMMEND);
        return poster;
    }

    @Before
    public void initTest() {
        posterSearchRepository.deleteAll();
        poster = createEntity(em);
    }

    @Test
    @Transactional
    public void createPoster() throws Exception {
        int databaseSizeBeforeCreate = posterRepository.findAll().size();

        // Create the Poster
        restPosterMockMvc.perform(post("/api/posters")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(poster)))
            .andExpect(status().isCreated());

        // Validate the Poster in the database
        List<Poster> posterList = posterRepository.findAll();
        assertThat(posterList).hasSize(databaseSizeBeforeCreate + 1);
        Poster testPoster = posterList.get(posterList.size() - 1);
        assertThat(testPoster.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testPoster.getImage()).isEqualTo(DEFAULT_IMAGE);
        assertThat(testPoster.getImageContentType()).isEqualTo(DEFAULT_IMAGE_CONTENT_TYPE);
        assertThat(testPoster.getRecommend()).isEqualTo(DEFAULT_RECOMMEND);

        // Validate the Poster in Elasticsearch
        Poster posterEs = posterSearchRepository.findOne(testPoster.getId());
        assertThat(posterEs).isEqualToIgnoringGivenFields(testPoster);
    }

    @Test
    @Transactional
    public void createPosterWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = posterRepository.findAll().size();

        // Create the Poster with an existing ID
        poster.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restPosterMockMvc.perform(post("/api/posters")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(poster)))
            .andExpect(status().isBadRequest());

        // Validate the Poster in the database
        List<Poster> posterList = posterRepository.findAll();
        assertThat(posterList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = posterRepository.findAll().size();
        // set the field null
        poster.setTitle(null);

        // Create the Poster, which fails.

        restPosterMockMvc.perform(post("/api/posters")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(poster)))
            .andExpect(status().isBadRequest());

        List<Poster> posterList = posterRepository.findAll();
        assertThat(posterList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkRecommendIsRequired() throws Exception {
        int databaseSizeBeforeTest = posterRepository.findAll().size();
        // set the field null
        poster.setRecommend(null);

        // Create the Poster, which fails.

        restPosterMockMvc.perform(post("/api/posters")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(poster)))
            .andExpect(status().isBadRequest());

        List<Poster> posterList = posterRepository.findAll();
        assertThat(posterList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllPosters() throws Exception {
        // Initialize the database
        posterRepository.saveAndFlush(poster);

        // Get all the posterList
        restPosterMockMvc.perform(get("/api/posters?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(poster.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE.toString())))
            .andExpect(jsonPath("$.[*].imageContentType").value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].image").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE))))
            .andExpect(jsonPath("$.[*].recommend").value(hasItem(DEFAULT_RECOMMEND.toString())));
    }

    @Test
    @Transactional
    public void getPoster() throws Exception {
        // Initialize the database
        posterRepository.saveAndFlush(poster);

        // Get the poster
        restPosterMockMvc.perform(get("/api/posters/{id}", poster.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(poster.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE.toString()))
            .andExpect(jsonPath("$.imageContentType").value(DEFAULT_IMAGE_CONTENT_TYPE))
            .andExpect(jsonPath("$.image").value(Base64Utils.encodeToString(DEFAULT_IMAGE)))
            .andExpect(jsonPath("$.recommend").value(DEFAULT_RECOMMEND.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingPoster() throws Exception {
        // Get the poster
        restPosterMockMvc.perform(get("/api/posters/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePoster() throws Exception {
        // Initialize the database
        posterRepository.saveAndFlush(poster);
        posterSearchRepository.save(poster);
        int databaseSizeBeforeUpdate = posterRepository.findAll().size();

        // Update the poster
        Poster updatedPoster = posterRepository.findOne(poster.getId());
        // Disconnect from session so that the updates on updatedPoster are not directly saved in db
        em.detach(updatedPoster);
        updatedPoster
            .title(UPDATED_TITLE)
            .image(UPDATED_IMAGE)
            .imageContentType(UPDATED_IMAGE_CONTENT_TYPE)
            .recommend(UPDATED_RECOMMEND);

        restPosterMockMvc.perform(put("/api/posters")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedPoster)))
            .andExpect(status().isOk());

        // Validate the Poster in the database
        List<Poster> posterList = posterRepository.findAll();
        assertThat(posterList).hasSize(databaseSizeBeforeUpdate);
        Poster testPoster = posterList.get(posterList.size() - 1);
        assertThat(testPoster.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testPoster.getImage()).isEqualTo(UPDATED_IMAGE);
        assertThat(testPoster.getImageContentType()).isEqualTo(UPDATED_IMAGE_CONTENT_TYPE);
        assertThat(testPoster.getRecommend()).isEqualTo(UPDATED_RECOMMEND);

        // Validate the Poster in Elasticsearch
        Poster posterEs = posterSearchRepository.findOne(testPoster.getId());
        assertThat(posterEs).isEqualToIgnoringGivenFields(testPoster);
    }

    @Test
    @Transactional
    public void updateNonExistingPoster() throws Exception {
        int databaseSizeBeforeUpdate = posterRepository.findAll().size();

        // Create the Poster

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restPosterMockMvc.perform(put("/api/posters")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(poster)))
            .andExpect(status().isCreated());

        // Validate the Poster in the database
        List<Poster> posterList = posterRepository.findAll();
        assertThat(posterList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deletePoster() throws Exception {
        // Initialize the database
        posterRepository.saveAndFlush(poster);
        posterSearchRepository.save(poster);
        int databaseSizeBeforeDelete = posterRepository.findAll().size();

        // Get the poster
        restPosterMockMvc.perform(delete("/api/posters/{id}", poster.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean posterExistsInEs = posterSearchRepository.exists(poster.getId());
        assertThat(posterExistsInEs).isFalse();

        // Validate the database is empty
        List<Poster> posterList = posterRepository.findAll();
        assertThat(posterList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchPoster() throws Exception {
        // Initialize the database
        posterRepository.saveAndFlush(poster);
        posterSearchRepository.save(poster);

        // Search the poster
        restPosterMockMvc.perform(get("/api/_search/posters?query=id:" + poster.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(poster.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE.toString())))
            .andExpect(jsonPath("$.[*].imageContentType").value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].image").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE))))
            .andExpect(jsonPath("$.[*].recommend").value(hasItem(DEFAULT_RECOMMEND.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Poster.class);
        Poster poster1 = new Poster();
        poster1.setId(1L);
        Poster poster2 = new Poster();
        poster2.setId(poster1.getId());
        assertThat(poster1).isEqualTo(poster2);
        poster2.setId(2L);
        assertThat(poster1).isNotEqualTo(poster2);
        poster1.setId(null);
        assertThat(poster1).isNotEqualTo(poster2);
    }
}
