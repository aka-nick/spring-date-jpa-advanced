package study.datajpa.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

@Transactional
@SpringBootTest
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Test
    void save() {
        Member member = new Member("newMember");

        Member saved = memberRepository.save(member);

        assertThat(saved).isEqualTo(member);
    }

    @Test
    void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");

        memberRepository.save(member1);
        memberRepository.save(member2);

        Member by1 = memberRepository.findById(member1.getId()).get();
        Member by2 = memberRepository.findById(member2.getId()).get();

        // 단건 입력 & 조회 테스트
        assertThat(member1).isEqualTo(by1);
        assertThat(member2).isEqualTo(by2);

        // 전체 조회 & 카운트 테스트
        assertThat(memberRepository.findAll().size())
                .isEqualTo(memberRepository.count());

        // 삭제 테스트
        memberRepository.delete(member1);
        memberRepository.delete(member2);
        assertThat(memberRepository.count()).isEqualTo(0);
    }

    @Test
    void findByUsernameAndGreaterThan() {
        Member m1 = new Member("AAA", 10, null);
        Member m2 = new Member("AAA", 20, null);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> find = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        assertThat(find.size()).isEqualTo(1);
        assertThat(find.get(0).getUsername()).isEqualTo("AAA");
        assertThat(find.get(0).getAge()).isGreaterThan(15);

    }

    @Test
    void jpqlTest() {
        Member m1 = new Member("AAA", 10, null);
        Member m2 = new Member("AAA", 20, null);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> find = memberRepository.findMember("AAA", 10);

        assertThat(find.size()).isEqualTo(1);
        assertThat(find.get(0)).isEqualTo(m1);
    }
}