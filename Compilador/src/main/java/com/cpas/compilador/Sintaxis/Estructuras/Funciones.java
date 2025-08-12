package com.cpas.compilador.Sintaxis.Estructuras;

import java.util.Stack;

import com.cpas.compilador.Sintaxis.Sintaxis;

public class Funciones extends AbstracSintaxis {

    private String nIdeFunc;
    private String tipoFuncGlobal = "I";
    
    private final Stack<String> pilaParams = new Stack<>();

    private boolean funcionConParams;

    public Funciones(Sintaxis sintaxis) {
        this.six = sintaxis;
        this.sem = this.six.semantica;

        this.gen = this.six.generadorPL0;
    }

    public void funciones() {
        boolean esPrincipal = false;

        avanzarToken();
        if (!this.six.getToken().equals("Ide") && !this.six.getLexe().equals("principal")) {
            this.tipoFuncGlobal = this.six.tipo();
            avanzarToken();
        }
        String nomF = this.six.getLexe();
        this.nIdeFunc = nomF;
        if (this.six.getToken().equals("Ide") || this.six.getLexe().equals("principal")) {
            if (this.six.getLexe().equals("principal")) {
                this.six.insertarTabSimb("_Principal",
                        new String[] { "F", "I", Integer.toString(this.gen.getContPrograma()), "0" });
                esPrincipal = true;
            }

            avanzarToken();
            if (!this.six.getLexe().equals(Character.toString('(')))
                six.hayError("Error de Sintaxis", "Se esperaba ( y llego: " + this.six.getLexe());
            avanzarToken();
            String inicioFunc = Integer.toString(this.gen.getContPrograma());
            if (!this.six.getLexe().equals(Character.toString(')')))
                params();

            if (!esPrincipal)
                six.insertarTabSimb(this.nIdeFunc, new String[] { "F", this.tipoFuncGlobal, inicioFunc, "0" });

            if (!this.six.getLexe().equals(Character.toString(')')))
                six.hayError("Error de Sintaxis", "Se esperaba ) y llego: " + this.six.getLexe());
            avanzarToken();
            this.six.comandoControl.block();

            if (!this.tipoFuncGlobal.equals("I")) {
                if (!this.six.comandoControl.controlAsignador.regresoValor()) {
                    six.hayError("Error Semantico", "La funcion '" + nomF + "' requiere un valor de regreso.");
                }
                this.tipoFuncGlobal = "I";
            }
            avanzarToken();

            if (nomF.equals("principal"))
                this.gen.insertCodPL0(new String[] { "OPR", "0", "0" });
            else
                this.gen.insertCodPL0(new String[] { "OPR", "0", "1" });
        }

        if (this.funcionConParams != false) this.funcionConParams = false;
    }

    public void params() {
        String deli1, deli2;
        String nFuncFinal = this.nIdeFunc + "$";
        do {

            String tipoParam = tipoAIndece(this.six.getLexe());
            six.tipo();
            avanzarToken();

            do {
                if (!this.six.getToken().equals("Ide"))
                    this.six.hayError("Error Sintaxis", "Se esperaba un identificador y llego: " + this.six.getLexe());

                agregarPilaParams(this.six.getLexe() + "," + tipoParam);
                avanzarToken();
                deli2 = this.six.getLexe();

                if (this.six.getLexe().equals(",")) avanzarToken(); 

                nFuncFinal += tipoParam + "$";
            } while (deli2.equals(","));
            deli1 = this.six.getLexe();
            if (this.six.getLexe().equals(";")) {
                avanzarToken();
            }
        } while (deli1.equals(";"));

        int contParam = 0;
        String[] params = new String[this.pilaParams.size()];

        while (!this.pilaParams.empty()) {
            String nameYTipo[] = this.pilaParams.pop().split(",");
            String nameParamFinal = nameYTipo[0] + "$" + nFuncFinal;
            six.insertarTabSimb(nameParamFinal, new String[] {"P", nameYTipo[1], "0", "0"});
            this.gen.insertCodPL0(new String[] { "STO", "0", nameParamFinal });
            params[contParam] = nameParamFinal;
            contParam++;
        }


        this.six.insertarFuncConParams(this.nIdeFunc, getContParamsTotales(nFuncFinal, params));


        this.nIdeFunc = nFuncFinal;
        this.funcionConParams = true;
    }

    private String[] getContParamsTotales(String nFuncConParams, String[] params) {
        String[] contParamsTotales = new String[1 + params.length];
        contParamsTotales[0] = nFuncConParams;
        System.arraycopy(params, 0, contParamsTotales, 1, params.length);

        return contParamsTotales;
    }

    private void agregarPilaParams(String valor) {
        this.pilaParams.add(valor);
    }

    public String getTipoFuncActual() {
        return this.tipoFuncGlobal;
    }

    public String getNameFuncActual() {
        return this.nIdeFunc;
    }

    public boolean isParamsInFunc() {
        return this.funcionConParams;
    }

}
