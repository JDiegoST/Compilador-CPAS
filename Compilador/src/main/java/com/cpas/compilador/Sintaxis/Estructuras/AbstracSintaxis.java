package com.cpas.compilador.Sintaxis.Estructuras;

import com.cpas.compilador.Generacion.GeneradorPL0;
import com.cpas.compilador.Semantico.Semantico;
import com.cpas.compilador.Sintaxis.Sintaxis;
import com.cpas.compilador.core.WorkingSpace;

public abstract class AbstracSintaxis extends WorkingSpace {

    protected Sintaxis six;
    protected Semantico sem;
    protected GeneradorPL0 gen;

    public void avanzarToken() {
        this.six.currentToken = this.six.lexico.passToken();
        //System.out.println(this.six.currentToken);
    }
}
