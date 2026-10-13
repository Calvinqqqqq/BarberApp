import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BarberFrame frame = new BarberFrame();
            frame.setVisible(true);
        });
    }
}

class BarberFrame extends JFrame {
    private DefaultTableModel model;
    private JTable table;
    private JTextField nameField, phoneField, dateField, timeField;
    private JComboBox<String> serviceBox;
    private File dataFile = new File("barber_appointments.txt");

    BarberFrame() {
        setTitle("BarberApp - Barber Randevu Sistemi - by Calvin");
        setSize(850, 550);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10,10));

        // TOP INPUT PANEL
        JPanel inputPanel = new JPanel(new GridLayout(2, 5, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Yeni Randevu Ekle"));

        nameField = new JTextField();
        phoneField = new JTextField();
        dateField = new JTextField("13/10/2025");
        timeField = new JTextField("14:30");
        serviceBox = new JComboBox<>(new String[]{"Sac Kesim - 300 TL", "Sakal - 150 TL", "Sac + Sakal - 400 TL", "Yikama - 100 TL", "Boyama"});

        inputPanel.add(new JLabel("Musteri Adi:")); inputPanel.add(new JLabel("Telefon:"));
        inputPanel.add(new JLabel("Tarih:")); inputPanel.add(new JLabel("Saat:")); inputPanel.add(new JLabel("Hizmet:"));

        inputPanel.add(nameField); inputPanel.add(phoneField); inputPanel.add(dateField);
        inputPanel.add(timeField); inputPanel.add(serviceBox);

        // BUTTONS
        JPanel buttonPanel = new JPanel();
        JButton addBtn = new JButton("Randevu Ekle +");
        JButton delBtn = new JButton("Secileni Sil");
        JButton clearBtn = new JButton("Temizle");

        addBtn.setBackground(new Color(46, 204, 113)); addBtn.setForeground(Color.WHITE);
        delBtn.setBackground(new Color(231, 76, 60)); delBtn.setForeground(Color.WHITE);

        buttonPanel.add(addBtn); buttonPanel.add(delBtn); buttonPanel.add(clearBtn);

        JPanel north = new JPanel(new BorderLayout());
        north.add(inputPanel, BorderLayout.CENTER);
        north.add(buttonPanel, BorderLayout.SOUTH);
        add(north, BorderLayout.NORTH);

        // TABLE
        model = new DefaultTableModel(new Object[]{"Musteri", "Telefon", "Tarih", "Saat", "Hizmet"}, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // ACTIONS
        addBtn.addActionListener(e -> addAppointment());
        delBtn.addActionListener(e -> deleteAppointment());
        clearBtn.addActionListener(e -> clearFields());

        loadData();
    }

    private void addAppointment() {
        if(nameField.getText().trim().isEmpty()){
            JOptionPane.showMessageDialog(this, "Musteri adi girin!");
            return;
        }
        model.addRow(new Object[]{
                nameField.getText().trim(),
                phoneField.getText().trim(),
                dateField.getText().trim(),
                timeField.getText().trim(),
                serviceBox.getSelectedItem()
        });
        saveData();
        clearFields();
    }

    private void deleteAppointment() {
        int row = table.getSelectedRow();
        if(row >= 0){ model.removeRow(row); saveData(); }
        else { JOptionPane.showMessageDialog(this, "Silmek icin listeden secin!"); }
    }

    private void clearFields() {
        nameField.setText(""); phoneField.setText(""); dateField.setText(""); timeField.setText("");
    }

    private void saveData() {
        try (PrintWriter pw = new PrintWriter(dataFile)) {
            for(int i=0; i<model.getRowCount(); i++){
                pw.println(model.getValueAt(i,0)+";"+model.getValueAt(i,1)+";"+model.getValueAt(i,2)+";"+model.getValueAt(i,3)+";"+model.getValueAt(i,4));
            }
        } catch(Exception ex){ ex.printStackTrace(); }
    }

    private void loadData() {
        if(!dataFile.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(dataFile))) {
            String line;
            while((line = br.readLine()) != null){
                String[] p = line.split(";");
                if(p.length==5) model.addRow(p);
            }
        } catch(Exception ex){ ex.printStackTrace(); }
    }
}