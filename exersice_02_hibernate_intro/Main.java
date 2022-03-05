package exersice_02_hibernate_intro;

import javax.persistence.*;

public class Main {
    public static void main(String[] args) {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("test");
        EntityManager entityManager = emf.createEntityManager();

        Engine engine = new Engine(entityManager);

        engine.run();

    }
}
