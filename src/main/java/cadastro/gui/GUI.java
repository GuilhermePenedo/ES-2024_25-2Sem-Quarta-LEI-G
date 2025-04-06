package cadastro.gui;

import cadastro.importer.Cadastro;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe que implementa a interface gráfica do sistema de gestão de propriedades.
 * Esta classe estende JFrame e fornece uma interface para importar, visualizar e gerenciar
 * cadastros de propriedades a partir de arquivos CSV.
 * 
 * @author [Lei-G]
 * @version 1.0
 */
public class GUI extends JFrame {

    private static final int DEFAULT_CADASTROS_LOAD = 5000;

    private final JTextField csvPathInput;
    private final JButton browseButton;
    private final JButton importButton;
    private final JButton showMore;
    private final JPanel resultsPanel;
    private int cadastrosResultPointer;
    private final List<JButton> sortButtons = new ArrayList<>();
    private List<Cadastro> cadastros;

    /**
     * Construtor da classe GUI.
     * Inicializa a interface gráfica com todos os componentes necessários,
     * incluindo campos para seleção de arquivo, botões de ação e painéis de resultados.
     */
    public GUI() {
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

        resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));

        showMore = new JButton("Mais");
        showMore.addActionListener(this::moreResults);

        JScrollPane scrollPane = new JScrollPane(resultsPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Adiciona componentes ao frame
        add(filePanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Configura listeners
        browseButton.addActionListener(this::browseFile);
        importButton.addActionListener(this::importCadastros);
    }

    /**
     * Abre um diálogo para seleção de arquivo CSV.
     * 
     * @param e O evento de ação que disparou o método
     */
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

    /**
     * Importa cadastros a partir do arquivo CSV selecionado.
     * Exibe mensagens de erro caso ocorra algum problema durante a importação.
     * 
     * @param e O evento de ação que disparou o método
     */
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
            JButton sortIdButtton = new JButton("Sort by ID");
            JButton sortLengthButtton = new JButton("Sort by Length");
            JButton sortAreaButtton = new JButton("Sort by Area");
            JButton sortOwnerButtton = new JButton("Sort by Owner");

            sortIdButtton.addActionListener(evento -> sortResults(evento, Cadastro.SORT_BY_ID));
            sortLengthButtton.addActionListener(evento -> sortResults(evento, Cadastro.SORT_BY_LENGTH));
            sortAreaButtton.addActionListener(evento -> sortResults(evento, Cadastro.SORT_BY_AREA));
            sortOwnerButtton.addActionListener(evento -> sortResults(evento, Cadastro.SORT_BY_OWNER));

            sortButtons.add(sortIdButtton);
            sortButtons.add(sortLengthButtton);
            sortButtons.add(sortAreaButtton);
            sortButtons.add(sortOwnerButtton);


            displayResults();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao importar: " + ex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    /**
     * Ordena os resultados de acordo com o critério especificado.
     * 
     * @param e O evento de ação que disparou o método
     * @param sortType O tipo de ordenação a ser aplicada (ID, comprimento, área ou proprietário)
     */
    private void sortResults(ActionEvent e, int sortType) {
        try {
            cadastros = Cadastro.sortCadastros(cadastros, sortType);
            displayResults();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao ordenar: " + ex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    /**
     * Exibe os resultados dos cadastros importados na interface.
     * Inclui botões de ordenação e os primeiros resultados.
     */
    private void displayResults() {
        resultsPanel.removeAll(); // Limpa resultados anteriores

        JPanel sortButtonsPanel = new JPanel();
        sortButtonsPanel.setLayout(new BoxLayout(sortButtonsPanel, BoxLayout.X_AXIS));
        for(JButton b : sortButtons){
            sortButtonsPanel.add(b);
        }

        resultsPanel.add(sortButtonsPanel);
        cadastrosResultPointer = 0;
        addResults();

        resultsPanel.add(showMore);

        resultsPanel.revalidate();
        resultsPanel.repaint();
    }

    /**
     * Carrega e exibe mais resultados quando o usuário clica no botão "Mais".
     * 
     * @param e O evento de ação que disparou o método
     */
    private void moreResults(ActionEvent e) {
        int toLoad = cadastrosResultPointer + DEFAULT_CADASTROS_LOAD;
        if (toLoad > cadastros.size()) {
            showMore.setEnabled(false);
        }

        resultsPanel.remove(showMore);

        addResults();

        resultsPanel.add(showMore);

        resultsPanel.revalidate();
        resultsPanel.repaint();
    }

    /**
     * Adiciona um lote de resultados ao painel de visualização.
     * O número de resultados carregados é limitado por DEFAULT_CADASTROS_LOAD.
     */
    private void addResults() {
        // Calculate how many to load
        int toLoad = Math.min(cadastrosResultPointer + DEFAULT_CADASTROS_LOAD, cadastros.size());

        // Add the cadastros to the results panel
        for (int i = cadastrosResultPointer; i < toLoad; i++) {
            showCadastroResult(cadastros.get(i));
        }

        cadastrosResultPointer = toLoad;
    }

    /**
     * Exibe as informações de um cadastro específico no painel de resultados.
     * 
     * @param cadastro O objeto Cadastro a ser exibido
     */
    private void showCadastroResult(Cadastro cadastro) {
        // Create components (don't immediately update the UI yet)
        JButton cadastroButton = new JButton("Mostrar shape");
        cadastroButton.addActionListener(_ -> showShapeWindow(cadastro));

        JPanel cardPanel = new JPanel(new BorderLayout());
        cardPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JLabel infoLabel = new JLabel(
                "<html>Id: " + cadastro.getId() +
                        "<br>Proprietário: " + cadastro.getOwner() +
                        "<br>Área: " + cadastro.getArea() +
                        "<br>Comprimento: " + cadastro.getLength() + "</html>");

        cardPanel.add(infoLabel, BorderLayout.CENTER);
        cardPanel.add(cadastroButton, BorderLayout.SOUTH);
        cardPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        // Add cardPanel to resultsPanel (don't revalidate/repaint yet)
        resultsPanel.add(cardPanel);
        resultsPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Espaço entre itens

        // Instead of calling revalidate and repaint after each result, we will do it once after a batch of results
        if (cadastrosResultPointer % DEFAULT_CADASTROS_LOAD == 0) {
            resultsPanel.revalidate(); // Revalidate once all results are added
            resultsPanel.repaint(); // Redraw after the update
        }
    }

    /**
     * Abre uma nova janela para visualização da forma geométrica do cadastro.
     * 
     * @param cadastro O objeto Cadastro cuja forma será exibida
     */
    private void showShapeWindow(Cadastro cadastro) {
        JFrame shapeFrame = new JFrame("Visualização da Shape - ID: " + cadastro.getId());
        shapeFrame.setSize(500, 500);
        shapeFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        ShapePanel shapePanel = new ShapePanel(cadastro.getShape());
        shapeFrame.add(shapePanel);

        shapeFrame.setVisible(true);
    }
}