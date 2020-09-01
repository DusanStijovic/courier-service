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
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import rs.etf.sab.operations.UserOperations;

/**
 *
 * @author ducati
 */
public class sd170145_UserOperations implements UserOperations {

    
    
    
    @Override
    public boolean insertUser(String userName, String firstName, String lastName, String password, int idAddress) {
        if(userName == null || firstName == null || lastName == null || password == null) return false;
        if( Character.isUpperCase(firstName.charAt(0)) == false ) return false;
        if( Character.isUpperCase(lastName.charAt(0)) == false ) return false;
        if( checkIfPasswordisValid(password) == false) return false;
        if(sd170145_AddressOperations.checkIfAddressExist(idAddress) == false) return false;
        
        String sqlCheckIfUserExist = "SELECT * FROM Korisnik WHERE korisnickoIme=?";
        Connection connection = DataBase.getDataBase().getConnection();
        try(
                  PreparedStatement statement = connection.prepareStatement(sqlCheckIfUserExist,
                     ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
           ) {
            statement.setString(1, userName);
            try (
                    ResultSet resultSet = statement.executeQuery();
                 ){
                    if (!resultSet.isBeforeFirst()) {  
                      resultSet.moveToInsertRow();
                      resultSet.updateString("korisnickoIme", userName);
                      resultSet.updateString("ime", firstName);
                      resultSet.updateString("prezime", lastName);
                      resultSet.updateString("sifra", password);
                      resultSet.updateInt("idAdresa", idAddress);
                      resultSet.insertRow();
                      String sqlInsertKupac = "INSERT INTO Kupac(korisnickoIme) VALUES(?)";
                      try(
                           PreparedStatement preparedStatement = connection.prepareStatement(sqlInsertKupac);
                      ){
                      preparedStatement.setString(1, userName);
                      preparedStatement.executeUpdate();
                      preparedStatement.close();
                      return true;
                      }
                    } 
                    return false;
                   
                 
                }   
        } catch (SQLException ex) {
            Logger.getLogger(sd170145_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
       return false;
    }

    @Override
    public boolean declareAdmin(String userName) {
        if(userName == null) return false;
        if(checkIfUserExist(userName) == false) return false;
        String checkIfAdmin = "SELECT * FROM Administrator WHERE korisnickoIme = ?";
        Connection connection = DataBase.getDataBase().getConnection();
        try(
                PreparedStatement statement = connection.prepareStatement(checkIfAdmin,ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
               
              
            ){
            statement.setString(1, userName);
            try(
                 ResultSet resultSet = statement.executeQuery();
            ){
                if(resultSet.isBeforeFirst()) return false;
                String insertAdmin = "INSERT INTO Administrator(korisnickoIme) VALUES (?)";
                try(
                    PreparedStatement statementInsertAdmin = connection.prepareStatement(insertAdmin);
                ){
                    statementInsertAdmin.setString(1, userName);
                    statementInsertAdmin.executeUpdate();   
                    return true;
                }
              
            }
        } catch (SQLException ex) {
            Logger.getLogger(sd170145_UserOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
    
        return false;
    }

    @Override
    public int getSentPackages(String... users) {
            String sql = "SELECT COUNT(idPaketa) AS 'brojIsporucenih' FROM Paket"
                    + " INNER JOIN ZahtevZaPrevozom ON Paket.idPaketa = ZahtevZaPrevozom.idZahteva"
                    + " INNER JOIN Korisnik ON ZahtevZaPrevozom.korisnickoIme = Korisnik.korisnickoIme"
                    + " WHERE Korisnik.korisnickoIme = ? AND Paket.statusPaketa IN (1,2,3)";
            Connection connection = DataBase.getDataBase().getConnection();
            try(   
                    PreparedStatement statement = connection.prepareStatement(sql);
               ) {
                    int brojPoslatihPaketa = 0;
                    boolean userExist = false;
                    for(String user: users){
                       if(user == null) continue;
                       if( userExist == false && checkIfUserExist(user) == true) userExist = true;
                       statement.setString(1, user);
                       
                        try( 
                                ResultSet resultSet = statement.executeQuery();
                        ){
                           if(resultSet.isBeforeFirst()){
                               resultSet.next();               
                               brojPoslatihPaketa += resultSet.getInt("brojIsporucenih");
                           }
                        }
                   }
                    if(userExist == true) 
                        return brojPoslatihPaketa;
                    else 
                        return -1;
            } catch (SQLException ex) {
                Logger.getLogger(sd170145_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
            return -1;
    }

    @Override
    public int deleteUsers(String... korisnickaImena) {
        String deleteUser = "DELETE FROM Korisnik WHERE korisnickoIme = ?";
        Connection connection = DataBase.getDataBase().getConnection();
        try(
                PreparedStatement preparedStatement = connection.prepareStatement(deleteUser, ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
            ){
            int brojObrisanih = 0;
            for(String korisnickoIme : korisnickaImena){
                if(korisnickoIme == null) continue;
                if(checkIfUserCourir(korisnickoIme) == true) continue;
                if(checkIfUserHavePackage(korisnickoIme) == true) continue;
                if(checkIfUserInDrive(korisnickoIme) == true) continue;
                if(checkIfUserHaveCourirRequest(korisnickoIme) == true) continue;
                preparedStatement.setString(1, korisnickoIme);
                brojObrisanih += preparedStatement.executeUpdate();
            }
             return brojObrisanih;
        }   catch (SQLException ex) {
                Logger.getLogger(sd170145_UserOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    @Override
    public List<String> getAllUsers() {
        String sql = "SELECT korisnickoIme FROM Korisnik";
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
    
    public static void deleteAllUsers(){
        String sqlDeleteCityies = "DELETE FROM Korisnik";
        Connection connection = DataBase.getDataBase().getConnection();
        try(   
                Statement statement = connection.createStatement();
           ) {
                statement.executeUpdate(sqlDeleteCityies);
        } catch (SQLException ex) {
            Logger.getLogger(sd170145_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static boolean checkIfUserExist(String userName){
        if(userName == null) return false;
        String sqlCheckIfUserExist = "SELECT * FROM Korisnik WHERE korisnickoIme=?";
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
    
    public static boolean checkIfPasswordisValid(String password) {
        if(password == null) return false;
        Pattern lowerCasePattern = Pattern.compile("[a-z]");
        Pattern upperCasePattern = Pattern.compile("[A-Z]");
        Pattern specialCharPattern = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Pattern digitCasePattern = Pattern.compile("[0-9]");
        
        if(password.length() < 8) return  false;
        if(!lowerCasePattern.matcher(password).find()) return false;
        if(!upperCasePattern.matcher(password).find()) return false;
        if(!specialCharPattern.matcher(password).find()) return false;
        return digitCasePattern.matcher(password).find();
    }
    
    public static String getUserVehicle(String userName){
        if(userName == null) return null;
        String sqlGetUserVehicle = "SELECT * FROM VozilaUVoznji WHERE korisnickoIme = ?";
        Connection connection = DataBase.getDataBase().getConnection();
        try(   
            PreparedStatement statement = connection.prepareStatement(sqlGetUserVehicle);
            ) {
                statement.setString(1, userName);
                try(
                        ResultSet resultSet = statement.executeQuery();
                    ){
                       if(resultSet.isBeforeFirst()){
                           resultSet.next();
                           return resultSet.getString("registracioniBroj");
                       }
                       return null;
                }
        } catch (SQLException ex) {
            Logger.getLogger(sd170145_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
        
    }
   
    public static boolean checkIfUserHavePackage(String userName){
        if(userName == null) return false;
        String sqlCheckIfUserHavePackage = "SELECT * FROM zahtevZaPrevozom WHERE korisnickoIme=?";
        Connection connection = DataBase.getDataBase().getConnection();
        try(
                PreparedStatement statement = connection.prepareStatement(sqlCheckIfUserHavePackage);
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
      
    public static boolean checkIfUserInDrive(String userName){
        if(userName == null) return false;
        String sqlCheckIfUserInDrive = "SELECT * FROM vozilaUVoznji WHERE korisnickoIme=?";
        Connection connection = DataBase.getDataBase().getConnection();
        try(
                PreparedStatement statement = connection.prepareStatement(sqlCheckIfUserInDrive);
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
     
    public static boolean checkIfUserCourir(String userName){
        if(userName == null) return false;
        String sqlCheckIfUserCourir = "SELECT * FROM Kurir WHERE korisnickoIme=?";
        Connection connection = DataBase.getDataBase().getConnection();
        try(
                PreparedStatement statement = connection.prepareStatement(sqlCheckIfUserCourir);
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
     
    public static boolean checkIfUserHaveCourirRequest(String userName){
        if(userName == null) return false;
        String sqlCheckIfUserCourir = "SELECT * FROM ZahtevZaKurira WHERE korisnickoIme=?";
        Connection connection = DataBase.getDataBase().getConnection();
        try(
                PreparedStatement statement = connection.prepareStatement(sqlCheckIfUserCourir);
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
    
  
}
