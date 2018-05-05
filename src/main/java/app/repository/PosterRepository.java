package app.repository;

import app.domain.Poster;
import org.springframework.stereotype.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the Poster entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PosterRepository extends JpaRepository<Poster, Long> {
    List<Poster> findAllByOrderByIdDesc();
}
