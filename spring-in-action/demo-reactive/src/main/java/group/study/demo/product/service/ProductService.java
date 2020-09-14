package group.study.demo.product.service;

import group.study.demo.persistence.entity.ProductEntity;
import group.study.demo.persistence.repository.ProductRepository;
import group.study.demo.product.model.Category;
import group.study.demo.product.model.request.ProductSearchRequest;
import group.study.demo.product.model.response.ProductResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    public Flux<ProductResponse> getAllProducts(ProductSearchRequest productSearchRequest) {
        Pageable pageable = PageRequest.of(productSearchRequest.getPageNum(), productSearchRequest.getPageSize());

        return productRepository.findAllBy(pageable)
                .map(this::productEntityToProductResponse);
    }

    public Flux<ProductResponse> getProductsByCategory(ProductSearchRequest productSearchRequest) {
        Pageable pageable = PageRequest.of(productSearchRequest.getPageNum(), productSearchRequest.getPageSize());
        Category category = Category.findByCategory(productSearchRequest.getCategory());

        return productRepository.findAllByCategory(category.getName(), pageable)
                .map(this::productEntityToProductResponse);
    }

    public Mono<ProductResponse> getProductByNo(Long no) {
        // TODO: 2020-08-10 예외처리
        return productRepository.findById(no)
                .map(this::productEntityToProductResponse);
    }

    private ProductResponse productEntityToProductResponse(ProductEntity productEntity) {
        return new ProductResponse(
                productEntity.getNo(),
                productEntity.getName(),
                productEntity.getCategory(),
                productEntity.getDescription(),
                productEntity.getPrice(),
                productEntity.getImage(),
                productEntity.getCreateDate(),
                productEntity.getUpdateDate());
    }
}
