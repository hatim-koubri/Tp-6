package com.example.service;

import com.example.model.Reservation;

import javax.persistence.OptimisticLockException;

public class ReservationRetryHandler {
    private static final int MAX_RETRIES = 3;
    private final ReservationService service;

    public ReservationRetryHandler(ReservationService service) {
        this.service = service;
    }

    public Reservation modifierSalleAvecRetry(Reservation reservationDetachee, String salleSouhaitee) {
        Long id = reservationDetachee.getId();
        Reservation tentative = reservationDetachee;
        for (int numero = 1; numero <= MAX_RETRIES; numero++) {
            tentative.setSalle(salleSouhaitee);
            try {
                return service.modifierReservation(tentative);
            } catch (OptimisticLockException conflit) {
                if (numero == MAX_RETRIES)
                    throw new OptimisticLockException("Échec après " + MAX_RETRIES + " tentatives.", conflit);
                System.out.println("Conflit détecté : relecture avant la tentative " + (numero + 1) + ".");
                tentative = service.trouverParId(id);
                if (tentative == null) throw new IllegalArgumentException("Réservation non trouvée : " + id);
            }
        }
        throw new IllegalStateException("Fin inattendue de la boucle de retry.");
    }
}