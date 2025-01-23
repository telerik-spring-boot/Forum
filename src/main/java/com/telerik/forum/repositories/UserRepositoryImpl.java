package com.telerik.forum.repositories;

import com.telerik.forum.exceptions.EntityNotFoundException;
import com.telerik.forum.models.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.List;

public class UserRepositoryImpl implements UserRepository {

    private final SessionFactory sessionFactory;


    public UserRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }


    @Override
    public List<User> getAll() {
        try(Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery("from User", User.class);

            return query.list();
        }
    }

    @Override
    public User getById(int id) {
        try(Session session = sessionFactory.openSession()) {
            User user = session.get(User.class, id);

            if(user == null){
                throw new EntityNotFoundException("User", "id", id);
            }

            return user;
        }
    }

    @Override
    public User getByEmail(String email) {
        try(Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery("from User where emailAddress = :email", User.class);

            query.setParameter("email", email);

            User user = query.uniqueResult();

            if(user == null){
                throw new EntityNotFoundException("User", "email", email);
            }

            return user;
        }
    }

    @Override
    public void create(User user) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            session.persist(user);

            session.getTransaction().commit();
        }
    }

    @Override
    public void update(User user) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            session.merge(user);

            session.getTransaction().commit();
        }
    }

    @Override
    public void delete(int id) {
        try(Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            User user = session.get(User.class, id);

            if(user == null){
                throw new EntityNotFoundException("User", "id", id);
            }

            session.remove(user);

            session.getTransaction().commit();
        }
    }
}
