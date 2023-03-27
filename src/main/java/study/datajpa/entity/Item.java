package study.datajpa.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Getter;

@Entity
@Getter
public class Item /*implements Persistable<String>*/ {

    @Id
    private Long id;

//    @Override
//    public String getId() {
//        return id;
//    }
//
//    @Override
//    public boolean isNew() {
//        return !StringUtils.hasText(id);
//    }
}
