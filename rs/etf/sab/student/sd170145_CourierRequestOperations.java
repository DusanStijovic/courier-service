/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.CourierRequestOperation;

/**
 *
 * @author ducati
 */
public class sd170145_CourierRequestOperations implements CourierRequestOperation{

    @Override
    public boolean insertCourierRequest(String userName, String driverLicenceNumber) {
        if(userName == null || driverLicenceNumber == null) return false;
        if(sd170145_UserOperations.checkIfUserExist(userName) == false) return false;
        if(sd170145_CourierOperations.checkIfCourierExist(userName) == true) return false;
        String sqlCheckIfUserExist = "SELECT * FROM ZahtevZaKurira WHERE korisnickoIme=? OR brojVozackeDozvole=?";
        Connection connection = DataBase.getDataBase().getConnection();
        try(
                  PreparedStatement statement = connection.prepareStatement(sqlCheckIfUserExist,
                     ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
           ) {
                statement.setString(1, userName);
                statement.setString(2, driverLicenceNumber);
                try (
                        ResultSet resultSet = statement.executeQuery();
                     ){
                            if (resultSet.isBeforeFirst()) {  
                             return false;
                            } 
                            resultSet.moveToInsertRow();
                            resultSet.updateString("korisnickoIme", userName);
                            resultSet.updateString("brojVozackeDozvole", driverLicenceNumber);
                            resultSet.insertRow();
                            return true;


                    }   
        } catch (SQLException ex) {
            Logger.getLogger(sd170145_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;  
    }

    @Override
    public boolean deleteCourierRequest(String userName) {
        if(userName == null) return false;
        String sqlCheckIfUserExist = "DELETE FROM ZahtevZaKurira WHERE korisnickoIme=?";
        Connection connection = DataBase.getDataBase().getConnection();
        try(
                PreparedStatement statement = connection.prepareStatement(sqlCheckIfUserExist,
                     ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
           ) {
                statement.setString(1, userName);
                return statement.executeUpdate() > 0;
                   
        } catch (SQLException ex) {
            Logger.getLogger(sd170145_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false; 
    }

    @Override
    public boolean changeDriverLicenceNumberInCourierRequest(String userName, String driveLicence) {
        if(userName == null || driveLicence == null) return false;
        String sqlCheckIfUserExist = "SELECT * FROM ZahtevZaKurira WHERE korisnickoIme=?";
        Connection connection = DataBase.getDataBase().getConnection();
        try(
                PreparedStatement statement = connection.prepareStatement(sqlCheckIfUserExist,
                     ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
           ) {
                statement.setString(1, userName);
                try (
                        ResultSet resultSet = statement.executeQuery();
                     ){
                            if (resultSet.isBeforeFirst()) { 
                                resultSet.next();
                                resultSet.updateString("brojVozackeDozvole", driveLicence);
                                resultSet.updateRow();
                                return true;
                            } 
                            return false;
                    }   
        } catch (SQLException ex) {
            Logger.getLogger(sd170145_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false; 
    }

    @Override
    public List<String> getAllCourierRequests() {  
        String sql = "SELECT korisnickoIme FROM ZahtevZaKurira";
        List<String> allUsers = new ArrayList<>();
        Connection connection = DataBase.getDataBase().getConnection();
        try(   
                Statement statement = connection.createStatement(
                     ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
           ) {
            try (
                    ResultSet resultSet = statement.executeQuery(sql);
                 ){
                    while(resultSet.next()){
                        allUsers.add(resultSet.getString("korisnickoIme"));
                    }
                }   
            } catch (SQLException ex) {
                Logger.getLogger(sd170145_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
            return allUsers;
    }

    @Override
    public boolean grantRequest(String userName) {
        if(userName == null) return false;
        String sqlCheckIfUserExist = "SELECT * FROM ZahtevZaKurira WHERE korisnickoIme=?";
        Connection connection = DataBase.getDataBase().getConnection();
        try(
                PreparedStatement statement = connection.prepareStatement(sqlCheckIfUserExist,
                     ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
           ) {
                statement.setString(1, userName);
                try (
                        ResultSet resultSet = statement.executeQuery();
                     ){
                        if (resultSet.isBeforeFirst()) { 
                            resultSet.next();
                            new sd170145_CourierOperations().insertCourier(userName, resultSet.getString("brojVozackeDozvole"));
                            resultSet.deleteRow();
                            return true;
                        } 
                        return false;
                    }   
        } catch (SQLException ex) {
            Logger.getLogger(sd170145_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false; 
    }

    public static void deleteAllCourierRequest(){
        String sqlDeleteCityies = "DELETE FROM ZahtevZaKurira";
        Connection connection = DataBase.getDataBase().getConnection();
        try(   
                Statement statement = connection.createStatement();
           ) {
                statement.executeUpdate(sqlDeleteCityies);
        } catch (SQLException ex) {
            Logger.getLogger(sd170145_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
