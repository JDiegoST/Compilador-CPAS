package com.cpas.compilador;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

import com.cpas.compilador.Lexico.Lexico;
import com.cpas.compilador.Sintaxis.Sintaxis;

/**
 * Hello world!
 *
 */
public class App {

    private String archivoE = "   ";
    private String entrada = "";

    public void menuPrograma() {
        BufferedReader aEnt;
        try (Scanner in = new Scanner(System.in)) {
            aEnt = null;
            while (!(this.archivoE.substring(this.archivoE.length() - 3).equals("icc"))) {
                System.out.print("Archivo a compilar (*.icc) [.] =salir: ");
                this.archivoE = in.nextLine();

                if (this.archivoE.equals("."))
                    System.exit(0);

                try {
                    aEnt = new BufferedReader(new FileReader(this.archivoE));
                    break;
                } catch (FileNotFoundException e) {
                    System.out.println(this.archivoE + " No existe, volver a intentar");
                    this.archivoE = "   ";
                }

            }
        }

        if (aEnt != null) {
            try {
                String linea;
                while ((linea = aEnt.readLine()) != null) {
                    this.entrada += linea + '\n';
                }
            } catch (IOException e) {
                System.out.println("Hubo un error");
            }
        }

        System.out.println("\n\n" + this.entrada + "\n\n");
        
    }
    
    public static void main( String[] args ) {
        App p1 = new App();

        p1.menuPrograma();

        Lexico lexico = new Lexico(p1.entrada);
        Sintaxis sintaxis = new Sintaxis(lexico);
        
        sintaxis.iniciarPrograma();

        if (!lexico.getErrA()) {
            System.out.println(p1.archivoE + ", COMPILO con EXITO!!!");
            String archivoS = p1.archivoE.substring(0, p1.archivoE.length() - 3) + "eje";
            try {
                File archivo = new File(archivoS);
                archivo.createNewFile();
                try (FileWriter write = new FileWriter(archivoS)) {
                    for (Map.Entry<String, String[]> tuplaXY : sintaxis.tablaSimb.entrySet()) {
                        String x = tuplaXY.getKey();
                        String[] y = tuplaXY.getValue();

                        write.write(x + ",");
                        write.write(y[0] + ",");
                        write.write(y[1] + ",");
                        write.write(y[2] + ",");
                        write.write(y[3] + ",");
                        write.write("#,\n");
                    }
                    write.write("@\n");
                    for (Map.Entry<Integer, String[]> tuplaXY : sintaxis.generadorPL0.getCodigoPL0().entrySet()) {
                        int x = tuplaXY.getKey();
                        String[] y = tuplaXY.getValue();

                        write.write(x + " ");
                        write.write(y[0] + " ");
                        write.write(y[1] + ", ");
                        write.write(y[2] + "\n");
                    }
                }
            } catch (IOException e) {
                System.out.println(p1.archivoE + " No existe, vuelve a intentarlo");
            }
        }
    }
}
