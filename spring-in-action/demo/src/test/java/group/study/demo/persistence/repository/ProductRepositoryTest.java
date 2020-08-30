package group.study.demo.persistence.repository;

import group.study.demo.persistence.entity.ProductEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

@DataJpaTest
class ProductRepositoryTest {
    @Autowired
    private ProductRepository productRepository;

    @Test
    void testFindAll() {
        System.out.println( productRepository.findAll() );
    }

    @Test
    void testFindByCategory() {

        List<ProductEntity> productEntityList = productRepository.findAllByCategory("차량", Pageable.unpaged());

        System.out.println(productEntityList);

        for (ProductEntity productEntity : productEntityList){
            assertEquals("차량", productEntity.getCategory());
        }
    }
}