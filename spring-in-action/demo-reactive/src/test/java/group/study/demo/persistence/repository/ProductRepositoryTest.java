package group.study.demo.persistence.repository;

import group.study.demo.common.config.R2dbcH2Config;
import group.study.demo.common.config.R2dbcH2ConfigTest;
import group.study.demo.common.config.Transaction;
import group.study.demo.persistence.entity.ProductEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

// TODO: 2020-09-10 실제로 service나 controller 적용, UserEntity에서 authorities부분 살리기, Security 살리기
@Slf4j
@DataR2dbcTest
@ComponentScan(basePackageClasses = {R2dbcH2Config.class})
class ProductRepositoryTest {
    @Autowired
    private DatabaseClient client;
    @Autowired
    private TransactionalOperator rxtx;
    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    public void transactionSetUp() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        Field field = Class.forName("group.study.demo.common.config.Transaction").getDeclaredField("rxtx");
        field.setAccessible(true);  // private, static field 접근
        field.set(null, rxtx);
        R2dbcH2ConfigTest r2dbcH2ConfigTest = new R2dbcH2ConfigTest();
        r2dbcH2ConfigTest.createSchema(client); // 스키마 초기화
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
                .description("설명")
                .image("이미지 경로")
                .createDate(LocalDateTime.now())
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
                .as(Transaction::withRollback)
                .as(StepVerifier::create)
                .assertNext(e -> {
                    assertNotNull(e.getNo());
                    assertEquals(productEntity1.getName(), e.getName());
                    assertEquals(productEntity1.getCategory(), e.getCategory());
                    assertEquals(productEntity1.getDescription(), e.getDescription());
                    assertEquals(productEntity1.getImage(), e.getImage());
                    assertEquals(productEntity1.getCreateDate().toLocalDate(), e.getCreateDate().toLocalDate());
                    assertEquals(productEntity1.getCreateDate().toLocalTime().toSecondOfDay(), e.getCreateDate().toLocalTime().toSecondOfDay());
                })
                .assertNext(e -> {
                    assertNotNull(e.getNo());
                    assertEquals(productEntity3.getName(), e.getName());
                    assertEquals(productEntity3.getCategory(), e.getCategory());
                })
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