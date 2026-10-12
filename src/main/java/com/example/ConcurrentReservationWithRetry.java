package com.example;

import com.example.model.Reservation;
import com.example.service.ReservationRetryHandler;
import com.example.service.ReservationService;
import com.example.service.ReservationServiceImpl;

import java.time.LocalDateTime;

public class ConcurrentReservationWithRetry {
    public static void main(String[] args) {
        ReservationService service = new ReservationServiceImpl();
        try {
            LocalDateTime debut = LocalDateTime.of(2026, 11, 16, 14, 0);
            Reservation creee = service.creerReservation(new Reservation("Salle Cèdre", debut, debut.plusHours(2)));
            Reservation transactionA = service.trouverParId(creee.getId());
            Reservation transactionB = service.trouverParId(creee.getId());
            transactionA.setSalle("Salle Jasmin");
            Reservation validee = service.modifierReservation(transactionA);
            System.out.println("Transaction A : modification acceptée, version " + validee.getVersion() + ".");
            ReservationRetryHandler retry = new ReservationRetryHandler(service);
            Reservation finale = retry.modifierSalleAvecRetry(transactionB, "Salle Palmier");
            System.out.println("Transaction B : modification appliquée après relecture.");
            System.out.println("Salle finale : " + finale.getSalle() + " (version " + finale.getVersion() + ").");
        } finally {
            service.fermer();
        }
    }
}