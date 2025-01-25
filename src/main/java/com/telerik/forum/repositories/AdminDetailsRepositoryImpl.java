package com.telerik.forum.repositories;


import com.telerik.forum.models.Admin;
import com.telerik.forum.models.AdminDetails;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AdminDetailsRepositoryImpl implements AdminDetailsRepository {

    private final SessionFactory sessionFactory;

    public AdminDetailsRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }


    @Override
    public List<AdminDetails> getAll() {
        try(Session session = sessionFactory.openSession()) {

            String hql = "SELECT DISTINCT ad FROM AdminDetails ad " +
                    "LEFT JOIN FETCH ad.user u " +
                    "LEFT JOIN FETCH u.roles r " +
                    "WHERE r.name = :roleName";

            Query<AdminDetails> query = session.createQuery(hql, AdminDetails.class);

            query.setParameter("roleName", "ADMIN");

            return query.list();
        }
    }

    @Override
    public AdminDetails getByUserId(int id) {
        try(Session session = sessionFactory.openSession()) {

            String hql = "SELECT DISTINCT ad FROM AdminDetails ad " +
                    "LEFT JOIN FETCH ad.user u " +
                    "LEFT JOIN FETCH u.roles r " +
                    "WHERE u.id = :userId AND r.name = :roleName";

            Query<AdminDetails> query = session.createQuery(hql, AdminDetails.class);

            query.setParameter("id", id);
            query.setParameter("roleName", "ADMIN");

            return query.uniqueResult();
        }
    }

    @Override
    public void create(AdminDetails admin) {
        try(Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            session.persist(admin);

            session.getTransaction().commit();
        }
    }

    @Override
    public void update(AdminDetails admin) {
        try(Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            session.merge(admin);

            session.getTransaction().commit();
        }
    }

    @Override
    public void delete(int userId) {
        try(Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            Query<AdminDetails> query = session.createQuery("from AdminDetails where user.id = :id", AdminDetails.class);

            query.setParameter("id", userId);

            AdminDetails admin = query.uniqueResult();

            session.remove(admin);

            session.getTransaction().commit();
        }
    }
}
