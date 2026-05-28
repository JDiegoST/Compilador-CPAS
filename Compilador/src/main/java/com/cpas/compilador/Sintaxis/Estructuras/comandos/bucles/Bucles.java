package com.cpas.compilador.Sintaxis.Estructuras.comandos.bucles;

import java.util.Stack;

import com.cpas.compilador.Semantico.Eventos.EventosSemanticos;
import com.cpas.compilador.Sintaxis.Estructuras.AbstracSintaxis;
import com.cpas.compilador.Sintaxis.Estructuras.comandos.Comandos;

public class Bucles extends AbstracSintaxis {

    private final Comandos com;

    private boolean dentroBucle;
    private final Stack<String> stackInterrumpeAnidado = new Stack<>();
    private String direccionContinua;
    private int contadorBucleAnidado;
    
    public Bucles(Comandos com) {
        this.com = com;
        this.six = this.com.sintaxis;
        this.sem = this.six.semantica;
        this.gen = this.six.generadorPL0;
    }

    public void eDesde() {
        this.dentroBucle = true;
        this.contadorBucleAnidado++;

        String etiX = this.six.genIdEtiqueta();
        this.stackInterrumpeAnidado.add(etiX);

        String etiCont = this.six.genIdEtiqueta();

        avanzarToken();
        String ideVar = this.six.getLexe();
        if (this.six.getToken().equals("Ide")) {
            String nIde = this.six.getLexe();
            this.sem.existeIde(nIde);
            avanzarToken();
            this.com.controlAsignador.asigna(nIde);
        }
        if (!this.six.getLexe().equals("hasta"))
            this.six.hayError("Error Sintaxis", "Se esperaba 'hasta' y llego: " + this.six.getLexe());
        int dirInicioBucle = this.gen.getContPrograma();
        avanzarToken();
        this.six.expresionControl.expr();

        this.gen.insertCodPL0(new String[] { "JMC", "F", etiX });
        this.gen.insertCodPL0(new String[] { "JMP", "0", etiCont });

        this.sem.consumir(new EventosSemanticos.ComprobadorTipoCondicionales("Bucle"));


        int regresoAIncr = this.gen.getContPrograma();
        this.direccionContinua = Integer.toString(regresoAIncr);

        this.gen.insertCodPL0(new String[] { "LOD", ideVar, "0" });
        if (this.six.getLexe().equals("incr")) {
            avanzarToken();
            this.six.expresionControl.expr();
        } else {
            this.gen.insertCodPL0(new String[] { "LIT", "1", "0" });
        }
        this.gen.insertCodPL0(new String[] { "OPR", "0", "2" });
        this.gen.insertCodPL0(new String[] { "STO", "0", ideVar });
        this.gen.insertCodPL0(new String[] { "JMP", "0", Integer.toString(dirInicioBucle) });


        int dirCont = this.gen.getContPrograma();
        if (this.six.getLexe().equals("inicio")) {
            this.com.block();
            avanzarToken();
        }

        this.gen.insertCodPL0(new String[] { "JMP", "0", Integer.toString(regresoAIncr) });
        int dirFinBucle = this.gen.getContPrograma();

        this.six.insertarTabSimb(etiX, new String[] { "E", "I", Integer.toString(dirFinBucle), "0" });
        this.six.insertarTabSimb(etiCont, new String[] { "E", "I", Integer.toString(dirCont), "0" });


        this.contadorBucleAnidado--;
        if (this.contadorBucleAnidado == 0)
            this.dentroBucle = !this.dentroBucle;
    }

    public void eRepite() {
        this.dentroBucle = true;
        this.contadorBucleAnidado++;


        int dirInicioBucle = this.gen.getContPrograma();
        this.direccionContinua = Integer.toString(dirInicioBucle);
        avanzarToken();

        if (this.six.getLexe().equals("inicio")) {
            this.com.block();
            avanzarToken();
        }
        
        if (!this.six.getLexe().equals("hasta"))
            this.six.hayError("Error de Sintaxis", "Se esperaba 'hasta' y llego: " + this.six.getLexe());
        avanzarToken();
        if (!this.six.getLexe().equals("que"))
            this.six.hayError("Error de Sintaxis", "Se esperaba 'que' y llego: " + this.six.getLexe());
        avanzarToken();
        this.six.expresionControl.expr();
        this.sem.consumir(new EventosSemanticos.ComprobadorTipoCondicionales("Bucle"));
        this.gen.insertCodPL0(new String[] { "JMC", "F", Integer.toString(dirInicioBucle) });
        
        this.stackInterrumpeAnidado.add(Integer.toString(this.gen.getContPrograma()));

        this.contadorBucleAnidado--;
        if (this.contadorBucleAnidado == 0)
            this.dentroBucle = !this.dentroBucle;
    }

    public void eMientras() {
        this.dentroBucle = true;
        this.contadorBucleAnidado++;

        String etiX = this.six.genIdEtiqueta();
        this.stackInterrumpeAnidado.add(etiX);
    
        avanzarToken();
        int dirInicioBucle = this.gen.getContPrograma();
        this.direccionContinua = Integer.toString(dirInicioBucle);
        this.six.expresionControl.expr();

        this.gen.insertCodPL0(new String[] { "JMC", "F", etiX });
        this.sem.consumir(new EventosSemanticos.ComprobadorTipoCondicionales("Bucle"));

        if (this.six.getLexe().equals("inicio")) {
            this.com.block();   
            avanzarToken();
        }

        this.gen.insertCodPL0(new String[] { "JMP", "0", Integer.toString(dirInicioBucle) });
        int dirFinBucle = this.gen.getContPrograma();

        this.six.insertarTabSimb(etiX, new String[] { "E", "I", Integer.toString(dirFinBucle), "0" });

        this.contadorBucleAnidado--;
        if (this.contadorBucleAnidado == 0)
            this.dentroBucle = !this.dentroBucle;
    }

    public void interrumpe() {
        if (!this.dentroBucle) {
            this.six.hayError("Error Semantico", "INTERRUPE no puede ser usado fuera de un bucle.");
        }
        avanzarToken();

        this.gen.insertCodPL0(new String[] { "JMP", "0", this.stackInterrumpeAnidado.pop() });
    }

    public void continua() {
        if (!this.dentroBucle) {
            this.six.hayError("Error Semantico", "CONTINUA no puede ser usado fuera de un bucle.");
        }

        avanzarToken();

        this.gen.insertCodPL0(new String[] { "JMP", "0", this.direccionContinua });

        this.direccionContinua = "";
    }
}

