package com.cpas.compilador.Models;


public class Token {

    private String toke = null;
    private String lexe = null;
    private final int[] pos = new int[2];
    
    public Token(String toke, String lexe, int renglon, int colum) {
        this.toke = toke;
        this.lexe = lexe;
        this.pos[0] = renglon;
        this.pos[1] = colum;
    }


    public String getToken() {
        return this.toke;
    }

    public String getLexema() {
        return this.lexe;
    }

    public int[] getPosicion() {
        return this.pos;
    }

    @Override
    public String toString() {
        return "Token: " + this.toke + ", lexema= (" + this.lexe + "). " + "[" + this.pos[0] + "]" + "[ " + this.pos[1] + "]";
    }
}
