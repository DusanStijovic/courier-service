/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ducati
 */
public class DataBase {
    
    private static final String username = "LAPTOP-OQLQE8GK\\ducati";
    private static final String password = "d7178s1809";
    private static final String database = "KurirskaSluzba";
    private static final int port = 1433;
    private static final String server = "localhost";
    private static final String connectionUrl = "jdbc:sqlserver://LAPTOP-OQLQE8GK:1433;databaseName=KurirskaSluzba;IntegratedSecurity=true";
    private static final String connectionUr2 = "jdbc:sqlserver://LAPTOP-OQLQE8GK:1433;databaseName=" + database + ";" + "username=" + username + ";password=" + password;

    
    
    private Connection connection;  
    private static DataBase dataBase = null;
    
    private DataBase(){
        try {
            connection = DriverManager.getConnection(connectionUrl);
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public Connection getConnection(){
        return connection;
    }
            
    public static  DataBase getDataBase(){  
       if(dataBase == null){
           dataBase = new DataBase();
       }
       return dataBase;
    }
}
