package com.cpas.compilador.Sintaxis.Estructuras.comandos.llamada_a_funcion;

import com.cpas.compilador.Semantico.Eventos.EventosSemanticos;
import com.cpas.compilador.Sintaxis.Estructuras.AbstracSintaxis;
import com.cpas.compilador.Sintaxis.Estructuras.comandos.Comandos;

public class LlamadaFunc extends AbstracSintaxis {
    
    private final Comandos comando;

    public LlamadaFunc(Comandos com) {
        this.comando = com;
        this.six = this.comando.sintaxis;
        this.sem = this.comando.sintaxis.semantica;
        this.gen = this.comando.sintaxis.generadorPL0;
    }

    public void lfunc(String nIde) {
        // GENRADOR DE CODIGO, LLAMADA DE FUNCION

        // if (funcIsTypeIndefinido(nIde) && this.estaImprimiendo) {
        //     this.six.hayError("Error Semantico", "No se puede imprimir una funcion que no regresa nada");
        // }

        String etiCFucn = this.six.genIdEtiqueta();
        this.gen.insertCodPL0(new String [] {"LOD", etiCFucn, "0"});

        String[] tieneParams = nIde.split("\\$");


        avanzarToken();
        if (!this.six.getLexe().equals(")")) {
            if (tieneParams.length == 1) {
                this.six.hayError("Error Sintaxis", "La funcion '" + tieneParams[0] + "', no necesita argumentos");
                while (!this.six.getLexe().equals(")")) {
                    avanzarToken();
                }
            } else { 
                uparams(tieneParams);
            }
        } else {
            if (tieneParams.length > 1)
                this.six.hayError("Error Sintaxis", "La funcion '" + tieneParams[0] + "', necesita " + (tieneParams.length - 1) + " argumentos");
        }
        if (!this.six.getLexe().equals(")"))
            this.six.hayError("Error Sintaxis", "Se esperaba '(' y llego: " + this.six.getLexe());
        avanzarToken();

        this.gen.insertCodPL0( new String [] { "CAL", nIde, "0" });
        this.six.insertarTabSimb(etiCFucn, new String[] { "E", "I", Integer.toString( this.gen.getContPrograma() ), "0" });
        
    }

    public void uparams(String[] params) {
        String deli;
        int contParams = 0;
        do {
            this.six.expresionControl.expr();
            contParams++;
            deli = this.six.getLexe();
            if (deli.equals(","))
                avanzarToken();
            else
                break;
        } while (true);

        this.sem.consumir(new EventosSemanticos.LlamadaFuncion(params, contParams));
    }
}
