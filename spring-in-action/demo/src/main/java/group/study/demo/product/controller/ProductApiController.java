package group.study.demo.product.controller;

import group.study.demo.product.model.request.ProductSearchRequest;
import group.study.demo.product.model.response.ProductResponse;
import group.study.demo.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/product")
public class ProductApiController {
    @Autowired
    private ProductService productService;

    @GetMapping
    public List<ProductResponse> getList(ProductSearchRequest productSearchRequest) {
        if (productSearchRequest.getCategory() == null) {
            return productService.getAllProducts(productSearchRequest);
        }

        return productService.getProductsByCategory(productSearchRequest);
    }

}
