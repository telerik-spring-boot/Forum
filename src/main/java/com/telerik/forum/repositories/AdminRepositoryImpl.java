package com.telerik.forum.repositories;

import com.telerik.forum.exceptions.EntityNotFoundException;
import com.telerik.forum.models.Admin;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.List;

public class AdminRepositoryImpl implements AdminRepository {

    private final SessionFactory sessionFactory;

    public AdminRepositoryImpl(SessionFactory sessionFactory) {
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
        try(Session session = sessionFactory.openSession()) {
            Query<Admin> query = session.createQuery("from Admin where User.id = :id", Admin.class);

            query.setParameter("id", id);

            Admin admin = query.uniqueResult();

            if(admin == null){
                throw new EntityNotFoundException("Admin", "user.id", id);
            }

            return admin;
        }
    }

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

            Query<Admin> query = session.createQuery("from Admin where User.id = :id", Admin.class);
            query.setParameter("id", userId);

            Admin admin = query.uniqueResult();

            if(admin == null){
                throw new EntityNotFoundException("Admin", "user.id", userId);
            }

            session.remove(admin);

            session.getTransaction().commit();
        }
    }
}
