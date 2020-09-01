/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student;

import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ducati
 */
public class Paket implements Cloneable{
    private int idPackage;
    private int idadress;
    private int xCoord;
    private int yCoord;
    private BigDecimal tezina;
    private BigDecimal cena;
    private int idGrad;
    private boolean inMagacin;
    private boolean zaIsporuku;
    private boolean zaPreuzimanje;

    public boolean isZaPreuzimanje() {
        return zaPreuzimanje;
    }

    public void setZaPreuzimanje(boolean zaPreuzimanje) {
        this.zaPreuzimanje = zaPreuzimanje;
    }

    public boolean isInMagacin() {
        return inMagacin;
    }


    public void setInMagacin(boolean inMagacin) {
        this.inMagacin = inMagacin;
    }

    public boolean isMagacin() {
        return inMagacin;
    }

    public void setIdGrad(int idGrad) {
        this.idGrad = idGrad;
    }

    public void setZaIsporuku(boolean zaIsporuku) {
        this.zaIsporuku = zaIsporuku;
    }

    public int getIdGrad() {
        return idGrad;
    }

    public boolean isZaIsporuku() {
        return zaIsporuku;
    }
    public void setTezina(BigDecimal tezina) {
        this.tezina = tezina;
    }

    public void setCena(BigDecimal cena) {
        this.cena = cena;
    }

    public BigDecimal getCena() {
        return cena;
    }

    public BigDecimal getTezina() {
        return tezina;
    }

    public Paket() {
       zaPreuzimanje = false;
       zaIsporuku = false;
       inMagacin = false;
    }

    public void setIdPackage(int idPackage) {
        this.idPackage = idPackage;
    }

    public void setIdadress(int idadress) {
        this.idadress = idadress;
    }

    public void setxCoord(int xCoordStart) {
        this.xCoord = xCoordStart;
    }

    public void setyCoord(int yCoordStart) {
        this.yCoord = yCoordStart;
    }

    public int getxCoord() {
        return xCoord;
    }

    public int getyCoord() {
        return yCoord;
    }

  
    public int getIdPackage() {
        return idPackage;
    }

    public int getIdadress() {
        return idadress;
    }
    
    
    public boolean equals(Object obj) {
    if (obj == null) return false;
    if (obj == this) return true;
    if (!(obj instanceof Paket)) return false;
    Paket o = (Paket) obj;
    return o.idPackage == this.idPackage;
}

    BigDecimal getEuclidsDistance(Paket paketZaPoredjenje) {
        int startX = paketZaPoredjenje.xCoord, startY = paketZaPoredjenje.yCoord;
        int endX = this.xCoord, endY = this.yCoord;
        return BigDecimal.valueOf(Math.sqrt(Math.pow(startX-endX, 2) + Math.pow(startY - endY, 2)));
    }

    @Override
    protected Paket clone() {
        try {
            Paket noviPaket =  (Paket)super.clone(); //To change body of generated methods, choose Tools | Templates.
             if(this.cena != null) noviPaket.cena = new BigDecimal(this.cena.toString());
             if(this.tezina != null) noviPaket.tezina = new BigDecimal(this.tezina.toString());
            return noviPaket;
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(Paket.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    
}
