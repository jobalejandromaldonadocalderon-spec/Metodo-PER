package com.mycompany.algoritmoper;

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
    public void calcularVarianza () {
        varianza = Math.pow((pesimista - optimista) /  6.0, 2);
    }
    public void agregarPredecesor ( AlgoritmoPER actividad) {
        if (! predecesores.contains (actividad)) {
            predecesores.add(actividad);
        }
    }

}
