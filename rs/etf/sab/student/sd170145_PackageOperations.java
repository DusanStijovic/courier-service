/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.PackageOperations;
/**
 *
 * @author ducati
 */
public class sd170145_PackageOperations implements PackageOperations{
    
    int startPrice[] = {15,175,250,350};
    int priceByKg[] = {0,100,100,500};
    
    @Override
    public int insertPackage(int addressFrom, int addressTo, String userName, int packageType, BigDecimal weight) {
        if(userName == null) return -1;
        if(weight.compareTo(BigDecimal.ZERO) <= 0) return -1;
        if(sd170145_AddressOperations.checkIfAddressExist(addressTo) == false) return -1;
        if(sd170145_AddressOperations.checkIfAddressExist(addressFrom) == false) return -1;
        if(sd170145_UserOperations.checkIfUserExist(userName) == false) return -1;
        if(packageType < 0 || packageType > 3) return -1;
        if(weight == null) return -1;
        if(weight.compareTo(BigDecimal.ZERO) <=0) return -1;
        String sql = "INSERT INTO ZahtevZaPrevozom(tipPaketa, tezinaPaketa, adresaPreuzimanja, adresaIsporuke, korisnickoIme) VALUES(?,?,?,?,?)";
        Connection connection = DataBase.getDataBase().getConnection();
        try(
                    PreparedStatement statement = connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
           ) {
                    statement.setInt(1, packageType);
                    statement.setBigDecimal(2, weight);
                    statement.setInt(3, addressFrom);
                    statement.setInt(4, addressTo);
                    statement.setString(5, userName);
                    statement.executeUpdate();
                    try (
                            ResultSet resultSet = statement.getGeneratedKeys();
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
    public boolean acceptAnOffer(int idPackage) {
        String sql = "SELECT statusPaketa, vremePrihvatanjaZahteva, trenutnaLokacija FROM Paket WHERE idPaketa=" + idPackage;
        Connection connection = DataBase.getDataBase().getConnection();
        try(   
                Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
           ) {
                    try( 
                            ResultSet resultSet = statement.executeQuery(sql);
                    ){
                        if(resultSet.isBeforeFirst()){
                            resultSet.next();
                            if(resultSet.getInt("statusPaketa") == 0){
                                resultSet.updateInt("statusPaketa", 1);
                                resultSet.updateTimestamp("vremePrihvatanjaZahteva", new Timestamp(System.currentTimeMillis()));
                                resultSet.updateRow();
                                return true;
                            }
                            return false;
                        }
                        return false;
                    }
        } catch (SQLException ex) {
            Logger.getLogger(sd170145_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public boolean rejectAnOffer(int idPackage) {
        String sql = "SELECT status FROM Paket WHERE idPaketa=" + idPackage;
        Connection connection = DataBase.getDataBase().getConnection();
        try(   
                Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
           ) {
                    try( 
                            ResultSet resultSet = statement.executeQuery(sql);
                    ){
                        if(resultSet.isBeforeFirst()){
                            resultSet.next();
                            if(resultSet.getInt("status") == 0){
                                resultSet.updateInt("status", 4);
                                resultSet.updateRow();
                                return true;
                            }
                            return false;
                        }
                        return false;
                    }
        } catch (SQLException ex) {
            Logger.getLogger(sd170145_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public List<Integer> getAllPackages() {
        String sql = "SELECT idPaketa FROM Paket";
        List<Integer> allPackages = new ArrayList<>();
        Connection connection = DataBase.getDataBase().getConnection();
        try(   
                Statement statement = connection.createStatement();
           ) {
                try (
                        ResultSet resultSet = statement.executeQuery(sql);
                     ){
                        while(resultSet.next()){
                            allPackages.add(resultSet.getInt("idPaketa"));
                        }
                    }   
        } catch (SQLException ex) {
            Logger.getLogger(sd170145_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return allPackages;
    }

    @Override
    public List<Integer> getAllPackagesWithSpecificType(int packageType) {
        if (packageType < 0 || packageType > 3) return null;
        String sql = "SELECT idZahteva FROM zahtevZaPrevozom WHERE tipPaketa=" + packageType;
        List<Integer> allPackages = new ArrayList<>();
        Connection connection = DataBase.getDataBase().getConnection();
        try(   
                Statement statement = connection.createStatement();
           ) {
                try (
                        ResultSet resultSet = statement.executeQuery(sql);
                     ){
                        while(resultSet.next()){
                            allPackages.add(resultSet.getInt("idZahteva"));
                        }
                    }   
        } catch (SQLException ex) {
            Logger.getLogger(sd170145_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return allPackages;
    }

    @Override
    public List<Integer> getAllUndeliveredPackages() {
        String sql = "SELECT idPaketa FROM Paket WHERE statusPaketa IN (1,2)";
        List<Integer> allPackages = new ArrayList<>();
        Connection connection = DataBase.getDataBase().getConnection();
        try(   
                Statement statement = connection.createStatement();
           ) {
                try (
                        ResultSet resultSet = statement.executeQuery(sql);
                     ){
                        while(resultSet.next()){
                            allPackages.add(resultSet.getInt("idPaketa"));
                        }
                    }   
        } catch (SQLException ex) {
            Logger.getLogger(sd170145_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return allPackages;
    }

    @Override
    public List<Integer> getAllUndeliveredPackagesFromCity(int idCity) {//sta ako grad ne postoji?
        String sql = "SELECT idPaketa"
                + " FROM Paket"
                + " INNER JOIN zahtevZaPrevozom ON Paket.idPaketa =  zahtevZaPrevozom.idZahteva"
                + " INNER JOIN Adresa ON Adresa.idAdresa = zahtevZaPrevozom.adresaPreuzimanja"
                + " WHERE statusPaketa IN (1,2) AND Adresa.idGrad=" + idCity;
        List<Integer> allPackages = new ArrayList<>();
        Connection connection = DataBase.getDataBase().getConnection();
        try(   
                Statement statement = connection.createStatement();
           ) {
                try (
                        ResultSet resultSet = statement.executeQuery(sql);
                     ){
                        while(resultSet.next()){
                            allPackages.add(resultSet.getInt("idPaketa"));
                        }
                    }   
        } catch (SQLException ex) {
            Logger.getLogger(sd170145_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return allPackages;
    }

    @Override
    public List<Integer> getAllPackagesCurrentlyAtCity(int cityId){
        if (sd170145_CityOperations.checkIfCityExists(cityId) == false) return null;
        List<Integer> allPackages = new ArrayList<>();
        String sql = "SELECT idPaketa"
                  + " FROM Paket INNER JOIN Adresa ON Adresa.idAdresa = Paket.trenutnaLokacija"
                  + " WHERE trenutnaLokacija IS NOT NULL AND Adresa.idGrad=" + cityId;
        Connection connection = DataBase.getDataBase().getConnection();
        try(   
                Statement statement = connection.createStatement();
           ) {
                    try( 
                            ResultSet resultSet = statement.executeQuery(sql);
                    ){
                        while(resultSet.next()) {
                            allPackages.add(resultSet.getInt("idPaketa"));
                        }
                        return allPackages;
                    }
        } catch (SQLException ex) {
            Logger.getLogger(sd170145_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
        
        
        
    }

    @Override
    public boolean deletePackage(int idPackage) {
        int packageStatus = getDeliveryStatus(idPackage);
        if(packageStatus != 0 && packageStatus != 3) return false; 
        String sqlDeletePackage = "DELETE FROM Paket WHERE statusPaketa IN (0,4) AND idPaketa =" + idPackage;
        Connection connection = DataBase.getDataBase().getConnection();
        try(   
                Statement statement = connection.createStatement();
           ) {  
                if(statement.executeUpdate(sqlDeletePackage) > 0){
                    String sqlDeleteDriveRequest = "DELETE FROM zahtevZaPrevozom WHERE idZahteva =" + idPackage;
                    statement.executeQuery(sqlDeleteDriveRequest);
                    return true;
                } 
                return false;
            } catch (SQLException ex) {
                Logger.getLogger(sd170145_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        } 
        return false;
    }

    @Override
    public boolean changeWeight(int packageId, BigDecimal newWeight) {
        if (newWeight.compareTo(BigDecimal.ZERO) < 0) return false;
  
        int packageStatus = getDeliveryStatus(packageId);
        if (packageStatus != 0) return false; 
        
        String sql = "SELECT * FROM ZahtevZaPrevozom WHERE idZahteva=?";
        Connection connection = DataBase.getDataBase().getConnection();
        try(   
                PreparedStatement statement = connection.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
           ) {
                statement.setInt(1, packageId);
                try(
                        ResultSet resultSet = statement.executeQuery();
                    ){
                         if(resultSet.isBeforeFirst()){
                             resultSet.next();
                             resultSet.updateBigDecimal("tezinaPaketa", newWeight);
                             resultSet.updateRow();
                             BigDecimal euclidsDecimal = sd170145_AddressOperations.calculateEuclidsDistance(resultSet.getInt("adresaPreuzimanja"), resultSet.getInt("adresaIsporuke"));
                             BigDecimal newPrice = calculatePricaOfDelivery(resultSet.getInt("tipPaketa"),newWeight,euclidsDecimal);
                             setPackagePrice(packageId, newPrice);
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
    public boolean changeType(int packageId, int newType) {
        if(newType < 0 || newType > 3) return false;
        
        int packageStatus = getDeliveryStatus(packageId);
        if(packageStatus != 0) return false; 
        
        String sql = "SELECT * FROM ZahtevZaPrevozom WHERE idZahteva=?";
        Connection connection = DataBase.getDataBase().getConnection();
        try(   
                PreparedStatement statement = connection.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
           ) {
                statement.setInt(1, packageId);
                try(
                        ResultSet resultSet = statement.executeQuery();
                    ){
                         if(resultSet.isBeforeFirst()){
                            resultSet.next();
                            resultSet.updateInt("tipPaketa", newType);
                            resultSet.updateRow();
                            BigDecimal euclidsDecimal = sd170145_AddressOperations.calculateEuclidsDistance(resultSet.getInt("adresaPreuzimanja"), resultSet.getInt("adresaIsporuke"));
                            BigDecimal newPrice = calculatePricaOfDelivery(newType,resultSet.getBigDecimal("tezinaPaketa"),euclidsDecimal);
                            setPackagePrice(packageId, newPrice);
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
    public BigDecimal getPriceOfDelivery(int idPackage) {
        String sql = "SELECT cena FROM Paket WHERE idPaketa=" + idPackage;
        Connection connection = DataBase.getDataBase().getConnection();
        try(   
                    Statement statement = connection.createStatement();
            ) {
                    try( 
                            ResultSet resultSet = statement.executeQuery(sql);
                    ){
                            if(resultSet.isBeforeFirst()){
                               resultSet.next();
                               return resultSet.getBigDecimal("cena");
                            }
                            return BigDecimal.valueOf(-1);
                    }
        } catch (SQLException ex) {
            Logger.getLogger(sd170145_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private BigDecimal calculatePricaOfDelivery(int packageType, BigDecimal packageWeight, BigDecimal euclidsDistance){
        BigDecimal packageStartPrice = BigDecimal.valueOf(startPrice[packageType]);
        BigDecimal packagePriceByKg = BigDecimal.valueOf(this.priceByKg[packageType]);
        return packageStartPrice.add(packagePriceByKg.multiply(packageWeight)).multiply(euclidsDistance);
    }
    
    @Override
    public Date getAcceptanceTime(int idPackage) {
        String sql = "SELECT vremePrihvatanjaZahteva FROM Paket WHERE vremePrihvatanjaZahteva IS NOT NULL AND idPaketa=" + idPackage;
        Connection connection = DataBase.getDataBase().getConnection();
        try(   
                    Statement statement = connection.createStatement();
            ) {
                    try( 
                            ResultSet resultSet = statement.executeQuery(sql);
                    ){
                        if(resultSet.isBeforeFirst()){
                           resultSet.next();
                           return resultSet.getDate("vremePrihvatanjaZahteva");
                        }
                        return null;
                    }
        } catch (SQLException ex) {
            Logger.getLogger(sd170145_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public int getDeliveryStatus(int idPackage) {
        String sql = "SELECT statusPaketa FROM Paket WHERE idPaketa=" + idPackage;
        Connection connection = DataBase.getDataBase().getConnection();
        try(   
                Statement statement = connection.createStatement();
           ) {
                    try( 
                            ResultSet resultSet = statement.executeQuery(sql);
                    ){
                        if(resultSet.isBeforeFirst()){
                            resultSet.next();
                            return resultSet.getInt("statusPaketa");
                        }
                        return -1;
                    }
        } catch (SQLException ex) {
            Logger.getLogger(sd170145_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    @Override
    public int getCurrentLocationOfPackage(int idPackage) {
        String sql = "SELECT trenutnaLokacija FROM Paket WHERE trenutnaLokacija IS NOT NULL AND idPaketa=" + idPackage;
        Connection connection = DataBase.getDataBase().getConnection();
        try(   
                Statement statement = connection.createStatement();
           ) {
                    try( 
                            ResultSet resultSet = statement.executeQuery(sql);
                    ){
                        if(resultSet.isBeforeFirst()){
                            resultSet.next();
                            return sd170145_AddressOperations.getAddressCity(resultSet.getInt("trenutnaLokacija"));             
                        }
                        return -1;
                    }
        } catch (SQLException ex) {
            Logger.getLogger(sd170145_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }
    
    public static boolean setPackagePrice(int idPackage, BigDecimal newPrice){
        String sql = "UPDATE Paket SET cena = ? WHERE idPaketa=?";
        Connection connection = DataBase.getDataBase().getConnection();
        try(   
                PreparedStatement statement = connection.prepareStatement(sql);
            ) {
                    statement.setBigDecimal(1, newPrice);
                    statement.setInt(2, idPackage);
                   
                    return statement.executeUpdate() > 0;
                   
        } catch (SQLException ex) {
            Logger.getLogger(sd170145_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
     
    public static Queue<Paket> getAllPackagesForDeliveryFromCity(int idCity, boolean zaIsporuku, boolean zaPreuzimanje) {//sta ako grad ne postoji?
        Queue<Paket> paketi = new LinkedList<>();//Ne smes iz magacina da uzmes
        String sql = "SELECT idPaketa , tezinaPaketa,cena, Adresa.idAdresa AS Ad, xKoordinata, yKoordinata"
                + " FROM Paket"
                + " INNER JOIN zahtevZaPrevozom ON Paket.idPaketa =  zahtevZaPrevozom.idZahteva"
                + " INNER JOIN Adresa ON Adresa.idAdresa = zahtevZaPrevozom.adresaPreuzimanja"
                + " WHERE oznacenZaPreuzimanje=0 AND Paket.idPaketa NOT IN (SELECT MagacinPaketi.idPaketa FROM MagacinPaketi WHERE MagacinPaketi.idPaketa=Paket.idPaketa)"  
                + " AND trenutnaLokacija IS NOT NULL "
                + "AND statusPaketa = 1 AND Adresa.idGrad=" + idCity + ""
                + " ORDER BY Paket.vremePrihvatanjaZahteva ASC";
       
        Connection connection = DataBase.getDataBase().getConnection();
        try(   
                Statement statement = connection.createStatement(
                     ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
           ) {
                try (
                        ResultSet resultSet = statement.executeQuery(sql);
                     ){
                      
                        while(resultSet.next()){
                            Paket paket = new Paket();
                            paket.setIdadress(resultSet.getInt("Ad"));
                            paket.setxCoord(resultSet.getInt("xKoordinata"));
                            paket.setyCoord(resultSet.getInt("yKoordinata"));
                            paket.setIdPackage(resultSet.getInt("idPaketa"));
                            paket.setTezina(resultSet.getBigDecimal("tezinaPaketa"));
                            paket.setCena(resultSet.getBigDecimal("cena"));
                            paket.setZaPreuzimanje(zaPreuzimanje);
                            paket.setZaIsporuku(zaIsporuku);
                            paketi.add(paket);
                        }
                        return paketi;
                    }   
        } catch (SQLException ex) {
            Logger.getLogger(sd170145_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null; 
    }
 
    public static Queue<Paket> getAllPackagesFromStockRoom(int idStockRoom, boolean zaIsporuku, boolean zaPreuzimanje) {//sta ako grad ne postoji?
        Queue<Paket> paketi = new LinkedList<>();
        String sql = " SELECT MagacinPaketi.idPaketa AS Pd, tezinaPaketa, cena, Adresa.idAdresa AS Ad, xKoordinata, yKoordinata"
                   + " FROM MagacinPaketi INNER JOIN zahtevZaPrevozom ON MagacinPaketi.idPaketa = zahtevZaPrevozom.idZahteva"
                   + " INNER JOIN Paket ON Paket.idPaketa = zahtevZaPrevozom.idZahteva"
                   + " INNER JOIN Adresa ON Adresa.idAdresa = MagacinPaketi.idMagacin"
                   + " WHERE  oznacenZaPreuzimanje=0"
                   + " AND trenutnaLokacija IS NOT NULL"
                   + " AND idMagacin = " + idStockRoom
                   + " ORDER BY Paket.vremePrihvatanjaZahteva ASC";
       
        Connection connection = DataBase.getDataBase().getConnection();
        try(   
                Statement statement = connection.createStatement(
                     ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
           ) {
                try (
                        ResultSet resultSet = statement.executeQuery(sql);
                     ){
                      
                        while(resultSet.next()){
                            Paket paket = new Paket();
                            paket.setIdadress(resultSet.getInt("Ad"));
                            paket.setxCoord(resultSet.getInt("xKoordinata"));
                            paket.setyCoord(resultSet.getInt("yKoordinata"));
                            paket.setIdPackage(resultSet.getInt("Pd"));
                            paket.setTezina(resultSet.getBigDecimal("tezinaPaketa"));
                            paket.setCena(resultSet.getBigDecimal("cena"));
                            paket.setZaPreuzimanje(zaPreuzimanje);
                            paket.setZaIsporuku(zaIsporuku);
                            paketi.add(paket);
                        }
                        return paketi;
                    }   
        } catch (SQLException ex) {
            Logger.getLogger(sd170145_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null; 
    }
   
    public static void deleteAllPackages(){
        String sqlDeleteRequests = "DELETE FROM zahtevZaPrevozom";
        String sqlDeletePackages = "DELETE FROM Paket";
        Connection connection = DataBase.getDataBase().getConnection();
        try(   
                Statement statement = connection.createStatement();
           ) {
                statement.executeUpdate(sqlDeletePackages);
                statement.executeUpdate(sqlDeleteRequests);
        } catch (SQLException ex) {
            Logger.getLogger(sd170145_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
