
package com.mycompany.algoritmoper;


import java.util.*;


public class ProyectoPERT {
     
    private static final double EPSILON = 0.0000001;

    private List<AlgoritmoPER> actividades;
    private List<AlgoritmoPER> ordenTopologico;
    private List<List<AlgoritmoPER>> rutasCriticas;

    private double duracionProyecto;
    private double varianzaProyecto;
    private double desviacionEstandar;
    private double plazoObjetivo;

    private String unidad;

    private Scanner entrada;
    private CalculadoraProbabilidad calculadora;

    public ProyectoPERT() {
        actividades = new ArrayList<>();
        ordenTopologico = new ArrayList<>();
        rutasCriticas = new ArrayList<>();

        entrada = new Scanner(System.in);
        calculadora = new CalculadoraProbabilidad();
    }

 
    private String generarNombre(int numero) {

        StringBuilder nombre = new StringBuilder();
        int n = numero + 1;

        while (n > 0) {
            n--;
            nombre.insert(0, (char) ('A' + n % 26));
            n /= 26;
        }

        return nombre.toString();
    }

    // CAPTURAR LOS DATOS DEL PROYECTO
    public void capturarDatos() {

        System.out.println("======================================");
        System.out.println("             SISTEMA PERT");
     

        int cantidad = leerEnteroPositivo(
                "Ingrese la cantidad de actividades: ");

        // Primero se crean todas las actividades.
        for (int i = 0; i < cantidad; i++) {

            String nombre = generarNombre(i);

            System.out.println("\n--------------------------------------");
            System.out.println("ACTIVIDAD " + nombre);
            System.out.println("--------------------------------------");

            double o = leerDoubleNoNegativo(
                    "Tiempo optimista (O): ");

            double m = leerDoubleNoNegativo(
                    "Tiempo mas probable (M): ");

            double p = leerDoubleNoNegativo(
                    "Tiempo pesimista (P): ");

            while (!(o <= m && m <= p)) {

                System.out.println(
                        "Error: debe cumplirse O <= M <= P.");

                o = leerDoubleNoNegativo(
                        "Tiempo optimista (O): ");

                m = leerDoubleNoNegativo(
                        "Tiempo mas probable (M): ");

                p = leerDoubleNoNegativo(
                        "Tiempo pesimista (P): ");
            }

            AlgoritmoPER actividad =
                    new AlgoritmoPER(nombre, o, m, p);

            actividad.calcularTiempoEsperado();
            actividad.calcularVarianza();

            actividades.add(actividad);
        }

        // Después se capturan los predecesores.
        capturarPredecesores();

        System.out.print("\nUnidad de tiempo (dias, semanas, meses, etc.): ");
        unidad = entrada.nextLine().trim();

        if (unidad.isEmpty()) {
            unidad = "unidades de tiempo";
        }

        plazoObjetivo = leerDoubleNoNegativo(
                "\nIngrese el plazo objetivo del proyecto (" +
                        unidad + "): ");
    }

    // CREAR MAPA DE ACTIVIDADES
    private Map<String, AlgoritmoPER> crearMapa() {

        Map<String, AlgoritmoPER> mapa = new HashMap<>();

        for (AlgoritmoPER actividad : actividades) {
            mapa.put(actividad.getNombre(), actividad);
        }

        return mapa;
    }

    // MOSTRAR LOS NOMBRES DE LAS ACTIVIDADES
    private String nombresActividades() {

        StringBuilder resultado = new StringBuilder();

        for (AlgoritmoPER actividad : actividades) {

            if (resultado.length() > 0) {
                resultado.append(", ");
            }

            resultado.append(actividad.getNombre());
        }

        return resultado.toString();
    }

    // CAPTURAR PREDECESORES
    private void capturarPredecesores() {

        System.out.println("\n======================================");
        System.out.println("       CAPTURA DE PREDECESORES");
     

        Map<String, AlgoritmoPER> mapa = crearMapa();

        for (AlgoritmoPER actividad : actividades) {

            System.out.println("\nActividad: " + actividad.getNombre());

            System.out.println(
                    "Actividades disponibles: " + nombresActividades());

            System.out.println(
                    "Escriba los predecesores separados por coma.");

            System.out.println(
                    "Ejemplo: A,B | Sin predecesores: -");

            while (true) {

                System.out.print("Predecesores: ");

                String texto = entrada.nextLine().trim();

                try {
                    procesarPredecesores(actividad, texto, mapa);
                    break;

                } catch (IllegalArgumentException e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }
        }
    }

    // VALIDAR Y AGREGAR PREDECESORES
    private void procesarPredecesores(
            AlgoritmoPER actividad,
            String texto,
            Map<String, AlgoritmoPER> mapa) {

        if (texto.equals("-")) {
            return;
        }

        if (texto.isEmpty()) {
            throw new IllegalArgumentException(
                    "Ingrese '-' o una lista de predecesores.");
        }

        String[] nombres = texto.split(",", -1);

        Set<String> utilizados = new HashSet<>();
        List<AlgoritmoPER> nuevosPredecesores = new ArrayList<>();

        // Primero se validan todos.
        for (String nombre : nombres) {

            nombre = nombre.trim().toUpperCase();

            if (nombre.isEmpty()) {
                throw new IllegalArgumentException(
                        "Hay un nombre de actividad vacio.");
            }

            if (!utilizados.add(nombre)) {
                throw new IllegalArgumentException(
                        "No repita el mismo predecesor.");
            }

            AlgoritmoPER predecesor = mapa.get(nombre);

            if (predecesor == null) {
                throw new IllegalArgumentException(
                        "La actividad " + nombre + " no existe.");
            }

            if (predecesor == actividad) {
                throw new IllegalArgumentException(
                        "Una actividad no puede depender de si misma.");
            }

            nuevosPredecesores.add(predecesor);
        }

        // Si todos son válidos, se agregan.
        for (AlgoritmoPER predecesor : nuevosPredecesores) {

            actividad.agregarPredecesor(predecesor);
            predecesor.agregarSucesor(actividad);
        }
    }

    // VALIDAR DEPENDENCIAS Y DETECTAR CICLOS
    public void validarPredecesores() {

        Map<AlgoritmoPER, Integer> grados = new HashMap<>();
        Queue<AlgoritmoPER> cola = new LinkedList<>();

        for (AlgoritmoPER actividad : actividades) {

            int grado = actividad.getPredecesores().size();

            grados.put(actividad, grado);

            if (grado == 0) {
                cola.add(actividad);
            }
        }

        ordenTopologico.clear();

        while (!cola.isEmpty()) {

            AlgoritmoPER actual = cola.poll();

            ordenTopologico.add(actual);

            for (AlgoritmoPER sucesora : actual.getSucesores()) {

                int nuevoGrado = grados.get(sucesora) - 1;

                grados.put(sucesora, nuevoGrado);

                if (nuevoGrado == 0) {
                    cola.add(sucesora);
                }
            }
        }

        if (ordenTopologico.size() != actividades.size()) {

            throw new IllegalArgumentException(
                    "Se detecto una dependencia circular. " +
                    "Revise los predecesores.");
        }
    }

    // RECORRIDO HACIA ADELANTE
    public void calcularTiemposTempranos() {

        for (AlgoritmoPER actividad : ordenTopologico) {

            double inicio = 0;

            for (AlgoritmoPER predecesor :
                    actividad.getPredecesores()) {

                inicio = Math.max(
                        inicio,
                        predecesor.getFinTemprano());
            }

            actividad.setInicioTemprano(inicio);

            actividad.setFinTemprano(
                    inicio + actividad.getTiempoEsperado());
        }

        duracionProyecto = 0;

        for (AlgoritmoPER actividad : actividades) {

            if (actividad.getSucesores().isEmpty()) {

                duracionProyecto = Math.max(
                        duracionProyecto,
                        actividad.getFinTemprano());
            }
        }
    }

    // RECORRIDO HACIA ATRAS
    public void calcularTiemposTardios() {

        for (int i = ordenTopologico.size() - 1; i >= 0; i--) {

            AlgoritmoPER actividad = ordenTopologico.get(i);

            double finTardio;

            if (actividad.getSucesores().isEmpty()) {

                finTardio = duracionProyecto;

            } else {

                finTardio = Double.POSITIVE_INFINITY;

                for (AlgoritmoPER sucesora :
                        actividad.getSucesores()) {

                    finTardio = Math.min(
                            finTardio,
                            sucesora.getInicioTardio());
                }
            }

            actividad.setFinTardio(finTardio);

            actividad.setInicioTardio(
                    finTardio - actividad.getTiempoEsperado());
        }
    }

    // CALCULAR HOLGURAS
    public void calcularHolguras() {

        for (AlgoritmoPER actividad : actividades) {

            double holgura =
                    actividad.getInicioTardio()
                    - actividad.getInicioTemprano();

            if (Math.abs(holgura) < EPSILON) {
                holgura = 0;
            }

            actividad.setHolgura(holgura);

            actividad.setCritica(
                    Math.abs(holgura) < EPSILON);
        }
    }

    // IDENTIFICAR RUTAS CRITICAS
    public void identificarRutaCritica() {

        rutasCriticas.clear();

        for (AlgoritmoPER actividad : actividades) {

            if (!actividad.isCritica()) {
                continue;
            }

            boolean tienePredecesorCritico = false;

            for (AlgoritmoPER predecesor :
                    actividad.getPredecesores()) {

                if (predecesor.isCritica()
                        && Math.abs(
                        predecesor.getFinTemprano()
                        - actividad.getInicioTemprano()) < EPSILON) {

                    tienePredecesorCritico = true;
                    break;
                }
            }

            if (!tienePredecesorCritico) {

                List<AlgoritmoPER> camino = new ArrayList<>();

                buscarRutasCriticas(actividad, camino);
            }
        }
    }

    private void buscarRutasCriticas(
            AlgoritmoPER actual,
            List<AlgoritmoPER> camino) {

        camino.add(actual);

        boolean encontroSucesora = false;

        for (AlgoritmoPER sucesora : actual.getSucesores()) {

            if (sucesora.isCritica()
                    && Math.abs(
                    actual.getFinTemprano()
                    - sucesora.getInicioTemprano()) < EPSILON) {

                encontroSucesora = true;

                buscarRutasCriticas(sucesora, camino);
            }
        }

        if (!encontroSucesora) {

            rutasCriticas.add(new ArrayList<>(camino));
        }

        camino.remove(camino.size() - 1);
    }

    // CALCULAR VARIANZA Y DESVIACION DEL PROYECTO
    public void calcularEstadisticasProyecto() {

        varianzaProyecto = 0;

    
        double mayorVarianza = -1;

        for (List<AlgoritmoPER> ruta : rutasCriticas) {

            double suma = 0;

            for (AlgoritmoPER actividad : ruta) {
                suma += actividad.getVarianza();
            }

            if (suma > mayorVarianza) {
                mayorVarianza = suma;
            }
        }

        if (mayorVarianza >= 0) {
            varianzaProyecto = mayorVarianza;
        }

        desviacionEstandar = Math.sqrt(varianzaProyecto);
    }

    // MOSTRAR TABLA DE TIEMPOS PERT
    public void mostrarTablaInicial() {

        System.out.println("\n\n==============================================");
        System.out.println("           TABLA DE TIEMPOS PERT");
     

        System.out.printf(
                "%-12s %-12s %-12s %-12s %-12s%n",
                "Actividad", "O", "M", "P", "TE");

        for (AlgoritmoPER a : actividades) {

            System.out.printf(
                    "%-12s %-12.2f %-12.2f %-12.2f %-12.2f%n",
                    a.getNombre(),
                    a.getOptimista(),
                    a.getProbable(),
                    a.getPesimista(),
                    a.getTiempoEsperado());
        }

        System.out.println("\nVARIANZAS");

        System.out.printf("%-15s %-15s%n",
                "Actividad", "Varianza");

        for (AlgoritmoPER a : actividades) {

            System.out.printf(
                    "%-15s %-15.4f%n",
                    a.getNombre(),
                    a.getVarianza());
        }
    }

    // MOSTRAR TABLA GENERAL
    public void mostrarResultados() {

        System.out.println("\n\n==============================================================");
        System.out.println("                    TABLA GENERAL PERT");
        System.out.println("==============================================================");

        System.out.printf(
                "%-10s %9s %9s %9s %9s %9s %9s %-10s%n",
                "Actividad", "TE", "IT", "FT",
                "ITa", "FTa", "Holgura", "Critica");

        for (AlgoritmoPER a : actividades) {

            System.out.printf(
                    "%-10s %9.2f %9.2f %9.2f %9.2f %9.2f %9.2f %-10s%n",
                    a.getNombre(),
                    a.getTiempoEsperado(),
                    a.getInicioTemprano(),
                    a.getFinTemprano(),
                    a.getInicioTardio(),
                    a.getFinTardio(),
                    a.getHolgura(),
                    a.isCritica() ? "SI" : "NO");
        }

        System.out.println("\n======================================");
        System.out.println("        RESULTADOS DEL PROYECTO");
        System.out.println("======================================");

        System.out.printf(
                "Duracion esperada: %.4f %s%n",
                duracionProyecto, unidad);

        System.out.printf(
                "Varianza del proyecto: %.4f%n",
                varianzaProyecto);

        System.out.printf(
                "Desviacion estandar: %.4f %s%n",
                desviacionEstandar, unidad);

        System.out.println("\nRUTAS CRITICAS:");

        if (rutasCriticas.isEmpty()) {

            System.out.println("No se encontraron rutas criticas.");

        } else {

            for (int i = 0; i < rutasCriticas.size(); i++) {

                System.out.print("Ruta " + (i + 1) + ": ");

                List<AlgoritmoPER> ruta = rutasCriticas.get(i);

                for (int j = 0; j < ruta.size(); j++) {

                    System.out.print(ruta.get(j).getNombre());

                    if (j < ruta.size() - 1) {
                        System.out.print(" -> ");
                    }
                }

                System.out.println();
            }
        }

        calcularProbabilidad();
    }

    // CALCULAR PROBABILIDAD
    public void calcularProbabilidad() {

        double z = calculadora.calcularZ(
                plazoObjetivo,
                duracionProyecto,
                desviacionEstandar);

        double probabilidad =
                calculadora.obtenerProbabilidad(z);

        System.out.println("\n======================================");
        System.out.println("       ANALISIS DE PROBABILIDAD");
        System.out.println("======================================");

        System.out.printf(
                "Plazo objetivo: %.2f %s%n",
                plazoObjetivo, unidad);

        System.out.printf(
                "Duracion esperada: %.4f %s%n",
                duracionProyecto, unidad);

        if (Double.isInfinite(z)) {

            System.out.println("Valor Z: " +
                    (z > 0 ? "+infinito" : "-infinito"));

        } else {

            System.out.printf("Valor Z: %.4f%n", z);
        }

        System.out.printf(
                "Probabilidad de terminar en el plazo o antes: %.2f%%%n",
                probabilidad * 100);

        System.out.printf(
                "Probabilidad de exceder el plazo: %.2f%%%n",
                (1 - probabilidad) * 100);
    }

    // LEER ENTERO POSITIVO
    private int leerEnteroPositivo(String mensaje) {

        while (true) {

            System.out.print(mensaje);

            String texto = entrada.nextLine().trim();

            try {

                int numero = Integer.parseInt(texto);

                if (numero > 0) {
                    return numero;
                }

                System.out.println(
                        "Ingrese un numero entero mayor que cero.");

            } catch (NumberFormatException e) {

                System.out.println("Entrada no valida.");
            }
        }
    }

    // LEER NUMERO DECIMAL NO NEGATIVO
    private double leerDoubleNoNegativo(String mensaje) {

        while (true) {

            System.out.print(mensaje);

            String texto = entrada.nextLine().trim();

            try {

                double numero = Double.parseDouble(
                        texto.replace(',', '.'));

                if (Double.isFinite(numero) && numero >= 0) {
                    return numero;
                }

                System.out.println(
                        "Ingrese un numero valido mayor o igual a cero.");

            } catch (NumberFormatException e) {

                System.out.println("Entrada no valida.");
            }
        }
    }

    // EJECUTAR EL PROYECTO COMPLETO
    public void ejecutar() {

        capturarDatos();

        try {

            validarPredecesores();

        } catch (IllegalArgumentException e) {

            System.out.println("\nERROR: " + e.getMessage());

            System.out.println(
                    "No es posible calcular el proyecto.");

            return;
        }

        calcularTiemposTempranos();
        calcularTiemposTardios();
        calcularHolguras();
        identificarRutaCritica();
        calcularEstadisticasProyecto();

        mostrarTablaInicial();
        mostrarResultados();
    }
}
