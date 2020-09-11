package group.study.demo.persistence.repository;

import group.study.demo.common.config.DatabaseSchemaInitializer;
import group.study.demo.common.config.Transaction;
import group.study.demo.persistence.entity.ProductEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

// TODO: 2020-09-10 실제로 service나 controller 적용, UserEntity에서 authorities부분 살리기, Security 살리기
@Slf4j
@DataR2dbcTest
class ProductRepositoryTest {
    @Autowired
    private DatabaseClient client;
    @Autowired
    private TransactionalOperator rxtx;
    @Autowired
    private ProductRepository productRepository;

    @BeforeAll
    public static void schemaSetUp() {
        DatabaseSchemaInitializer.init();
    }

    @BeforeEach
    public void transactionSetUp() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        Field field = Class.forName("group.study.demo.common.config.Transaction").getDeclaredField("rxtx");
        field.setAccessible(true);  // private, static field 접근
        field.set(null, rxtx);
    }

    @Test
    public void testDatabaseClientExisted() {
        assertNotNull(client);
    }

    @Test
    public void testProductRepositoryExisted() {
        assertNotNull(productRepository);
    }

    @Test
    void testFindAll() {
        productRepository.findAll()
                .log()
                .as(StepVerifier::create)
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void testFindAll_pageable() {
        ProductEntity productEntity1 = ProductEntity.builder()
                .name("스프링 인 액션")
                .category("도서")
                .build();
        ProductEntity productEntity2 = ProductEntity.builder()
                .name("Porsche 718 Boxster")
                .category("차량")
                .build();
        ProductEntity productEntity3 = ProductEntity.builder()
                .name("모던 자바 인 액션")
                .category("도서")
                .build();
        insertProductEntities(productEntity1, productEntity2, productEntity3)
                .thenMany(productRepository.findAllBy(PageRequest.of(0, 2)))
                .log()
                .as(Transaction::withRollback)
                .as(StepVerifier::create)
                .expectNext(productEntity1)
                .expectNext(productEntity2)
                .verifyComplete();
    }

    @Test
    void testFindByCategory() {
        ProductEntity productEntity1 = ProductEntity.builder()
                .name("스프링 인 액션")
                .category("도서")
                .build();
        ProductEntity productEntity2 = ProductEntity.builder()
                .name("Porsche 718 Boxster")
                .category("차량")
                .build();
        ProductEntity productEntity3 = ProductEntity.builder()
                .name("모던 자바 인 액션")
                .category("도서")
                .build();
        insertProductEntities(productEntity1, productEntity2, productEntity3)
                .thenMany(productRepository.findAllByCategory("도서", Pageable.unpaged()))
                .log()
                .as(Transaction::withRollback)
                .as(StepVerifier::create)
                .expectNext(productEntity1)
                .expectNext(productEntity3)
                .verifyComplete();
    }

    @Test
    void testFindByCategory_해당하는_데이터가_없음() {
        ProductEntity productEntity1 = ProductEntity.builder()
                .name("스프링 인 액션")
                .category("도서")
                .build();

        insertProductEntities(productEntity1)
                .thenMany(productRepository.findAllByCategory("차량", Pageable.unpaged()))
                .log()
                .as(Transaction::withRollback)
                .as(StepVerifier::create)
                .expectNextCount(0)
                .verifyComplete();
    }

    private Flux<ProductEntity> insertProductEntities(ProductEntity... productEntities) {
        return this.productRepository.saveAll(List.of(productEntities));
    }
}