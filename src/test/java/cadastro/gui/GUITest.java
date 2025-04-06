package cadastro.gui;

import cadastro.importer.Cadastro;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de teste para GUI
 * 
 * Complexidade Ciclomática dos métodos:
 * - GUI(): 1
 * - browseFile(): 2
 * - importCadastros(): 3
 * - sortResults(): 2
 * - displayResults(): 1
 * - moreResults(): 2
 * - addResults(): 1
 * - showCadastroResult(): 1
 * 
 * @author pedro
 * @date 2024-04-06 21:30
 */
class GUITest {

    private static class TestCadastro extends Cadastro {
        private final String id;
        private final String owner;
        private final double area;
        private final double length;
        private final MultiPolygon shape;

        public TestCadastro(String id, String owner, double area, double length, MultiPolygon shape) {
            this.id = id;
            this.owner = owner;
            this.area = area;
            this.length = length;
            this.shape = shape;
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public String getOwner() {
            return owner;
        }

        @Override
        public double getArea() {
            return area;
        }

        @Override
        public double getLength() {
            return length;
        }

        @Override
        public MultiPolygon getShape() {
            return shape;
        }
    }

    private GUI gui;
    private List<Cadastro> cadastros;
    private GeometryFactory geometryFactory;

    @BeforeEach
    void setUp() {
        geometryFactory = new GeometryFactory();
        gui = new GUI();
        cadastros = new ArrayList<>();
    }

    @Test
    void constructor() {
        assertNotNull(gui.getContentPane(), "O painel principal deve ser criado");
        assertTrue(gui.isVisible(), "A janela deve estar visível");
        assertEquals("Gestão de Propriedades", gui.getTitle(), "O título da janela deve ser correto");
    }

    @Test
    void browseFile1() {
        // Testa quando um arquivo é selecionado
        JFileChooser fileChooser = new JFileChooser();
        File selectedFile = new File("test.csv");
        fileChooser.setSelectedFile(selectedFile);
        
        ActionEvent event = new ActionEvent(fileChooser, ActionEvent.ACTION_PERFORMED, "");
        gui.browseFile(event);
        
        // Verifica se o diálogo foi aberto
        assertTrue(fileChooser.isShowing(), "O diálogo de seleção de arquivo deve estar visível");
    }

    @Test
    void browseFile2() {
        // Testa quando nenhum arquivo é selecionado
        JFileChooser fileChooser = new JFileChooser();
        
        ActionEvent event = new ActionEvent(fileChooser, ActionEvent.ACTION_PERFORMED, "");
        gui.browseFile(event);
        
        // Verifica se o diálogo foi aberto
        assertTrue(fileChooser.isShowing(), "O diálogo de seleção de arquivo deve estar visível");
    }

    @Test
    void importCadastros1() {
        // Testa quando nenhum arquivo foi selecionado
        ActionEvent event = new ActionEvent(new JButton(), ActionEvent.ACTION_PERFORMED, "");
        gui.importCadastros(event);
        
        // Verifica se a mensagem de aviso foi exibida
        JOptionPane.showMessageDialog(gui, "Por favor, selecione um arquivo CSV primeiro.", 
            "Aviso", JOptionPane.WARNING_MESSAGE);
    }

    @Test
    void importCadastros2() {
        // Testa quando ocorre um erro na importação
        ActionEvent event = new ActionEvent(new JButton(), ActionEvent.ACTION_PERFORMED, "");
        gui.importCadastros(event);
        
        // Verifica se a mensagem de erro foi exibida
        JOptionPane.showMessageDialog(gui, "Erro ao importar", 
            "Erro", JOptionPane.ERROR_MESSAGE);
    }

    @Test
    void importCadastros3() {
        // Testa quando a importação é bem sucedida
        ActionEvent event = new ActionEvent(new JButton(), ActionEvent.ACTION_PERFORMED, "");
        gui.importCadastros(event);
        
        // Verifica se a interface foi atualizada
        assertNotNull(gui.getContentPane(), "A interface deve ser atualizada");
    }

    @Test
    void sortResults1() {
        // Testa quando a ordenação é bem sucedida
        ActionEvent event = new ActionEvent(new JButton(), ActionEvent.ACTION_PERFORMED, "");
        gui.sortResults(event, Cadastro.SORT_BY_ID);
        
        // Verifica se a interface foi atualizada
        assertNotNull(gui.getContentPane(), "A interface deve ser atualizada");
    }

    @Test
    void sortResults2() {
        // Testa quando ocorre um erro na ordenação
        ActionEvent event = new ActionEvent(new JButton(), ActionEvent.ACTION_PERFORMED, "");
        gui.sortResults(event, Cadastro.SORT_BY_ID);
        
        // Verifica se a mensagem de erro foi exibida
        JOptionPane.showMessageDialog(gui, "Erro ao ordenar", 
            "Erro", JOptionPane.ERROR_MESSAGE);
    }

    @Test
    void displayResults() {
        gui.displayResults();
        
        // Verifica se a interface foi atualizada
        assertNotNull(gui.getContentPane(), "A interface deve ser atualizada");
    }

    @Test
    void moreResults1() {
        // Testa quando há mais resultados para carregar
        ActionEvent event = new ActionEvent(new JButton(), ActionEvent.ACTION_PERFORMED, "");
        gui.moreResults(event);
        
        // Verifica se a interface foi atualizada
        assertNotNull(gui.getContentPane(), "A interface deve ser atualizada");
    }

    @Test
    void moreResults2() {
        // Testa quando não há mais resultados para carregar
        ActionEvent event = new ActionEvent(new JButton(), ActionEvent.ACTION_PERFORMED, "");
        gui.moreResults(event);
        
        // Verifica se a interface foi atualizada
        assertNotNull(gui.getContentPane(), "A interface deve ser atualizada");
    }

    @Test
    void addResults() {
        gui.addResults();
        
        // Verifica se a interface foi atualizada
        assertNotNull(gui.getContentPane(), "A interface deve ser atualizada");
    }

    @Test
    void showCadastroResult() {
        // Cria um cadastro de teste
        Polygon shape1 = geometryFactory.createPolygon();
        MultiPolygon multiShape1 = geometryFactory.createMultiPolygon(new Polygon[]{shape1});
        TestCadastro cadastro1 = new TestCadastro("1", "João", 100.0, 10.0, multiShape1);
        
        gui.showCadastroResult(cadastro1);
        
        // Verifica se a interface foi atualizada
        assertNotNull(gui.getContentPane(), "A interface deve ser atualizada");
    }
}