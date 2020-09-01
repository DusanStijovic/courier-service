/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/**
 *
 * @author ducati
 */
public class VoznjaInfo {
    
    private String idVozilo;
    private int tipVozila;
    private BigDecimal potrosnja;
    private String korsnickoIme;
    private BigDecimal profit;
    private Queue<Paket> ruta;
    private Queue<Paket> zaPreuzimanje;
    private Map<Integer, Queue<Paket>> paketiIzMagacina;
    private BigDecimal trenutnaTezina;
    private BigDecimal nosivost;
    private int courirCity;
    private int adresaMagacina;
    private BigDecimal ukupnaDistanca;
    private BigDecimal ukupnaCena;
    private Queue<Paket> paketiZaIsporuku;

    public void setPaketiZaIsporuku(Queue<Paket> paketiZaIsporuku) {
        this.paketiZaIsporuku = paketiZaIsporuku;
    }

    public Queue<Paket> getPaketiZaIsporuku() {
        return paketiZaIsporuku;
    }


    public void setPaketiIzMagacina(Map<Integer, Queue<Paket>> paketiIzMagacina) {
        this.paketiIzMagacina = paketiIzMagacina;
    }

    public Map<Integer, Queue<Paket>> getPaketiIzMagacina() {
        return paketiIzMagacina;
    }

    public VoznjaInfo() {
        zaPreuzimanje = new LinkedList<>();
        paketiIzMagacina = new HashMap<>();
    }
 
    public Queue<Paket> getZaPreuzimanje() {
        return zaPreuzimanje;
    }

    public void setZaPreuzimanje(Queue<Paket> zaPreuzimanje) {
        this.zaPreuzimanje = zaPreuzimanje;
    }

    
    
    public int getTipVozila() {
        return tipVozila;
    }

    public BigDecimal getPotrosnja() {
        return potrosnja;
    }

    public void setTipVozila(int tipVozila) {
        this.tipVozila = tipVozila;
    }

    public void setPotrosnja(BigDecimal potrosnja) {
        this.potrosnja = potrosnja;
    }

    
    
    public void setUkupnaCena(BigDecimal ukupnaCena) {
        this.ukupnaCena = ukupnaCena;
    }

    public BigDecimal getUkupnaCena() {
        return ukupnaCena;
    }

    public void setUkupnaDistanca(BigDecimal ukupnaDistanca) {
        this.ukupnaDistanca = ukupnaDistanca;
    }

    public BigDecimal getUkupnaDistanca() {
        return ukupnaDistanca;
    }
    

    public void setTrenutnaTezina(BigDecimal trenutnaTezina) {
        this.trenutnaTezina = trenutnaTezina;
    }

    public BigDecimal getTrenutnaTezina() {
        return trenutnaTezina;
    }
    
    public void setAdresaMagacina(int adresaMagacina) {
        this.adresaMagacina = adresaMagacina;
    }

    public void setRuta(Queue<Paket> ruta) {
        this.ruta = ruta;
    }


    public Queue<Paket> getRuta() {
        return ruta;
    }


    public int getAdresaMagacina() {
        return adresaMagacina;
    }

    public int getCourirCity() {
        return courirCity;
    }

    public void setCourirCity(int courirCity) {
        this.courirCity = courirCity;
    }

    public void setIdVozilo(String idVozilo) {
        this.idVozilo = idVozilo;
    }

    public void setKorsnickoIme(String korsnickoIme) {
        this.korsnickoIme = korsnickoIme;
    }

    public void setProfit(BigDecimal profit) {
        this.profit = profit;
    }

    public void setNosivost(BigDecimal nosivost) {
        this.nosivost = nosivost;
    }

    public String getIdVozilo() {
        return idVozilo;
    }

    public String getKorsnickoIme() {
        return korsnickoIme;
    }

    public BigDecimal getProfit() {
        return profit;
    }

    public BigDecimal getNosivost() {
        return nosivost;
    }
    
    public int getCenaPoLitru(){
        if(tipVozila == 0) return 15;
        if(tipVozila == 1) return 32;
        if(tipVozila == 2) return 36;
        return 0;
    }
    
    public BigDecimal izracunajProfit(){
        BigDecimal ukupnaPotrosnja = ukupnaDistanca;
        ukupnaPotrosnja = ukupnaPotrosnja.multiply(potrosnja);
        ukupnaPotrosnja = ukupnaPotrosnja.multiply(BigDecimal.valueOf(getCenaPoLitru()));   
        return ukupnaCena.subtract(ukupnaPotrosnja);
    }
    
}
