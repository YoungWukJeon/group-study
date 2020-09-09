package group.study.demo.persistence.repository;

import group.study.demo.persistence.entity.ProductEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Flux;

public interface ProductRepository extends ReactiveSortingRepository<ProductEntity, Long> {
    @Query("select * from product where category = :category")
    Flux<ProductEntity> findAllByCategory(String category);

//    Flux<ProductEntity> findAll(Pageable pageable);
}
