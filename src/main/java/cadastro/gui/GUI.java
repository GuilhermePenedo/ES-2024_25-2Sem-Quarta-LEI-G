package cadastro.gui;

import cadastro.importer.Cadastro;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final Logger logger = LoggerFactory.getLogger(GUI.class);
    private static final int DEFAULT_CADASTROS_LOAD = 5000;

    // Declaração dos componentes da interface
    private final JTextField csvPathInput = new JTextField(20);
    private final JButton browseButton = new JButton("Procurar");
    private final JButton importButton = new JButton("Importar");
    private final JButton showMore = new JButton("Mais");
    private final JPanel resultsPanel = new JPanel();
    private int cadastrosResultPointer;
    private final List<JButton> sortButtons = new ArrayList<>();
    private List<Cadastro> cadastros;

    /**
     * Construtor da classe GUI.
     * Inicializa a interface gráfica com todos os componentes necessários,
     * incluindo campos para seleção de arquivo, botões de ação e painéis de resultados.
     * 
     * @throws RuntimeException se houver erro na inicialização da interface
     */
    public GUI() {
        try {
            setTitle("Gestão de Propriedades");
            setSize(800, 600);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLayout(new BorderLayout());

            initializeComponents();
            setupLayout();
            configureListeners();
            
            logger.info("Interface gráfica inicializada com sucesso");
        } catch (Exception e) {
            logger.error("Erro ao inicializar a interface gráfica: {}", e.getMessage());
            throw new RuntimeException("Falha na inicialização da interface gráfica", e);
        }
    }

    private void initializeComponents() {
        csvPathInput.setEditable(false);
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
    }

    private void setupLayout() {
        JPanel filePanel = new JPanel(new BorderLayout());
        JLabel fileLabel = new JLabel("Selecione o arquivo CSV:");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(browseButton);
        buttonPanel.add(importButton);

        filePanel.add(fileLabel, BorderLayout.WEST);
        filePanel.add(csvPathInput, BorderLayout.CENTER);
        filePanel.add(buttonPanel, BorderLayout.EAST);

        JScrollPane scrollPane = new JScrollPane(resultsPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(filePanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void configureListeners() {
        browseButton.addActionListener(this::browseFile);
        importButton.addActionListener(this::importCadastros);
        showMore.addActionListener(this::moreResults);
    }

    /**
     * Abre um diálogo para seleção de arquivo CSV.
     * 
     * @param e O evento de ação que disparou o método
     */
    private void browseFile(ActionEvent e) {
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int returnValue = fileChooser.showOpenDialog(this);

            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                if (!selectedFile.exists()) {
                    throw new IllegalArgumentException("Arquivo selecionado não existe");
                }
                csvPathInput.setText(selectedFile.getAbsolutePath());
                logger.info("Arquivo selecionado: {}", selectedFile.getAbsolutePath());
            }
        } catch (Exception ex) {
            logger.error("Erro ao selecionar arquivo: {}", ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    "Erro ao selecionar arquivo: " + ex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Importa cadastros a partir do arquivo CSV selecionado.
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
            logger.info("Iniciando importação de cadastros do arquivo: {}", path);
            cadastros = Cadastro.getCadastros(path);
            
            if (cadastros == null || cadastros.isEmpty()) {
                throw new IllegalStateException("Nenhum cadastro foi importado do arquivo");
            }

            initializeSortButtons();
            displayResults();
            logger.info("Importação concluída com sucesso. Total de cadastros: {}", cadastros.size());
        } catch (Exception ex) {
            logger.error("Erro ao importar cadastros: {}", ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    "Erro ao importar: " + ex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initializeSortButtons() {
        sortButtons.clear();
        String[] buttonLabels = {"Sort by ID", "Sort by Length", "Sort by Area", "Sort by Owner"};
        int[] sortTypes = {Cadastro.SORT_BY_ID, Cadastro.SORT_BY_LENGTH, Cadastro.SORT_BY_AREA, Cadastro.SORT_BY_OWNER};

        for (int i = 0; i < buttonLabels.length; i++) {
            JButton button = new JButton(buttonLabels[i]);
            final int sortType = sortTypes[i];
            button.addActionListener(evento -> sortResults(evento, sortType));
            sortButtons.add(button);
        }
    }

    /**
     * Ordena os resultados de acordo com o critério especificado.
     * 
     * @param e O evento de ação que disparou o método
     * @param sortType O tipo de ordenação a ser aplicada
     */
    private void sortResults(ActionEvent e, int sortType) {
        try {
            if (cadastros == null || cadastros.isEmpty()) {
                throw new IllegalStateException("Nenhum cadastro para ordenar");
            }

            logger.info("Iniciando ordenação por tipo: {}", sortType);
            cadastros = Cadastro.sortCadastros(cadastros, sortType);
            displayResults();
            logger.info("Ordenação concluída com sucesso");
        } catch (Exception ex) {
            logger.error("Erro ao ordenar cadastros: {}", ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    "Erro ao ordenar: " + ex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Exibe os resultados dos cadastros importados na interface.
     */
    private void displayResults() {
        try {
            resultsPanel.removeAll();
            addSortButtonsPanel();
            cadastrosResultPointer = 0;
            addResults();
            resultsPanel.add(showMore);
            resultsPanel.revalidate();
            resultsPanel.repaint();
            logger.debug("Resultados exibidos com sucesso");
        } catch (Exception ex) {
            logger.error("Erro ao exibir resultados: {}", ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    "Erro ao exibir resultados: " + ex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addSortButtonsPanel() {
        JPanel sortButtonsPanel = new JPanel();
        sortButtonsPanel.setLayout(new BoxLayout(sortButtonsPanel, BoxLayout.X_AXIS));
        for (JButton button : sortButtons) {
            sortButtonsPanel.add(button);
        }
        resultsPanel.add(sortButtonsPanel);
    }

    /**
     * Carrega e exibe mais resultados quando o usuário clica no botão "Mais".
     * 
     * @param e O evento de ação que disparou o método
     */
    private void moreResults(ActionEvent e) {
        try {
            if (cadastros == null || cadastros.isEmpty()) {
                throw new IllegalStateException("Nenhum cadastro para exibir");
            }

            int toLoad = cadastrosResultPointer + DEFAULT_CADASTROS_LOAD;
            if (toLoad > cadastros.size()) {
                showMore.setEnabled(false);
            }

            resultsPanel.remove(showMore);
            addResults();
            resultsPanel.add(showMore);
            resultsPanel.revalidate();
            resultsPanel.repaint();
            logger.debug("Mais resultados carregados. Total exibido: {}", cadastrosResultPointer);
        } catch (Exception ex) {
            logger.error("Erro ao carregar mais resultados: {}", ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar mais resultados: " + ex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Adiciona um lote de resultados ao painel de visualização.
     */
    private void addResults() {
        try {
            int toLoad = Math.min(cadastrosResultPointer + DEFAULT_CADASTROS_LOAD, cadastros.size());
            for (int i = cadastrosResultPointer; i < toLoad; i++) {
                showCadastroResult(cadastros.get(i));
            }
            cadastrosResultPointer = toLoad;
        } catch (Exception ex) {
            logger.error("Erro ao adicionar resultados: {}", ex.getMessage());
            throw new RuntimeException("Erro ao adicionar resultados ao painel", ex);
        }
    }

    /**
     * Exibe as informações de um cadastro específico no painel de resultados.
     * 
     * @param cadastro O objeto Cadastro a ser exibido
     */
    private void showCadastroResult(Cadastro cadastro) {
        try {
            if (cadastro == null) {
                throw new IllegalArgumentException("Cadastro não pode ser nulo");
            }

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

            resultsPanel.add(cardPanel);
            resultsPanel.add(Box.createRigidArea(new Dimension(0, 10)));

            if (cadastrosResultPointer % DEFAULT_CADASTROS_LOAD == 0) {
                resultsPanel.revalidate();
                resultsPanel.repaint();
            }
        } catch (Exception ex) {
            logger.error("Erro ao exibir cadastro: {}", ex.getMessage());
            throw new RuntimeException("Erro ao exibir cadastro no painel", ex);
        }
    }

    /**
     * Exibe uma janela com a forma geométrica do cadastro.
     * 
     * @param cadastro O cadastro cuja forma será exibida
     */
    private void showShapeWindow(Cadastro cadastro) {
        try {
            if (cadastro == null) {
                throw new IllegalArgumentException("Cadastro não pode ser nulo");
            }

            JFrame shapeFrame = new JFrame("Shape - " + cadastro.getId());
            shapeFrame.setSize(600, 600);
            shapeFrame.setLocationRelativeTo(this);

            ShapePanel shapePanel = new ShapePanel(cadastro.getShape());
            shapeFrame.add(shapePanel);

            shapeFrame.setVisible(true);
            logger.debug("Janela de shape exibida para o cadastro: {}", cadastro.getId());
        } catch (Exception ex) {
            logger.error("Erro ao exibir shape: {}", ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    "Erro ao exibir shape: " + ex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}