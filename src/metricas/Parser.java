package metricas;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Parser {
	public static List<Metodo> getMetodos (String archivoJava) {
		List<Metodo> funciones = new ArrayList<Metodo>();
		
		File archivo = new File(archivoJava);
		BufferedReader br = null;
		FileReader fr = null;
		
		try {
            fr = new FileReader(archivo);
            br = new BufferedReader(fr);
            String linea;
            //String lineaOriginal;
            
			while ((linea = br.readLine()) != null) {
				//lineaOriginal = linea; 
				linea = linea.trim();
				
                if (linea.startsWith("/*")) {
                    while (!linea.endsWith("*/") && ((linea = br.readLine().trim()) != null)) {
                    }
                    
                    continue;
                }
                
                if (linea.startsWith("//") || linea.equals("") || linea.startsWith("@")) {
                    continue;
                }
                
                if (linea.endsWith("\n")) {
                	linea = linea.substring(0, linea.length() - 1);
                }
                
                while (linea.startsWith("\n")) {
                	linea = linea.substring(1, linea.length());
                }
                
                if (esCabeceraDeMetodo(linea)) {
                    String nombre = getNombreMetodo(linea);
                    String codigo = linea + "\n";
                    
                    if (!linea.endsWith(";")) {
                        int contadorLlaves = 0;
                        
                        if(linea.contains("{"))	{
                        	contadorLlaves++;
                        }
                        
                        while (((linea = br.readLine()) != null)) {
                            codigo += linea + "\n";
                            linea = linea.trim();
                            
                            if(linea.contains("{"))	{
                            	contadorLlaves++;
                            }
                            
                            if(linea.contains("}")) {
                            	contadorLlaves--;
                            }
                            
                            if (contadorLlaves == 0) {
                            	break;
                            }
                        }
                        
                        codigo = codigo.substring(0, codigo.length() - 1);
                    }
                 
                    funciones.add(new Metodo(nombre, codigo));
                }
                 
			}
		} catch (Exception ex) {
			System.out.println(ex);
		} finally {
            try {
                fr.close();
            } catch (IOException ex) {
            	System.err.println(ex);
            }
		}
		
		return funciones;
	}
	
	private static String getNombreMetodo(String linea) {
        String anterior = "";
        String[] cabecera = linea.split(" ");
        
	    for (String elemento : cabecera) {
	    	elemento = elemento.trim();
	    	
	    	if (elemento.contains("(")) {
	    		if(elemento.substring(0, elemento.indexOf("(")).isEmpty())
	    			return anterior;
	    		else
	    			return elemento.substring(0, elemento.indexOf("("));
	    	}
	    	
	    	anterior = elemento;
	    }
	    
	    return null;
	}
	
	private static boolean esCabeceraDeMetodo(String linea) {
		if (linea.equals("")) {
        	return false;
        }
        
        if (linea.startsWith("//")) {
        	return false;
        }

        if (linea.endsWith("}")) {
        	return false;
        }
        
        if (linea.contains("class")) {
        	return false;
        }
        
		if ((linea.contains("public") || linea.contains("private") || linea.contains("protected")) && linea.contains("(")) {
			return true;
		}
		
		return false;
    }
}
