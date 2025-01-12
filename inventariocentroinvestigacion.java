import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.*;

// Clase principal
public class    inventariocentroinvestigacion {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Centro::new);
    }
}

// Clase para la interfaz de registro de equipos
class Centro extends JFrame {
    private JTextField descField, ctField, muField, fechaField, nfField, ciField;
    private JButton agregarBtn, reporteBtn, salirBtn;
    private static final String ARCHIVO_INVENTARIO = "inventario.txt";

    public Centro() {
        setTitle("Registro y Control de Equipos");
        setLayout(new BorderLayout());

        // Panel principal
        JPanel panelCampos = new JPanel(new GridLayout(6, 2, 10, 10));
        panelCampos.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Campos de entrada
        panelCampos.add(new JLabel("Descripción:"));
        descField = new JTextField();
        panelCampos.add(descField);

        panelCampos.add(new JLabel("Cantidad:"));
        ctField = new JTextField();
        panelCampos.add(ctField);

        panelCampos.add(new JLabel("Costo unitario (Bs):"));
        muField = new JTextField();
        panelCampos.add(muField);

        panelCampos.add(new JLabel("Fecha de adquisición (dd/mm/aaaa):"));
        fechaField = new JTextField();
        panelCampos.add(fechaField);

        panelCampos.add(new JLabel("Número de factura:"));
        nfField = new JTextField();
        panelCampos.add(nfField);

        panelCampos.add(new JLabel("C.I. del responsable:"));
        ciField = new JTextField();
        panelCampos.add(ciField);

        add(panelCampos, BorderLayout.CENTER);

        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout());
        agregarBtn = new JButton("Registrar Datos");
        agregarBtn.addActionListener(e -> agregarInventario());
        panelBotones.add(agregarBtn);

        reporteBtn = new JButton("Generar Reporte");
        reporteBtn.addActionListener(e -> new Reporte());
        panelBotones.add(reporteBtn);

        salirBtn = new JButton("Salir");
        salirBtn.addActionListener(e -> System.exit(0));
        panelBotones.add(salirBtn);

        add(panelBotones, BorderLayout.SOUTH);

        // Configuración de la ventana
        setSize(450, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void agregarInventario() {
        try {
            String desc = descField.getText().trim();
            int ct = Integer.parseInt(ctField.getText().trim());
            double mu = Double.parseDouble(muField.getText().trim());
            String fecha = fechaField.getText().trim();
            String nf = nfField.getText().trim();
            String ci = ciField.getText().trim();

            // Validaciones
            if (desc.isEmpty() || fecha.isEmpty() || nf.isEmpty() || ci.isEmpty()) {
                throw new IllegalArgumentException("Todos los campos deben estar llenos.");
            }

            if (!esFechaValida(fecha)) {
                throw new IllegalArgumentException("La fecha debe tener el formato dd/mm/aaaa.");
            }

            // Guardar en archivo
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARCHIVO_INVENTARIO, true))) {
                writer.write(desc + "#" + ct + "#" + mu + "#" + fecha + "#" + nf + "#" + ci + "\n");
            }

            JOptionPane.showMessageDialog(this, "Equipo registrado correctamente.");
            limpiarCampos();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Error: Cantidad y costo unitario deben ser valores numéricos.");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar en el archivo: " + ex.getMessage());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private boolean esFechaValida(String fecha) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            sdf.setLenient(false);
            sdf.parse(fecha);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void limpiarCampos() {
        descField.setText("");
        ctField.setText("");
        muField.setText("");
        fechaField.setText("");
        nfField.setText("");
        ciField.setText("");
    }
}

// Clase para la generación de reportes
class Reporte extends JFrame {
    private JTextArea reporteArea;
    private JButton individualBtn, generalBtn, salirBtn;
    private JTextField ciField;
    private static final String ARCHIVO_INVENTARIO = "inventario.txt";

    public Reporte() {
        setTitle("Generar Reporte");
        setLayout(new BorderLayout());

        // Panel superior
        JPanel panelSuperior = new JPanel(new GridLayout(2, 2, 10, 10));
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panelSuperior.add(new JLabel("C.I. Responsable:"));
        ciField = new JTextField();
        panelSuperior.add(ciField);

        individualBtn = new JButton("Reporte Individual");
        individualBtn.addActionListener(e -> generarReporteIndividual());
        panelSuperior.add(individualBtn);

        generalBtn = new JButton("Reporte General");
        generalBtn.addActionListener(e -> generarReporteGeneral());
        panelSuperior.add(generalBtn);

        add(panelSuperior, BorderLayout.NORTH);

        // Area de texto para reportes
        reporteArea = new JTextArea();
        reporteArea.setEditable(false);
        add(new JScrollPane(reporteArea), BorderLayout.CENTER);

        // Panel inferior con botón de salir
        JPanel panelInferior = new JPanel(new FlowLayout());
        salirBtn = new JButton("Cerrar");
        salirBtn.addActionListener(e -> dispose());
        panelInferior.add(salirBtn);

        add(panelInferior, BorderLayout.SOUTH);

        setSize(500, 400);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void generarReporteIndividual() {
        String ci = ciField.getText().trim();
        if (ci.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese un C.I. válido.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(ARCHIVO_INVENTARIO))) {
            String linea;
            int totalEquipos = 0;
            double totalCosto = 0.0;

            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split("#");
                if (datos[5].equals(ci)) {
                    int cantidad = Integer.parseInt(datos[1]);
                    double costoUnitario = Double.parseDouble(datos[2]);
                    totalEquipos += cantidad;
                    totalCosto += cantidad * costoUnitario;
                }
            }

            if (totalEquipos == 0) {
                reporteArea.setText("No se encontraron equipos para el responsable con C.I.: " + ci);
            } else {
                reporteArea.setText("Reporte para C.I.: " + ci + "\n" +
                        "Total equipos: " + totalEquipos + "\n" +
                        "Monto total: " + totalCosto + " Bs.");
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error al leer el archivo: " + ex.getMessage());
        }
    }

    private void generarReporteGeneral() {
        Map<String, Integer> equiposPorCI = new HashMap<>();
        Map<String, Double> costoPorCI = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(ARCHIVO_INVENTARIO))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split("#");
                String ci = datos[5];
                int cantidad = Integer.parseInt(datos[1]);
                double costoUnitario = Double.parseDouble(datos[2]);

                equiposPorCI.put(ci, equiposPorCI.getOrDefault(ci, 0) + cantidad);
                costoPorCI.put(ci, costoPorCI.getOrDefault(ci, 0.0) + cantidad * costoUnitario);
            }

            StringBuilder reporte = new StringBuilder("Reporte General:\n");
            for (String ci : equiposPorCI.keySet()) {
                reporte.append("C.I.: ").append(ci)
                        .append(" - Total Equipos: ").append(equiposPorCI.get(ci))
                        .append(" - Monto Total: ").append(costoPorCI.get(ci)).append(" Bs.\n");
            }

            reporteArea.setText(reporte.toString());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error al leer el archivo: " + ex.getMessage());
        }
    }
}
