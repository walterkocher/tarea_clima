package com.example.tarea_clima;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class RegistroClima {

    static double[][] datosClima = {
            // mes/año   tempMax tempMin tempMaxMes diaMaxTemp tempMinMes diaMinTemp precipTotal precipMax diaPrecipMax
            {12, 1999, 23.3, 8, 28.8, 25, 1.6, 5, 10.2, 4.2, 28},
            {1, 2000, 24, 8.7, 28.4, 26, 4, 22, 5, 2.1, 28},
            {2, 2000, 22.9, 10.1, 32.2, 19, 4, 13, 129.2, 27.2, 10},
            {12, 2009, 20.3, 8, 24, 6, 3.5, 3, 61.8, 19.4, 23},
            {1, 2010, 22.7, 7.9, 28.6, 25, 2.2, 31, 35.4, 24.6, 11},
            {2, 2010, 22, 8.3, 31.9, 24, 2.2, 4, 70, 27.6, 6},
            {12, 2019, 23.6, 7, 28.9, 31, 1.4, 25, 14.8, 7.4, 23},
            {1, 2020, 26.3, 8.7, 32.9, 22, 2.7, 15, 16, 12.7, 7},
            {2, 2020, 27, 7.6, 37.5, 29, 2.9, 7, 23.2, 18.5, 3}
    };

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        do {
        System.out.print("Ingrese el año (1990-2022): ");
        int año;
        do {
            año = scanner.nextInt();
            if (año < 1990 || año > 2022) {
                System.out.println("Año fuera del rango válido (1999-2022). Inténtelo de nuevo.");
            }
        } while (año < 1990 || año > 2022);

        int mesNum;
        String nombreMes;
        do {
            System.out.print("Ingrese el nombre del mes (diciembre, enero, febrero): ");
            nombreMes = scanner.next().toLowerCase();
            mesNum = obtenerNumeroMes(nombreMes);
        } while (mesNum == -1);

        if (mesNum != 1 && mesNum != 2 && mesNum != 12) {
            System.out.println("Mes inválido. Solo se pueden ingresar datos para diciembre, enero y febrero.");
            return;
        }

        System.out.print("Ingrese el día: ");
        int dia;
        do {
            dia = scanner.nextInt();
            if ((mesNum == 2 && (dia < 1 || (esAnioBisiesto(año) ? dia > 29 : dia > 28))) || (dia < 1 || dia > 31)) {
                System.out.println("Día inválido para el mes y año ingresados. Inténtelo de nuevo.");
            }
        } while ((mesNum == 2 && (dia < 1 || (esAnioBisiesto(año) ? dia > 29 : dia > 28))) || (dia < 1 || dia > 31));

        System.out.print("Ingrese la temperatura máxima del día: ");
        double tempMaxDia = scanner.nextDouble();

        System.out.print("Ingrese la temperatura mínima del día: ");
        double tempMinDia;
        do {
            tempMinDia = scanner.nextDouble();
            if (tempMinDia >= tempMaxDia) {
                System.out.println("La temperatura mínima debe ser menor que la temperatura máxima. Inténtelo de nuevo.");
            }
        } while (tempMinDia >= tempMaxDia);

        System.out.print("Ingrese la precipitación del día: ");
        double precipitacionDia = scanner.nextDouble();

        double[] nuevosDatos = { mesNum, año, tempMaxDia, tempMinDia, 0.0, 0, 0.0, 0, precipitacionDia, 0.0, dia};

        // Calcular las variables restantes
        calcularVariables(nuevosDatos);

        // Agregar los nuevos datos a la base de datos
        agregarDatos(nuevosDatos);

        // Guardar en archivo
        try {
            guardarEnArchivoPorAñoMes(nuevosDatos, año, mesNum);
        } catch (IOException e) {
            System.err.println("Error al guardar en el archivo.");
            e.printStackTrace();
        }

        // Mostrar el resumen para el mes y año del archivo correspondiente
        mostrarResumenDesdeArchivo(año, mesNum);
            System.out.println();
            System.out.print("¿Desea ingresar más datos? (si/no): ");
            String continuar = scanner.next();

            if (!continuar.equalsIgnoreCase("si")) {
                break;
            }

        } while (true);

        scanner.close();
    }


    static boolean esAnioBisiesto(int año) {
        return (año % 4 == 0 && año % 100 != 0) || (año % 400 == 0);
    }
    static void mostrarResumenDesdeArchivo(int año, int mes) {
        try {
            BufferedReader br = new BufferedReader(new FileReader("datos_" + obtenerNombreMes(mes) + "_" + año + ".txt"));
            String linea;
            double tempMaxMedia = 0.0;
            double tempMinMedia = 0.0;
            double tempMaxMes = Double.MIN_VALUE;
            int diaMaxTemp = -1;
            double tempMinMes = Double.MAX_VALUE;
            int diaMinTemp = -1;
            double precipTotal = 0.0;
            double precipMax = Double.MIN_VALUE;
            int diaPrecipMax = -1;
            int conteo = 0;

            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split("\t");
                double[] datos = new double[partes.length];
                for (int i = 0; i < partes.length; i++) {
                    datos[i] = Double.parseDouble(partes[i]);
                }

                tempMaxMedia += datos[2];
                tempMinMedia += datos[3];
                tempMaxMes = Math.max(tempMaxMes, datos[4]);
                if (datos[4] == tempMaxMes) {
                    diaMaxTemp = (int) datos[10];
                }
                tempMinMes = Math.min(tempMinMes, datos[6]);
                if (datos[6] == tempMinMes) {
                    diaMinTemp = (int) datos[10];
                }
                precipTotal += datos[8];
                precipMax = Math.max(precipMax, datos[9]);
                if (datos[9] == precipMax) {
                    diaPrecipMax = (int) datos[10];
                }
                conteo++;
            }

            br.close();

            if (conteo > 0) {
                tempMaxMedia /= conteo;
                tempMinMedia /= conteo;

                System.out.println("\nResumen para " + obtenerNombreMes(mes) + " " + año + ":");
                System.out.println("Temperatura máxima media: " + tempMaxMedia);
                System.out.println("Temperatura mínima media: " + tempMinMedia);
                System.out.println("Temperatura máxima del mes: " + tempMaxMes);
                System.out.println("Día de máxima temperatura: " + diaMaxTemp);
                System.out.println("Temperatura mínima del mes: " + tempMinMes);
                System.out.println("Día de mínima temperatura: " + diaMinTemp);
                System.out.println("Precipitación total: " + precipTotal);
                System.out.println("Precipitación máxima: " + precipMax);
                System.out.println("Día de precipitación máxima: " + diaPrecipMax);
            } else {
                System.out.println("\nNo se encontraron datos para " + obtenerNombreMes(mes) + " " + año + ".");
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo.");
            e.printStackTrace();
        }
    }

    static void guardarEnArchivoPorAñoMes(double[] nuevosDatos, int año, int mes) throws IOException {
        String nombreArchivo = "datos_" + obtenerNombreMes(mes) + "_" + año + ".txt";
        FileWriter fw = new FileWriter(nombreArchivo, true);

        for (double dato : nuevosDatos) {
            fw.write(dato + "\t");
        }

        fw.write("\n");
        fw.close();
    }

    static void calcularVariables(double[] datos) {
        datos[4] = datos[2];
        datos[5] = (int) datos[3];
        datos[6] = datos[3];
        datos[7] = (int) datos[3];
        datos[9] = datos[8];
        datos[10] = (int) datos[10];
    }

    static void agregarDatos(double[] nuevosDatos) {
        double[][] nuevosDatosClima = new double[datosClima.length + 1][nuevosDatos.length];
        for (int i = 0; i < datosClima.length; i++) {
            for (int j = 0; j < datosClima[i].length; j++) {
                nuevosDatosClima[i][j] = datosClima[i][j];
            }
        }
        nuevosDatosClima[datosClima.length] = nuevosDatos;
        datosClima = nuevosDatosClima;
    }

    static String obtenerNombreMes(int mes) {
        String[] nombres = {"enero", "febrero", "marzo", "abril", "mayo", "junio", "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre"};
        return nombres[mes - 1];
    }

    static int obtenerNumeroMes(String nombreMes) {
        String[] nombres = {"enero", "febrero", "marzo", "abril", "mayo", "junio", "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre"};
        for (int i = 0; i < nombres.length; i++) {
            if (nombreMes.equals(nombres[i])) {
                return i + 1;
            }
        }
        return -1;
    }
}
