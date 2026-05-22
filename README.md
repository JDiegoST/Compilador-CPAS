# Compilador-CPAS
CPAS es un lenguaje de programación en español compilado basado en la sintaxis de C y PASCAL que  permite recrear algunas de las sentencias básicas de cualquier lenguaje estructurado (Condicionales, bucles, funciones, arreglos y matrices, recursión, etc…) (Este proyecto es el COMPILADOR de CPAS, Se necesita de un INTERPRETE para ejecutarlo)
# Instalacion
Para la correcta ejecucion del compilador es necesario contar con: 
- Java 16 o superior
- Maven

Una vez instalado ambas herramientas de manera global en la computadora, ejecutamos el proyecto bajo los estandares de MAVEN: 
- Compilamos el proyecto en maven utilizando: ``` $ mvn compile ```  
- Ejecutamos con el comando: ``` $ mvn exec:java '-Dexec.mainClass=com.cpas.compilador.App' ```  
O en su caso:
- Se crea el ejecutable .jar del proyecto con el comando: ``` mvn package ```
- Se ejecuta el .jar con: ``` java -jar .\target\Compilador-1.0-SNAPSHOT.jar  ```

# Funcionamiento
El compilador consta de 4 etapas fundamentales para su creacion: Lexico, sintaxis, semantica y generacion de codigo.
## Lexico
La Etapa lexica, consiste en clasificar y categorizar toda la fuente de texto. En un lenguaje de programacion, existen palabras reservadas que permiten escribir cada estructura logica para la creacion de codigo. 

- Identificacion de identificadores de el nombre de una variable, constante, funcion.
- Constantes decimales y enteros (numeros).
- Logicos (valores booleanos).
- Cadenas de texto (Strings).
- Palabras reservadas del lenguaje. La forma en la que se indica en el lenguaje un bucle, una condicional, la declaracion de una variable, una funcion, etc...

A este proceso le conocemos como tokenizacion. Donde el TOKEN, representa el tipo de la palabra (si es reservada, si es un identificador de una variable o una funcion, si en un numero, un valor logico, un caracer que representa alguna operacion logica o aritmetica) y al valor real del token le denominamos lexema.

Por ejemplo... supongamos que tenemos la expresion: ``` p = 3 + 0; ```

La estapa lexica clasifica cada caracter individual o en conjunto para separar tipos de palabras.
```p``` es un texto que representa una variable a la que se le esta asignando el resultado de una suma. El token es ```IDENTIFICADOR``` (el compilador categorizara cualquier 'palabra' que no sea palabra reservada y un string como identificador de una variable, constante o funcion). Y el valor del lexema es ```p```.

El compilador tiene sus propios tokens creados para cada tipo de caracter o 'palabra'.

- Para -> `=`, su lexema es `=` y el token es `DELIMITADOR`.
- Para -> `3` y `0` su lexemas son `3 y 0` y el token es `Caracter_ENTERO`.
- Para -> `;` su lexema es `:` y el token es `DELIMITADOR`

## Sintactico 
Sintaxis genera las estructuras complejas que permiten darle forma a una condicion, bucle, declaracion de variables, constantes o funciones. 

Por ejemplo... 
En Cpas, la estructura de un `if-else` es la siguiente...
```
    si (CONDICION LOGICA) hacer inicio
        // INSTRUCCIONES
    fin sino inicio
        INSTRUCCIONES
    fin;
```

La etapa sintactica se encarga que cuando escribas la sentencia (ya sea condicional como en el ejemplo o alguna otra), esta cumpla con todos los lexemas y token en orden para su construccion.

En el ejemplo de el `si` muestra que, para que el compilador sepa que el desarrollador quiere hacer una condicion, primero va la palabra reservada de `si`, despues una expresion que como resultado ofrezca un valor para saber si se ejecua o no la sentencia, despues la palabra reservada `hacer`, `inicio`, posibles operaciones (asignacion, llamadas de funcion, resolucion de problemas matematicos) y por ultimo `fin`. Si llegamos a poner alguna otro token donde deba de una valor en especifico 
## Semantico
## Generacion de CODIGO

# Estructura del proyecto
```
|—— pom.xml
|—— Compilador
|   |—— src
|       |—— main
|           |—— java
|               |—— com
|                   |—— cpas
|                       |—— compilador
|                           |—— App.java
|                           |—— core
|                               |—— WorkingSpace.java
|                           |—— Generacion
|                               |—— GeneradorPL0.java
|                           |—— Lexico
|                               |—— Lexico.java
|                           |—— Models
|                               |—— ControlError.java
|                               |—— Token.java
|                           |—— Semantico
|                               |—— Eventos
|                                   |—— EventosSemanticos.java
|                               |—— interfaces
|                                   |—— Estatuto.java
|                               |—— Semantico.java
|                           |—— Sintaxis
|                               |—— Estructuras
|                                   |—— AbstracSintaxis.java
|                                   |—— comandos
|                                       |—— asignadores
|                                           |—— Asignaciones.java
|                                       |—— bucles
|                                           |—— Bucles.java
|                                       |—— Comandos.java
|                                       |—— condicionales
|                                           |—— Condicionales.java
|                                       |—— llamada_a_funcion
|                                           |—— LlamadaFunc.java
|                                       |—— metodos
|                                           |—— Metodos.java
|                                   |—— Expresiones.java
|                                   |—— Funciones.java
|                                   |—— Variables.java
|                               |—— Sintaxis.java
|               |—— java
|                   |—— lang
|       |—— test
|           |—— java
|               |—— com
|                   |—— cpas
|                       |—— compilador
|                           |—— AppTest.java
```

##### Creador: JDiego

