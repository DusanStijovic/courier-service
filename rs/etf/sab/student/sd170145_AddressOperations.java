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
import rs.etf.sab.operations.AddressOperations;

/**
 *
 * @author ducati
 */
public class sd170145_AddressOperations implements AddressOperations {

    @Override
    public int deleteAllAddressesFromCity(int cityId) {
     List<Integer> addressesFromCity = getAllAddressesFromCity(cityId);
     if(addressesFromCity == null) return 0;
     int brojObrisanih = 0;
     for (int address: addressesFromCity){
         if(deleteAdress(address))
             brojObrisanih++;
     }
     return brojObrisanih;
    }

    @Override
    public int insertAddress(String street, int number, int cityId, int xCord, int yCord) {//Dodati da su koordinate jedinstvene...
        if(street == null) return -1;
        if(sd170145_CityOperations.checkIfCityExists(cityId) == false) return -1;
        String sql = "INSERT INTO Adresa(ulica, broj, xKoordinata, yKoordinata, idGrad) VALUES(?,?,?,?,?)";
        Connection connection = DataBase.getDataBase().getConnection();
        try(
                  PreparedStatement statement = connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
           ) {
                statement.setString(1, street);
                statement.setInt(2, number);
                statement.setInt(4, yCord);
                statement.setInt(3, xCord);
                statement.setInt(5, cityId);
                statement.executeUpdate();
                try (
                        ResultSet resultSet =statement.getGeneratedKeys();
                     ){
                        resultSet.next();
                        return resultSet.getInt(1);  
                    }   
        } catch (SQLException ex) {
            Logger.getLogger(sd170145_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    @Override
    public int deleteAddresses(String street, int number) { 
        if(street == null) return 0;
        List<Integer> allAddreses = getAllAddressId(street, number);
        int brojObrisanih = 0;
        for (int address : allAddreses){
            if(deleteAdress(address))
                brojObrisanih++;
        }
        return brojObrisanih;
    }

    @Override
    public boolean deleteAdress(int idAdresa) {
     if(checkIfHaveConnection(idAdresa) == true) return false;
        String sqlDeleteAdress = "DELETE FROM Adresa WHERE idAdresa =" + idAdresa;
        Connection connection = DataBase.getDataBase().getConnection();
        try(   
                Statement statement = connection.createStatement();
           ) {  
                return statement.executeUpdate(sqlDeleteAdress) > 0;
            } catch (SQLException ex) {
                Logger.getLogger(sd170145_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
       
    return false;
    }

    @Override
    public List<Integer> getAllAddresses() {
        String sql = "SELECT idAdresa FROM Adresa";
        List<Integer> allCityies = new ArrayList<>();
        Connection connection = DataBase.getDataBase().getConnection();
        try(   
                Statement statement = connection.createStatement();
           ) {
            try (
                    ResultSet resultSet = statement.executeQuery(sql);
                 ){
                    while(resultSet.next()){
                        allCityies.add(resultSet.getInt("idAdresa"));
                    }
                }   
            } catch (SQLException ex) {
                Logger.getLogger(sd170145_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        return allCityies;
    }

    @Override
    public List<Integer> getAllAddressesFromCity(int cityId) {
        if(sd170145_CityOperations.checkIfCityExists(cityId) == false ) return null;
        String sql = "SELECT idAdresa FROM Adresa INNER JOIN Grad ON Adresa.idGrad = Grad.idGrad WHERE Grad.idGrad =" + cityId;
        List<Integer> allAdresses = new ArrayList<>();
        Connection connection = DataBase.getDataBase().getConnection();
        try(   
                Statement statement = connection.createStatement();
           ) {
            try (
                    ResultSet resultSet = statement.executeQuery(sql);
                 ){
                    while(resultSet.next()){
                        allAdresses.add(resultSet.getInt("idAdresa"));
                    }
                }   
            } catch (SQLException ex) {
                Logger.getLogger(sd170145_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        return allAdresses;
    }
    
    public static boolean checkIfAddressExist(int idAddress){
        String sqlCheckIfAdressExists = "SELECT * FROM Adresa WHERE idAdresa=" + idAddress;
        Connection connection = DataBase.getDataBase().getConnection();
        try(
                  Statement statement = connection.createStatement();
           ) {
            try (
                    ResultSet resultSet = statement.executeQuery(sqlCheckIfAdressExists);
                 ){ 
                    return resultSet.isBeforeFirst();
                }   
        } catch (SQLException ex) {
            Logger.getLogger(sd170145_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    };

    
    public static void deleteAllAdresses(){
        String sqlDeleteAdresses = "DELETE FROM Adresa";
        Connection connection = DataBase.getDataBase().getConnection();
        try(   
                Statement statement = connection.createStatement();
            ) {
                statement.executeUpdate(sqlDeleteAdresses);
        
        }catch (SQLException ex) {
                Logger.getLogger(sd170145_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    public static boolean checkIfAddressHasUser(int idAddress){ 
        String sqlCheckIfAdressHasUser = "SELECT * FROM Korisnik WHERE idAdresa=" + idAddress;
        Connection connection = DataBase.getDataBase().getConnection();
        try(
                  Statement statement = connection.createStatement();
           ) {
            try (
                    ResultSet resultSet = statement.executeQuery(sqlCheckIfAdressHasUser);
                 ){ 
                    return resultSet.isBeforeFirst();
                }   
        } catch (SQLException ex) {
            Logger.getLogger(sd170145_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
       
    public static boolean checkIfAddressHasStockroom(int idAddress){ 
        String sqlCheckIfAdressHasStockroom = "SELECT * FROM Magacin WHERE idMagacin=" + idAddress;
        Connection connection = DataBase.getDataBase().getConnection();
        try(
                  Statement statement = connection.createStatement();
           ) {
            try (
                    ResultSet resultSet = statement.executeQuery(sqlCheckIfAdressHasStockroom);
                 ){ 
                    return resultSet.isBeforeFirst();
                }   
        } catch (SQLException ex) {
            Logger.getLogger(sd170145_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
  
    public static boolean checkIfAddressHasPackage(int idAddress){ 
        String sqlCheckIfAdressHasPackage = "SELECT * FROM Paket WHERE trenutnaLokacija IS NOT NULL AND trenutnaLokacija=" + idAddress;
        Connection connection = DataBase.getDataBase().getConnection();
        try(
                  Statement statement = connection.createStatement();
           ) {
            try (
                    ResultSet resultSet = statement.executeQuery(sqlCheckIfAdressHasPackage);
                 ){ 
                    return resultSet.isBeforeFirst();
                }   
        } catch (SQLException ex) {
            Logger.getLogger(sd170145_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public static boolean checkIfAddressHasRequestForDrive(int idAddress){ 
        String sqlCheckIfAdressHasRequestForDrive = "SELECT * FROM zahtevZaPrevozom WHERE adresaPreuzimanja=" + idAddress + " OR adresaIsporuke=" + idAddress;
        Connection connection = DataBase.getDataBase().getConnection();
        try(
                  Statement statement = connection.createStatement();
           ) {
            try (
                    ResultSet resultSet = statement.executeQuery(sqlCheckIfAdressHasRequestForDrive);
                 ){ 
                    return resultSet.isBeforeFirst();
                }   
        } catch (SQLException ex) {
            Logger.getLogger(sd170145_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public static List<Integer>getAllAddressId(String street, int number){
        if(street == null) return null;
        String sql = "SELECT * FROM Adresa where ulica = ? AND broj = ?";
        Connection connection = DataBase.getDataBase().getConnection();
        List<Integer> allAddresses = new ArrayList<>();
        try(
                PreparedStatement statement = connection.prepareStatement(sql);
           ) {
             statement.setString(1, street);
             statement.setInt(2, number);
            try (
                    ResultSet resultSet = statement.executeQuery();
                 ){
                  while(resultSet.next()){
                      allAddresses.add(resultSet.getInt("idAdresa"));
                  }
                  return allAddresses;
                }   
        } catch (SQLException ex) {
            Logger.getLogger(sd170145_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
       return null;
    }

    public static int getAddressCity(int idAddress){
        String sqlCheckIfAdressExists = "SELECT idGrad FROM Adresa WHERE idAdresa=" + idAddress;
        Connection connection = DataBase.getDataBase().getConnection();
        try(
                  Statement statement = connection.createStatement();
           ) {
            try (
                    ResultSet resultSet = statement.executeQuery(sqlCheckIfAdressExists);
                ){ 
                    if (resultSet.isBeforeFirst()){
                        resultSet.next();
                        return resultSet.getInt("idGrad");
                    }
                    else 
                        return -1;
                }   
            } catch (SQLException ex) {
                Logger.getLogger(sd170145_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        return -1;
    }
 
    public static BigDecimal calculateEuclidsDistance(int idAddress1, int idAddress2){
        String sql = "SELECT xKoordinata, yKoordinata FROM Adresa WHERE idAdresa IN (" + idAddress1 + "," + idAddress2+ ")";
        Connection connection = DataBase.getDataBase().getConnection();
        try(   
                Statement statement = connection.createStatement();
      ) {
               try( 
                       ResultSet resultSet = statement.executeQuery(sql);
               ){
                   if(resultSet.isBeforeFirst()){
                       if(idAddress1 == idAddress2) return BigDecimal.valueOf(0);
                       resultSet.next();
                       int startX = resultSet.getInt("xKoordinata"), startY = resultSet.getInt("yKoordinata");
                       if(!resultSet.next()) return BigDecimal.valueOf(-1);
                       int endX = resultSet.getInt("xKoordinata"), endY = resultSet.getInt("yKoordinata");
                       return BigDecimal.valueOf(Math.sqrt(Math.pow(startX-endX, 2) + Math.pow(startY - endY, 2)));
                   }
                   return BigDecimal.valueOf(-1);
               }
        } catch (SQLException ex) {
            Logger.getLogger(sd170145_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return BigDecimal.valueOf(-1);
     
        
    }

    private boolean checkIfHaveConnection(int idAdress) {
       if(checkIfAddressHasPackage(idAdress) == true) return true;
       if(checkIfAddressHasRequestForDrive(idAdress) == true) return true;
       if(checkIfAddressHasStockroom(idAdress) == true) return true;
       if(checkIfAddressHasUser(idAdress) == true) return true;
       return false;
    }
    
}
    

