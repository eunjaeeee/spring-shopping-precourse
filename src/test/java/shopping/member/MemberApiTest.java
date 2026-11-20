package shopping.member;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

@SpringBootTest
@AutoConfigureMockMvc
class MemberApiTest {

    @Autowired
    private MockMvcTester mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("이메일과 비밀번호를 입력받아 회원을 등록한다")
    void registerMember() {
        String request = """
                {"email": "test@example.com", "password": "password123"}
                """;

        assertThat(mockMvc.post()
                .uri("/api/members/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .hasStatus(HttpStatus.CREATED);
        assertThat(memberRepository.findByEmail("test@example.com")).isPresent();
    }
}
