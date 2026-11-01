package com.mycompany.algoritmoper;

import java.util.ArrayList;
import java.util.List;

import java.util.ArrayList;
import java.util.List;

public class AlgoritmoPER {

    private String nombre;

    private List<AlgoritmoPER> predecesores;
    private List<AlgoritmoPER> sucesores;

    private double optimista;
    private double probable;
    private double pesimista;

    private double tiempoEsperado;
    private double varianza;

    private double inicioTemprano;
    private double finTemprano;

    private double inicioTardio;
    private double finTardio;

    private double holgura;
    private boolean critica;

    public AlgoritmoPER(String nombre, double optimista, double probable, double pesimista) {
        this.nombre = nombre;
        this.optimista = optimista;
        this.probable = probable;
        this.pesimista = pesimista;

        predecesores = new ArrayList<>();
        sucesores = new ArrayList<>();

    }

    public void calcularTiempoEsperado() {
        tiempoEsperado = (optimista + 4 * probable + pesimista) / 6.0;
    }

    public void calcularVarianza() {
        varianza = Math.pow((pesimista - optimista) / 6.0, 2);
    }

    public void agregarPredecesor(AlgoritmoPER actividad) {
        if (!predecesores.contains(actividad)) {
            predecesores.add(actividad);
        }
    }

    public void agregarSucesor(AlgoritmoPER actividad) {
        if (!sucesores.contains(actividad)) {
            sucesores.add(actividad);
        }

    }

    public String getNombre() {
        return nombre;
    }

    public List<AlgoritmoPER> getPredecesores() {
        return predecesores;
    }

    public List<AlgoritmoPER> getSucesores() {
        return sucesores;
    }

    public double getOptimista() {
        return optimista;
    }

    public double getProbable() {
        return probable;
    }

    public double getPesimista() {
        return pesimista;
    }

    public double getTiempoEsperado() {
        return tiempoEsperado;
    }

    public double getVarianza() {
        return varianza;
    }

    public double getInicioTemprano() {
        return inicioTemprano;
    }

    public double getFinTemprano() {
        return finTemprano;
    }

    public double getInicioTardio() {
        return inicioTardio;
    }

    public double getFinTardio() {
        return finTardio;
    }

    public double getHolgura() {
        return holgura;
    }

    public boolean isCritica() {
        return critica;
    }

    public void setInicioTemprano(double valor) {
        inicioTemprano = valor;
    }

    public void setFinTemprano(double valor) {
        finTemprano = valor;
    }

    public void setInicioTardio(double valor) {
        inicioTardio = valor;
    }

    public void setFinTardio(double valor) {
        finTardio = valor;
    }

    public void setHolgura(double valor) {
        holgura = valor;
    }

    public void setCritica(boolean valor) {
        critica = valor;
    }

}
