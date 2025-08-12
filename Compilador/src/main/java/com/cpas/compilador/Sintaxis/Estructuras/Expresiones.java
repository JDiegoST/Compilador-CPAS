package com.cpas.compilador.Sintaxis.Estructuras;

import com.cpas.compilador.Semantico.Eventos.EventosSemanticos;
import com.cpas.compilador.Sintaxis.Estructuras.comandos.Comandos;
import com.cpas.compilador.Sintaxis.Sintaxis;

public class Expresiones extends AbstracSintaxis{

    private final Comandos com;

    public Expresiones(Sintaxis sintaxis) {
        this.six = sintaxis;
        this.sem = this.six.semantica;
        this.gen = this.six.generadorPL0;

        this.com = this.six.comandoControl;
    }

    public void expr() {
        
        String deli;
        boolean bdr = false;
        do {
            opy();
            if (bdr) {
                this.sem.consumir(new EventosSemanticos.ComprobadorTipoOperaciones());
                this.gen.insertCodPL0(new String[] {"OPR", "0", "16" });
                bdr = false;
            }

            deli = this.six.getLexe();
            if (deli.equals("o")) {
                bdr = true;
                this.sem.agregarApilaTipos(this.six.getLexe());
                avanzarToken();
            }
        } while (deli.equals("o"));
    }

    private void opy() {
        String deli;
        boolean bdr = false;
        do {
            opno();

            if (bdr) {
                this.sem.consumir(new EventosSemanticos.ComprobadorTipoOperaciones());

                this.gen.insertCodPL0(new String[] {"OPR", "0", "15"});
                bdr = false;
            }

            deli = this.six.getLexe();
            if (this.six.getLexe().equals("y")) {
                bdr = true;
                this.sem.agregarApilaTipos(this.six.getLexe());
                avanzarToken();
            }
        } while (deli.equals("y"));
    }

    private void opno() {
        String op = "";
        if (this.six.getLexe().equals("no")) {
            op = this.six.getLexe();
            this.sem.agregarApilaTipos("no");
            avanzarToken();
        }
        oprel();
        if (op.equals("no")) {
            
            this.sem.consumir(new EventosSemanticos.ComprobadorTipoOperaciones("no"));

            this.gen.insertCodPL0(new String[] {"OPR", "0", "17"});
        }
    }

    private void oprel() {
        String deli;
        String op = "";
        boolean btr = false;
        do {
            suma();

            if (btr) {
                String dir2 = "9";

                switch (op) {
                    case ">" -> dir2 = "10";
                    case "<=" -> dir2 = "11";
                    case ">=" -> dir2 = "12";
                    case "<>" -> dir2 = "13";
                    case "==" -> dir2 = "14";
                }

                this.sem.consumir(new EventosSemanticos.ComprobadorTipoOperaciones());

                this.gen.insertCodPL0(new String[] {"OPR", "0", dir2});
            }

            deli = this.six.getToken();
            if (this.six.getToken().equals("CpL")) {
                op = this.six.getLexe();
                btr = true;
                this.sem.agregarApilaTipos(op);
                avanzarToken();
            }
        } while (deli.equals("CpL"));
    }

    private void suma() {
        String deli;
        String op = "";
        do {
            multi();

            if (op.equals("+") || op.equals("-")) {
                String dir2 = "2";
                this.sem.consumir(new EventosSemanticos.ComprobadorTipoOperaciones());

                if (op.equals("-"))
                    dir2 = "3";
                this.gen.insertCodPL0(new String[] {"OPR", "0", dir2});
                op = "";
            }

            deli = this.six.getLexe();
            if (this.six.getLexe().equals("+") || this.six.getLexe().equals("-")) {
                op = this.six.getLexe();
                this.sem.agregarApilaTipos(op);
                avanzarToken();
            }
        } while (deli.equals("+") || deli.equals("-"));
    }

    private void multi() {
        String[] opAMDM = { "*", "/", "%" };
        String deli;
        String op = "";
        do {
            expo();

            if (estaDentro(opAMDM, op)) {
                String dir2 = "4";
                this.sem.consumir(new EventosSemanticos.ComprobadorTipoOperaciones());

                if (op.equals("/"))
                    dir2 = "5";
                else if (op.equals("%"))
                    dir2 = "6";

                this.gen.insertCodPL0(new String[] {"OPR", "0", dir2});
                op = "";
            }

            deli = this.six.getLexe();
            if (estaDentro(opAMDM, this.six.getLexe())) {
                op = this.six.getLexe();
                this.sem.agregarApilaTipos(op);
                avanzarToken();
            }
        } while (estaDentro(opAMDM, deli));
    }

    private void expo() {
        String deli;
        String op = "";
        do {
            signo();

            if (op.equals("^")) {
                this.sem.consumir(new EventosSemanticos.ComprobadorTipoOperaciones());
                this.gen.insertCodPL0(new String[] {"OPR", "0", "7"});
                op = "";
            }

            deli = this.six.getLexe();
            if (this.six.getLexe().equals("^")) {
                op = this.six.getLexe();
                this.sem.agregarApilaTipos(op);
                avanzarToken();
            }
        } while (deli.equals("^"));
    }

    private void signo() {
        String op = "";
        if (this.six.getLexe().equals("-")) {
            op = this.six.getLexe();
            this.sem.agregarApilaTipos(op);
            avanzarToken();
        }
        termino();
        if (op.equals("-")) {
            this.sem.consumir(new EventosSemanticos.ComprobadorTipoOperaciones("-"));
            this.gen.insertCodPL0(new String[] {"OPR", "0", "8"});
        }
    }

    private void termino() {
        String[] tipVarble = { "Ent", "Dec", "CtA", "CtL" };
        if (this.six.getLexe().equals("(")) {
            avanzarToken();
            expr();
            if (!this.six.getLexe().equals(")"))
                this.six.hayError("Error de Sintaxis",
                        "Se esperaba ')', llego: " + this.six.getLexe());
            avanzarToken();
        } else if (this.six.getToken().equals("Ide")) {
            String nIde = this.six.getLexe();
            nIde = sem.comprobarParamFuncVar(nIde);

            this.sem.buscaInsTipo(nIde);

            avanzarToken();

            this.sem.consumir(new EventosSemanticos.ControlAccesoDimenciones(nIde));

            if (this.six.getLexe().equals("(")) {
                this.com.controlLlamadaFunc.lfunc(nIde);
            }
            this.gen.insertCodPL0(new String[] {"LOD", nIde, "0"});

        } else if (estaDentro(tipVarble, this.six.getToken())) {
            String cte = this.six.getLexe();
            this.sem.agregarApilaTipos(this.six.tokenAIndiceTipo());
            switch (cte) {
                case "verdadero" -> cte = "V";
                case "falso" -> cte = "F";
            }
            this.gen.insertCodPL0(new String[] {"LIT", cte.replaceAll("\\s+", " "), "0"});
            avanzarToken();
        } else {
            this.six.hayError("Error de Sintaxis", "Se necesita una expresion.", this.six.lexico.getPosicionAnt());
        }
    }
}