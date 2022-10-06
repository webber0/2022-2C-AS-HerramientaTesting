package metricas;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JList;
import java.io.File;
import java.io.FileFilter;
import java.text.Format;
import java.text.NumberFormat;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.filechooser.FileSystemView;
import javax.swing.ListSelectionModel;
import javax.swing.JTextArea;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.SystemColor;
import javax.swing.ImageIcon;
import java.awt.Toolkit;
import javax.swing.SwingConstants;
import java.awt.Font;
import javax.swing.JTextPane;
import java.awt.Color;
import java.awt.FlowLayout;

public class FormMetricas extends JFrame {
	private JPanel contentPane;
	private DefaultListModel<File> modelList;
	private JSplitPane splitPane1;
	private TableModelMetodo modelTable;
	private JSplitPane splitPane2;
	private JScrollPane scrlMetricas;
	private JTable tblMetricas;
	private JScrollPane scrlCodigo;
	private JTextArea txtCodigo;
	private JPanel panel;
	private JScrollPane scrlArchivos;
	private JList<File> lstArchivos;
	private JLabel lblNewLabel;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.getDefaults().put("SplitPane.border", BorderFactory.createEmptyBorder());
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					FormMetricas frame = new FormMetricas();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public FormMetricas() {
		setIconImage(Toolkit.getDefaultToolkit().getImage(FormMetricas.class.getResource("/images/testing.png")));
		setTitle("Métricas de código");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 958, 569);
		setLocationRelativeTo(null);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		splitPane1 = new JSplitPane();
		splitPane1.setResizeWeight(0.35);
		contentPane.add(splitPane1);

		splitPane2 = new JSplitPane();
		splitPane2.setResizeWeight(0.65);
		splitPane2.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane1.setRightComponent(splitPane2);


		scrlMetricas = new JScrollPane();
		scrlMetricas.setPreferredSize(new Dimension(0, 0));
		splitPane2.setLeftComponent(scrlMetricas);

		modelTable = new TableModelMetodo(null);
		tblMetricas = new JTable(modelTable);
		tblMetricas.setFont(new Font("Courier New", Font.PLAIN, 12));
		tblMetricas.setGridColor(SystemColor.control);
		tblMetricas.setRowHeight(25);
		//tblMetricas.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tblMetricas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		iniciarTabla();
		
		tblMetricas.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				mostrarCodigo();
			}
		});

		//"RLOC: Lineas (lineas de código + lineas blancas)\n LOC: Lineas de código\n BLOC: Lineas blancas\n COM: comentarios\n %COM: porcentaje de comentarios\n FIN: Fan In\n FOUT: Fan Out\n NCC: Complejidad ciclomática\n HN: Longitud\n HV: Volumen\n HE: Esfuerzo"
		scrlMetricas.setViewportView(tblMetricas);

		scrlCodigo = new JScrollPane();
		scrlCodigo.setPreferredSize(new Dimension(0, 0));
		splitPane2.setRightComponent(scrlCodigo);

		txtCodigo = new JTextArea();
		txtCodigo.setFont(new Font("Courier New", Font.PLAIN, 12));
		txtCodigo.setTabSize(2);
		txtCodigo.setEditable(false);
		txtCodigo.setLineWrap(true);
		scrlCodigo.setViewportView(txtCodigo);

		panel = new JPanel();
		panel.setPreferredSize(new Dimension(0, 0));
		splitPane1.setLeftComponent(panel);
		panel.setLayout(new BorderLayout(0, 5));
		
				JButton btnSeleccionarCarpeta = new JButton("Seleccionar carpeta...");
				btnSeleccionarCarpeta.setIcon(new ImageIcon(FormMetricas.class.getResource("/images/folder.png")));
				btnSeleccionarCarpeta.setPreferredSize(new Dimension(0, 30));
				panel.add(btnSeleccionarCarpeta, BorderLayout.NORTH);
				
						btnSeleccionarCarpeta.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent arg0) {
								buscarCarpeta();
							}
						});

		scrlArchivos = new JScrollPane();
		scrlArchivos.setPreferredSize(new Dimension(0, 0));
		panel.add(scrlArchivos);

		modelList = new DefaultListModel<File>();
		lstArchivos = new JList<File>(modelList);
		lstArchivos.setCellRenderer(new FileRenderer(true));
		
		lstArchivos.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				File file = lstArchivos.getSelectedValue();
				
				if (file != null) {
					cargarMetodos(file.toString());
				}
			}
		});
		
		scrlArchivos.setViewportView(lstArchivos);
		
		lblNewLabel = new JLabel("Archivos");
		scrlArchivos.setColumnHeaderView(lblNewLabel);
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
	}

	private void iniciarTabla() {
		TableColumnModel tcm = tblMetricas.getColumnModel();
		TableColumn tc;

		tc = tcm.getColumn(0);
		tc.setPreferredWidth(200);

		tc = tcm.getColumn(1);
		//tc.setPreferredWidth(60);
		tc.setCellRenderer(new NumberCellRenderer());

		tc = tcm.getColumn(2);
		//tc.setPreferredWidth(60);
		tc.setCellRenderer(new NumberCellRenderer());

		tc = tcm.getColumn(3);
		//tc.setPreferredWidth(60);
		tc.setCellRenderer(new NumberCellRenderer());

		tc = tcm.getColumn(4);
		//tc.setPreferredWidth(60);
		tc.setCellRenderer(new NumberCellRenderer());

		tc = tcm.getColumn(5);
		//tc.setPreferredWidth(60);
		tc.setCellRenderer(new PercentCellRenderer());

		tc = tcm.getColumn(6);
		//tc.setPreferredWidth(60);
		tc.setCellRenderer(new NumberCellRenderer());
		
		tc = tcm.getColumn(7);
		//tc.setPreferredWidth(60);
		tc.setCellRenderer(new NumberCellRenderer());
		
		tc = tcm.getColumn(8);
		//tc.setPreferredWidth(60);
		tc.setCellRenderer(new NumberCellRenderer());
	
		tc = tcm.getColumn(9);
		//tc.setPreferredWidth(60);
		tc.setCellRenderer(new NumberCellRenderer());
		
		tc = tcm.getColumn(10);
		//tc.setPreferredWidth(60);
		tc.setCellRenderer(new NumberCellRenderer());
		
	}

	private void buscarCarpeta() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			modelList.clear();
			txtCodigo.setText("");
			
			cargarArchivos(fileChooser.getSelectedFile());
		}
	}
	
	private void cargarArchivos(File directory) {
		for (File file : directory.listFiles(new JavaFilter())) {
			/*
			if(file.isDirectory()) {
				cargarArchivos(file);
			} else {
				modelList.addElement(file);
			}
			*/
			if(!file.isDirectory()) {
				modelList.addElement(file);
			}
		}
	}
	
	public class JavaFilter implements FileFilter {
		public boolean accept(File file) {
			if(file.isDirectory()) {
				return true;
			}
			
			if (file.getName().toLowerCase().endsWith(".java")) {
				return true;
			}

			return false;
		}
	}

	class FileRenderer extends DefaultListCellRenderer {

	    private boolean pad;
	    private Border padBorder = new EmptyBorder(3,3,3,3);

	    FileRenderer(boolean pad) {
	        this.pad = pad;
	    }

	    @Override
	    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

	        Component c = super.getListCellRendererComponent(list,value,index,isSelected,cellHasFocus);
	        JLabel l = (JLabel)c;
	        File f = (File)value;
	        l.setText(f.getName());
	        l.setIcon(FileSystemView.getFileSystemView().getSystemIcon(f));
	        
	        if (pad) {
	            l.setBorder(padBorder);
	        }

	        return l;
	    }
	}
	
	private void cargarMetodos(String archivo) {
		List<Metodo> metodos = Parser.getMetodos(archivo);
		
		for(Metodo m: metodos) {
			m.calcular(metodos);
		}
		
		modelTable = new TableModelMetodo(metodos);
		tblMetricas.setModel(modelTable);
		txtCodigo.setText("");
		iniciarTabla();
	}

	private void mostrarCodigo() {
		int row = tblMetricas.getSelectedRow();
		
		if (row != -1) {
			Metodo m = (Metodo) tblMetricas.getValueAt(row, 0);
			txtCodigo.setText(m.getCodigo());
			txtCodigo.setCaretPosition(0);			
		}
	}
	
	public class NumberCellRenderer extends DefaultTableCellRenderer {
		Format nf = NumberFormat.getNumberInstance();
		
	    public NumberCellRenderer() {
	    	super();
	    	setHorizontalAlignment(JLabel.RIGHT);
	    }
	    
		public void setValue(Object value)
		{
			try
			{
				if (value != null)
					value = nf.format(value);
			}
			catch(IllegalArgumentException e) {}

			super.setValue(value);
		}
	}
	
	public class PercentCellRenderer extends DefaultTableCellRenderer {
		Format nf = NumberFormat.getPercentInstance();
		
	    public PercentCellRenderer() {
	    	super();
	    	setHorizontalAlignment(JLabel.RIGHT);
	    }
	    
		public void setValue(Object value)
		{
			try
			{
				if (value != null)
					value = nf.format(value);
			}
			catch(IllegalArgumentException e) {}

			super.setValue(value);
		}
	}
}
