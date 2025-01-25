package com.telerik.forum.repositories;

import com.telerik.forum.models.Admin;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AdminRepositoryOldImpl implements AdminRepositoryOld {

    private final SessionFactory sessionFactory;

    @Autowired
    public AdminRepositoryOldImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<Admin> getAll() {
        try (Session session = sessionFactory.openSession()) {

            Query<Admin> query = session.createQuery("from Admin", Admin.class);

            return query.list();
        }
    }

    @Override
    public Admin getByUserId(int id) {
        try (Session session = sessionFactory.openSession()) {
            Query<Admin> query = session.createQuery("from Admin where user.id = :id", Admin.class);

            query.setParameter("id", id);

            return query.uniqueResult();
        }
    }

//    @Override
//    public boolean existsById(int id) {
//        try (Session session = sessionFactory.openSession()) {
//            Query<Long> query = session.createQuery("select count(*) from Admin where user.id = :id", Long.class);
//
//            query.setParameter("id", id);
//
//            Long count = query.uniqueResult();
//
//            return count != null && count > 0;
//        }
//    }
//
//    @Override
//    public boolean isActiveAdmin(int id) {
//        try (Session session = sessionFactory.openSession()) {
//            Query<Long> query = session.createQuery("select count(*) from Admin where user.id = :id and user.blocked = false", Long.class);
//
//            query.setParameter("id", id);
//
//            Long count = query.uniqueResult();
//
//            return count != null && count > 0;
//        }
//    }

    @Override
    public void create(Admin admin) {
        try(Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            session.persist(admin);

            session.getTransaction().commit();
        }
    }

    @Override
    public void update(Admin admin) {
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

            Query<Admin> query = session.createQuery("from Admin where user.id = :id", Admin.class);

            query.setParameter("id", userId);

            Admin admin = query.uniqueResult();

            session.remove(admin);

            session.getTransaction().commit();
        }
    }
}
