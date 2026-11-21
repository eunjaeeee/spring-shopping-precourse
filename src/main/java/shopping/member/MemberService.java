package shopping.member;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shopping.common.DuplicateException;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void register(MemberRequest request) {
        validateNewEmail(request.email());
        String encodedPassword = passwordEncoder.encode(request.password());
        memberRepository.save(new Member(request.email(), encodedPassword));
    }

    private void validateNewEmail(String email) {
        if (memberRepository.existsByEmail(email)) {
            throw new DuplicateException("이미 가입된 이메일입니다.");
        }
    }
}
