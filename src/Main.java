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
    private JTextField nameField, phoneField, dateField, timeField, amountField;
    private JComboBox<String> serviceBox;
    private File dataFile = new File("barber_appointments.txt");

    BarberFrame() {
        setTitle("BarberApp - Barber Randevu Sistemi - by Calvin - Ankara");
        setSize(1050, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10,10));

        // TOP INPUT PANEL - NOW 6 COLUMNS
        JPanel inputPanel = new JPanel(new GridLayout(2, 6, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Yeni Randevu Ekle - Fiyatı Berber Girsin"));

        nameField = new JTextField();
        phoneField = new JTextField();
        dateField = new JTextField("14/10/2025");
        timeField = new JTextField("14:30");
        // FIXED: No fixed price in service name anymore
        serviceBox = new JComboBox<>(new String[]{"Sac Kesim", "Sakal", "Sac + Sakal", "Yikama", "Boyama", "Diger"});
        amountField = new JTextField(); // NEW FIELD
        amountField.setToolTipText("Örn: 350");

        inputPanel.add(new JLabel("Musteri Adi:")); 
        inputPanel.add(new JLabel("Telefon:"));
        inputPanel.add(new JLabel("Tarih:")); 
        inputPanel.add(new JLabel("Saat:")); 
        inputPanel.add(new JLabel("Hizmet:"));
        inputPanel.add(new JLabel("Odenen Tutar (TL):")); // NEW LABEL

        inputPanel.add(nameField); 
        inputPanel.add(phoneField); 
        inputPanel.add(dateField);
        inputPanel.add(timeField); 
        inputPanel.add(serviceBox);
        inputPanel.add(amountField); // NEW FIELD ADDED

        // BUTTONS
        JPanel buttonPanel = new JPanel();
        JButton addBtn = new JButton("Randevu Ekle +");
        JButton delBtn = new JButton("Secileni Sil");
        JButton clearBtn = new JButton("Temizle");
        JButton totalBtn = new JButton("Günlük Kazanç Hesapla");

        addBtn.setBackground(new Color(46, 204, 113)); addBtn.setForeground(Color.WHITE);
        delBtn.setBackground(new Color(231, 76, 60)); delBtn.setForeground(Color.WHITE);
        totalBtn.setBackground(new Color(52, 152, 219)); totalBtn.setForeground(Color.WHITE);

        buttonPanel.add(addBtn); buttonPanel.add(delBtn); buttonPanel.add(clearBtn); buttonPanel.add(totalBtn);

        JPanel north = new JPanel(new BorderLayout());
        north.add(inputPanel, BorderLayout.CENTER);
        north.add(buttonPanel, BorderLayout.SOUTH);
        add(north, BorderLayout.NORTH);

        // TABLE - NOW WITH TUTAR COLUMN
        model = new DefaultTableModel(new Object[]{"Musteri", "Telefon", "Tarih", "Saat", "Hizmet", "Tutar (TL)"}, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // ACTIONS
        addBtn.addActionListener(e -> addAppointment());
        delBtn.addActionListener(e -> deleteAppointment());
        clearBtn.addActionListener(e -> clearFields());
        totalBtn.addActionListener(e -> calculateTotal());

        loadData();
    }

    private void addAppointment() {
        if(nameField.getText().trim().isEmpty()){
            JOptionPane.showMessageDialog(this, "Musteri adi girin!");
            return;
        }
        if(amountField.getText().trim().isEmpty()){
            JOptionPane.showMessageDialog(this, "Lütfen ödenen tutarı girin! Örn: 350");
            return;
        }
        // Validate amount is number
        try {
            Integer.parseInt(amountField.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Tutar sadece rakam olmalı! Örn: 400");
            return;
        }

        model.addRow(new Object[]{
                nameField.getText().trim(),
                phoneField.getText().trim(),
                dateField.getText().trim(),
                timeField.getText().trim(),
                serviceBox.getSelectedItem(),
                amountField.getText().trim() + " TL"
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
        nameField.setText(""); phoneField.setText(""); dateField.setText(""); timeField.setText(""); amountField.setText("");
    }

    private void calculateTotal() {
        int total = 0;
        int count = 0;
        for(int i=0; i<model.getRowCount(); i++){
            try {
                String tutarStr = model.getValueAt(i,5).toString().replace(" TL","").replace("TL","").trim();
                total += Integer.parseInt(tutarStr);
                count++;
            } catch(Exception ex) {}
        }
        JOptionPane.showMessageDialog(this, "Toplam Randevu: " + count + "\nToplam Kazanç: " + total + " TL\n\nHarika iş abi! 💈💰");
    }

    private void saveData() {
        try (PrintWriter pw = new PrintWriter(dataFile)) {
            for(int i=0; i<model.getRowCount(); i++){
                pw.println(model.getValueAt(i,0)+";"+model.getValueAt(i,1)+";"+model.getValueAt(i,2)+";"+model.getValueAt(i,3)+";"+model.getValueAt(i,4)+";"+model.getValueAt(i,5));
            }
        } catch(Exception ex){ ex.printStackTrace(); }
    }

    private void loadData() {
        if(!dataFile.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(dataFile))) {
            String line;
            while((line = br.readLine()) != null){
                String[] p = line.split(";");
                if(p.length==5) {
                    // OLD DATA COMPATIBILITY: old file had 5 columns, add 0 TL
                    model.addRow(new Object[]{p[0], p[1], p[2], p[3], p[4], "0 TL"});
                } else if(p.length==6) {
                    model.addRow(p);
                }
            }
        } catch(Exception ex){ ex.printStackTrace(); }
    }
}
