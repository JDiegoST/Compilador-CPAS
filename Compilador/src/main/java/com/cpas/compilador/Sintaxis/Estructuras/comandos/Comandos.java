package com.cpas.compilador.Sintaxis.Estructuras.comandos;


import com.cpas.compilador.Sintaxis.Estructuras.AbstracSintaxis;
import com.cpas.compilador.Sintaxis.Estructuras.comandos.asignadores.Asignaciones;
import com.cpas.compilador.Sintaxis.Estructuras.comandos.bucles.Bucles;
import com.cpas.compilador.Sintaxis.Estructuras.comandos.condicionales.Condicionales;
import com.cpas.compilador.Sintaxis.Estructuras.comandos.llamada_a_funcion.LlamadaFunc;
import com.cpas.compilador.Sintaxis.Estructuras.comandos.metodos.Metodos;
import com.cpas.compilador.Sintaxis.Sintaxis;

public class Comandos extends AbstracSintaxis {

    public final Sintaxis sintaxis;
    public final Asignaciones controlAsignador;
    public final Bucles controlBucles;
    public final Condicionales controlCondicionales;
    public final LlamadaFunc controlLlamadaFunc;
    public final Metodos controlMetodos;
    

    public Comandos(Sintaxis sintaxis) {
        this.sintaxis = sintaxis;

        this.six = this.sintaxis;
        this.sem = this.six.semantica;
        this.gen = this.six.generadorPL0;

        this.controlAsignador = new Asignaciones(this);
        this.controlBucles = new Bucles(this);
        this.controlCondicionales = new Condicionales(this);
        this.controlLlamadaFunc = new LlamadaFunc(this);
        this.controlMetodos = new Metodos(this);
    }

    public void block() {
        
        if (!this.six.getLexe().equals("inicio"))
            this.six.hayError("Error de Sintaxis", "Se esperaba 'inicio' y llego: " + this.six.getLexe());
        avanzarToken();
        if (!this.six.getLexe().equals("fin"))
            estatutos();
        if (!this.six.getLexe().equals("fin"))
            this.six.hayError("Error de Sintaxis", "Se esperaba 'fin' y llego: " + this.six.getLexe());
    }

    public void estatutos() {
        boolean paso;
        do {
            paso = true;
            if (this.six.getLexe().equals(";")) {
                avanzarToken();
            }
            if (!this.six.getLexe().equals(";") && !this.six.getLexe().equals("fin")) {
                comando();
                paso = false;
            }
        } while (this.six.getLexe().equals(";"));

        if (!paso && this.controlCondicionales.getDentroSobre()) {
            int[] cordAnt = this.six.lexico.getPosicionAnt();
            this.six.hayError("Error Sintaxis", "Falta ';' de cierre de estatuto", cordAnt);
        }
    }

    public void comando() {
        boolean nImp = false;
        if (this.six.getLexe().equals("imprime") || this.six.getLexe().equals("imprimeln")) {
            if (this.six.getLexe().equals("imprimeln"))
                nImp = true;
            avanzarToken();
            this.controlMetodos.imprimir(nImp);
        } else if (this.six.getToken().equals("Ide")) {
            String nIde = this.six.getLexe();
            
            nIde = this.sem.comprobarParamFuncVar(nIde);

            this.sem.existeIde(nIde);
            avanzarToken();
            if (this.six.getLexe().equals("(")) {
                this.controlLlamadaFunc.lfunc(nIde);
                this.gen.insertCodPL0(new String[] { "LOD", nIde, "0" });
            } else {
                this.controlAsignador.asigna(nIde);
            }
        } else if (this.six.getToken().equals("Res")) {
            switch (this.six.getLexe()) {
                case "si" -> this.controlCondicionales.eSi();
                case "desde" -> this.controlBucles.eDesde();
                case "repite" -> this.controlBucles.eRepite();
                case "mientras" -> this.controlBucles.eMientras();
                case "lmp" -> {
                    this.gen.insertCodPL0(new String[] { "OPR", "0", "18" });
                    avanzarToken();
                    if (this.six.getLexe().equals("("))
                        this.six.hayError("Error Sintaxis", "La palabra reservada 'lmp' no necesita de '()' para ser ejecutado.");
                    else if (this.six.getLexe().equals("["))
                        this.six.hayError("Error Sintaxis", "El atributo 'lmp', no necesita de una dimencion para ser ejecutado.");
                }
                case "lee" -> this.controlMetodos.eLee();
                case "interrumpe" -> this.controlBucles.interrumpe();
                case "continua" -> this.controlBucles.continua();
                case "regresa" -> this.controlAsignador.cregresa();
                case "sobre" -> this.controlCondicionales.eSobre();
                default -> {
                    this.six.hayError("Error Sintaxis", "Token no reconocido: '" + this.six.getLexe() + "'");
                }
            }
        }
    }

    public boolean esVariable(String nIde) {
        if (this.sintaxis.tablaSimb.containsKey(nIde)) {
            String[] contenido = this.sintaxis.tablaSimb.get(nIde);
            if (contenido[1].equals("V")) return true;

        }
        return false;
    }
}
