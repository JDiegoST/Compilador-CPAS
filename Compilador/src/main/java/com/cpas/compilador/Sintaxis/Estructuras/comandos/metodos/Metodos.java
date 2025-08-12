package com.cpas.compilador.Sintaxis.Estructuras.comandos.metodos;

import com.cpas.compilador.Semantico.Eventos.EventosSemanticos;
import com.cpas.compilador.Sintaxis.Estructuras.AbstracSintaxis;
import com.cpas.compilador.Sintaxis.Estructuras.comandos.Comandos;

public class Metodos extends AbstracSintaxis {
    private final Comandos com;

    public Metodos(Comandos com) {
        this.com = com;
        this.six = this.com.sintaxis;
        this.sem = this.six.semantica;
        this.gen = this.six.generadorPL0;
    }

    public void imprimir(boolean banderaLn) {
        if (!this.six.getLexe().equals("(")) {
            this.six.hayError("Error de Sintaxis", "Se esperaba '(' y llego: " + this.six.getLexe());
        }

        String delim = ",";
        while (delim.equals(",")) {
            avanzarToken();

            this.six.expresionControl.expr();

            if (this.six.getLexe().equals(",")) {
                // this.pilaTipos.pop();
                this.gen.insertCodPL0(new String[] { "OPR", "0", "20" });
            }
            delim = this.six.getLexe();
        }
        if (!this.six.getLexe().equals(")")) {
            this.six.hayError("Error de Sintaxis", "Se esperaba ')' y llego: " + this.six.getLexe());
        } else {
            // this.pilaTipos.pop();
            if (banderaLn)
                this.gen.insertCodPL0(new String[] { "OPR", "0", "21" });
            else
                this.gen.insertCodPL0(new String[] { "OPR", "0", "20" });
        }
        avanzarToken();
    }

    public void eLee() {
        String nIde = "";

        avanzarToken();
        if (!this.six.getLexe().equals("("))
            this.six.hayError("Error de Sintaxis", "Se esperaba ( y llego: " + this.six.getLexe());
        avanzarToken();
        if (!this.six.getToken().equals("Ide"))
            this.six.hayError("Error de Sintaxis", ": Se esperaba una variable");
        else
            nIde = this.six.getLexe();
        avanzarToken();
        
        this.sem.consumir(new EventosSemanticos.ControlAccesoDimenciones(nIde));

        if (!this.six.getLexe().equals(")"))
            this.six.hayError("Error de Sintaxis", "Se esperaba ) y llego: " + this.six.getLexe());

        this.gen.insertCodPL0(new String[] { "OPR", nIde, "19" });
        avanzarToken();
    }
}
