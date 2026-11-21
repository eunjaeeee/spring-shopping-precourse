package shopping.member;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shopping.auth.AuthTokenService;
import shopping.common.DuplicateException;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthTokenService authTokenService;

    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder,
            AuthTokenService authTokenService) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.authTokenService = authTokenService;
    }

    @Transactional
    public TokenResponse register(MemberRequest request) {
        validateNewEmail(request.email());
        String encodedPassword = passwordEncoder.encode(request.password());
        Member member = memberRepository.save(new Member(request.email(), encodedPassword));
        return new TokenResponse(authTokenService.issue(member.getId()));
    }

    private void validateNewEmail(String email) {
        if (memberRepository.existsByEmail(email)) {
            throw new DuplicateException("이미 가입된 이메일입니다.");
        }
    }
}
