package com.cpas.compilador.Semantico.Eventos;

import java.util.ArrayList;
import java.util.Arrays;

import com.cpas.compilador.Semantico.Semantico;
import com.cpas.compilador.Sintaxis.Sintaxis;
import com.cpas.compilador.core.WorkingSpace;
 
public abstract class EventosSemanticos extends WorkingSpace {
    
    protected static Semantico sem;
    private static Sintaxis six;

    public void setIntanceSema(Semantico sema) {
        sem = sema;
        six = sema.sintaxis;
    }

    public  static ArrayList<String> idenTipo = new ArrayList<>(Arrays.asList("E", "D", "L", "A"));

    public abstract void consumeEvento();
    
    
    public static class Arreglos extends EventosSemanticos {
        private final String nIde;
        private final String tipoVariable;
        private final String[] valores;

        public Arreglos (String nIde, String elementosArreglo, String tipoVar) {
            this.nIde = nIde;
            this.valores = elementosArreglo.split(" ");
            this.tipoVariable = tipoVar; 
        }

        public String[] getValores() {
            return this.valores;
        }

        public String tipoVariable() {
            return this.tipoVariable;
        }

        public String getnIde() {
            return this.nIde;
        }

        @Override
        public void consumeEvento() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    public static class AccesoValorArreglo extends EventosSemanticos {
        private final String valorPila;

        public AccesoValorArreglo(String valorPila) {
            this.valorPila = valorPila;
        }

        @Override
        public void consumeEvento() {
            if (!valorPila.equals("E")) {
                six.hayError("Error Semantico", "Para acceder a un valor del arreglo, se necesita un identificador ENTERO, no '" + indiceATipoInfo(this.valorPila) + "'");
            }

        }
        
    }

    public static class ControlAccesoDimenciones extends  EventosSemanticos {
        private final String nIde;


        public ControlAccesoDimenciones(String nIden) {
            this.nIde = nIden;
        }

        @Override
        public void consumeEvento() {

            boolean aceptaDimen1 = false;
            boolean aceptaDimen2 = false;
            String dimen1 = "0", dimen2 = "0", clase = "";
            if (six.tablaSimb.containsKey(nIde)) {
                String datos[] = six.tablaSimb.get(nIde);
                clase = datos[0];
                dimen1 = datos[2];
                dimen2 = datos[3];
                if (Integer.parseInt(dimen1) > 0 ) aceptaDimen1 = true;
                if (Integer.parseInt(dimen2) > 0 ) aceptaDimen2 = true;
            } 


            if (six.getLexe().equals("(")) {
                return;
            } else {
                if (clase.equals("F")) {
                    six.hayError("Error Sintaxis", "El identificador '" + nIde.split("\\$")[0] + "' en una funcion." +
                                                    " Falta '()'.");
                    return;
                }
            }


            if (six.getLexe().equals("[") ) {
                if (!aceptaDimen1) {
                    six.hayError("Error Semantico", "La variable '" + nIde + "' no esta dimencionada.");
                }
                six.variablesControl.udim();
                if (aceptaDimen2) {
                    if(!six.getLexe().equals("["))
                        six.hayError("Error Semantico", "La variable '" + nIde + "' esta bidimencionada. [" + dimen1 + "][" + dimen2 + "] y no llega su segunda dimencion.");
                    else 
                        six.variablesControl.udim();
                } else {
                    if (six.getLexe().equals("[")) 
                        six.hayError("Error Semantico", "La variable '" + nIde + "' no esta bidimencionada.");
                }
            } else {
                String infoDobleDimencion[] = {"dimencionada", "[x]"};
                if (aceptaDimen2) {
                    infoDobleDimencion[0] = "bidimencionada";
                    infoDobleDimencion[1] = "[x][y]";
                }    
                if (aceptaDimen1)
                    six.hayError("Error Semantico", "La variable '" + nIde + "' esta " + infoDobleDimencion[0] + " y no llega ninguna dimencion. Se espera " + infoDobleDimencion[1]);
            }
        }
    } 

    public static class Dimenciones extends EventosSemanticos {

        private String nIde;

        public Dimenciones() {}
      
        public Dimenciones(String nameIde) {
            this.nIde = nameIde;
        }

        @Override
        public void consumeEvento() {

            if (six.getToken().equals("Ide")) {
                if (six.variablesControl.tablaVarConst.containsKey(six.getLexe())) { // Checo que el IDE de la constante este declarada para poder definir el tamano de un arreglo
                    String contenido[] = six.variablesControl.tablaVarConst.get(six.getLexe());
                    String isConst = contenido[0];
                    if (!isConst.equals("C")) { // Debe de ser constante
                        six.hayError("Error Semantico", "El atributo: " + six.getLexe() + " debe ser CONSTANTE para dimencionar un arreglo.");
                    }
                    String tipo = contenido[1];
                    if (!tipo.equals("E")) { // Debe de contener un valor tipo ENTERO
                        six.hayError("Error Semantico", "La constante: '" + six.getLexe() + "' DEBE ser entera para dimencionar un arreglo.");
                    }
                } else {
                    six.hayError("Error Semantico", "No se puede usar la constante: '" + six.getLexe() + "' para dimencionar un arreglo. No esta declarada.");
                }
            } else if (!six.getLexe().equals("]")) { // Si no es un IDE, veo que no venga el ']' de cierre (eso seria un error ya que se necesita un valor)
                if (!six.getToken().equals("Ent")) // El valor o constante literal debe de ser ENTERA
                    six.hayError("Error Semantico", "El valor de dimencion del arreglo debe ser ENTERO");

            } else { // Si el lexema trae el ']' de cierre, le notifico al usuario que se necesita un valor Entero (en caso de ser una variable debe ser CONSTANTE) para declarar el ARREEGLO
                six.hayError("Error Semantico", "La variable '" + this.nIde
                        + "', necesita de una dimencion ENTERA y ser CONSTANTE para declarar el tamano de un ARREGLO.");
            }
        }    
    }

    public static class Asignacion extends EventosSemanticos {

        private final String operador = "=";
        public Asignacion() {}
        
        @Override
        public void consumeEvento() {
            String valorIzq = Character.toString(six.getToken().charAt(0)); // Tipo de un caracter
            if (valorIzq.equals("C")) {
                valorIzq = Character.toString(six.getToken().charAt(2));
            }

            String valorDer = six.variablesControl.getTipoVariable();
            String key = valorDer + this.operador + valorIzq;
            if (!sem.tipoResult(key)) {
                six.hayError("Error Semantico", "No es posible asignar un tipo " + indiceATipoInfo(valorIzq) + " a un tipo " + indiceATipoInfo(valorDer));

                six.hayError("Error Semantico",
                        "El valor de asignacion: " + six.getLexe() + ", es de un tipo diferente al declarado.");
            }
        }
        
    }

    public static class ComprobadorTipoOperaciones extends EventosSemanticos {

        private final String operador;

        public ComprobadorTipoOperaciones() {
            this("");
        }

        public ComprobadorTipoOperaciones(String operacion) {
            this.operador = operacion;
        }

        @Override
        public void consumeEvento() {
            String keyTipoResultante;

            if (this.operador.equalsIgnoreCase("")) {
                keyTipoResultante = sem.crearLLaveOperBin();
            } else {
                keyTipoResultante = sem.crearLLaveOperUni();
            }

            String valorIzq = Character.toString(keyTipoResultante.charAt(0));
            String valorDer = Character.toString(keyTipoResultante.charAt(keyTipoResultante.length()-1));

            if (!sem.tipoResult(keyTipoResultante)) {
                six.hayError("Error Semantico", "Conflicto de tipos. El tipo '" + indiceATipoInfo(valorIzq) + "' no puede ser operado con un tipo '" + indiceATipoInfo(valorDer) + "'");
            }
        }
    }

    public static class TipoFuncionRetorno extends EventosSemanticos {

        private final String nideFunc;
        private final String tipoFunc;

        public TipoFuncionRetorno(String tipoFunc, String nIdeFunc) {
            this.tipoFunc = tipoFunc;
            this.nideFunc = nIdeFunc;
        }

        @Override
        public void consumeEvento() {
            String keyTipoRes = this.tipoFunc + "=" + sem.verCimaPilaTipos();
                if (!sem.tipoResult(keyTipoRes)) 
                    six.hayError("Error Semantico", "La funcion " + this.nideFunc + " tipo '" + indiceATipoInfo(this.tipoFunc) + "' no puede regresar un valor de tipo '" + indiceATipoInfo(sem.verCimaPilaTipos()) + "'");

        }
    }

    public static class LlamadaFuncion extends EventosSemanticos {
        private String focoInspeccion = "llamada";
        private String[] funcParams;
        private int paramsContados;

        public LlamadaFuncion(String foco) {
            this.focoInspeccion = foco;
        }

        public LlamadaFuncion(String[] params, int paramsContados) {
            this.funcParams = params;
            this.paramsContados = paramsContados;
        }


        @Override
        public void consumeEvento() {
            switch (this.focoInspeccion) {
                case "llamada" -> {
                    int paramsNecesarios = this.funcParams.length - 1;

                    if (paramsNecesarios != this.paramsContados) {
                        six.hayError("Error Semantico", "La funcion '" + this.funcParams[0] + "' requiere de " + paramsNecesarios + " argumentos");
                    } else {

                        while (this.paramsContados > 0) {
                            String tipoEnPila = sem.tomarCimaPilaTipos();
                            String keyTipoResult = this.funcParams[paramsNecesarios] +  "=" + tipoEnPila;
                            
                            if (!sem.tipoResult(keyTipoResult)) {
                                six.hayError("Error Semantico", "La funcion '" + this.funcParams[0] + "' requiere el parametro de tipo: " + indiceATipoInfo(this.funcParams[paramsNecesarios]) +
                                                                        ", y llego: " + indiceATipoInfo(tipoEnPila) + ". " + this.funcParams[0] + "(" + sem.mostrStructParsFunc(this.funcParams) + ")");                }
                            this.paramsContados--;
                            if (paramsNecesarios > 1) paramsNecesarios--;
                        }
                    }
                }
                case "parametros" -> {

                }
                default -> {}
            }
        }
    }

    public static class ComprobarComparadorSiga extends EventosSemanticos {

        private final String nIde;
        private final String tipoVariable;

        public ComprobarComparadorSiga(String nombreIdentificador, String tipoVariableAsignada) {
            this.nIde = nombreIdentificador;
            this.tipoVariable = tipoVariableAsignada;
        }

        @Override
        public void consumeEvento() {
            String resStack = sem.tomarCimaPilaTipos();


            String keyResult = this.tipoVariable + "==" + resStack;

            if (!sem.tipoResult(keyResult)) {
                six.hayError("Error Semantico", "No se puede Operar: " + indiceATipoInfo(this.tipoVariable) + "==" + indiceATipoInfo(resStack)+ ".");
                six.hayError("Error Semantico", "La variable" + this.nIde + "' de tipo "
                                    + indiceATipoInfo(this.tipoVariable) + " no puede ser comparado con valores de tipo: " + indiceATipoInfo(resStack));
            }
        }
    }

    public static class ComprobarTipoAsigancion extends EventosSemanticos {

        private final String nIde;
        private final String tipoVariable;

        public ComprobarTipoAsigancion(String nombreIdentificador, String tipoVariableAsignada) {
            this.nIde = nombreIdentificador;
            this.tipoVariable = tipoVariableAsignada;
        }

        @Override
        public void consumeEvento() {
            String resStack = sem.verCimaPilaTipos();
            String posibleParam[] = nIde.split("\\$");

            String contMsgErrVarOParam = posibleParam.length > 1 ? "El parametro '" : "La variable '";
            String keyResult = this.tipoVariable + "=" + resStack;

            if (!sem.tipoResult(keyResult)) {
                six.hayError("Error Semantico", "No se puede Operar: " + indiceATipoInfo(this.tipoVariable) + "=" + indiceATipoInfo(resStack)+ ".");
                six.hayError("Error Semantico", contMsgErrVarOParam + posibleParam[0] + "' de tipo "
                                    + indiceATipoInfo(this.tipoVariable) + " no puede almacenar valores de tipo: " + indiceATipoInfo(resStack));
            }
        }
    }


    public static class ComprobadorTipoCondicionales extends EventosSemanticos {

        private final String estatuto;
        public ComprobadorTipoCondicionales(String msjEstatuto) {
            this.estatuto = msjEstatuto;
        }

        @Override
        public void consumeEvento() {
            if (!sem.getPilaTipos().empty()) {
                String dtoL = sem.tomarCimaPilaTipos();
                if (!dtoL.equals("L")) {
                    six.hayError("Error Semantico", "Tipo '" + indiceATipoInfo(dtoL) + "' dentro del " + this.estatuto + ", no puede ser convertido a uno LOGICO");
                }
            }
        }
        
    }
    
}
