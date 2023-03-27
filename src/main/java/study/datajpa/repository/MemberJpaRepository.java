package study.datajpa.repository;

import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import study.datajpa.entity.Member;

@Repository
public class MemberJpaRepository {

    private final EntityManager em;

    public MemberJpaRepository(EntityManager em) {
        this.em = em;
    }

    public Member save(Member member) {
        em.persist(member);
        return member;
    }

    public long count() {
        return em.createQuery("select count(m) from Member m ", Long.class)
                .getSingleResult();
    }

    public Optional<Member> findById(Long id) {
        return Optional.ofNullable(em.find(Member.class, id));
    }

    public Member find(Long id) {
        return em.find(Member.class, id);
    }

    public List<Member> findAll() {
        return em.createQuery("select m from Member m ", Member.class)
                .getResultList();
    }

    public void delete(Member member) {
        em.remove(member);
    }

    public List<Member> findByUsernameAndGreaterThan(String username, int age) {
        return em.createQuery(
                        "select m from Member m where m.username = :username and m.age > :age")
                .setParameter("username", username)
                .setParameter("age", age)
                .getResultList();
    }

    public List<Member> findByPage(int age, int offset, int limit) {
        return em.createQuery("select m from Member m where m.age = :age order by m.username desc")
                .setParameter("age", age)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }
    public long totalCount(int age) {
        return em.createQuery("select count(m) from Member m where m.age = :age", Long.class)
                .setParameter("age", age)
                .getSingleResult();
    }
}
