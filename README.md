# Compilador-CPAS
CPAS es un lenguaje de programación en español compilado basado en la sintaxis de C y PASCAL que  permite recrear algunas de las sentencias básicas de cualquier lenguaje estructurado (Condicionales, bucles, funciones, arreglos y matrices, recursión, etc…) (Este proyecto es el COMPILADOR de CPAS, Se necesita de un INTERPRETE para ejecutarlo)
# Instalacion
Para la correcta ejecucion del compilador es necesario contar con: 
- Java 16 o superior
- Maven 

Una vez instalado ambas herramientas de manera global en la computadora: 
- Compilamos el proyecto en maven utilizando: ``` $ mvn compile ```  
- Ejecutamos con el comando: ``` $ mvn exec:java '-Dexec.mainClass=com.cpas.compilador.App' ```  
O en su caso:
- Se crea el ejecutable .jar del proyecto con el comando: ``` mvn package ```
- Se ejecuta el .jar con: ```  ```

# Funcionamiento

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

