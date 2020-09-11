package group.study.demo.persistence.repository;

import group.study.demo.persistence.entity.ProductEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

public interface ProductRepository extends R2dbcRepository<ProductEntity, Long> {
//    @Query("select * from product where category = :category")
    Flux<ProductEntity> findAllByCategory(String category, Pageable pageable);
    Flux<ProductEntity> findAllBy(Pageable pageable);
}
