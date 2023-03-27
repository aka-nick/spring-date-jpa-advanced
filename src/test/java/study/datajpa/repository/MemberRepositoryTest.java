package study.datajpa.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Optional;
import javax.persistence.NonUniqueResultException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

@Transactional
@SpringBootTest
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    TeamRepository teamRepository;

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

    @Test
    void findUsernameTest() {
        Member m1 = new Member("AAA", 10, null);
        Member m2 = new Member("AAA", 20, null);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> find = memberRepository.findUsernameList();

        assertThat(find.size()).isEqualTo(2);
    }

    @Test
    void findMemberDtoTest() {
        Team t1 = new Team("Team1");
        teamRepository.save(t1);

        Member m1 = new Member("AAA", 10, null);
        memberRepository.save(m1);
        m1.changeTeam(t1);

        MemberDto find = memberRepository.findMemberDto().get(0);

        assertThat(find.getUsername()).isEqualTo(m1.getUsername());
        assertThat(find.getTeamName()).isEqualTo(t1.getName());
    }

    @Test
    void findByNamesTest() {
        Member m1 = new Member("AAA1", 10, null);
        Member m2 = new Member("AAA2", 20, null);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> names = List.of("AAA1", "AAA2");
        List<Member> find = memberRepository.findByNames(names);

        assertThat(find.size()).isEqualTo(2);
    }

    @Test
    void findManyTypeNamesTest() {
        Member m1 = new Member("AAA1", 10, null);
        Member m2 = new Member("AAA2", 20, null);
        Member m3 = new Member("AAA2", 20, null);
        memberRepository.save(m1);
        memberRepository.save(m2);
        memberRepository.save(m3);

        List<Member> find = memberRepository.findListByUsername("AAA1");
        Member aaa1 = memberRepository.findByUsername("AAA1");
        Member um = memberRepository.findByUsername("어떻게회원이름이엄준식");
        Optional<Member> aaa11 = memberRepository.findOptionalByUsername("AAA1");

        assertThat(find.size()).isEqualTo(1);
        assertThat(aaa1).isEqualTo(m1);
        assertThat(aaa11.get()).isEqualTo(m1);
        assertThat(um).isNull(); // 결과가 없으면 예외를 터트리는 JPA와 달리, data jpa는 null을 반환한다.
                                        // 그러나 알아만 두고, 그냥 Optional을 쓰자.
        assertThatThrownBy(() -> memberRepository.findByUsername("AAA2"))
                .isInstanceOf(IncorrectResultSizeDataAccessException.class); // JPA가 발생시키는 NonUniqueResultSizeException을 data jpa가 래핑한 예외
    }
}