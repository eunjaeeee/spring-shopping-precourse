package shopping.wish;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shopping.common.NotFoundException;
import shopping.product.Product;
import shopping.product.ProductRepository;

@Service
public class WishService {

    private final WishRepository wishRepository;
    private final ProductRepository productRepository;

    public WishService(WishRepository wishRepository, ProductRepository productRepository) {
        this.wishRepository = wishRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public WishResponse add(Long memberId, WishRequest request) {
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new NotFoundException("존재하지 않는 상품입니다."));
        Wish wish = wishRepository.save(new Wish(memberId, product));
        return WishResponse.from(wish);
    }
}
