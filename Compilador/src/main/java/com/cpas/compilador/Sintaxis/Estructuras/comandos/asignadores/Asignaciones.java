package com.cpas.compilador.Sintaxis.Estructuras.comandos.asignadores;


import com.cpas.compilador.Semantico.Eventos.EventosSemanticos;
import com.cpas.compilador.Sintaxis.Estructuras.AbstracSintaxis;
import com.cpas.compilador.Sintaxis.Estructuras.comandos.Comandos;


public class Asignaciones extends AbstracSintaxis{

    private final Comandos comando;
    private String tipoFuncActual;
    private String nideFuncActual;

    private boolean regresaValor = false;

    
    public Asignaciones(Comandos comando) {
        this.comando = comando;

        this.six = this.comando.sintaxis;
        this.sem = this.six.semantica;
        this.gen = this.six.generadorPL0;

    }

    public void asigna(String nIde) {

        String tipoVariable = "I";

        this.sem.existeIde(nIde);

        if (this.six.tablaSimb.containsKey(nIde)) {
            tipoVariable = this.six.tablaSimb.get(nIde)[1];
        } 

        this.sem.consumir(new EventosSemanticos.ControlAccesoDimenciones(nIde));

        if (!this.six.getLexe().equals("="))
            this.six.hayError("Error de Sintaxis", "Falta el signo '=' y llego: " + this.six.getLexe());
        avanzarToken();

        this.six.expresionControl.expr();

        this.sem.consumir(new EventosSemanticos.ComprobarTipoAsigancion(nIde, tipoVariable));

        this.gen.insertCodPL0(new String[] { "STO", "0", nIde });
    }

    public void cregresa() {
        this.nideFuncActual = this.six.funcionesControl.getNameFuncActual();
        this.tipoFuncActual = this.six.funcionesControl.getTipoFuncActual();

        boolean btr = false;
        boolean infoTipoRetorno = false;
        if (this.tipoFuncActual.equals("I")) btr = true;
        else infoTipoRetorno = true;

        avanzarToken();
        if (!this.six.getLexe().equals(";")) {
            if (btr) 
                this.six.hayError("Error Semantico", "Funciones sin tipo no pueden regresar un valor.");
            this.six.expresionControl.expr();

            if (infoTipoRetorno) {
                this.sem.consumir(new EventosSemanticos.TipoFuncionRetorno(this.tipoFuncActual, this.nideFuncActual));
                
                this.six.generadorPL0.insertCodPL0(new String[] {"STO", "0", this.nideFuncActual});
            }

        } else {
            if (!btr)
                this.six.hayError("Error Semantico", "La funcion " + this.nideFuncActual + " con tipo '" + indiceATipoInfo(this.tipoFuncActual) + "' necesita regresar un valor.");

        }
        this.regresaValor = true;


        this.six.generadorPL0.insertCodPL0(new String [] { "OPR", "0", "1" });
    }

    public boolean regresoValor() {
        boolean r = this.regresaValor;
        this.regresaValor = false;
        return r;
    }

}
