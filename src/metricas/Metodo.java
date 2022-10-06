package metricas;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Metodo {
	private final String[] simbolos = {"auto", "extern", "register", "static", "typedef", "virtual", "mutable", "inline", "const", "friend", "volatile", "transient", "final", "break", "case", "continue", "default", "do", "if", "else", "enum", "for", "goto", "if", "new", "return", "asm", "operator", "private", "protected", "public", "sizeof", "struct", "switch", "union", "while", "this", "namespace", "using", "try", "catch", "throw", "throws", "finally", "strictfp", "instanceof", "interface", "extends", "implements", "abstract", "concrete", "const_cast", "static_cast", "dynamic_cast", "reinterpret_cast", "typeid", "template", "explicit", "true", "false", "typename", "!", "!=", "%", "%=", "&", "&&", "||", "&=", "(", ")", "{", "}", "[", "]", "*", "*=", "+", "++", "+=", ",", "-", "--", "-=->", ".", "...", "/", "/=", ":", "::", "<", "<<", "<<=", "<=", "=", "==", ">", ">=", ">>", ">>>", ">>=>>>=", "?", "^", "^=", "|", "|=", "~", ";", "=&", "#", "##", "~"};
	
	private String nombre;
	private String codigoOriginal;
	
	private ArrayList<String> codigo;
	private ArrayList<String> codigoLimpio;

	private int lineasCodigo = 0;
	private int lineasComentario = 0;
	private int lineasBlancas = 0;
	private int lineasReales = 0;
	private double porcentajeComentarios = 0f;
	private int fanIn = 0;
	private int fanOut = 0;
	private int complejidadCiclomatica = 0;

	private int halsteadLargo = 0;
	private int halsteadVocabulario = 0;
	private double halsteadVolumen = 0;
	
	public Metodo(String nombre, String codigo) {
		this.nombre = nombre;
		this.codigoOriginal = codigo;
		this.codigo = new ArrayList<String>();
		
		for(String linea: codigo.split("\n")) {
			this.codigo.add(linea.trim());
		}
	}
	
	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public ArrayList<String> getCodigoArray() {
		return codigo;
	}

	public String getCodigo() {
		return codigoOriginal;
	}

	public int getLineasCodigo() {
		return lineasCodigo;
	}
	
	public int getLineasReales() {
		return lineasReales;
	}

	public int getLineasBlancas() {
		return lineasBlancas;
	}
	
	public int getLineasComentario() {
		return lineasComentario;
	}
	
	public double getPorcentajeComentarios() {
		return porcentajeComentarios;
	}

	public int getFanIn() {
		return fanIn;
	}

	public int getFanOut() {
		return fanOut;
	}

	public int getComplejidadCiclomatica() {
		return complejidadCiclomatica;
	}
	
	public int getHalsteadLargo() {
		return halsteadLargo;
	}

	public int getHalsteadVocabulario() {
		return halsteadVocabulario;
	}

	public double getHalsteadVolumen() {
		return halsteadVolumen;
	}
	
	private void calcularLineas() {
		int contadorLineasReales = 0;
		int contadorComentarios = 0;
		int contadorBlancos = 0;
		int contadorLineasCodigo = 0;
		boolean comentarioMultiLinea = false;
		
		this.codigoLimpio = new ArrayList<String>();
		
		for(String linea: this.codigo) {
			contadorLineasReales++;
			
			if(linea.isEmpty()) {
				contadorBlancos++;
			} else { 
				contadorLineasCodigo++;
				
				if (linea.contains("/*")) {
					comentarioMultiLinea = true;
					contadorComentarios++;
				} else if (comentarioMultiLinea) {
					contadorComentarios++;
				} else if (linea.contains("//")) {
					contadorComentarios++;
				} else {
					this.codigoLimpio.add(linea);
				}
				
				if (linea.contains("*/")) {
					comentarioMultiLinea = false;
				}
			}
		}
		
		this.lineasCodigo = contadorLineasCodigo;
		this.lineasReales = contadorLineasReales;
		this.lineasBlancas = contadorBlancos;
		this.lineasComentario = contadorComentarios;
		
		if (contadorLineasCodigo > 0) {
			this.porcentajeComentarios = (float) contadorComentarios / contadorLineasCodigo;
		}
	}
	
	private void calcularHalstead() {
		ArrayList<String> operadores = new ArrayList<>();
		ArrayList<String> operandos = new ArrayList<>();
		HashSet<String> operadoresSinRepetidos;
		HashSet<String> operandosSinRepetidos = new HashSet<>();
		ArrayList<String> codigoSinCabecera = this.codigoLimpio;
		
		codigoSinCabecera.remove(0);
		codigoSinCabecera.remove(codigoSinCabecera.size()-1);
		
		for(String linea : codigoSinCabecera) {
			for(String token : linea.replace("(", " ( ").replace(")", " ) ").replace(",", " , ").split(" ")) {
				if(esOperador(token)) {
					operadores.add(token);
				} else {
					operandos.add(token);
				}
			}
		}
		
		operandos = limpiarOperandos(operandos);
		
		operadoresSinRepetidos =new HashSet<>(operadores);
		operandosSinRepetidos =new HashSet<>(operandos);
		
		int n1 = operadoresSinRepetidos.size();
		int N1 = operadores.size();
		int n2 = operandosSinRepetidos.size();
		int N2 = operandos.size();

		this.halsteadLargo = N1 + N2;
		this.halsteadVocabulario = n1 + n2;
		this.halsteadVolumen = this.halsteadLargo * log(this.halsteadVocabulario, 2);
		
	}
	
	private boolean esOperador(String o) {
		for(String s : this.simbolos) {
			if(s.equals(o)) {
				return true;
			}
		}
		
		return false;
	}
	
	private ArrayList<String> limpiarOperandos(ArrayList<String> operandos) {
		boolean esString = false;
		ArrayList<String> string = new ArrayList<>();
		ArrayList<String> limpio = new ArrayList<>();

		for(String operando : operandos) {
			if (operando.startsWith("\"") || operando.startsWith("\'")) {
				esString = true;
			}
						
			if (operando.endsWith("\"") || operando.endsWith("\'")) {
				string.add(operando);
				limpio.add(joinArrayString(string));
				string = new ArrayList<String>();
				esString = false;
			} else if (esString) {
				string.add(operando); 
			} else if (!operando.equals("(") && !operando.equals(")") && !operando.equals(",")) {
				limpio.add(operando);
			}
		}
		
		return limpio;
	}
	
	private String joinArrayString(ArrayList<String> a) {
		String j = null;
		
		for(String s : a) {
			j += s + " ";
		}
		
		return j.substring(0, j.length()-1); 
	}
	
	private double log(double x, int base)
	{
	    return Math.log(x) / Math.log(base);
	}
	
	private void calcularFanOut(List<Metodo> metodos) {
		Set<Metodo> metodosNoRepetidos = new HashSet<Metodo>(metodos);
		int contador = 0;
		
		for (String linea : this.codigo) {
			for (Metodo metodo : metodosNoRepetidos) {
				if (linea.contains(metodo.getNombre())) {
					contador++;
				}
			}
		}
		this.fanOut = contador - 1;
	}
	
	private void calcularFanIn(List<Metodo> metodos) {
		int contador = 0;

		for (Metodo metodo : metodos) {
			for (String linea : metodo.getCodigoArray()) {
				if (linea.contains(this.nombre)) {
					contador++;
				}
			}
		}

		this.fanIn = contador - 1;
	}

	private void calcularComplejidadCiclomatica() {
		int contador = 0;
		int comentarioMultiLinea = 0;
		int indice = 0;

		for (String linea : this.codigo) {
			if (linea.contains("/*")) {
				comentarioMultiLinea = 1;
				if (Pattern.matches(".*(if|while|switch).*(\\/\\*)", linea))
					contador++;
			}
			
			indice = 0;

			if (comentarioMultiLinea == 0) {
				if (Pattern.matches(".*(\\s*)if(\\s*)(\\(.*\\)).*", linea) && !Pattern.matches(".*(\\/\\/).*if.*", linea))
					contador++;

				if (Pattern.matches(".*(\\s*)while(\\s*)(\\(.*\\)).*", linea) && !Pattern.matches(".*(\\/\\/).*while.*", linea))
					contador++;
				
				if (Pattern.matches(".*(\\s*)switch(\\s*)(\\(.*\\)).*", linea) && !Pattern.matches(".*(\\/\\/).*switch.*", linea))
					contador++;
				
				if (Pattern.matches(".*(\\s*)for(\\s*)(\\(.*\\)).*", linea) && !Pattern.matches(".*(\\/\\/).*for.*", linea))
					contador++;
				
				indice = linea.indexOf("&&");
				while(indice != -1) {
					contador++;
					if(indice + 2 <= linea.length())
						indice += 2;
					if(linea.substring(indice).contains("&&")) {
						indice += linea.substring(indice).indexOf("&&");
					}
					else
						indice = -1;
				}
				
				indice = linea.indexOf("||");
				while(indice != -1) {
					contador++;
					if(indice + 2 <= linea.length())
						indice += 2;
					if(linea.substring(indice).contains("||")) {
						indice += linea.substring(indice).indexOf("||");
					}
					else
						indice = -1;
				}
				
			} else if (linea.contains("*/")) {
				comentarioMultiLinea = 0;
				if (Pattern.matches(".*(\\*\\/).*(if|while|switch).*", linea))
					contador++;
			}
		}
		
		this.complejidadCiclomatica = contador + 1;
	}

	public void calcular(List<Metodo> metodos) {
		this.calcularLineas();
		this.calcularFanIn(metodos);
		this.calcularFanOut(metodos);
		this.calcularComplejidadCiclomatica();
		this.calcularHalstead();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Metodo other = (Metodo) obj;
		if (nombre == null) {
			if (other.nombre != null)
				return false;
		} else if (!nombre.equals(other.nombre))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return this.getNombre();
	}

}
