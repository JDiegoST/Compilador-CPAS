package com.cpas.compilador.Generacion;

import java.util.LinkedHashMap;
import java.util.Map;

public class GeneradorPL0 {

    private int contPrograma = 1;
    private final Map<Integer, String[]> codigoPL0 = new LinkedHashMap<>();

    public GeneradorPL0() {}

    public int getContPrograma() {
        return this.contPrograma;
    }

    public void insertCodPL0(String[] direcciones) {
        this.codigoPL0.put(this.contPrograma, direcciones);
        this.contPrograma++;
    }

    public Map<Integer, String[]> getCodigoPL0() {
        return this.codigoPL0;
    }

    public void modificarRegistro(int indice, String[] nuevoContenido) {
        this.codigoPL0.replace(indice, nuevoContenido);
    }
    
}
