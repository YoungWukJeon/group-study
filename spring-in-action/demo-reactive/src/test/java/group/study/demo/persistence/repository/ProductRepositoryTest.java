package group.study.demo.persistence.repository;

import group.study.demo.persistence.entity.ProductEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

@DataR2dbcTest
class ProductRepositoryTest {
    @Autowired
    private ProductRepository productRepository;

    @Test
    void testFindAll() {
        productRepository.findAll().subscribe(System.out::println);
    }

    @Test
    void testFindByCategory() {
        Flux<ProductEntity> productEntities = productRepository.findAllByCategory("차량");

        productEntities.subscribe(System.out::println);

//        for (ProductEntity productEntity : productEntityList){
//            assertEquals("차량", productEntity.getCategory());
//        }
    }
}