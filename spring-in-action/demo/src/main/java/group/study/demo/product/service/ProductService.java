package group.study.demo.product.service;

import group.study.demo.persistence.entity.ProductEntity;
import group.study.demo.persistence.repository.ProductRepository;
import group.study.demo.product.model.request.ProductSearchRequest;
import group.study.demo.product.model.response.ProductResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    public List<ProductResponse> getAllProducts(ProductSearchRequest productSearchRequest) {
        Pageable pageable = PageRequest.of(productSearchRequest.getPageNum(), productSearchRequest.getPageSize());

        return productRepository.findAll(pageable)
                .stream()
                .map(this::productEntityToProductResponse)
                .collect(Collectors.toList());
    }

    public ProductResponse getProductByNo(Long no) {
        // TODO: 2020-08-10 예외처리
        return productEntityToProductResponse(productRepository.findById(no).orElseThrow());
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
