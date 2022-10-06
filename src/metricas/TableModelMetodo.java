package metricas;

import java.util.List;

import javax.swing.table.AbstractTableModel;

public class TableModelMetodo extends AbstractTableModel {
	private List<Metodo> data = null;
	
	protected TableModelMetodo() {
		super();
	}
	
	protected TableModelMetodo(List<Metodo> data) {
		super();
		this.data = data;
	}

	@Override
	public int getRowCount() {
		if (data != null)
			return data.size();
		
		return 0;
	}

	@Override
	public String getColumnName(int column) {
		switch (column) {
		case 0:
			return "Método";
		case 1:
			return "LReales";
		case 2:
			return "LCódigo";
		case 3:
			return "LBlancas";
		case 4:
			return "Comentarios";
		case 5: 
			return "%Comentarios";
		case 6:
			return "Fan In";
		case 7:
			return "Fan Out";
		case 8:
			return "CCiclomática";
		case 9:
			return "Longitud";
		case 10:
			return "Volumen";
		}

		return "???";
	}

	@Override
	public int getColumnCount() {
		return 11;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Metodo m = data.get(rowIndex);

		switch (columnIndex) {
		case 0:
			return m;
		case 1:
			return m.getLineasReales();
		case 2:
			return m.getLineasCodigo();
		case 3:
			return m.getLineasBlancas();
		case 4:
			return m.getLineasComentario();
		case 5:
			return m.getPorcentajeComentarios();
		case 6:
			return m.getFanIn();
		case 7:
			return m.getFanOut();
		case 8:
			return m.getComplejidadCiclomatica();
		case 9:
			return m.getHalsteadLargo();
		case 10:
			return m.getHalsteadVolumen();
		}

		return null;
	}
}
