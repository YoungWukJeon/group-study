package group.study.demo.persistence.repository;

import group.study.demo.persistence.entity.ProductEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

public interface ProductRepository extends R2dbcRepository<ProductEntity, Long> {
    @Query("select * from product where category = :category")
    Flux<ProductEntity> findByCategory(String category);
}
