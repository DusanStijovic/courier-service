/*+
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
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.StockroomOperations;

/**
 *
 * @author ducati
 */
public class sd170145_StockroomOperations implements StockroomOperations{

    @Override
    public int insertStockroom(int idAddress) {
        int idCity = sd170145_AddressOperations.getAddressCity(idAddress);
        if (idCity == -1) return -1;
        if(checkIfCityAlreadyHaveStockRoom(idCity) == true) return -1;
        String sql = "INSERT INTO Magacin(idMagacin) VALUES(" + idAddress + ")";
        Connection connection = DataBase.getDataBase().getConnection();
        try(
                  Statement statement = connection.createStatement();
           ) { 
                statement.executeUpdate(sql);
                return idAddress;
        } catch (SQLException ex) {
            Logger.getLogger(sd170145_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    @Override
    public boolean deleteStockroom(int idMagacin) {
        if (checkIhhaveSomethingInStockRoom(idMagacin) == true) return false;
        String sqlDeleteStockRoom = "DELETE FROM Magacin WHERE idMagacin =" + idMagacin;
        Connection connection = DataBase.getDataBase().getConnection();
        try(   
               Statement statement = connection.createStatement();
          ) {  
                boolean deleted = statement.executeUpdate(sqlDeleteStockRoom) > 0; 
                return deleted;
        } catch (SQLException ex) {
            Logger.getLogger(sd170145_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public int deleteStockroomFromCity(int idCity) {
       int idStockRoom = getStockRoomInCity(idCity);
       if (idStockRoom == -1) return -1;
       if (deleteStockroom(idStockRoom))
            return idStockRoom;
       else 
           return -1;
    }

    @Override
    public List<Integer> getAllStockrooms() {
        String sql = "SELECT idMagacin  FROM Magacin";
        List<Integer> allStockRooms = new ArrayList<>();
        Connection connection = DataBase.getDataBase().getConnection();
        try(   
                Statement statement = connection.createStatement();
           ) {
                try (
                        ResultSet resultSet = statement.executeQuery(sql);
                     ){
                        while(resultSet.next()){
                            allStockRooms.add(resultSet.getInt("idMagacin"));
                        }
                    }   
        } catch (SQLException ex) {
            Logger.getLogger(sd170145_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return allStockRooms;
    }
     
    public static boolean checkIfCityAlreadyHaveStockRoom(int idCity){
        String sql = "SELECT idGrad FROM Magacin INNER JOIN Adresa ON Magacin.idMagacin = Adresa.idAdresa WHERE Adresa.idGrad = " + idCity;
        Connection connection = DataBase.getDataBase().getConnection();
        try(
                Statement statement = connection.createStatement();
           ) {
                try (
                        ResultSet resultSet = statement.executeQuery(sql);
                     ){

                        return resultSet.isBeforeFirst();  
                    }   
        } catch (SQLException ex) {
            Logger.getLogger(sd170145_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;  
    }

    public static void deleteAllStockRoom(){
        String sqlDeleteCityies = "DELETE FROM Magacin";
        Connection connection = DataBase.getDataBase().getConnection();
        try(   
                Statement statement = connection.createStatement();
        ) {
                statement.executeUpdate(sqlDeleteCityies);
        } catch (SQLException ex) {
                Logger.getLogger(sd170145_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static boolean checkIfStockRoomExist(int stockRoomId){
           String sqlCheckIfExist = "SELECt * FROM Magacin WHERE idMagacin = ?";
           Connection connection = DataBase.getDataBase().getConnection();
            try(   
                PreparedStatement statement = connection.prepareStatement(sqlCheckIfExist);
            ) {
                statement.setInt(1, stockRoomId);
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

    public static int getStockRoomInCity(int idCity){
        String sqlCheckIfAdressExists = "SELECT idMagacin FROM Magacin INNER JOIN Adresa ON Magacin.idMagacin = Adresa.idAdresa WHERE idGrad=" + idCity;
        Connection connection = DataBase.getDataBase().getConnection();
        try(
                Statement statement = connection.createStatement();
           ) {
                try (
                        ResultSet resultSet = statement.executeQuery(sqlCheckIfAdressExists);
                    ){ 
                        if (resultSet.isBeforeFirst()){
                            resultSet.next();
                            return resultSet.getInt("idMagacin");
                        }
                        else 
                            return -1;
                    }   
        } catch (SQLException ex) {
            Logger.getLogger(sd170145_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    private boolean checkIhhaveSomethingInStockRoom(int idStockom) {
        if(checkIfHaveVehicleInStockRoom(idStockom) == true) return true;
        return  checkIfHavePackageInStockRoom(idStockom) == true;
    }
    
    private boolean checkIfHaveVehicleInStockRoom(int idStockRoom){
        String sql = "SELECT idMagacin FROM MagacinVozilo WHERE MagacinVozilo.idMagacin=" + idStockRoom;
        Connection connection = DataBase.getDataBase().getConnection();
        try(
                Statement statement = connection.createStatement();
           ) {
                try (
                        ResultSet resultSet = statement.executeQuery(sql);
                     ){

                        return resultSet.isBeforeFirst();  
                    }   
        } catch (SQLException ex) {
            Logger.getLogger(sd170145_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    private boolean checkIfHavePackageInStockRoom(int idStockRoom){
        String sql = "SELECT idMagacin FROM MagacinPaketi WHERE MagacinPaketi.idMagacin=" + idStockRoom;
        Connection connection = DataBase.getDataBase().getConnection();
        try(
                Statement statement = connection.createStatement();
           ) {
                try (
                        ResultSet resultSet = statement.executeQuery(sql);
                     ){

                        return resultSet.isBeforeFirst();  
                    }   
        } catch (SQLException ex) {
            Logger.getLogger(sd170145_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }


    public static void deletePackageFromStockRoom(int idStockRoom, Queue<Paket> paketi){
            if(idStockRoom == -1 || paketi == null) return;
           String sqlCheckIfExist = "DELETE FROM MagacinPaketi WHERE idMagacin = ? AND idPaketa = ?";
           Connection connection = DataBase.getDataBase().getConnection();
            try(   
                PreparedStatement statement = connection.prepareStatement(sqlCheckIfExist);
            ) {
                statement.setInt(1, idStockRoom);
                for(Paket paket : paketi){
                    statement.setInt(2,paket.getIdPackage() );
                    statement.executeUpdate();
                }
            } catch (SQLException ex) {
                Logger.getLogger(sd170145_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
            return;
    }
    
   

}
