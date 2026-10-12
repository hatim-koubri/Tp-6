package com.example;

import com.example.model.Reservation;
import com.example.service.ReservationService;
import com.example.service.ReservationServiceImpl;

import javax.persistence.OptimisticLockException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ConcurrentReservationSimulator {
    public static void main(String[] args) {
        ReservationService service = new ReservationServiceImpl();
        try {
            LocalDateTime debut = LocalDateTime.of(2026, 11, 15, 9, 0);
            Reservation creee = service.creerReservation(new Reservation("Salle Atlas", debut, debut.plusHours(2)));
            Reservation transactionA = service.trouverParId(creee.getId());
            Reservation transactionB = service.trouverParId(creee.getId());
            System.out.println("Réservation " + creee.getId() + " créée : " + creee.getSalle() + " (version " + creee.getVersion() + ").");
            transactionA.setSalle("Salle Oasis");
            Reservation validee = service.modifierReservation(transactionA);
            System.out.println("Transaction A : modification acceptée, version " + validee.getVersion() + ".");
            transactionB.setSalle("Salle Jardin");
            try {
                service.modifierReservation(transactionB);
                System.out.println("Transaction B : modification acceptée.");
            } catch (OptimisticLockException conflit) {
                System.out.println("Transaction B : conflit optimiste détecté — " + conflit.getMessage());
                System.out.println("La valeur en base reste : " + service.trouverParId(creee.getId()).getSalle() + ".");
            }
            System.out.println("\nRéservations enregistrées :");
            service.listerReservations().forEach(r -> System.out.println("- " + r.getSalle() + " | version " + r.getVersion() + " | " + r.getDateDebut().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));
        } finally {
            service.fermer();
        }
    }
}