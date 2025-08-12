package com.cpas.compilador.Sintaxis.Estructuras.comandos.condicionales;

import com.cpas.compilador.Semantico.Eventos.EventosSemanticos;
import com.cpas.compilador.Sintaxis.Estructuras.AbstracSintaxis;
import com.cpas.compilador.Sintaxis.Estructuras.comandos.Comandos;

public class Condicionales extends AbstracSintaxis {

    private final Comandos com;
    private boolean hayCasoDefecto = false;
    private boolean dentroSobre = false;

    private int refUltimoCaso = -1; //Guardo la referencias de la linea de codigo del generador PL0 para cambiar las direcciones
    private int refCasoDefecto = -1; //  del registro en el caso de que se requiera

    private String etiqCasoDefecto = "";
    
    public Condicionales(Comandos com) {
        this.com = com;
        this.six = this.com.sintaxis;
        this.sem = this.six.semantica;
        this.gen = this.six.generadorPL0;
    }

    /*
     * Sentencia if, if-else y if condicional adaptado a CPAS ( si, sino, sino si CONDICIONAL... )
     *  -> La sentancia if es:
     * si CONDICIONAL hacer
     * inicio
     *      INTRUCCIONES
     * fin
     * 
     *  -> La sentancia if-else:
     * si CONDICIONAL hacer
     * inicio
     *      INTRUCCIONES
     * fin sino inicio
     *      INTRUCCIONES
     * fin
     * 
     *  -> La sentencia if anidado: 
     * si CONDICIONAL hacer
     * inicio
     *      INTRUCCIONES
     * fin sino si CONDICIONAL hacer
     * inicio 
     *      INTRUCCIONES
     * fin sino si CONDICIONAL....
     * 
     */

    public void eSi() {
        boolean anidado;

        String etiX;
        String etiY = this.six.genIdEtiqueta();
        do {

            anidado = false;
            
            avanzarToken();
            this.six.expresionControl.expr();

            this.sem.consumir(new EventosSemanticos.ComprobadorTipoCondicionales("Condicional"));
            
            if (!this.six.getLexe().equals("hacer"))
                this.six.hayError("Error Sintaxis", "Se esperaba 'hacer' y llego: " + this.six.getLexe());
            
            etiX = this.six.genIdEtiqueta();

            this.gen.insertCodPL0(new String[] { "JMC", "F", etiX });
            avanzarToken();
            if (this.six.getLexe().equals("inicio")) {
                this.com.block();
                avanzarToken();
            }
            this.gen.insertCodPL0(new String[] { "JMP", "0", etiY });
            this.six.insertarTabSimb(etiX, new String[] { "E", "I", Integer.toString(this.gen.getContPrograma()), "0" });
            if (this.six.getLexe().equals("sino")) {
                avanzarToken();

                if ( this.six.getLexe().equals("si")) {
                    anidado = true;
                } else if (this.six.getLexe().equals("inicio")) {
                    this.com.block();
                    avanzarToken();
                }
            }
        } while(anidado);
        this.six.insertarTabSimb(etiY, new String[] { "E", "I", Integer.toString(this.gen.getContPrograma()), "0" });
    }

    /**
     * Senencia condicional switch adaptado a CPAS.
     *  lA sentencia es: 
     * sobre ( condicional... ) verifica
     * inicio
     *      caso VALORAVERIFICAR hacer
     *      inicio
     *          INTRUCCIONES
     *      fin;
     *      ultimo caso hacer 
     *      inicio
     *          INTRUCCIONES
     *      fin;
     * fin;
     * */ 

    public void eSobre() {
        this.dentroSobre = true;
        String nIdeDeControl;

        String etFin = this.six.genIdEtiqueta();

        avanzarToken();

        if (!this.six.getLexe().equals("(")) 
            this.six.hayError("Error Sintaxis", "Se esperaba '(' y llego: " + this.six.getLexe());
        avanzarToken();
        
        if (!this.six.getToken().equals("Ide") || this.com.esVariable(this.six.getLexe()))
            this.six.hayError("Error Sintaxis", "Se esperaba una variable");
        
        nIdeDeControl = this.six.getLexe();
        this.sem.buscaInsTipo(nIdeDeControl);
        avanzarToken();
        if (!this.six.getLexe().equals(")")) 
            this.six.hayError("Error Sintaxis", "Se esperaba ')' y llego: " + this.six.getLexe());
        avanzarToken();
        if (!this.six.getLexe().equals("verifica")) 
            this.six.hayError("Error Sintaxis", "Se esperaba la palabra reservada 'verifica' y llego: " + this.six.getLexe());
        avanzarToken();
        if (!this.six.getLexe().equals("inicio")) 
            this.six.hayError("Error Sintaxis", "Se esperaba la palabra reservada 'inicio' y llego: " + this.six.getLexe());

        avanzarToken();

        while (this.six.getLexe().equals("caso")) { 

            String etCaso = this.six.genIdEtiqueta();
            if (this.six.getLexe().equals("caso")) eCaso(nIdeDeControl, etCaso);

            this.gen.insertCodPL0(new String[] { "JMP" ,"0",  etFin});


            this.six.insertarTabSimb(etCaso, new String[] { "E", "I", Integer.toString(this.gen.getContPrograma()), "0" });  
        }

        if (this.hayCasoDefecto) {
            if (this.refUltimoCaso != -1) {
                this.gen.modificarRegistro(this.refUltimoCaso, new String[] { "JMC", "F", this.etiqCasoDefecto });
            } else {
                this.gen.modificarRegistro(this.refCasoDefecto, new String[] { "JMP", "0", this.etiqCasoDefecto });
            }
        }

        
        if (!this.six.getLexe().equals("fin")) 
            this.six.hayError("Error Sintaxis", "Se esperaba la palabra reservada 'fin' y llego: " + this.six.getLexe());
        
        this.six.insertarTabSimb(etFin, new String[] { "E", "I", Integer.toString(this.gen.getContPrograma()), "0" });
        
        avanzarToken();

        this.hayCasoDefecto = false;
        this.dentroSobre = false;
    }

    private void eCaso(String nIdeControl, String etiquetaXCaso) {
        avanzarToken();

        if (!this.six.getLexe().equals("defecto")) {

            this.gen.insertCodPL0(new String[] {"LOD", nIdeControl, "0"});

            this.six.expresionControl.expr();
            
            this.sem.consumir(new EventosSemanticos.ComprobarComparadorSiga(nIdeControl, this.six.tablaSimb.get(nIdeControl)[1]));

            this.gen.insertCodPL0(new String[] { "OPR", "0", "14" });

            //Problema con el caso por defecto. AL poder estar en cualquier posicion no queda claro como llamarlo al final
            this.refUltimoCaso = this.gen.getContPrograma();
            this.gen.insertCodPL0(new String[] { "JMC" ,"F",  etiquetaXCaso});
        } else {
            if (this.hayCasoDefecto) 
                six.hayError("Error Sintaxis", "Solo puede haber un caso por defecto en la estructura sobre().");
            else {
                this.hayCasoDefecto = true;
                this.etiqCasoDefecto = this.six.genIdEtiqueta();
                this.gen.insertCodPL0(new String[] { "JMP", "0", etiquetaXCaso });
                this.refCasoDefecto = this.gen.getContPrograma();
                this.six.insertarTabSimb(this.etiqCasoDefecto, new String[] { "E", "I", Integer.toString(this.gen.getContPrograma()), "0" });
            }
            avanzarToken();
        }

        if (!this.six.getLexe().equals("hacer"))
            this.six.hayError("Error Sintaxis", "Se esperaba la palabra reservada 'hacer' y llego: " + this.six.getLexe());
        avanzarToken();
        this.six.comandoControl.block();
        avanzarToken();
    }

    public boolean getDentroSobre() {
        return this.dentroSobre;
    }
}
