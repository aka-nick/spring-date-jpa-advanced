package study.datajpa.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.NonUniqueResultException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
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

    @Autowired
    EntityManager em;

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

    @Test
    void paging() {
        Member member1 = new Member("memberA1", 10, null);
        Member member2 = new Member("memberA2", 10, null);
        Member member3 = new Member("memberA3", 10, null);
        Member member4 = new Member("memberA4", 10, null);
        Member member5 = new Member("memberA5", 10, null);

        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);
        memberRepository.save(member4);
        memberRepository.save(member5);

        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Direction.DESC, "username"));
        Page<Member> result = memberRepository.findByAge(10, pageRequest);

        // DTO로 쉽게 매핑하는 방법
        Page<MemberDto> resultDto = result.map(
                member -> new MemberDto(member.getId(), member.getUsername(), null));

//        Slice<Member> result = memberRepository.findSliceByAge(10, pageRequest);

        assertThat(result.getNumberOfElements()).isEqualTo(3); // 페이지 당 컨텐트 개수
        assertThat(result.getTotalElements()).isEqualTo(5); // 총 컨텐트 개수(totalCount)
        assertThat(result.getNumber()).isEqualTo(0); // 페이지 넘버
        assertThat(result.isFirst()).isTrue();
        assertThat(result.hasNext()).isTrue();
    }

    @Test
    void bulk() {
        Member member1 = new Member("memberA1", 10, null);
        Member member2 = new Member("memberA2", 14, null);
        Member member3 = new Member("memberA3", 15, null);
        Member member4 = new Member("memberA4", 16, null);
        Member member5 = new Member("memberA5", 17, null);

        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);
        memberRepository.save(member4);
        memberRepository.save(member5);

        int updated = memberRepository.bulkAgePlus(15);

        assertThat(updated).isEqualTo(3);

        Member memberA5 = memberRepository.findByUsername("memberA5");
        assertThat(memberA5.getAge()).isEqualTo(17); // 18이 아니다!!!
        /**
         벌크 연산은 영속성 컨텍스트의 엔티티를 통하지 않고 바로 쿼리를 날리는 것이기 때문에 유의해야 한다!!
         아래처럼 다시 영속성 컨텍스트를 초기화하여 문제를 회피할 수 있다!!
         */
        em.flush();
        em.clear();

        Member reMemberA5 = memberRepository.findByUsername("memberA5");
        assertThat(reMemberA5.getAge()).isEqualTo(18); // 이제 18이다

        /**
         혹은 리포지토리 메서드에 @Modifying(clearAutomatically = true) 라고 옵션을 주면
         벌크 연산 뒤에 알아서 엔티티매니저를 clear한다.

         하지만 가장 좋은 것은, 벌크 연산 뒤에는 그냥 아무 것도 안 하는 것이 문제 생길 일이 없고 좋다.
         */
    }

    @Test
    void findMemberLazy() {
        // given
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 15, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        // when
        System.out.println("============   WHEN   ===============");
        List<Member> members = memberRepository.findAll();
//        List<Member> members = mem1berRepository.findMemberFetch();
        for (Member member : members) {
            System.out.println("member = " + member.getUsername());
            System.out.println("member = " + member.getTeam().getName());
        }
    }

    @Test
    void queryHint() {
        Member member1 = new Member("member1", 10, null);
        memberRepository.save(member1);
        em.flush();
        em.clear();

        Member find = memberRepository.findReadonlyByUsername(member1.getUsername());
        find.setUsername("member2");
        em.flush();
    }

    @Test
    void lock() {
        Member member1 = new Member("member1", 10, null);
        memberRepository.save(member1);
        em.flush();
        em.clear();

        Member find = memberRepository.findLockByUsername(member1.getUsername()).get(0);
    }

}