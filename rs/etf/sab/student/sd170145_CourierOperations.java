/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.CourierOperations;

/**
 *
 * @author ducati
 */
public class sd170145_CourierOperations implements CourierOperations{

    static int getCourierCity(String userName) {
        if(userName == null) return -1;
        String sqlCheckIfUserExist = "SELECT idGrad FROM Kurir"
                + " INNER JOIN Korisnik ON Kurir.korisnickoIme = Korisnik.korisnickoIme"
                + " INNER JOIN Adresa ON Korisnik.idAdresa = Adresa.idAdresa"
                + " WHERE Kurir.korisnickoIme=?";
        Connection connection = DataBase.getDataBase().getConnection();
        try(
                  PreparedStatement statement = connection.prepareStatement(sqlCheckIfUserExist);
           ) {
            statement.setString(1, userName);
            try (
                    ResultSet resultSet = statement.executeQuery();
                 ){
                    if(resultSet.isBeforeFirst()){  
                        resultSet.next();
                        return resultSet.getInt("idGrad");
                    }
                    return -1;
                }   
        } catch (SQLException ex) {
            Logger.getLogger(sd170145_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
       return -1;
        
    }

    @Override
    public boolean insertCourier(String userName, String driveLicenceNumber) {
        if(userName == null || driveLicenceNumber == null) return false;
        if(sd170145_UserOperations.checkIfUserExist(userName) == false) return false;
        String sqlCheckIfUserExist = "SELECT * FROM Kurir WHERE korisnickoIme = ? OR brojVozackeDozvole = ?";
        Connection connection = DataBase.getDataBase().getConnection();
        try(
                  PreparedStatement statement = connection.prepareStatement(sqlCheckIfUserExist,
                     ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
           ) {
            statement.setString(1, userName);
            statement.setString(2,driveLicenceNumber);
            try (
                    ResultSet resultSet = statement.executeQuery();
                ){
                    if (!resultSet.isBeforeFirst()) {  
                        resultSet.moveToInsertRow();
                        resultSet.updateString("korisnickoIme", userName);
                        resultSet.updateString("brojVozackeDozvole", driveLicenceNumber);
                        resultSet.insertRow();
                        return true;
                    }                       
                    return false;          
                }
        }catch(SQLException ex){
             Logger.getLogger(sd170145_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    @Override
    public boolean deleteCourier(String userName) {
        if(userName == null) return false;
        if(checkIfCourierInDrive(userName) == true) return false;//Ako je status 0 to znaci da ne vozi
        String deleteUser = "DELETE FROM Kurir WHERE korisnickoIme = ? AND status = 0";
        Connection connection = DataBase.getDataBase().getConnection();
        try(
                PreparedStatement preparedStatement = connection.prepareStatement(deleteUser, ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
            ){
                preparedStatement.setString(1, userName);
                return preparedStatement.executeUpdate() > 0;
        }   catch (SQLException ex) {
                Logger.getLogger(sd170145_UserOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public List<String> getCouriersWithStatus(int status) {
        
        if (status < 0 || status > 1) return null;
        String sql = "SELECT korisnickoIme FROM Kurir WHERE status = " + status;
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
    public List<String> getAllCouriers() {
        String sql = "SELECT korisnickoIme FROM Kurir";
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
    public BigDecimal getAverageCourierProfit(int numberOfDeliveres) {
        String sql = "";
        if(numberOfDeliveres > -1)
            sql = "SELECT sum(profit) as ukupanProfit FROM Kurir WHERE brojIsporucenihPaketa=" + numberOfDeliveres;
        else 
            sql = "SELECT sum(profit) as ukupanProfit FROM Kurir";
        Connection connection = DataBase.getDataBase().getConnection();
        try(   
                Statement statement = connection.createStatement();
           ) {
            try (
                    ResultSet resultSet = statement.executeQuery(sql);
                 ){
                    if(resultSet.isBeforeFirst()){
                        resultSet.next();
                        return resultSet.getBigDecimal("ukupanProfit");
                    } else 
                        return BigDecimal.ZERO;
                }   
            } catch (SQLException ex) {
                Logger.getLogger(sd170145_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
       return BigDecimal.valueOf(-1);
    }
    
    public static boolean checkIfCourierExist(String userName){
        if(userName == null) return false;
        String sqlCheckIfUserExist = "SELECT * FROM Kurir WHERE korisnickoIme=?";
        Connection connection = DataBase.getDataBase().getConnection();
        try(
                  PreparedStatement statement = connection.prepareStatement(sqlCheckIfUserExist);
           ) {
            statement.setString(1, userName);
            try (
                    ResultSet resultSet = statement.executeQuery();
                 ){
                    return resultSet.isBeforeFirst();  
                }   
        } catch (SQLException ex) {
            Logger.getLogger(sd170145_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
       return false;   
    }

    public static boolean checkIfCourierInDrive(String userName){
        if(userName == null) return false;
        String sqlCheckIfExist = "SELECT * FROM VozilaUVoznji WHERE korisnickoIme = ?";
        Connection connection = DataBase.getDataBase().getConnection();
        try(   
            PreparedStatement statement = connection.prepareStatement(sqlCheckIfExist);
            ) {
                statement.setString(1, userName);
                try(
                        ResultSet resultSet = statement.executeQuery();
                    ){
                        return resultSet.isBeforeFirst();
                }
        } catch (SQLException ex) {
            Logger.getLogger(sd170145_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
       
    public static void setCourierStatus(String userName, int newStatus){
        if(userName == null) return;
        String sqlCheckIfExist = "UPDATE Kurir SET status = ? WHERE korisnickoIme = ?";
        Connection connection = DataBase.getDataBase().getConnection();
        try(   
            PreparedStatement statement = connection.prepareStatement(sqlCheckIfExist);
            ) {
                statement.setInt(1, newStatus);
                statement.setString(2, userName);
                statement.executeUpdate();
                return;
        } catch (SQLException ex) {
            Logger.getLogger(sd170145_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return;
    }

    
    public static void increaseCourirDeliveredPackages(String userName){
        if(userName == null) return;
        String sqlCheckIfExist = "UPDATE Kurir SET brojIsporucenihPaketa = brojIsporucenihPaketa + 1 WHERE korisnickoIme = ?";
        Connection connection = DataBase.getDataBase().getConnection();
        try(   
            PreparedStatement statement = connection.prepareStatement(sqlCheckIfExist);
            ){
                statement.setString(1, userName);
                statement.executeUpdate();
                return;
        } catch (SQLException ex) {
            Logger.getLogger(sd170145_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return;
    }
  
    
}
