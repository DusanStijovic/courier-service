/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.GeneralOperations;

/**
 *
 * @author ducati
 */
public class sd170145_GeneralOperations implements GeneralOperations{

    @Override
    public void eraseAll() {
       //Mozda promeniti ovo na paketnu obradu, lepse mi tako    
       
        sd170145_PackageOperations.deleteAllPackages();
        sd170145_CourierRequestOperations.deleteAllCourierRequest();
        sd170145_StockroomOperations.deleteAllStockRoom();
        sd170145_VehicleOperations.deleteAllVehicle();
        sd170145_UserOperations.deleteAllUsers();
        sd170145_AddressOperations.deleteAllAdresses();
        sd170145_CityOperations.deleteAllCities();
    
        Connection connection = DataBase.getDataBase().getConnection();
        try(
            Statement statement = connection.createStatement();
            )
        {     
            statement.addBatch("DELETE FROM Paket");
            statement.addBatch("DELETE FROM ZahtevZaKurira");
            statement.addBatch("DELETE FROM Magacin");
            statement.addBatch("DELETE FROM Vozilo");
            statement.addBatch("DELETE FROM Korisnik");
            statement.addBatch("DELETE FROM Adresa");
            statement.addBatch("DELETE FROM Grad");
            statement.executeBatch();        
        } catch (SQLException ex) {
            Logger.getLogger(sd170145_GeneralOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    
    
    
    }
    
}
