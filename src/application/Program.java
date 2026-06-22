package application;

import model.dao.DaoFactory;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

import java.time.LocalDate;

public class Program {
    static void main(String[] args) {

        SellerDao sellerDao = DaoFactory.createSellerDao();
        
        System.out.println("==== TEST 1: seller findById ====");
        Seller seller = sellerDao.findById(7);

        System.out.println(seller);
    }
}