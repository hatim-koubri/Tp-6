package com.example.service;

import com.example.model.Reservation;

import javax.persistence.*;
import java.util.List;

public class ReservationServiceImpl implements ReservationService {
    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("reservationPU");

    @Override
    public Reservation creerReservation(Reservation reservation) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(reservation);
            tx.commit();
            return reservation;
        } catch (RuntimeException erreur) {
            if (tx.isActive()) tx.rollback();
            throw erreur;
        } finally {
            em.close();
        }
    }

    @Override
    public Reservation modifierReservation(Reservation reservation) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            // merge garde la version transportée par l'objet détaché.
            Reservation modifiee = em.merge(reservation);
            em.flush();
            tx.commit();
            return modifiee;
        } catch (RuntimeException erreur) {
            if (tx.isActive()) tx.rollback();
            if (estConflitOptimiste(erreur))
                throw new OptimisticLockException("Cette réservation a été modifiée par une autre transaction.", erreur);
            throw erreur;
        } finally {
            em.close();
        }
    }

    private boolean estConflitOptimiste(Throwable erreur) {
        for (Throwable cause = erreur; cause != null; cause = cause.getCause()) {
            if (cause instanceof OptimisticLockException || cause instanceof org.hibernate.StaleStateException || cause instanceof org.hibernate.StaleObjectStateException)
                return true;
        }
        return false;
    }

    @Override
    public Reservation trouverParId(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Reservation.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Reservation> listerReservations() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("select r from Reservation r order by r.id", Reservation.class).getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public void fermer() {
        if (emf.isOpen()) emf.close();
    }
}