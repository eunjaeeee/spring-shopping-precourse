package shopping.product;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

@SpringBootTest
@AutoConfigureMockMvc
@Import(FakeProfanityChecker.class)
class ProductApiTest {

    @Autowired
    private MockMvcTester mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("상품 이름, 가격, 이미지 URL을 입력받아 상품을 등록한다")
    void createProduct() {
        String request = """
                {"name": "사과", "price": 1000, "imageUrl": "https://example.com/apple.png"}
                """;

        assertThat(postProduct(request))
                .hasStatus(HttpStatus.CREATED)
                .bodyJson()
                .extractingPath("$.name").isEqualTo("사과");
    }

    @Test
    @DisplayName("상품 등록에 성공하면 상품 정보를 저장한다")
    void saveProduct() {
        String request = """
                {"name": "바나나", "price": 2000, "imageUrl": "https://example.com/banana.png"}
                """;

        assertThat(postProduct(request)).hasStatus(HttpStatus.CREATED);
        assertThat(productRepository.findAll())
                .extracting(Product::getName)
                .contains("바나나");
    }

    @Test
    @DisplayName("비속어가 포함된 상품 이름은 등록을 거부한다")
    void rejectProfanityName() {
        String request = """
                {"name": "badword", "price": 1000, "imageUrl": "https://example.com/bad.png"}
                """;

        assertThat(postProduct(request))
                .failure()
                .hasRootCauseInstanceOf(IllegalArgumentException.class)
                .hasRootCauseMessage("상품 이름에 비속어를 사용할 수 없습니다.");
        assertThat(productRepository.findAll())
                .extracting(Product::getName)
                .doesNotContain(FakeProfanityChecker.PROFANITY);
    }

    private MockMvcTester.MockMvcRequestBuilder postProduct(String body) {
        return mockMvc.post()
                .uri("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body);
    }
}
