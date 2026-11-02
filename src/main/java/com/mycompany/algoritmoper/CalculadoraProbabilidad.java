
package com.mycompany.algoritmoper;


public class CalculadoraProbabilidad {
    
   
    // CALCULAR VALOR Z
    public double calcularZ(
            double plazo,
            double duracion,
            double desviacion) {

        if (desviacion == 0) {

            return plazo >= duracion
                    ? Double.POSITIVE_INFINITY
                    : Double.NEGATIVE_INFINITY;
        }

        return (plazo - duracion) / desviacion;
    }

    // OBTENER PROBABILIDAD DE LA NORMAL ESTANDAR
    public double obtenerProbabilidad(double z) {

        if (z == Double.POSITIVE_INFINITY) {
            return 1.0;
        }

        if (z == Double.NEGATIVE_INFINITY) {
            return 0.0;
        }

        double x = Math.abs(z);

        double t = 1.0 / (1.0 + 0.2316419 * x);

        double d = 0.3989422804 *
                Math.exp(-x * x / 2.0);

        double probabilidad = d * t *
                (0.319381530
                + t * (-0.356563782
                + t * (1.781477937
                + t * (-1.821255978
                + t * 1.330274429))));

        if (z >= 0) {
            return 1.0 - probabilidad;
        } else {
            return probabilidad;
        }
    }
}


