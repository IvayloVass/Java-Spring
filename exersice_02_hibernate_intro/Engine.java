package exersice_02_hibernate_intro;

import exersice_02_hibernate_intro.entities.*;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Set;


public class Engine implements Runnable {

    private final EntityManager entityManager;
    private final BufferedReader bufferedReader;

    public Engine(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.bufferedReader = new BufferedReader(new InputStreamReader(System.in));
    }

    @Override
    public void run() {

        System.out.println("Please enter exercise number");
        try {
            int exerciseNum = Integer.parseInt(bufferedReader.readLine().trim());
            switch (exerciseNum) {
                case 2:
                    exerciseTwoChangeCasing();
                    break;
                case 3:
                    exerciseThreeContainsEmployee();
                    break;
                case 4:
                    exerciseFourSalaryOver();
                    break;
                case 5:
                    exerciseFiveEmployeesFromDep();
                    break;
                case 6:
                    exerciseSixAddingNewAddressAndUpdatingEmployee();
                    break;
                case 7:
                    exerciseSevenAddressesWithEmployeeCount();
                    break;
                case 8:
                    exerciseEightGetEmployeeWithProject();
                    break;
                case 9:
                    exerciseNineFindLatest10Projects();
                    break;
                case 10:
                    exerciseTenIncreaseSalaries();
                    break;
                case 11:
                    exerciseElevenFindEmployeesByFirstName();
                case 12:
                    exerciseTwelveEmployeesMaximumSalaries();
                case 13:
                    exerciseThirteenRemoveTowns();
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private void exerciseTwoChangeCasing() {

        entityManager.getTransaction().begin();

        Query query = entityManager.createQuery("UPDATE Town t SET t.name = UPPER(t.name) WHERE length(t.name) <= :len");

        query.setParameter("len", 5);
        System.out.println(query.executeUpdate());

        entityManager.getTransaction().commit();
        entityManager.close();
    }

    private void exerciseThreeContainsEmployee() throws IOException {

        System.out.println("Please enter the name you searching for:");

        String searchedName = bufferedReader.readLine();

        Long singleResult = entityManager
                .createQuery("SELECT COUNT(e) FROM Employee e WHERE CONCAT(e.firstName, ' ', e.lastName) = :n", Long.class)
                .setParameter("n", searchedName)
                .getSingleResult();

        System.out.println(singleResult == 0 ? "No" : "YES");

    }

    private void exerciseFourSalaryOver() {

        BigDecimal num = new BigDecimal(50000);

        entityManager.createQuery("SELECT e.firstName FROM Employee e WHERE e.salary > :s", String.class)
                .setParameter("s", num).getResultList().forEach(System.out::println);


    }

    private void exerciseFiveEmployeesFromDep() {

        String depName = "Research and Development";
        entityManager.createQuery("SELECT e FROM Employee e" +
                        " WHERE e.department.name = :dep_name ORDER BY e.salary, e.id", Employee.class)
                .setParameter("dep_name", depName)
                .getResultList().forEach(employee -> System.out.printf("%s %s from %s - $%.2f\n",
                        employee.getFirstName(), employee.getLastName(), employee.getDepartment().getName(), employee.getSalary()));


    }

    private void exerciseSixAddingNewAddressAndUpdatingEmployee() throws IOException {

        System.out.println("Please enter employee's last name below:");
        String employeeLastName = bufferedReader.readLine();

        Address address = addAddress("Vitoshka 15");

        Employee employee = entityManager.createQuery("SELECT e FROM Employee e WHERE e.lastName = :em_last_name", Employee.class)
                .setParameter("em_last_name", employeeLastName)
                .getSingleResult();


        employee.setAddress(address);

        entityManager.close();
    }

    private Address addAddress(String addressText) {

        Address address = new Address();
        address.setText(addressText);

        entityManager.getTransaction().begin();
        entityManager.persist(address);
        entityManager.getTransaction().commit();

        return address;
    }

    private void exerciseSevenAddressesWithEmployeeCount() {

        List<Address> addresses = entityManager.createQuery("SELECT a FROM Address a ORDER BY a.employees.size DESC", Address.class)
                .setMaxResults(10)
                .getResultStream().toList();

        for (Address address : addresses) {
            System.out.printf("%s, %s - %d employees\n", address.getText(), address.getTown().getName(), address.getEmployees().size());
        }

    }


    private void exerciseEightGetEmployeeWithProject() {

        Employee employee = entityManager.find(Employee.class, 147);

        System.out.printf("%s %s - %s\n", employee.getFirstName(), employee.getLastName(), employee.getJobTitle());
        employee.getProjects()
                .stream()
                .sorted(Comparator.comparing(Project::getName))
                .forEach(p -> System.out.println(p.getName()));

    }

    private void exerciseNineFindLatest10Projects() {

        List<Project> last10Projects = entityManager.createQuery("SELECT p FROM Project p ORDER BY p.startDate DESC", Project.class)
                .setMaxResults(10).getResultList();

        StringBuilder builder = new StringBuilder();
        last10Projects.stream()
                .sorted(Comparator.comparing(Project::getName))
                .forEach(p -> {
                    builder.append("Project name: ").append(p.getName()).append(System.lineSeparator())
                            .append("   Project Description: ").append(p.getDescription()).append(System.lineSeparator())
                            .append("   Project Start Date:").append(p.getStartDate()).append(System.lineSeparator())
                            .append("   Project End Date: ").append(p.getEndDate()).append(System.lineSeparator());

                });

        System.out.print(builder);

    }

    private void exerciseTenIncreaseSalaries() {

        entityManager.getTransaction().begin();

        int affectedEmployees = entityManager.createQuery("Update Employee e SET e.salary = e.salary * 1.12 " +
                        " WHERE e.department.id IN (:dep_ids)")
                .setParameter("dep_ids", Set.of(1, 2, 4, 11))
                .executeUpdate();

        entityManager.createQuery("SELECT e FROM Employee e WHERE e.department.id IN (:dep_ids)", Employee.class)
                .setParameter("dep_ids", Set.of(1, 2, 4, 11))
                .getResultList().forEach(employee ->
                        System.out.printf("%s %s $%.2f\n", employee.getFirstName(), employee.getLastName(), employee.getSalary()));


        entityManager.getTransaction().commit();
        entityManager.close();

    }

    private void exerciseElevenFindEmployeesByFirstName() throws IOException {

        System.out.println("Please enter first name pattern below:");

        String pattern = bufferedReader.readLine();

        entityManager.createQuery("SELECT e FROM Employee e WHERE e.firstName LIKE :pattern", Employee.class)
                .setParameter("pattern", pattern + "%")
                .getResultList()
                .forEach(e -> System.out.printf("%s %s - %s ($%.2f)\n", e.getFirstName(), e.getLastName(), e.getJobTitle(), e.getSalary()));
    }

    @SuppressWarnings("unchecked")
    private void exerciseTwelveEmployeesMaximumSalaries() {
        List<Object[]> result = entityManager.createNativeQuery("SELECT d.name, MAX(e.salary) AS max_salary FROM employees AS e\n" +
                        "JOIN departments AS d on e.department_id = d.department_id\n" +
                        "GROUP BY d.name\n" +
                        "HAVING max_salary NOT BETWEEN 30000 AND 70000;")
                .getResultList();

        for (Object[] rows : result) {
            System.out.println(rows[0] + " " + rows[1]);

        }
    }

    private void exerciseThirteenRemoveTowns() throws IOException {

        System.out.println("Please enter town name below:");

        String townName = bufferedReader.readLine();

        Town town = getTownByName(townName);

        Integer townId = town.getId();

        List<Address> addresses = getAddressesByTownId(townId);

        entityManager.getTransaction().begin();

        String str = addresses.size() == 1 ? "address" : "addresses";
        addresses.forEach(entityManager::remove);

        entityManager.remove(town);

        entityManager.getTransaction().commit();
        entityManager.close();

        System.out.printf("%d %s in %s deleted", addresses.size(), str, townName);

    }

    private List<Address> getAddressesByTownId(Integer townId) {
        return entityManager.createQuery("SELECT a FROM Address a WHERE a.town.id = :id", Address.class)
                .setParameter("id", townId)
                .getResultList();
    }

    private Town getTownByName(String townName) {
        return entityManager.createQuery("Select t FROM Town t WHERE t.name = :t_name", Town.class)
                .setParameter("t_name", townName)
                .getSingleResult();
    }

}
