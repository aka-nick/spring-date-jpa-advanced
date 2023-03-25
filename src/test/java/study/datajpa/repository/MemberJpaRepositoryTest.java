package study.datajpa.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

@Transactional
@SpringBootTest
class MemberJpaRepositoryTest {

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Test
    void save() {
        Member member = new Member("memberA");

        Member saved = memberJpaRepository.save(member);

        assertThat(saved.getId()).isEqualTo(member.getId());
        assertThat(saved.getUsername()).isEqualTo(member.getUsername());
        assertThat(saved).isEqualTo(member);
    }

}