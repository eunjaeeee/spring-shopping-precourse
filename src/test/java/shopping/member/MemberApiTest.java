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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

@SpringBootTest
@AutoConfigureMockMvc
class MemberApiTest {

    @Autowired
    private MockMvcTester mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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

        assertThat(postRegister(request)).hasStatus(HttpStatus.CREATED);
        assertThat(memberRepository.findByEmail("test@example.com")).isPresent();
    }

    @Test
    @DisplayName("비밀번호를 해시 처리하여 저장한다")
    void savePasswordAsHash() {
        String request = """
                {"email": "hash@example.com", "password": "password123"}
                """;

        assertThat(postRegister(request)).hasStatus(HttpStatus.CREATED);
        String savedPassword = memberRepository.findByEmail("hash@example.com")
                .orElseThrow()
                .getPassword();
        assertThat(savedPassword).isNotEqualTo("password123");
        assertThat(passwordEncoder.matches("password123", savedPassword)).isTrue();
    }

    @Test
    @DisplayName("이미 가입된 이메일이면 회원가입을 거부한다")
    void rejectDuplicateEmail() {
        String request = """
                {"email": "dup@example.com", "password": "password123"}
                """;
        postRegister(request).exchange();

        assertThat(postRegister(request))
                .hasStatus(HttpStatus.CONFLICT)
                .bodyJson()
                .extractingPath("$.message").isEqualTo("이미 가입된 이메일입니다.");
        assertThat(memberRepository.count()).isEqualTo(1);
    }

    private MockMvcTester.MockMvcRequestBuilder postRegister(String body) {
        return mockMvc.post()
                .uri("/api/members/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body);
    }
}
