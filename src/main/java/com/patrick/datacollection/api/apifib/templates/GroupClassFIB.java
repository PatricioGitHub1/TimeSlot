package com.patrick.datacollection.api.apifib.templates;

import java.time.LocalTime;

public class GroupClassFIB {
    private String codi_assig;
    private String grup;
    private int dia_setmana;
    private LocalTime inici;
    private int durada;
    private String tipus;
    private String aules;
    private String idioma;

    public String getCodi_assig() {
        return codi_assig;
    }

    public void setCodi_assig(String codi_assig) {
        this.codi_assig = codi_assig;
    }

    public String getGrup() {
        return grup;
    }

    public void setGrup(String grup) {
        this.grup = grup;
    }

    public int getDia_setmana() {
        return dia_setmana;
    }

    public void setDia_setmana(int dia_setmana) {
        this.dia_setmana = dia_setmana;
    }

    public LocalTime getInici() {
        return inici;
    }

    public void setInici(String inici) {
        this.inici = LocalTime.parse(inici);
    }

    public int getDurada() {
        return durada;
    }

    public void setDurada(int durada) {
        this.durada = durada;
    }

    public String getTipus() {
        return tipus;
    }

    public void setTipus(String tipus) {
        this.tipus = tipus;
    }

    public String getAules() {
        return aules;
    }

    public void setAules(String aules) {
        this.aules = aules;
    }

    public String getIdioma() {
        return idioma;
    }

    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }
}
