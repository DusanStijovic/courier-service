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
import rs.etf.sab.operations.CityOperations;

/**
 *
 * @author ducati
 */
public class sd170145_CityOperations implements CityOperations{

    @Override
    public int insertCity(String name, String postalCode) {
        if(name == null || postalCode == null) return -1;
        String sql = "SELECT * FROM Grad WHERE postanskiBroj=?";
        Connection connection = DataBase.getDataBase().getConnection();
        try(
                  PreparedStatement statement = connection.prepareStatement(sql,
                     ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
           ) {
                statement.setString(1, postalCode);
                try (
                        ResultSet resultSet = statement.executeQuery();
                     ){
                        if (resultSet.isBeforeFirst()) {  
                            return -1; //Vec imamo grad za datim postanskim brojem.
                        } 
                        resultSet.moveToInsertRow();
                        resultSet.updateString("naziv", name);
                        resultSet.updateString("postanskiBroj", postalCode);
                        resultSet.insertRow();
                        resultSet.moveToCurrentRow();
                        resultSet.next();
                        return resultSet.getInt("idGrad");  
                    }   
        } catch (SQLException ex) {
            Logger.getLogger(sd170145_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }
    
    public static void deleteAllCities(){
        String sqlDeleteCityies = "DELETE FROM Grad";
        Connection connection = DataBase.getDataBase().getConnection();
        try(   
                Statement statement = connection.createStatement();
           ) {
                statement.executeUpdate(sqlDeleteCityies);
        } catch (SQLException ex) {
            Logger.getLogger(sd170145_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public int deleteCity(String... gradovi) {
        String sqlGetIdGrada  = "SELECT idGrad FROM Grad WHERE naziv = ?";
        Connection connection = DataBase.getDataBase().getConnection();
         try(
                PreparedStatement statement = connection.prepareStatement(sqlGetIdGrada);
            ) {
               int brojObrisanih = 0;
                for(String imeGrada: gradovi){
                       if (imeGrada == null) continue;
                       statement.setString(1, imeGrada);
                    try (
                        ResultSet resultSet = statement.executeQuery();
                    ){
                       if (resultSet.isBeforeFirst()) {  
                           resultSet.next();
                           if (deleteCity(resultSet.getInt("idGrad")))
                               brojObrisanih++; 
                       } 
                    
                   }
            }
            return brojObrisanih;
        } catch (SQLException ex) {
            Logger.getLogger(sd170145_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
       return -1;
    }

    @Override
    public boolean deleteCity(int idGrad) {
        if(checkIfHaveAdress(idGrad) == true) return false;
        String sqlDeleteCity = "DELETE FROM Grad WHERE idGrad =" + idGrad;
        Connection connection = DataBase.getDataBase().getConnection();
        try(   
                Statement statement = connection.createStatement();
           ) {  
                return statement.executeUpdate(sqlDeleteCity) > 0;
        } catch (SQLException ex) {
            Logger.getLogger(sd170145_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

    @Override
    public List<Integer> getAllCities() {
        String sql = "SELECT idGrad FROM Grad";
        List<Integer> allCityies = new ArrayList<Integer>();
        Connection connection = DataBase.getDataBase().getConnection();
        try(   
                Statement statement = connection.createStatement();
           ) {
            try (
                    ResultSet resultSet = statement.executeQuery(sql);
                 ){
                    while(resultSet.next()){
                        allCityies.add(resultSet.getInt("idGrad"));
                    }
                }   
        } catch (SQLException ex) {
            Logger.getLogger(sd170145_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return allCityies;
    }
    
    public static boolean checkIfCityExists(int cityId){
        String sqlFindCity = "SELECT * FROM Grad WHERE idGrad=" + cityId;
        Connection connection = DataBase.getDataBase().getConnection();
        try(
                  Statement statement = connection.createStatement();
           ) {
            try (
                    ResultSet resultSet = statement.executeQuery(sqlFindCity);
                 ){ 
                   return resultSet.isBeforeFirst();
                }   
        } catch (SQLException ex) {
            Logger.getLogger(sd170145_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
        
        
    }
    public static boolean checkIfHaveAdress(int cityId){
        if(sd170145_CityOperations.checkIfCityExists(cityId) == false ) return false;
        String sql = "SELECT idAdresa FROM Adresa INNER JOIN Grad ON Adresa.idGrad = Grad.idGrad WHERE Grad.idGrad =" + cityId;
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
       return true;
    }
}
