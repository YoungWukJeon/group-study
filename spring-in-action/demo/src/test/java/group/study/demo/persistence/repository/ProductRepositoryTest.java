package group.study.demo.persistence.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class ProductRepositoryTest {
    @Autowired
    private ProductRepository productRepository;

    @Test
    void testFindAll() {
        System.out.println( productRepository.findAll() );
    }

    @Test
    void testFindByCategory() {
        System.out.println( productRepository.findByCategory("category") );
    }
}