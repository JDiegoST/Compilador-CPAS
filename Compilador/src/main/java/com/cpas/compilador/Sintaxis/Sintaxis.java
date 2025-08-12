package com.cpas.compilador.Sintaxis;

import java.util.LinkedHashMap;
import java.util.Map;

import com.cpas.compilador.Generacion.GeneradorPL0;
import com.cpas.compilador.Lexico.Lexico;
import com.cpas.compilador.Models.ControlError;
import com.cpas.compilador.Models.Token;
import com.cpas.compilador.Semantico.Semantico;
import com.cpas.compilador.Sintaxis.Estructuras.Expresiones;
import com.cpas.compilador.Sintaxis.Estructuras.Funciones;
import com.cpas.compilador.Sintaxis.Estructuras.Variables;
import com.cpas.compilador.Sintaxis.Estructuras.comandos.Comandos;
import com.cpas.compilador.core.WorkingSpace;

public class Sintaxis extends WorkingSpace{

    public Lexico lexico;
    public Semantico semantica;
    public GeneradorPL0 generadorPL0;
    public Comandos comandoControl;
    public Expresiones expresionControl;
    public Funciones funcionesControl;
    public Variables variablesControl;

    public Token currentToken;

    public final Map<String, String[]> tablaSimb = new LinkedHashMap<>();
    public final Map<String, String[]> tabFuncParams = new LinkedHashMap<>();

    private int idEtiqueta = 0;

    public Sintaxis(Lexico lexico) {
        this.generadorPL0 = new GeneradorPL0();
        this.semantica = new Semantico(this);
        this.lexico = lexico;
        this.currentToken = this.lexico.getCurrentLexico();
        
        this.comandoControl = new Comandos(this);
        this.funcionesControl = new Funciones(this);
        this.variablesControl = new Variables(this);
        this.expresionControl = new Expresiones(this);
    }


    /*
     * Esta funcion instancia una clase donde estan los metodos y atributos necesarios para 
     *  avisar al usario el tipo de error y su emblema. Ademas de puntualizar la linea y la columna donde 
     *   surgio el error.
     */
    public void hayError(String tyE, String meE) {
        int[] posRC = this.currentToken.getPosicion();
        ControlError.getInstance().repotarError(tyE, meE, posRC[0], posRC[1]);
        this.lexico.huboError();
    }

    /*
     * La razon por la que esta funcion se subscribe es porque las lineas y columnas son globales dados por el lexico. Esto quiere
     *  decir que cada vez que pasa de token y lexema, la linea y columna se actualiza, y estas mismos valores, se utilizan en la clase 
     *   ControlError() para decirle al programador en donde ocurrio el error.
     * 
     * Sin embargo, hay ocasiones que la linea y columna no coinciden exactamente donde ocurrio el error, por eso es necasario
     *  hacer una funcion donde se le pueda pasar la linea y columna donde ocurrio el error.
     * 
     * POR EJEMPLO: 
     * 
     * 1    funcion principal() inicio
     * 2
     * 3       imprimeln("Este es un ejemplo")
     * 4
     * 5       si bandera hacer
     * 6       inicio
     * 7            ...
     * 8            ...
     * 9       fin;
     * 10   fin
     * 
     * Si no ponemos punto y coma ';' despues de cada estatuto (llamada de funcion, asignacion, condicionales, bucles, etc...)
     *  debe de dar el error: "Falta ; [3][48]".
     * Sim embargo, cuando el compilador avisa del error ya paso al siguiente token, por lo tanto, ahora los valores globales no son
     *  linea=[3], columna=[48], si no linea=[5], columna=[16] con el comando 'si', por lo tanto no es posible utilizar los valores actuales
     *   de la linea y columna.
     * 
     * Entonces, para avisar exactamente la posicion de donde ocurrio el error, accedemos a la posicion anterior del token anterior 
     *  y  se lo pasamos a esta funcion.
     */
    public void hayError(String tyE, String meE, int[] posRC) {
        ControlError.getInstance().repotarError(tyE, meE, posRC[0], posRC[1]);
        this.lexico.huboError();
    }

    /*
     * Insertamos a la tabla de simbolos el nombre de la variable/constante/funcion y sus caracteristicas en un diccionario
     */
    public void insertarTabSimb(String valueKey, String[] contenido) {
        this.tablaSimb.put(valueKey, contenido);
    }

    /*
     * Esta metodo permite ingresar aquellas funciones que necesitan parametros para funcionar a la tabla de funciones con parametros
     */
    public void insertarFuncConParams(String nIdenSinParams, String[] contenido) {
        this.tabFuncParams.put(nIdenSinParams, contenido);
    }

    /*
     * 
     */
    public String tipo() {
        String lexe = this.currentToken.getLexema();
        String tipoDato[] = { "alfabetica", "decimal", "entera", "logica" };
        if (!estaDentro(tipoDato, lexe))
            hayError("Error de Sintaxis",
                    "Se esperaba tipo: alfabetica, decimal, entera o logica y llego: " + lexe);

        return !lexe.equals("") ? Character.toString(lexe.toUpperCase().charAt(0)) : "I";
    } 

    
    public void iniciarPrograma() {
        
        while (this.currentToken.getLexema().equals("constante") || this.currentToken.getLexema().equals("variable")) {
            this.variablesControl.variableOConstante();
        }
        
        // Paso las variables y constantes de la tabla de declaracion TabVarConst a la tabla de simbolos.
        // Esto lo hago asi, para poder guardar en TabVarConst 5 cosas: NOMBRE, CLASE, TIPO, DIMENCION, VALOR DESPUES DEL IGUAL.
        // DIMENCION guardara "0" si la variable no tiene una dimencion. Guardara "x" donde x es la cantidad de elementos del arreglo, 
        //   Guardara "x,y" si la variable tiene dos dimenciones: X es la cantidad de arreglos dentro de un arreglo y Y cantidad de elementos
        //     de cada arreglo perteneciente.
        // VALOR guarda "x" donde x representa el valor que se le asigno si es que al declarar la variable la inicializamos,
        //  GUARDA "x y z a b c " donde cada letra representa un valor perteneciente a una variable dimencionada (arreglo, Aqui DIMENCION GUARDA el valor: "X") 
        //    Guarda "x y; z a " donde es lo mismo que el anterior nomas que el ';' represeta donde termina una fila de una matriz (Aqui DIMENCION GUARDA el valor: "x,y")
        //      GUARDA "" si la variable no fue inicializada al declararse (NO HAY '=')
        this.variablesControl.tablaVarConst.forEach((nIde, cont) -> {
            int d1, d2 = -1;
            String[] valores = cont[3].trim().split(";");
            String dimens[] = cont[2].split(",");
            d1 = Integer.parseInt(dimens[0]);
            if (dimens.length > 1) {
                d2 = Integer.parseInt(dimens[1]);
            }
            this.insertarTabSimb(nIde, new String[] { cont[0], cont[1], Integer.toString(d1), d2 == -1 ? "0" : Integer.toString(d2) });
            if (valores.length >= 1 && !valores[0].equals("") && !this.lexico.getErrA()) {
                int dimenFinal = d1;
                if (d2 != -1) {
                    dimenFinal = d2;
                    d1--;
                }
                for (int x = 0; x <= d1; x++) {
                    String valor[] = valores[x].trim().split(" ");

                    if (dimenFinal != 0) {
                        for (int i = 0; i < valor.length ; i++) {
                            if (d2 != -1)
                                this.generadorPL0.insertCodPL0(new String[] { "LIT", Integer.toString(x), "0" });
                            this.generadorPL0.insertCodPL0(new String[] { "LIT", Integer.toString(i), "0" });
                            this.generadorPL0.insertCodPL0(new String[] { "LIT", valor[i], "0" });
                            this.generadorPL0.insertCodPL0(new String[] { "STO", "0", nIde });
                        }
                    } else {
                        this.generadorPL0.insertCodPL0(new String[] { "LIT", isIdeLogico(cont[3]), "0" });
                        this.generadorPL0.insertCodPL0(new String[] { "STO", "0", nIde });
                    }
                    if (d2 == -1) break;
                }
            }
        });

        this.insertarTabSimb("_P", new String[] { "I", "I", "1", "0" });
        this.generadorPL0.insertCodPL0(new String[] { "JMP", "0", "_Principal" });
        if (!this.currentToken.getLexema().equals("funcion")) {
            hayError("Error de Sintaxis",
                    "Un programa en CPAS debe tener al menos una FUNCION: " + this.currentToken.getLexema());
                } else {
                    while (this.currentToken.getLexema().equals("funcion"))
                    this.funcionesControl.funciones();
                }
            }
            
            private String isIdeLogico(String valor) {
                if (valor.equals("verdadero")) return "V";
        else if (valor.equals("falso")) return "F"; 
        return valor;
    }

    public String tokenAIndiceTipo() {
        String indiceTipo = "";
        switch (this.currentToken.getToken()) {
            case "Ent" -> indiceTipo = "E";
            case "Dec" -> indiceTipo = "D";
            case "CtA" -> indiceTipo = "A";
            case "CtL" -> indiceTipo = "L";
        }
        return indiceTipo;
    }
    
    public String genIdEtiqueta() {
        this.idEtiqueta++;
        return "_E" + this.idEtiqueta;
    }

    public String getToken() {
        return this.currentToken.getToken();
    }

    public String getLexe() {
        return this.currentToken.getLexema();
    }
}
