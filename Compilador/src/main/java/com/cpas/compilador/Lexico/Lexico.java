package com.cpas.compilador.Lexico;

import com.cpas.compilador.Models.ControlError;
import com.cpas.compilador.Models.Token;
import com.cpas.compilador.core.WorkingSpace;

public class Lexico extends WorkingSpace {
    private int idx = 0;
    private final String entrada;
    private final int ACP = 99;
    private final int ERR = -1;
    private boolean errA = false;
    private int linePrev = -1;
    private int columPrev = -1;
    private int countLine = 1;
    private int countColum = 1;

    private Token currentToken;

    public Lexico(String archivo) {
        this.entrada = archivo;
    }

    private final int[][] matrizEstados = {
        /*
                   tab  +,-
                   sp   *     
        _,let dig  nl   /     %    .     "   del,  #    =      <    >  sig          */
        {1,   2,   0,   5,     6, ERR,   9,   11,  12,  14,   17,  16,  18}, // 0
        {1,   1,   ACP, ACP, ACP, ACP, ACP,  ACP, ACP, ACP,  ACP, ACP, ACP}, // 1
        {ACP, 2,   ACP, ACP, ACP, 3  , ACP,  ACP, ACP, ACP,  ACP, ACP, ACP}, // 2
        {ERR, 4,   ERR, ERR, ERR, ERR, ERR,  ERR, ERR, ERR,  ERR, ERR, ERR}, // 3
        {ACP, 4,   ACP, ACP, ACP, ACP, ACP,  ACP, ACP, ACP,  ACP, ACP, ACP}, // 4
        {ACP, ACP, ACP, ACP, ACP, ACP, ACP,  ACP, ACP, ACP,  ACP, ACP, ACP}, // 5
        {ACP, ACP, ACP, ACP,   7, ACP, ACP,  ACP, ACP, ACP,  ACP, ACP, ACP}, // 6
        {  7,   7, 7  ,   7,   7,   7,   7,    7,   7,   7,    7,   7,   7}, // 7
        {ACP, ACP, ACP, ACP, ACP, ACP, ACP,  ACP, ACP, ACP,  ACP, ACP, ACP}, // 8
        {9,     9,   9,   9,   9,   9,  10,    9,   9,   9,    9,   9,   9}, // 9
        {ACP, ACP, ACP, ACP, ACP, ACP, ACP,  ACP, ACP, ACP,  ACP, ACP, ACP}, // 10
        {ACP, ACP, ACP, ACP, ACP, ACP, ACP,  ACP, ACP, ACP,  ACP, ACP, ACP}, // 11
        {12 ,  12,  12,  12,  12,  12,  12,   12,  13,  12,   12,  12,  12}, // 12
        {ACP, ACP, ACP, ACP, ACP, ACP, ACP,  ACP, ACP, ACP,  ACP, ACP, ACP}, // 13
        {ACP, ACP, ACP, ACP, ACP, ACP, ACP,  ACP, ACP,  15,  ACP, ACP, ACP}, // 14
        {ACP, ACP, ACP, ACP, ACP, ACP, ACP,  ACP, ACP, ACP,  ACP, ACP, ACP}, // 15
        {ACP, ACP, ACP, ACP, ACP, ACP, ACP,  ACP, ACP,  15,  ACP, ACP, ACP}, // 16
        {ACP, ACP, ACP, ACP, ACP, ACP, ACP,  ACP, ACP,  15,  ACP,  15, ACP}, // 17
        {ACP, ACP, ACP, ACP, ACP, ACP, ACP,  ACP, ACP, ACP,  ACP, ACP, ACP}, // 18
    };

    private final String[] opLog = { "y", "o", "no" };
    private final String[] cteLog = { "verdadero", "falso" };

    private final String[] palRes = {
            "interrumpe", "si", "sino", "funcion", "entera", "decimal", "logica", "alfabetica",
            "constante", "hasta", "hacer", "incr", "inicio", "fin", "continua", "desde",
            "regresa", "variable", "que", "mientras", "lmp", "imprime", "lee", "imprimeln",
            "principal", "repite", "sobre", "defecto", "caso",
    };

    public void hayError(String tipoError, String descripError) {
        ControlError.getInstance().repotarError(tipoError, descripError, countLine, countColum);
        this.errA = true;
    }

    private int colCar(char c) {
        if (c == '_' || Character.isLetter(c))
            return 0;
        if (Character.isDigit(c))
            return 1;
        String saltotabEsp[] = { "\n", " ", "\t" };
        if (estaDentro(saltotabEsp, Character.toString(c)))
            return 2;
        String opeAritmeticos[] = { "+", "-", "*", "/", "^" };
        if (estaDentro(opeAritmeticos, Character.toString(c)))
            return 3;
        if (c == '%')
            return 4;
        if (c == '.')
            return 5;
        if (c == '"')
            return 6;
        String delimitadores[] = { "[", "]", "{", "}", ",", ";", "(", ")", ":" };
        if (estaDentro(delimitadores, Character.toString(c)))
            return 7;
        if (c == '#')
            return 8;
        if (c == '=')
            return 9;
        if (c == '<')
            return 10;
        if (c == '>')
            return 11;
        String signos[] = { "!", "`", "@", "^", "$", "&", "|", "*", ".", "\\" };
        if (estaDentro(signos, Character.toString(c)))
            return 12;
        return ERR;
    }

    public void lexical() {
        String tok = "";
        String lex = "";
        int estado = 0;
        int col;
        int estadoAnterior = 0;

        while (this.idx < entrada.length() && estado != this.ERR && estado != this.ACP) {
            char x = entrada.charAt(this.idx);
            if (x == '\n' && (estado == 0 || estado == 7 || estado == 9 || estado == 12)) {
                this.countLine++;
                this.countColum = 1;
            } else if (x == '\t' && estado == 0) {
                this.countColum += 3;
            } else {
                this.countColum++;
            }
            this.idx++;
            col = colCar(x);
            if (col >= 0 && col <= 12) {
                if (estado == 7) {
                    lex = "";
                    if (x == '\n')
                        estado = 8;
                } else {
                    estado = this.matrizEstados[estado][col];
                }
                if (estado != this.ERR && estado != this.ACP) {
                    estadoAnterior = estado;
                    String escape[] = { " ", "\t", "\n" };
                    if (estado == 9 || (estado != 7 && estado != 9 && estado != 12
                            && (!estaDentro(escape, Character.toString(x))))) {
                        lex += x;
                    }
                }
            }
        }

        // Clasificacion de Tokens, Lexemas
        if (estado == this.ACP || estado == this.ERR) {
            this.idx--;
            this.countColum--;
        } else {
            estadoAnterior = estado;
        }
        switch (estadoAnterior) {
            case 1 -> {
                tok = "Ide";
                if (estaDentro(this.cteLog, lex))
                    tok = "CtL";
                if (estaDentro(this.opLog, lex))
                    tok = "OpL";
                if (estaDentro(this.palRes, lex))
                    tok = "Res";
            }
            case 3 -> {
                tok = "CtD";
                hayError("Error Lexico", "Constante DECIMAL sin cerrar: " + lex);
            }
            case 2 -> tok = "Ent";
            case 4 -> tok = "Dec";
            case 5, 6 -> tok = "OpA";
            case 8 -> tok = "Com";
            case 9 -> {
                tok = "CtA";
                hayError("Error Lexico", "Constante ALFABETICA sin Cerrar: Falta \"");
            }
            case 10 -> tok = "CtA";
            case 11 -> tok = "Del";
            case 12 -> {
                this.countLine--;
                tok = "CtM";
                hayError("Error Lexico", "Comentario MULTILINEA sin cerrar: Falta # de cierre");
            }
            case 13 -> tok = "CtM";
            case 14 -> tok = "Asi";
            case 15, 16, 17 -> tok = "CpL";
            case 18 -> tok = "Sig";
        }

        this.currentToken = new Token(tok, lex, this.countLine, this.countColum);
    }

    private void lexico() {
        this.linePrev = this.countLine;
        this.columPrev = this.countColum;

        do {
            lexical();
        } while (this.currentToken.getToken().equals("Com") || this.currentToken.getToken().equals("CtM"));
    }

    public Token getCurrentLexico() {
        if (this.currentToken == null)
            lexico();
        
        return this.currentToken;
    }

    public Token passToken() {
        lexico();

        return this.currentToken;
    }

    public int[] getPosicionAnt() {
        if ( this.linePrev == -1 ) {
            this.linePrev = this.countLine;
            this.columPrev = this.countColum;
        }
        
        return new int[] { this.linePrev, this.columPrev };
    }

    public boolean getErrA() {
        return this.errA;
    }

    public void huboError() {
        this.errA = true;
    }
}
