package com.cpas.compilador.Semantico;

import java.util.Map;
import java.util.Stack;

import com.cpas.compilador.Semantico.Eventos.EventosSemanticos;
import com.cpas.compilador.Semantico.interfaces.Estatuto;
import com.cpas.compilador.Sintaxis.Sintaxis;
import com.cpas.compilador.core.WorkingSpace;

public class Semantico extends WorkingSpace implements Estatuto{
    
    public final Sintaxis sintaxis;

    /*
     * Esta pila permite guardar el tipo de cada variable o funcion de retorno que el compilador vaya realizando.
     *  EJEMPLO: [L, ==, L] => puede pertenecer a esta sintaxis de CPAS: si (falso == falso)
     *    Pila vacia = [],
     *    toma el primer falso, verifica el tipo (logico) y lo agrega a pila. >> [L],
     *    luego toma el == y lo reconoce como un caracter reservado para hacer una operacion de comparacion. >> [L, ==]
     *    y finalmente toma el siguiente valor falso (logico) y lo agrega a la pila. >> [L, ==, L].
     * 
     *  Esto permitira verificar que la operacion realizada sea compatibles entre los tipo: 
     *      toma L, toma == y toma L dejando: "L==L", este valor lo toma para ser la key para ver si existe en el mapa de tipos.
     *      Si existe, toma el contenido que se encuentra en el espacio de esa key, si no, agrega I (Indefinido).
     */
    private final Stack<String> pilaTipos = new Stack<>();

    // Este diccionario contiene las operaciones entre los tipos que son permitidos.
    private final Map<String, String> mapTipos = Map.ofEntries(
        Map.entry("E=E", ""), Map.entry("A=A", ""), Map.entry("D=D", ""), Map.entry("D=E", ""), Map.entry("L=L", ""),
        Map.entry("A+A", "A"), Map.entry("E+E", "E"), Map.entry("E+D", "D"), Map.entry("D+E", "D"), Map.entry("D+D", "D"),
        Map.entry("E-E", "E"), Map.entry("E-D", "D"), Map.entry("D-E", "D"), Map.entry("D-D", "D"),
        Map.entry("E*E", "E"), Map.entry("E*D", "D"), Map.entry("D*E", "D"), Map.entry("D*D", "D"),
        Map.entry("E/E", "D"), Map.entry("E/D", "D"), Map.entry("D/E", "D"), Map.entry("D/D", "D"),
        Map.entry("-E", "E"), Map.entry("-D", "D"),
        Map.entry("E%E", "E"),
        Map.entry("E^E", "D"), Map.entry("E^D", "D"), Map.entry("D^E", "D"), Map.entry("D^D", "D"),
        Map.entry("LyL", "L"), Map.entry("LoL", "L"), Map.entry("noL", "L"),
        Map.entry("E<E", "L"), Map.entry("E<D", "L"), Map.entry("D<E", "L"), Map.entry("D<D", "L"), Map.entry("A<A", "L"),
        Map.entry("E>E", "L"), Map.entry("E>D", "L"), Map.entry("D>E", "L"), Map.entry("D>D", "L"), Map.entry("A>A", "L"),
        Map.entry("E>=E", "L"), Map.entry("E>=D", "L"), Map.entry("D>=E", "L"), Map.entry("D>=D", "L"), Map.entry("A>=A", "L"),
        Map.entry("E<=E", "L"), Map.entry("E<=D", "L"), Map.entry("D<=E", "L"), Map.entry("D<=D", "L"), Map.entry("A<=A", "L"),
        Map.entry("E<>E", "L"), Map.entry("E<>D", "L"), Map.entry("D<>E", "L"), Map.entry("D<>D", "L"), Map.entry("A<>A", "L"),
        Map.entry("E==E", "L"), Map.entry("E==D", "L"), Map.entry("D==E", "L"), Map.entry("D==D", "L"), Map.entry("A==A", "L")
    );

    public Semantico(Sintaxis sintaxis) {
        this.sintaxis = sintaxis;
    }

    /*
     * Verfica que los tipos de las operacion que se realicen puedan hacerse entre si 
     *  retornando un booleano. En ambos casos (que la key este en el mapa de tipos, agrega a pila el valor 
     *   resultante de la operacion). -> El contenido de la key.
     *    EJEMPLO: L == L -> L, => Logico es igual a logico ??, Esta operacion produce Logico (si o no).
     *             E + E -> E, => Entero mas Entero ??, produce un Entero
     */
    public boolean tipoResult(String keyResult) {
        if (!this.mapTipos.containsKey(keyResult)) {
            
            if (keyResult.length() > 2) {
                if (keyResult.charAt(1) != '=') this.pilaTipos.add("I");
            }

            return false;
        } else {
            boolean meteAPilaTipos = !this.mapTipos.get(keyResult).equals("");
            if (meteAPilaTipos) this.pilaTipos.add(this.mapTipos.get(keyResult));
        }

        return true;
    }
    
    /*
     * Agrega un tipo de dato a la pila.
     */
    public void agregarApilaTipos(String tipo) {
        this.pilaTipos.push(tipo);
        
        //System.out.println(this.pilaTipos);
    }

    /*
     * Toma el valor de la pila y lo elimina
     */
    public String tomarCimaPilaTipos() {
        return this.pilaTipos.pop();
    }

    /*
     * Toma el valor top de la pila pero sin eliminarlo
     */
    public String verCimaPilaTipos() {
        return this.pilaTipos.peek();
    }

    /*
     * Verifica si un identificador ya existe (sea variable, constante o una funcion)
     */
    public void idenDuplicado(String nIde) {
        if (!this.sintaxis.tablaSimb.isEmpty()) {
            if (this.sintaxis.tablaSimb.containsKey(nIde) )
                this.sintaxis.hayError("Error Semantico", "Ya existe un identificador con ese nombre: " + nIde);
        }
    }

    // Getter sobre la pila. Permite obtener el objeto privado de la clase
    public Stack<String> getPilaTipos() {
        return this.pilaTipos;
    }


    /*
     * Crea una llave entre los dos valores a operar y el operador. (Parecido al codigo en tres dimenciones)
     *  Por ejemplo  1+3 => { suma ambos valores: 1+3 = 4 }
     */
    public String crearLLaveOperBin() {
        if (this.pilaTipos.size() > 2) {
            String dD = this.pilaTipos.pop();
            String opr = this.pilaTipos.pop();
            String dI = this.pilaTipos.pop();
            return dI + opr + dD;
        }
        return "";
    }

    /*
     * Crea una llave entre la operacion que se debe realizar a
     *  un unico valor. (por ejemplo: opera -12 => { hace negativo el 12 })
     */
    public String crearLLaveOperUni() {
        if (this.pilaTipos.size() > 1) {
            String dD = this.pilaTipos.pop();
            String opr = this.pilaTipos.pop();
            return opr + dD;
        }
        return "";
    }

    /*
     *
     * Comprueba si el identificador tomado desde la sintaxis de CPAS es 
     *  una funcion que contenga parametros
     * 
    */
    public String comprobarParamFuncVar(String nIde) {
        if (sintaxis.tabFuncParams.containsKey(nIde)) { // Por si el Identificador pertenece a una funcion con parametros
            nIde = sintaxis.tabFuncParams.get(nIde)[0];
        } else if (sintaxis.funcionesControl.isParamsInFunc()) {
            String nIdeSinParams = sintaxis.funcionesControl.getNameFuncActual().split("\\$")[0];
            String nIdePosibleParam = nIde + "$" + sintaxis.funcionesControl.getNameFuncActual();
            if (sintaxis.tabFuncParams.containsKey(nIdeSinParams)) {
                String[] paramsFuncActual = sintaxis.tabFuncParams.get(nIdeSinParams);
                for (int i = 1; i < paramsFuncActual.length; i++) {
                    if (paramsFuncActual[i].equals(nIdePosibleParam)) {
                        nIde = paramsFuncActual[i];
                        break;
                    }
                }
            }
        }
        return nIde;
    }

    public void buscaInsTipo(String ide) {
        String nIdeInformativo = ide.split("\\$")[0]; 
        if (sintaxis.tablaSimb.containsKey(ide)) {
            String[] o = sintaxis.tablaSimb.get(ide);

            this.pilaTipos.push(o[1]);
        } else {
            sintaxis.hayError("Error Semantico", "El identificador '" + nIdeInformativo + "' no esta declarado.");
        }
    }

    public void existeIde(String ide) {
        String nIdeInformativo = ide.split("\\$")[0];
        if(!sintaxis.tablaSimb.containsKey(ide)) {
            sintaxis.hayError("Error Semantico", "El identificador '" + nIdeInformativo + "' no esta declarado.");
        }
    }

    /*
     * Muestra el tipo de los paramatros en el orden que deben ser pasados.
     */
    public String mostrStructParsFunc(String[] pars) { 
        String infoParsString = "";
        int cont = -1;
        for (String e : pars) {
            cont++; 
            if (cont == 0) continue;

            infoParsString += indiceATipoInfo(e);

            if (cont != pars.length - 1) infoParsString += ", ";
        }
        return infoParsString;
    }


    /*
     * Esta funcion sera el metodo que se mande a llamar cada vez que sintaxis quiera verificar algo que 
     *  tenga relacion con la parte semantica
     */
    @Override
    public void consumir(EventosSemanticos evt) {
        evt.setIntanceSema(this);
    
        switch (evt) {
            case EventosSemanticos.Dimenciones e -> e.consumeEvento();
            case EventosSemanticos.Arreglos e -> e.consumeEvento();
            case EventosSemanticos.AccesoValorArreglo e -> e.consumeEvento();
            case EventosSemanticos.Asignacion e -> e.consumeEvento();
            case EventosSemanticos.ComprobadorTipoOperaciones e -> e.consumeEvento();
            case EventosSemanticos.ComprobarTipoAsigancion e -> e.consumeEvento();
            case EventosSemanticos.ControlAccesoDimenciones e -> e.consumeEvento();
            case EventosSemanticos.LlamadaFuncion e -> e.consumeEvento();
            case EventosSemanticos.TipoFuncionRetorno e -> e.consumeEvento();
            case EventosSemanticos.ComprobadorTipoCondicionales e -> e.consumeEvento();
            case EventosSemanticos.ComprobarComparadorSiga e -> e.consumeEvento();
            default -> {
            }
        }
    }
}
