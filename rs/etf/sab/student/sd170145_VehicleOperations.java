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
import rs.etf.sab.operations.VehicleOperations;

/**
 *
 * @author ducati
 */
public class sd170145_VehicleOperations implements VehicleOperations{

  
public enum FuelType{PLIN,DIZEL,BENZIN}
    
    @Override
    public boolean insertVehicle(String licencePlateNumber, int fuelType, BigDecimal fuelConsumtion, BigDecimal capacity) {
        if(licencePlateNumber == null) return false;
        if(fuelConsumtion.compareTo(BigDecimal.ZERO) <= 0) return false;
        if(capacity.compareTo(BigDecimal.ZERO) <= 0) return false;
        if(fuelType < 0 || fuelType > 2) return false;
        String sql = "SELECT * FROM Vozilo WHERE registracioniBroj = ?";
        Connection connection = DataBase.getDataBase().getConnection();
        try(
                PreparedStatement statement = connection.prepareStatement(sql,
                     ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
           ) {
                statement.setString(1, licencePlateNumber);
                try (
                        ResultSet resultSet = statement.executeQuery();
                     ){
                        if (resultSet.isBeforeFirst()) {  
                            return false;
                        } 
                        resultSet.moveToInsertRow();
                        resultSet.updateString("registracioniBroj", licencePlateNumber);
                        resultSet.updateInt("tipGoriva", fuelType);
                        resultSet.updateBigDecimal("potrosnja", fuelConsumtion);
                        resultSet.updateBigDecimal("nosivost", capacity);
                        resultSet.insertRow();
                        return true;  
                    }   
        } catch (SQLException ex) {
            Logger.getLogger(sd170145_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;  
    }

    @Override
    public int deleteVehicles(String... vehicles) {
        String deleteVehicle = "DELETE FROM Vozilo WHERE registracioniBroj = ?";
        Connection connection = DataBase.getDataBase().getConnection();
        try(
                PreparedStatement preparedStatement = connection.prepareStatement(deleteVehicle, ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
            ){
            int brojObrisanih = 0;
            for(String licencePlate : vehicles){
                if(licencePlate == null) continue;
                if(checkIfVehicleInStockRoom(licencePlate) == true || checkIfVehicleInDrive(licencePlate) == false){
                    preparedStatement.setString(1, licencePlate);
                    brojObrisanih += preparedStatement.executeUpdate();
                }
            }
            return brojObrisanih;
        }   catch (SQLException ex) {
                Logger.getLogger(sd170145_UserOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;  
    }

    @Override
    public List<String> getAllVehichles() {
        String sql = "SELECT registracioniBroj FROM Vozilo";
        List<String> allVehicle = new ArrayList<>();
        Connection connection = DataBase.getDataBase().getConnection();
        try(   
                Statement statement = connection.createStatement(
                     ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
           ) {
                try (
                        ResultSet resultSet = statement.executeQuery(sql);
                     ){
                        while(resultSet.next()){
                            allVehicle.add(resultSet.getString("registracioniBroj"));
                        }
                    }   
        } catch (SQLException ex) {
            Logger.getLogger(sd170145_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return allVehicle;
    }

    @Override
    public boolean changeFuelType(String licencePlate, int fuelType) {
        if(licencePlate == null) return false;
        if(checkIfVehicleInStockRoom(licencePlate) == false) return false;
        if( fuelType < 0 || fuelType > 2) return false;
        String sql = "UPDATE Vozilo SET tipGoriva=? WHERE registracioniBroj = ?";
        Connection connection = DataBase.getDataBase().getConnection();
        try(   
                PreparedStatement statement = connection.prepareStatement(sql);
           ) {
            statement.setInt(1, fuelType);
            statement.setString(2, licencePlate);
            return statement.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(sd170145_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public boolean changeConsumption(String licencePlate, BigDecimal consumption) {
        if(licencePlate == null) return false;
        if(consumption.compareTo(BigDecimal.ZERO) <= 0) return false;
        if(checkIfVehicleInStockRoom(licencePlate) == false) return false;
        String sql = "UPDATE Vozilo SET potrosnja=? WHERE registracioniBroj = ?";
        Connection connection = DataBase.getDataBase().getConnection();
        try(   
                PreparedStatement statement = connection.prepareStatement(sql);
            ) {
            statement.setBigDecimal(1, consumption);
            statement.setString(2, licencePlate);
            return statement.executeUpdate() > 0;
            } catch (SQLException ex) {
                Logger.getLogger(sd170145_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        return false;
    }

    @Override
    public boolean changeCapacity(String licencePlate, BigDecimal capacity) {
        if(licencePlate == null) return false;
        if(capacity.compareTo(BigDecimal.ZERO) <= 0) return false;
        if(checkIfVehicleInStockRoom(licencePlate) == false) return false;
        String sql = "UPDATE Vozilo SET nosivost=? WHERE registracioniBroj = ?";
        Connection connection = DataBase.getDataBase().getConnection();
        try(   
                PreparedStatement statement = connection.prepareStatement(sql);
            ) {
                statement.setBigDecimal(1, capacity);
                statement.setString(2, licencePlate);
                return statement.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(sd170145_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public boolean parkVehicle(String licencePlate, int idStockRoom) {
        if(licencePlate == null) return false;
        if(checkIfVehicleInDrive(licencePlate) == true) return false;
        if (sd170145_StockroomOperations.checkIfStockRoomExist(idStockRoom) ==  false) return false;
        if(checkIfVehicleExist(licencePlate) == false) return false;
        String sql = "SELECT * FROM MagacinVozilo WHERE registracioniBroj = ?";
        Connection connection = DataBase.getDataBase().getConnection();
        try(
                PreparedStatement statement = connection.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
           ) {
                
                statement.setString(1, licencePlate);
                try(
                        ResultSet resultSet = statement.executeQuery();
                   ){
                    if(resultSet.isBeforeFirst()){
                        resultSet.next();
                        resultSet.updateInt("idMagacin", idStockRoom);
                        resultSet.updateRow();
                    } else{
                        resultSet.moveToInsertRow();
                        resultSet.updateString("registracioniBroj", licencePlate);
                        resultSet.updateInt("idMagacin", idStockRoom);
                        resultSet.insertRow();
                    }
                    return true;
                }      
        } catch (SQLException ex) {
            Logger.getLogger(sd170145_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

   
    public static boolean checkIfVehicleInDrive(String licencePlate){ 
        if(licencePlate == null) return false;
        String sqlCheckIfExist = "SELECt * FROM VozilaUVoznji WHERE registracioniBroj = ?";
        Connection connection = DataBase.getDataBase().getConnection();
        try(   
            PreparedStatement statement = connection.prepareStatement(sqlCheckIfExist);
            ) {
                statement.setString(1, licencePlate);
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
    
    
    public static void deleteAllVehicle(){
        String sqlDeleteCityies = "DELETE FROM Vozilo";
        Connection connection = DataBase.getDataBase().getConnection();
        try(   
                Statement statement = connection.createStatement();
            ) {
                statement.executeUpdate(sqlDeleteCityies);
        } catch (SQLException ex) {
            Logger.getLogger(sd170145_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    public static boolean checkIfVehicleExist(String licencePlate){
            if(licencePlate == null) return false;
            String sqlCheckIfExist = "SELECt * FROM Vozilo WHERE registracioniBroj = ?";
            Connection connection = DataBase.getDataBase().getConnection();
            try(   
                    PreparedStatement statement = connection.prepareStatement(sqlCheckIfExist);
                ) {
                    statement.setString(1, licencePlate);
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
    
    
    public static boolean checkIfVehicleInStockRoom(String licencePlate){
            if(licencePlate == null) return false;
            String sqlCheckIfExist = "SELECT * FROM MagacinVozilo WHERE registracioniBroj = ?";
            Connection connection = DataBase.getDataBase().getConnection();
            try(   
                PreparedStatement statement = connection.prepareStatement(sqlCheckIfExist);
                ) {
                    statement.setString(1, licencePlate);
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
    
    
    public static boolean setVehicleInDrive(String licencePlate, String userName){
            if(licencePlate == null || userName == null) return false;
            String sqlCheckIfExist = "INSERT INTO VozilaUVoznji(registracioniBroj,KorisnickoIme) VALUES (?,?)";
            Connection connection = DataBase.getDataBase().getConnection();
            try(   
                PreparedStatement statement = connection.prepareStatement(sqlCheckIfExist);
                ) {
                    statement.setString(1, licencePlate);
                    statement.setString(2, userName);
                    statement.executeUpdate();
            } catch (SQLException ex) {
                Logger.getLogger(sd170145_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
            return false;
        }
    
    public static String setVehicleNotInDrive(String userName){
            if(userName == null) return null;
            String sqlCheckIfExist = "SELECT * FROM VozilaUVoznji WHERE korisnickoIme=?";
            Connection connection = DataBase.getDataBase().getConnection();
            try(   
                PreparedStatement statement = connection.prepareStatement(sqlCheckIfExist, ResultSet.TYPE_SCROLL_SENSITIVE,  ResultSet.CONCUR_UPDATABLE);
                ) {
                    statement.setString(1, userName);
                    try(
                            ResultSet resultSet = statement.executeQuery();
                        ){
                            if(resultSet.isBeforeFirst()){
                                resultSet.next();
                                String licencePlate = resultSet.getString("registracioniBroj");
                                resultSet.deleteRow();
                                return licencePlate;
                            }
                            return null;
                    }
            } catch (SQLException ex) {
                Logger.getLogger(sd170145_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
        }
    
    public static boolean setVehicleInStockRoom(){
        return false;
    }
   
    public static boolean getVehicleFromStockRoom(){
        return false;
    }
    
    
   
}
