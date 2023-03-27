package study.datajpa.repository;

import java.util.List;
import javax.persistence.EntityManager;
import study.datajpa.entity.Member;

public class MemberRepositoryImpl implements MemberCustomRepository{

    private final EntityManager em;

    public MemberRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public List<Member> findCustomMember() {
        return em.createQuery("select m from Member m where 200 = 200 ").getResultList();
    }
}
