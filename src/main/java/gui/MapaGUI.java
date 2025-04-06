package gui;

import cadastro.importer.Cadastro;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;

public class MapaGUI extends JFrame {

    private final JTextField csvPathInput;
    private final JButton browseButton;
    private final JButton importButton;
    private final JPanel resultsPanel;
    private List<Cadastro> cadastros;

    public MapaGUI() {
        setTitle("Gestão de Propriedades");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Painel superior com seleção de arquivo
        JPanel filePanel = new JPanel(new BorderLayout());
        JLabel fileLabel = new JLabel("Selecione o arquivo CSV:");
        csvPathInput = new JTextField(20);
        csvPathInput.setEditable(false);
        browseButton = new JButton("Procurar");
        importButton = new JButton("Importar");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(browseButton);
        buttonPanel.add(importButton);

        filePanel.add(fileLabel, BorderLayout.WEST);
        filePanel.add(csvPathInput, BorderLayout.CENTER);
        filePanel.add(buttonPanel, BorderLayout.EAST);

        // Painel de resultados com scroll
        resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(resultsPanel);

        // Adiciona componentes ao frame
        add(filePanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Configura listeners
        browseButton.addActionListener(this::browseFile);
        importButton.addActionListener(this::importCadastros);

        JButton viewMapButton = new JButton("Visualizar Mapa");
        buttonPanel.add(viewMapButton);
        viewMapButton.addActionListener(this::viewPropertiesMap);
    }

    private void browseFile(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int returnValue = fileChooser.showOpenDialog(this);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            csvPathInput.setText(selectedFile.getAbsolutePath());
        }
    }

    private void importCadastros(ActionEvent e) {
        String path = csvPathInput.getText();
        if (path.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, selecione um arquivo CSV primeiro.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            cadastros = Cadastro.getCadastros(path);
            displayResults();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao importar: " + ex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void displayResults() {
        resultsPanel.removeAll(); // Limpa resultados anteriores

        for (Cadastro cadastro : cadastros) {
            JButton cadastroButton = new JButton("Cadastro ID: " + cadastro.getId());
            cadastroButton.addActionListener(e -> showShapeWindow(cadastro));

            JPanel cardPanel = new JPanel(new BorderLayout());
            cardPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

            JLabel infoLabel = new JLabel(
                    "<html>Proprietário: " + cadastro.getOwner() +
                            "<br>Área: " + cadastro.getArea() +
                            "<br>Comprimento: " + cadastro.getLength() + "</html>");

            cardPanel.add(infoLabel, BorderLayout.CENTER);
            cardPanel.add(cadastroButton, BorderLayout.SOUTH);
            cardPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

            resultsPanel.add(cardPanel);
            resultsPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Espaço entre itens
        }

        resultsPanel.revalidate();
        resultsPanel.repaint();
    }

    private void showShapeWindow(Cadastro cadastro) {
        JFrame shapeFrame = new JFrame("Visualização da Shape - ID: " + cadastro.getId());
        shapeFrame.setSize(500, 500);
        shapeFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        ShapePanel shapePanel = new ShapePanel(cadastro.getShape());
        shapeFrame.add(shapePanel);

        shapeFrame.setVisible(true);
    }

    private void viewPropertiesMap(ActionEvent e) {
        if (cadastros == null || cadastros.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, importe os dados CSV primeiro.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFrame mapFrame = new JFrame("Mapa de Propriedades");
        mapFrame.setSize(1200, 800);
        mapFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        MapPanel mapPanel = new MapPanel(cadastros);
        mapFrame.add(new JScrollPane(mapPanel));

        mapFrame.setVisible(true);
    }
}