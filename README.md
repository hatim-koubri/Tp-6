## Captures des résultats

### 1. Détection d’un conflit optimiste

La transaction A modifie la réservation et incrémente sa version. La transaction B, qui possède une ancienne version, est rejetée. La valeur enregistrée en base reste `Salle Oasis`.

<img width="1560" height="1016" alt="5S" src="https://github.com/user-attachments/assets/67b7e3a9-2904-48f9-aace-91a41bed17db" />

### 2. Gestion du conflit avec retry

La première tentative de la transaction B rencontre un conflit. Le programme recharge ensuite la réservation et applique la modification avec la nouvelle version.

<img width="1707" height="1002" alt="2W" src="https://github.com/user-attachments/assets/363d5ea2-ca6e-4ab9-9daa-202a563b5c5d" />

### 3. Résultat après la nouvelle tentative

La modification est appliquée après relecture. La salle finale est `Salle Palmier` et sa version est `2`.

<img width="1575" height="1010" alt="3W" src="https://github.com/user-attachments/assets/03aebc76-d2cd-4b4d-b23b-c321c937d6d6" />
