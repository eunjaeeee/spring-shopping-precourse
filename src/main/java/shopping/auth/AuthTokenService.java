package shopping.auth;

import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthTokenService {

    private final AuthTokenRepository authTokenRepository;

    public AuthTokenService(AuthTokenRepository authTokenRepository) {
        this.authTokenRepository = authTokenRepository;
    }

    @Transactional
    public String issue(Long memberId) {
        String token = UUID.randomUUID().toString();
        authTokenRepository.save(new AuthToken(token, memberId));
        return token;
    }
}
