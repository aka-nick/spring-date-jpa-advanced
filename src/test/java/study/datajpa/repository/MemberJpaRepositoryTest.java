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

    @Test
    void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");

        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        Member by1 = memberJpaRepository.findById(member1.getId()).get();
        Member by2 = memberJpaRepository.findById(member2.getId()).get();

        // 단건 입력 & 조회 테스트
        assertThat(member1).isEqualTo(by1);
        assertThat(member2).isEqualTo(by2);

        // 전체 조회 & 카운트 테스트
        assertThat(memberJpaRepository.findAll().size())
                .isEqualTo(memberJpaRepository.count());

        // 삭제 테스트
        memberJpaRepository.delete(member1);
        memberJpaRepository.delete(member2);
        assertThat(memberJpaRepository.count()).isEqualTo(0);
    }

    @Test
    void findByUsernameAndGreaterThan() {
        Member m1 = new Member("AAA", 10, null);
        Member m2 = new Member("AAA", 20, null);

        memberJpaRepository.save(m1);
        memberJpaRepository.save(m2);

        List<Member> find = memberJpaRepository.findByUsernameAndGreaterThan("AAA", 15);

        assertThat(find.size()).isEqualTo(1);
        assertThat(find.get(0).getUsername()).isEqualTo("AAA");
        assertThat(find.get(0).getAge()).isGreaterThan(15);

    }

    @Test
    void paging() {
        Member member1 = new Member("memberA1", 10, null);
        Member member2 = new Member("memberA2", 10, null);
        Member member3 = new Member("memberA3", 10, null);
        Member member4 = new Member("memberA4", 10, null);
        Member member5 = new Member("memberA5", 10, null);

        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);
        memberJpaRepository.save(member3);
        memberJpaRepository.save(member4);
        memberJpaRepository.save(member5);

        List<Member> byPage = memberJpaRepository.findByPage(10, 0, 3);
        long l = memberJpaRepository.totalCount(10);

        assertThat(byPage.size()).isEqualTo(3);
        assertThat(l).isEqualTo(5);
    }
}