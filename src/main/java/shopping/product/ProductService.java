package shopping.product;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProfanityChecker profanityChecker;

    public ProductService(ProductRepository productRepository, ProfanityChecker profanityChecker) {
        this.productRepository = productRepository;
        this.profanityChecker = profanityChecker;
    }

    @Transactional
    public ProductResponse create(ProductRequest request) {
        Price price = new Price(request.price());
        ImageUrl imageUrl = new ImageUrl(request.imageUrl());
        Product product = new Product(toCheckedName(request.name()), price, imageUrl);
        return ProductResponse.from(productRepository.save(product));
    }

    @Transactional(readOnly = true)
    public ProductResponse findById(Long id) {
        Product product = productRepository.findById(id).orElseThrow();
        return ProductResponse.from(product);
    }

    private ProductName toCheckedName(String value) {
        ProductName name = new ProductName(value);
        if (profanityChecker.containsProfanity(name.value())) {
            throw new IllegalArgumentException("상품 이름에 비속어를 사용할 수 없습니다.");
        }
        return name;
    }
}
