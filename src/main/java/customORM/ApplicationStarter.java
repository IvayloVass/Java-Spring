package customORM;

import customORM.entity.Department;
import customORM.entity.Employee;
import customORM.entity.User;
import ormFramework.core.EntityManager;
import ormFramework.core.EntityManagerFactory;


import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.sql.SQLException;

public class ApplicationStarter {

    public static void main(String[] args) throws SQLException, URISyntaxException,
            ClassNotFoundException, InvocationTargetException, InstantiationException,
            IllegalAccessException, NoSuchMethodException {

        EntityManager entityManager =
                EntityManagerFactory.create("mysql",
                        "localhost",
                        3306,
                        "",
                        "",
                        "test_orm",
                        ApplicationStarter.class);

        User user = new User("Pesho", 17);
        entityManager.doAlter(user);

//        User pesho = entityManager.findById(1, User.class);
//        pesho.setAge(30);
//        entityManager.persist(pesho);

//        User maria = entityManager.findById(2, User.class);
//        entityManager.delete(maria);

//        Department byId = entityManager.findById(2, Department.class);
    }

}
