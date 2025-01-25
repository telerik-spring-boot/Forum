package com.telerik.forum.repositories;


import com.telerik.forum.models.Role;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class RoleRepositoryImpl implements RoleRepository {

    private final SessionFactory sessionFactory;

    public RoleRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }


    @Override
    public Role findByName(String name) {
        try(Session session = sessionFactory.openSession()) {
            Query<Role> query = session.createQuery("from Role where name = :name", Role.class);

            query.setParameter("name", name);

            return query.getSingleResult();
        }
    }
}
