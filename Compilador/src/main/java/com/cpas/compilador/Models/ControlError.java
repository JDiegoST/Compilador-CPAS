package com.cpas.compilador.Models;

public class ControlError {

    private static ControlError instance;

    private String typeError;
    private String messageError;
    private final int[] posError;

    private ControlError() {
        this.typeError = "";
        this.messageError = "";
        this.posError = new int[2];
        this.posError[0] = -1;
        this.posError[1] = -1;
    }

    public static ControlError getInstance() {
        if (instance == null) {
            instance = new ControlError();
        }

        return instance;
    }

    public void repotarError (String tipoError, String messageError, int lineError, int columnError) {
        this.typeError = tipoError;
        this.messageError = messageError;
        this.posError[0] = lineError;
        this.posError[1] = columnError;
        imprimirError();
    }

    private void imprimirError() {
        System.err.printf("[%d][%d] %s, %s%n", this.posError[0], this.posError[1], this.typeError, this.messageError);
    }
    
}
