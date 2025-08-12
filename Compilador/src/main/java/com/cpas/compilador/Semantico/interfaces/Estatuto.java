package com.cpas.compilador.Semantico.interfaces;

import com.cpas.compilador.Semantico.Eventos.EventosSemanticos;

@FunctionalInterface
public interface Estatuto {
    void consumir(EventosSemanticos evt);
}
