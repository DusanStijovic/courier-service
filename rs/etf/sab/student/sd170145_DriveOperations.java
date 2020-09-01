/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;
import rs.etf.sab.operations.DriveOperation;

/**
 *
 * @author ducati
 */
public class sd170145_DriveOperations implements DriveOperation{
    
    private static Map<String,VoznjaInfo> voznjaInfo = new HashMap<>();
    private static String userName;
    
    @Override
    public boolean planingDrive(String userName) {
       sd170145_DriveOperations.userName = userName;
       int courierCity = sd170145_CourierOperations.getCourierCity(userName);
       if(courierCity == -1) return false;
        System.out.println("asdfg");
       if(sd170145_CourierOperations.checkIfCourierInDrive(userName) == true) return false;
       if(assignVehicleToCourier(userName) == false) return false;
       String licencePlate = voznjaInfo.get(userName).getIdVozilo();   
       Queue<Paket> paketi = pickUpPackages(licencePlate);
       if(paketi == null){
           takeVehicleFromCourier(userName);
           voznjaInfo.remove(userName);
           return false;
       }
       makeDriveRoute(paketi);
       return true;
    }

    @Override
    public int nextStop(String userName) {
        if(sd170145_CourierOperations.checkIfCourierInDrive(userName) == false) return -3;
        VoznjaInfo voznja = voznjaInfo.get(userName);
        Queue<Paket> paketi = voznja.getRuta();
        Paket paket = paketi.remove();
        if(paketi.size() == 0){
            insertPackagesInStockRoom(voznjaInfo.get(userName).getAdresaMagacina(), voznjaInfo.get(userName).getZaPreuzimanje());
            removePackagesFromVehicleForStockRoom(voznjaInfo.get(userName).getIdVozilo());
            updateCourierProfit(userName);
            takeVehicleFromCourier(userName);
            voznjaInfo.remove(userName);
            return -1;
        }
        if(paket.isZaIsporuku()){
            if(paket.isZaPreuzimanje()){
                if(paket.isMagacin()){
                     putPackagesInVehicle(voznjaInfo.get(userName).getPaketiIzMagacina().get(paket.getIdadress()), voznjaInfo.get(userName).getIdVozilo(), true);
                     sd170145_StockroomOperations.deletePackageFromStockRoom(paket.getIdadress(), voznjaInfo.get(userName).getPaketiIzMagacina().get(paket.getIdadress()));
                }else {
                     putPackageInVehicle(voznjaInfo.get(userName).getIdVozilo(), paket);//promenice se statusu na preuzet i lokacija se menja
                } 
                return -2;
            } else {
                sd170145_CourierOperations.increaseCourirDeliveredPackages(userName);
                takePackageFromVehicle(voznjaInfo.get(userName).getIdVozilo(),paket);//Unutra ce se promeniti i status paketa na isporucen i lokacija se menja
                return paket.getIdPackage();
            }
        } else {
            int adresa = paket.getIdadress();
            if(paket.isMagacin()){
                putPackagesInVehicle(voznjaInfo.get(userName).getPaketiIzMagacina().get(adresa), voznjaInfo.get(userName).getIdVozilo(), true);
                voznjaInfo.get(userName).getZaPreuzimanje().addAll(voznjaInfo.get(userName).getPaketiIzMagacina().get(adresa));
                sd170145_StockroomOperations.deletePackageFromStockRoom(paket.getIdadress(), voznjaInfo.get(userName).getPaketiIzMagacina().get(paket.getIdadress()));
            } else{
                voznjaInfo.get(userName).getZaPreuzimanje().add(paket);
                putPackageInVehicle(voznjaInfo.get(userName).getIdVozilo(), paket);
            }
            return -2;
        }
    }

    @Override
    public List<Integer> getPackagesInVehicle(String courierUserName) {
        if(courierUserName == null) return null;
        String licencePlate = sd170145_UserOperations.getUserVehicle(courierUserName);
        if (licencePlate == null) return null;
        String sqlCheckIfExist = "SELECT idPaketa FROM PaketVozilo WHERE registracioniBroj = ?";
        Connection connection = DataBase.getDataBase().getConnection();
        List<Integer> packageInVehicle = new ArrayList<>();
        try(   
            PreparedStatement statement = connection.prepareStatement(sqlCheckIfExist);
            ) {
                statement.setString(1, licencePlate);
                try(
                        ResultSet resultSet = statement.executeQuery();
                    ){
                       while(resultSet.next()){
                            packageInVehicle.add(resultSet.getInt("idPaketa"));
                       }
                       return packageInVehicle;
                }
        } catch (SQLException ex) {
            Logger.getLogger(sd170145_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null; 
    }
    
    private boolean assignVehicleToCourier(String userName){
        int courierCity = sd170145_CourierOperations.getCourierCity(userName);
        if(courierCity == -1) return false;
       
        String sql = "SELECT TOP 1 registracioniBroj,idMagacin FROM MagacinVozilo"
                   + " WHERE idMagacin IN (SELECT idAdresa FROM Adresa WHERE idGrad = ?)";
        Connection connection = DataBase.getDataBase().getConnection();
        try(
                PreparedStatement statement = connection.prepareStatement(sql,
                     ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
           ) {
                statement.setInt(1, courierCity);
                try (
                        ResultSet resultSet = statement.executeQuery();
                     ){
                        if (resultSet.isBeforeFirst()) {  
                            resultSet.next();
                            String licencePlate = resultSet.getString("registracioniBroj");
                            int idMagacin = resultSet.getInt("idMagacin");
                            resultSet.deleteRow();
                            sd170145_VehicleOperations.setVehicleInDrive(licencePlate, userName);
                            sd170145_CourierOperations.setCourierStatus(userName, 1);
                            voznjaInfo.put(userName, new VoznjaInfo());
                            voznjaInfo.get(userName).setAdresaMagacina(idMagacin);
                            voznjaInfo.get(userName).setIdVozilo(licencePlate);
                            voznjaInfo.get(userName).setCourirCity(courierCity);
                            setVehiclePropreties(userName, licencePlate);
                            return true;
                        } 
                      
                        return false; 
                    }   
        } catch (SQLException ex) {
            Logger.getLogger(sd170145_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;  
    }
    
    private void takeVehicleFromCourier(String userName){
           sd170145_VehicleOperations.setVehicleNotInDrive(userName);
           sd170145_CourierOperations.setCourierStatus(userName, 0);  
           String licencePlate = voznjaInfo.get(userName).getIdVozilo();
           int idMagacin = voznjaInfo.get(userName).getAdresaMagacina();
           new sd170145_VehicleOperations().parkVehicle(licencePlate, idMagacin);
    }
    
    private Queue<Paket> pickUpPackages(String licencePlate){
       Queue<Paket> paketi = sd170145_PackageOperations.getAllPackagesForDeliveryFromCity(voznjaInfo.get(userName).getCourirCity(), true, true);
       Queue<Paket> paketiZaIsporuku = new LinkedList<>();
       Queue<Paket> trebaIsporuciti = new LinkedList<>();
       BigDecimal nosivost = voznjaInfo.get(userName).getNosivost();
       BigDecimal trenutnaNosivost = new BigDecimal(0.0);
       
       for(Paket paket: paketi){
            if(trenutnaNosivost.compareTo(nosivost) >=0) continue;//probati da ubacis neki sledeci;
            paketiZaIsporuku.add(paket);
            trenutnaNosivost = trenutnaNosivost.add(paket.getTezina());
       }
       for(Paket paket: paketiZaIsporuku)
             trebaIsporuciti.add(paket.clone());
       Queue<Paket> magacinPaketi = new LinkedList<>();
       if(trenutnaNosivost.compareTo(nosivost) < 0){
           paketi = sd170145_PackageOperations.getAllPackagesFromStockRoom(voznjaInfo.get(userName).getAdresaMagacina(), true, true);
            for(Paket paket: paketi){
                if(trenutnaNosivost.compareTo(nosivost) >=0) continue;
                magacinPaketi.add(paket);
                trenutnaNosivost = trenutnaNosivost.add(paket.getTezina());
            }
       }
        for(Paket paket: magacinPaketi)
             trebaIsporuciti.add(paket.clone());
       if(magacinPaketi.size()!=0){
          voznjaInfo.get(userName).getPaketiIzMagacina().put(voznjaInfo.get(userName).getAdresaMagacina(), magacinPaketi);
          Paket paket = new Paket();
          paket.setInMagacin(true);
          paket.setZaPreuzimanje(true);
          paket.setZaIsporuku(true);
          paket.setIdadress(magacinPaketi.peek().getIdadress());
          paket.setxCoord(magacinPaketi.peek().getxCoord());
          paket.setyCoord(magacinPaketi.peek().getyCoord());
          markPackagesForPickUp(magacinPaketi);
          markPackagesForPickUp(paketiZaIsporuku);
          paketiZaIsporuku.add(paket);
       } else{
           markPackagesForPickUp(paketiZaIsporuku);
       }
       if(paketiZaIsporuku.size() == 0 && magacinPaketi.size()==0) return null;
       voznjaInfo.get(userName).setTrenutnaTezina(trenutnaNosivost);
       for(Paket paket:trebaIsporuciti){
           paket.setZaPreuzimanje(false);
       }
       voznjaInfo.get(userName).setPaketiZaIsporuku(trebaIsporuciti);
       return paketiZaIsporuku;
    }
    
    private Queue<Paket> pickUpPackagesFromCity(int idGrada){
       Queue<Paket> paketi = sd170145_PackageOperations.getAllPackagesForDeliveryFromCity(idGrada, false, true);
       Queue<Paket> paketiZaIsporuku = new LinkedList<>();
       BigDecimal nosivost = voznjaInfo.get(userName).getNosivost();
       BigDecimal trenutnaNosivost = voznjaInfo.get(userName).getTrenutnaTezina();
       
       for(Paket paket: paketi){
            if(trenutnaNosivost.compareTo(nosivost) >=0) continue;//probati da ubacis neki sledeci;
            paketiZaIsporuku.add(paket);
            trenutnaNosivost.add(paket.getTezina());
       }
       int idMagacin = sd170145_StockroomOperations.getStockRoomInCity(idGrada);
       Queue<Paket> paketiIzMagacina = new LinkedList<>();
       if(trenutnaNosivost.compareTo(nosivost) < 0 && idMagacin != -1){
            paketi = sd170145_PackageOperations.getAllPackagesFromStockRoom(idMagacin, false, true);
            for(Paket paket: paketi){
                if(trenutnaNosivost.compareTo(nosivost) >=0) continue;
                paketiIzMagacina.add(paket);   
                trenutnaNosivost =  trenutnaNosivost.add(paket.getTezina());
            }
       } 
        if(paketiIzMagacina.size()!=0){
            voznjaInfo.get(userName).getPaketiIzMagacina().put(idMagacin,paketiIzMagacina );
            Paket paket = new Paket();
            paket.setInMagacin(true);
            paket.setZaPreuzimanje(true);
            paket.setZaIsporuku(false);
            paket.setIdadress(paketiIzMagacina.peek().getIdadress());
            paket.setxCoord(paketiIzMagacina.peek().getxCoord());
            paket.setyCoord(paketiIzMagacina.peek().getyCoord());
            markPackagesForPickUp(paketiIzMagacina);
            markPackagesForPickUp(paketiZaIsporuku);
            paketiZaIsporuku.add(paket);
       } else {
            markPackagesForPickUp(paketiZaIsporuku);
        }
       voznjaInfo.get(userName).setTrenutnaTezina(trenutnaNosivost);
       if(paketiZaIsporuku.size() == 0 && paketiIzMagacina.size() == 0) return null;
       //oznaci da ce ih neko preuzeti
        
       return paketiZaIsporuku;
    }
    
    private void getPakcageDeliveryCordinates(Queue<Paket> paketi, boolean pocetnaAdresa){
        paketi.forEach((paket) -> {
            int id = paket.getIdPackage();
            String sql = "SELECT xKoordinata, yKoordinata,idAdresa, idGrad"
                    + " FROM ZahtevZaPrevozom"
                    + " INNER JOIN Adresa ON Adresa.idAdresa = zahtevZaPrevozom.adresaIsporuke"
                    + " WHERE idZahteva = " + id;
            
            if(pocetnaAdresa) 
                sql = "SELECT xKoordinata, yKoordinata,idAdresa, idGrad"
                    + " FROM ZahtevZaPrevozom"
                    + " INNER JOIN Adresa ON Adresa.idAdresa = zahtevZaPrevozom.adresaPreuzimanja"
                    + " WHERE idZahteva = " + id;
            
            Connection connection = DataBase.getDataBase().getConnection();
            try(   
                    Statement statement = connection.createStatement();
                    ) {
                try (
                        ResultSet resultSet = statement.executeQuery(sql);
                        ){
                    resultSet.next();
                    paket.setIdadress(resultSet.getInt("idAdresa"));
                    paket.setxCoord(resultSet.getInt("xKoordinata"));
                    paket.setyCoord(resultSet.getInt("yKoordinata"));
                    paket.setIdGrad(resultSet.getInt("idGrad"));
                }
                
                
            }   catch (SQLException ex) {
                Logger.getLogger(sd170145_DriveOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
   
    }
   
    private void getStartCoordinates(Paket paket){
        int id = voznjaInfo.get(userName).getAdresaMagacina();
        String sql = "SELECT xKoordinata, yKoordinata,idAdresa, idGrad"
                    + " FROM Adresa" 
                    + " WHERE idAdresa=" + id;

            Connection connection = DataBase.getDataBase().getConnection();
            try(   
                    Statement statement = connection.createStatement();
                    ) {
                try (
                        ResultSet resultSet = statement.executeQuery(sql);
                        ){
                    resultSet.next();
                    paket.setIdadress(resultSet.getInt("idAdresa"));
                    paket.setxCoord(resultSet.getInt("xKoordinata"));
                    paket.setyCoord(resultSet.getInt("yKoordinata"));
                    paket.setIdGrad(resultSet.getInt("idGrad"));
                }
                
                
            }   catch (SQLException ex) {
                Logger.getLogger(sd170145_DriveOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
         
    }
    
    private void makeDriveRoute(Queue<Paket> paketi){
        Queue<Paket> temp = paketi;
        paketi = voznjaInfo.get(userName).getPaketiZaIsporuku();
        getPakcageDeliveryCordinates(paketi, false);
        Queue<Paket> sortiraniPaketi = new LinkedList<>();
        Paket paketZaPoredjenje = new Paket();
        getStartCoordinates(paketZaPoredjenje);
        Paket krajRute = paketZaPoredjenje;
        while(paketi.size() != 0){    
            Iterator<Paket> iter = paketi.iterator();
            Paket zaBrisanje = null;
            BigDecimal najmanjeRastojanje = new BigDecimal(BigInteger.ZERO);
            while(iter.hasNext()){
                Paket paket = iter.next();
                if(najmanjeRastojanje.compareTo(paket.getEuclidsDistance(paketZaPoredjenje))>=0 || zaBrisanje == null){
                    najmanjeRastojanje = paket.getEuclidsDistance(paketZaPoredjenje);
                    zaBrisanje = paket;
                }
            }
           paketZaPoredjenje = zaBrisanje;
           if(zaBrisanje != null) paketi.remove(zaBrisanje);
           sortiraniPaketi.add(paketZaPoredjenje);
        }
        Queue<Paket> rutaZaIsporuku = new LinkedList<>();
        int idGrada = -1;
        int idAdresaProlsa = -1;
        BigDecimal nosivostVozila = voznjaInfo.get(userName).getTrenutnaTezina();
        for(Paket paket:sortiraniPaketi){
           int idGradTrenutni = sd170145_AddressOperations.getAddressCity(paket.getIdadress());
           if(idGrada == -1) idGrada = idGradTrenutni;
            if(idGrada != idGradTrenutni){
               //zavrsili sa isporukom paketa u jednom gradu, treba pokupiti sada pakete.
               Queue<Paket> zaPreuzimanje = pickUpPackagesFromCity(idGrada);
               if(zaPreuzimanje != null){
                    rutaZaIsporuku.addAll(zaPreuzimanje);
               }
               idGrada = idGradTrenutni;//Sada smo u novom gradu.
           }
           idAdresaProlsa = paket.getIdadress();
           nosivostVozila.subtract(paket.getTezina());
           voznjaInfo.get(userName).setTrenutnaTezina(nosivostVozila);
           rutaZaIsporuku.add(paket); 
        }
        idGrada = sd170145_AddressOperations.getAddressCity(idAdresaProlsa);
        Queue<Paket> zaPreuzimanje = pickUpPackagesFromCity(idGrada);
        if(zaPreuzimanje != null){
          rutaZaIsporuku.addAll(zaPreuzimanje);
        }      
       rutaZaIsporuku.add(krajRute);
       temp.addAll(rutaZaIsporuku);
       voznjaInfo.get(userName).setRuta(temp);
       paketZaPoredjenje = krajRute;
       BigDecimal ukupnaDistanca = new BigDecimal(0.0);
       BigDecimal ukupnaCena = new BigDecimal(0.0);
       for(Paket paket: temp){
           ukupnaDistanca = ukupnaDistanca.add(paket.getEuclidsDistance(paketZaPoredjenje));
           paketZaPoredjenje = paket;
           if(paket.isZaIsporuku() && !paket.isZaPreuzimanje()) ukupnaCena = ukupnaCena.add(paket.getCena());
       }
       voznjaInfo.get(userName).setUkupnaDistanca(ukupnaDistanca);
       voznjaInfo.get(userName).setUkupnaCena(ukupnaCena);
    }
    
    public static void updateCourierProfit(String userName){
        if(userName == null) return;
        BigDecimal profit = voznjaInfo.get(userName).izracunajProfit();
        String sqlUpdateProfit = "UPDATE Kurir SET profit=profit+? WHERE korisnickoIme=?";
        Connection connection = DataBase.getDataBase().getConnection();
        try(
                PreparedStatement statement = connection.prepareStatement(sqlUpdateProfit);
            ) {
                statement.setBigDecimal(1, profit);
                statement.setString(2, userName);
                statement.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(sd170145_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return;
    };
    
    public static void markPackagesForPickUp(Queue<Paket> paketi){
        if(paketi == null) return;
        String sqlSetPackageStatus;
            sqlSetPackageStatus = "UPDATE Paket SET oznacenZaPreuzimanje=1 WHERE idPaketa=?";
        Connection connection = DataBase.getDataBase().getConnection();
        try(
                PreparedStatement statement = connection.prepareStatement(sqlSetPackageStatus);
            ) {
                for(Paket paket:paketi){
                    statement.setInt(1, paket.getIdPackage());
                    statement.executeUpdate();
                }
        } catch (SQLException ex) {
            Logger.getLogger(sd170145_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
       
        
        
    }
    
    public static void putPackagesInVehicle(Queue<Paket> paketi, String licencePlate, boolean zaPreuzimanje){
        if(paketi == null || licencePlate == null) return;
        String sqlPickUpPackages = "INSERT INTO PaketVozilo(idPaketa, registracioniBroj) VALUES(?,?)";
        String sqlSetPackageStatus;
        sqlSetPackageStatus = "UPDATE Paket SET statusPaketa=2, trenutnaLokacija=NULL WHERE idPaketa=?";
        Connection connection = DataBase.getDataBase().getConnection();
        try(
                PreparedStatement statement = connection.prepareStatement(sqlPickUpPackages);
                PreparedStatement statement1 = connection.prepareStatement(sqlSetPackageStatus);
            ){
                statement.setString(2,licencePlate);
                for(Paket paket:paketi){
                    statement.setInt(1, paket.getIdPackage());
                    statement.executeUpdate();
                    statement1.setInt(1, paket.getIdPackage());
                    statement1.executeUpdate();
                }
        } catch (SQLException ex) {
            Logger.getLogger(sd170145_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }  
    }
    
    public static void setVehiclePropreties(String userName, String licencePlate) {
        if(userName == null || licencePlate == null) return;
            String sqlCheckIfExist = "SELECT * FROM Vozilo WHERE registracioniBroj = ?";
            Connection connection = DataBase.getDataBase().getConnection();
            try(   
                PreparedStatement statement = connection.prepareStatement(sqlCheckIfExist);
                ) {
                    statement.setString(1, licencePlate);
                    try(
                            ResultSet resultSet = statement.executeQuery();
                        ){
                            if(resultSet.isBeforeFirst()){
                                resultSet.next();
                                VoznjaInfo voznja = voznjaInfo.get(userName);
                                voznja.setNosivost(resultSet.getBigDecimal("nosivost"));
                                voznja.setPotrosnja(resultSet.getBigDecimal("potrosnja"));
                                voznja.setTipVozila(resultSet.getInt("tipGoriva"));
                                return;
                            }
                    }
            } catch (SQLException ex) {
                Logger.getLogger(sd170145_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
            return; 
    }
      
    public void putPackageInVehicle(String licencePlate, Paket paket){
        String sqlInsertIntoVehicle;
        sqlInsertIntoVehicle = "INSERT INTO PaketVozilo(idPaketa,registracioniBroj) VALUES(?,?)";
        Connection connection = DataBase.getDataBase().getConnection();
        try(
                PreparedStatement statement = connection.prepareStatement(sqlInsertIntoVehicle);
            ) {
                    statement.setInt(1, paket.getIdPackage());
                    statement.setString(2, licencePlate);
                    statement.executeUpdate(); 
        } catch (SQLException ex) {
            Logger.getLogger(sd170145_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        setPackageLocation(paket.getIdPackage(), -1,2);
     }
     
    public void takePackageFromVehicle(String licencePlate, Paket paket){
        String sqlRmoveFromVehicle;
        sqlRmoveFromVehicle = "DELETE FROM PaketVozilo WHERE idPaketa=" + paket.getIdPackage();     
        Connection connection = DataBase.getDataBase().getConnection();
        try(
                Statement statement = connection.createStatement();
            ) {
                    statement.executeUpdate(sqlRmoveFromVehicle); 
        } catch (SQLException ex) {
            Logger.getLogger(sd170145_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        setPackageLocation(paket.getIdPackage(), paket.getIdadress(), 3);
     }
     
    public void setPackageLocation(int idPackage, int newLocation, int newStatus){
        String sqlSetPackageStatus;
        sqlSetPackageStatus = "UPDATE Paket SET trenutnaLokacija=NULL, statusPaketa="+ newStatus + "WHERE idPaketa=" + idPackage;
        if(newLocation > 0)  
            sqlSetPackageStatus = "UPDATE Paket SET trenutnaLokacija=" + newLocation  + ", statusPaketa=" + newStatus+ " WHERE idPaketa=" + idPackage;          
        Connection connection = DataBase.getDataBase().getConnection();
        try(
                Statement statement = connection.createStatement();
            ) {
                    statement.executeUpdate(sqlSetPackageStatus); 
        } catch (SQLException ex) {
            Logger.getLogger(sd170145_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
     }
     
    public static void insertPackagesInStockRoom(int idStockRoom, Queue<Paket> paketi){
        if(paketi == null) return;
        String sqlPickUpPackages = "INSERT INTO MagacinPaketi(idPaketa, idMagacin) VALUES(?,?)";
        String sqlSetPackageStatus;
        sqlSetPackageStatus = "UPDATE Paket SET trenutnaLokacija=" + idStockRoom + ", oznacenZaPreuzimanje=0 WHERE idPaketa=?";
        Connection connection = DataBase.getDataBase().getConnection();
        try(
                PreparedStatement statement = connection.prepareStatement(sqlPickUpPackages);
                PreparedStatement statement1 = connection.prepareStatement(sqlSetPackageStatus);
            ){
                statement.setInt(2, idStockRoom);
                for(Paket paket:paketi){
                    statement.setInt(1, paket.getIdPackage());
                    statement.executeUpdate();
                    statement1.setInt(1, paket.getIdPackage());
                    statement1.executeUpdate();
                }
        } catch (SQLException ex) {
            Logger.getLogger(sd170145_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
 }
 
    public static void removePackagesFromVehicleForStockRoom(String licencePlate){
        String sqlRmoveFromVehicle;
        sqlRmoveFromVehicle = "DELETE FROM PaketVozilo WHERE registracioniBroj=?";
        Connection connection = DataBase.getDataBase().getConnection();
        try(
                PreparedStatement statement = connection.prepareStatement(sqlRmoveFromVehicle);
            ) {
                    statement.setString(1, licencePlate);
                    statement.executeUpdate(); 
        } catch (SQLException ex) {
            Logger.getLogger(sd170145_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}