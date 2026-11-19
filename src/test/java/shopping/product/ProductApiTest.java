package shopping.product;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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
                .hasStatus(HttpStatus.BAD_REQUEST)
                .bodyJson()
                .extractingPath("$.message").isEqualTo("상품 이름에 비속어를 사용할 수 없습니다.");
        assertThat(productRepository.findAll())
                .extracting(Product::getName)
                .doesNotContain(FakeProfanityChecker.PROFANITY);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "가나다라마바사아자차카타파하가나", "사과!"})
    @DisplayName("입력값이 유효하지 않으면 400과 오류 메시지를 응답한다")
    void respondBadRequestForInvalidName(String name) {
        String request = """
                {"name": "%s", "price": 1000, "imageUrl": "https://example.com/a.png"}
                """.formatted(name);

        assertThat(postProduct(request))
                .hasStatus(HttpStatus.BAD_REQUEST)
                .bodyJson()
                .extractingPath("$.message").asString().isNotBlank();
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    @DisplayName("상품 가격이 0원 이하이면 400과 오류 메시지를 응답한다")
    void respondBadRequestForInvalidPrice(int price) {
        String request = """
                {"name": "사과", "price": %d, "imageUrl": "https://example.com/a.png"}
                """.formatted(price);

        assertThat(postProduct(request))
                .hasStatus(HttpStatus.BAD_REQUEST)
                .bodyJson()
                .extractingPath("$.message").isEqualTo("상품 가격은 1원 이상이어야 합니다.");
    }

    private MockMvcTester.MockMvcRequestBuilder postProduct(String body) {
        return mockMvc.post()
                .uri("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body);
    }
}
