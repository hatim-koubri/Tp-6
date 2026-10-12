package com.example;

import com.example.model.Reservation;
import com.example.service.ReservationService;
import com.example.service.ReservationServiceImpl;
import org.junit.Test;
import javax.persistence.OptimisticLockException;
import java.time.LocalDateTime;
import static org.junit.Assert.*;

public class OptimisticLockingTest {
    @Test public void staleReservationIsRejectedAndVersionIncrements() {
        ReservationService service=new ReservationServiceImpl();
        try {
            LocalDateTime debut=LocalDateTime.of(2026,12,1,10,0);
            Reservation creee=service.creerReservation(new Reservation("Salle Test",debut,debut.plusHours(1)));
            Reservation copieA=service.trouverParId(creee.getId());
            Reservation copieB=service.trouverParId(creee.getId());
            copieA.setSalle("Salle A");
            Reservation maj=service.modifierReservation(copieA);
            assertEquals(1,maj.getVersion());
            copieB.setSalle("Salle B");
            try { service.modifierReservation(copieB); fail("La copie obsolète doit être refusée."); }
            catch (OptimisticLockException attendu) { assertTrue(attendu.getMessage().contains("modifiée")); }
            assertEquals("Salle A",service.trouverParId(creee.getId()).getSalle());
        } finally { service.fermer(); }
    }
}