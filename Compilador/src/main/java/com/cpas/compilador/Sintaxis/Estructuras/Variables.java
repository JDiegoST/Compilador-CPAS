package com.cpas.compilador.Sintaxis.Estructuras;

import java.util.LinkedHashMap;
import java.util.Map;

import com.cpas.compilador.Semantico.Eventos.EventosSemanticos;
import com.cpas.compilador.Sintaxis.Sintaxis;

public class Variables extends AbstracSintaxis {

    private String valoresArr;
    private String tipoVarActual;

    public final Map<String, String[]> tablaVarConst = new LinkedHashMap<>();

    private void insertarTablaVarConst(String nameIde, String[] contenido) {
        this.tablaVarConst.put(nameIde, contenido);
    }

    public Variables(Sintaxis sintaxis) {
        this.six = sintaxis;

        this.sem = this.six.semantica;
    }

    public void variableOConstante() {
        if (this.six.getLexe().equals("constante"))
            constante();
        else
            variable();
    }


    public void variable() {
        String deli;
        String nIde;

        avanzarToken();
        this.tipoVarActual = this.six.tipo();
        avanzarToken();
        do {
            String dim = "0", valor = "", dim2 = "0";

            if (!this.six.getToken().equals("Ide"))
                this.six.hayError("Error Sintaxis", "Se espera un Identificador y llego: " + this.six.getLexe());
            nIde = this.six.getLexe();

            // CHECAR SI UNA VARIABLE YA FUE DECLARADA CON EL MISMO NOMBRE (IDENTIFICADOR)
            this.sem.idenDuplicado(nIde);

            boolean hayDim2 = false;

            avanzarToken();
            if (this.six.getLexe().equals("[")) {
                dim = dimen(nIde);
                if (this.six.getLexe().equals("[")) {
                    dim2 = dimen(nIde);
                    hayDim2 = true;
                }
            }

            if (this.six.getLexe().equals("=")) {
                avanzarToken();
                if (this.six.getLexe().equals("{")) {

                    if (dim.equals("0"))
                        this.six.hayError("Error Sintaxis", "La variable '" + nIde + "' no esta dimencionada");

                    avanzarToken();
                    if (hayDim2) {
                        dim2Ctes(dim, dim2, nIde);
                        valor = this.valoresArr;
                        if (!this.six.getLexe().equals("}"))
                            this.six.hayError("Error Sintaxis",
                                    "Asignacion de valores a arreglo no finalizada: falta '}'");
                    } else {
                        ctes(dim, nIde);
                        valor = this.valoresArr;
                        if (!this.six.getLexe().equals("}"))
                            this.six.hayError("Error Sintaxis",
                                    "Asignacion de valores a arreglo no finalizada: falta '}'");
                    }
                    avanzarToken();
                } else {
                    valor = this.six.getLexe();
                    cte();
                }
            }

            // GUARDO EL IDE, LA CLASE, EL TIPO, LA DIMENCION Y EL VALOR DE LA VARIABLE (SI
            // ES QUE SE LE ASIGNA UN VALOR)
            // En caso de arreglos, guardo la dimencion (longitud global del arreglo) y en
            // valor guardo un string con todos los valores del arreglo.
            this.insertarTablaVarConst(nIde, new String[] { "V", this.tipoVarActual, formatoDimens(dim, dim2), valor });
            this.valoresArr = "";
            deli = this.six.getLexe();
            if (this.six.getLexe().equals(",")) {
                deli = this.six.getLexe();
                avanzarToken();
            }
        } while (deli.equals(","));

        if (!this.six.getLexe().equals(";")) {
            this.six.hayError("Error Sintaxis", "Falta cerrar linea: ';'");
        }
        avanzarToken();
    }

    public void constante() {
        String delim1;

        avanzarToken();
        this.tipoVarActual = six.tipo();

        avanzarToken();
        do {

            if (!this.six.getToken().equals("Ide")) {
                six.hayError("Error Sintaxis", "Se espera un Identificador y llego: " + this.six.getLexe());
            }

            String nameConst = this.six.getLexe();

            // CHECO QUE NINGUNA OTRA CONSTANTE TENGA EL MISMO NOMBRE (IDENTIFICADOR)
            this.sem.idenDuplicado(nameConst);

            avanzarToken();

            while (this.six.getLexe().equals("[")) {
                six.hayError("Error Sintaxis",
                        "Una constante no puede ser un valor dimencionado (un arreglo o matriz)");

                avanzarToken();

                if (this.six.getLexe().equals("="))
                    break;

                avanzarToken();
                if (this.six.getLexe().equals("="))
                    break;

                avanzarToken();
            }

            if (!this.six.getLexe().equals("=")) {
                six.hayError("Error Sintaxis", "Una constante debe ser inicializada (=), llego: " + this.six.getLexe());
            }

            avanzarToken();
            String valor = this.six.getLexe();
            cte();

            // Guardo el identificador (nombre de la variable), su clase (constante), su
            // dimencion (por defecto, una
            // constante en CPAS) no puede ser dimencionada (ser un arreglo); y su valor
            // (Una constante se le debe ser asignado un valor)
            insertarTablaVarConst(nameConst, new String[] { "C", this.tipoVarActual, "0", valor });

            this.valoresArr = "";
            delim1 = this.six.getLexe();
            if (delim1.equals(",")) {
                avanzarToken();
            }
        } while (delim1.equals(","));

        if (!this.six.getLexe().equals(";")) {
            six.hayError("Error Sintaxis", "Falta cerrar linea: ';'");
        }

        avanzarToken();
    }

    public void cte() {
        String[] tipVarble = { "Ent", "Dec", "CtA", "CtL" };

        if (!estaDentro(tipVarble, this.six.getToken())) {
            this.six.hayError("Error Sintaxis",
                    "Se esperaba un valor (Entero, Decimal, Logico, Alfabetico) y llego: " + this.six.getLexe());
        } else {
            // Se lanza el evento semantico para checar que el tipo de la constante
            // corresponda al tipo de la variable
            this.sem.consumir(new EventosSemanticos.Asignacion());

        }
        this.valoresArr += this.six.getLexe() + " ";
        avanzarToken();
    }

    public void ctes(String dim, String nIde) {
        String deli;
        int contDimen = 1;
        do {
            cte();
            deli = this.six.getLexe();
            if (deli.equals(",")) {
                contDimen++;
                avanzarToken();
            } else {
                if (contDimen > Integer.parseInt(dim))
                    this.six.hayError("Error Sintaxis", "La variable '" + nIde + "' solo puede contener " + dim
                            + " valores. Llegaron: " + contDimen);

                for (int i = contDimen + 1; i <= Integer.parseInt(dim); i++)
                    this.valoresArr += "0 ";
            }
        } while (deli.equals(","));
    }

    public String dimen(String IdeName) {
        avanzarToken();

        String tamannoArreglo = this.six.getLexe();

        // Se lanza un evento sematico para verficar que el valor que le dara dimencion
        // al arreglo sea ENTERO y UN VALOR CONSTANTE.
        // Ya sea por una identificador de clase cconstante o un valor ENTERO numerico
        // (1, 2, 3....)
        this.sem.consumir(new EventosSemanticos.Dimenciones(IdeName));

        if (this.six.getToken().equals("Ide")) {
            if (this.tablaVarConst.containsKey(this.six.getLexe())) {
                String[] contenido = this.tablaVarConst.get(this.six.getLexe());

                tamannoArreglo = contenido[3];
            }
        } else {
            // Se iguala a 0 en caso de error para que java siga la compilacion sin colapso.
            tamannoArreglo = "0";
        }

        avanzarToken();
        if (!this.six.getLexe().equals("]"))
            this.six.hayError("Error Sintaxis", "Se esperaba ']' y llego: " + this.six.getLexe());

        avanzarToken();
        return tamannoArreglo;
    }

    public void dim2Ctes(String dim1, String dim2, String nIde) {

        int conRow = 1;
        String deli;
        do {
            if (!this.six.getLexe().equals("{"))
                this.six.hayError("Error Sintaxis", "Se esperaba un '{' de apertura y llego: '" + this.six.getLexe() + "'");
            avanzarToken();
            ctes(dim2, nIde);

            if (!this.six.getLexe().equals("}"))
                this.six.hayError("Error Sintaxis", "Se esperaba un '}' de apertura y llego: '" + this.six.getLexe() + "'");
            avanzarToken();

            deli = this.six.getLexe();
            if (deli.equals(",")) {
                avanzarToken();
                conRow++;
                this.valoresArr += ";";
            } else {
                if (conRow > Integer.parseInt(dim1))
                    this.six.hayError("Error Sintaxis",
                            "La matriz '" + nIde + "' solo puede contener " + dim1 + " arreglos. Llegaron: " + conRow);
            }
        } while (deli.equals(","));
    }

    public void udim() {
        avanzarToken();
        this.six.expresionControl.expr();

        if (!this.six.getLexe().equals("]")) {
            this.six.hayError("Error de Sintaxis", "Se esperaba ']' de cierre y llego: " + this.six.getLexe());
        }

        String valorTopPila = this.sem.tomarCimaPilaTipos();

        this.sem.consumir(new EventosSemanticos.AccesoValorArreglo(valorTopPila));
        avanzarToken();
    }

    public String formatoDimens(String dim1, String dim2) {
        if (!dim2.equals("0"))
            return dim1 + "," + dim2;
        return dim1;
    }

    public String getTipoVariable() {
        return this.tipoVarActual;
    }
}
