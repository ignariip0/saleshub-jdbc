package application;

import model.dao.DaoFactory;
import model.dao.DepartmentDao;
import model.entities.Department;

import java.util.List;

public class Program2 {
        public static void main(String[] args) {
            DepartmentDao departmentDao = DaoFactory.createDepartmentDao();

            System.out.println("==== TEST 1: department findById ====");
            Department dep = departmentDao.findById(2);
            System.out.println(dep);

            System.out.println("\n==== TEST 2: department findAll ====");
            List<Department> list = departmentDao.findAll();
            for (Department obj : list) {
                System.out.println(obj);
            }

            System.out.println("\n==== TEST 3: department insert ====");
            Department newDep = new Department(null, "Toys");
            departmentDao.insert(newDep);
            System.out.println("Insert completed! New Id = " + newDep.getId());

            System.out.println("\n==== TEST 4: department update ====");
            dep = departmentDao.findById(1);
            dep.setName("Computers Updated");
            departmentDao.update(dep);
            System.out.println("Update completed!");

            System.out.println("\n==== TEST 5: department deleteById ====");
            departmentDao.deleteById(newDep.getId());
            System.out.println("Delete completed!");
        }
    }

