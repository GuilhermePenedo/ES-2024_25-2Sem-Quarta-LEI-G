package org.example;

import cadastro.importer.Cadastro;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        logger.info("Iniciando aplicação");
        
        String filePath = "Dados/Madeira-Moodle-1.1.csv";
        logger.info("Carregando cadastros do arquivo: {}", filePath);
        
        List<Cadastro> cadastros = Cadastro.getCadastros(filePath);
        logger.info("Total de cadastros carregados: {}", cadastros.size());
        
        cadastros.forEach(cadastro -> logger.info("Cadastro: {}", cadastro));
        
        logger.info("Aplicação finalizada com sucesso");
    }
}
