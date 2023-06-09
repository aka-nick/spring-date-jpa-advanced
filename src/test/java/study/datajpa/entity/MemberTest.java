package study.datajpa.entity;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.repository.MemberRepository;

@Transactional
@SpringBootTest
class MemberTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    EntityManager em;

    @Test
    void entityTest() {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 15, teamA);
        Member member3 = new Member("member3", 20, teamB);
        Member member4 = new Member("member4", 27, teamB);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        em.flush();
        em.clear();

        System.out.println("============   영속성 컨텍스트 클리어   =============");

        List<Member> members = em.createQuery("select m from Member m ",
                Member.class).getResultList();

        assertThat(members.get(0).getTeam().getName()).isEqualTo(teamA.getName());
        assertThat(members.get(1).getTeam().getName()).isEqualTo(teamA.getName());
        assertThat(members.get(2).getTeam().getName()).isEqualTo(teamB.getName());
        assertThat(members.get(3).getTeam().getName()).isEqualTo(teamB.getName());

    }

    @Test
    void jpaEventBaseEntityTest() throws InterruptedException {
        // given
        Member member = new Member("member1");
        memberRepository.save(member); // @PrePersist

        Thread.sleep(300);
        member.setUsername("memberUpdate");

        em.flush(); // @PreUpdate
        em.clear();

        // when
        Member find = memberRepository.findById(member.getId()).get();

        // then
        assertThat(find.getId()).isEqualTo(member.getId());
        assertThat(find.getUsername()).isEqualTo("memberUpdate");

        System.out.println("find = " + find.getCreatedDate());
        System.out.println("find = " + find.getLastModifiedDate());
        System.out.println("find = " + find.getCreatedBy());
        System.out.println("find = " + find.getLastModifiedBy());
    }
}