package com.cpas.compilador.core;
/*
 * 
 * Esta clase contendra todas las funciones que repiten su utilidad 
 * en varias clases.
 */

public abstract class WorkingSpace {

    protected boolean estaDentro(String bloque[], String valor) {
        for (String e:bloque) {
            if (e.equals(valor)) return true;
        }
        return false;   
    }

    protected static String indiceATipoInfo(String indice) {
        String tipoInfo;
        switch(indice) {
            case "E" -> tipoInfo = "ENTERA";
            case "D" -> tipoInfo = "DECIMAL";
            case "A" -> tipoInfo = "ALFABETICA";
            case "L" -> tipoInfo = "LOGICA";
            default -> tipoInfo = "INDEFINIDA";
        }
        return tipoInfo;
    }

    protected static String tipoAIndece(String tipo) {
        String indiceInfoTipo ;
        switch (tipo) {
            case "entera" -> indiceInfoTipo = "E";
            case "decimal" -> indiceInfoTipo = "D";
            case "logica" -> indiceInfoTipo = "L";
            case "alfabetica" -> indiceInfoTipo = "A";
            default -> indiceInfoTipo = "I";
        }
        
        return indiceInfoTipo;
    }
}
