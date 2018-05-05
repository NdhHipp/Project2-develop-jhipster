package app.repository.search;

import app.domain.Poster;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Poster entity.
 */
public interface PosterSearchRepository extends ElasticsearchRepository<Poster, Long> {
}
