package org.example;

import cadastro.importer.Cadastro;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {
        List< Cadastro> cadastros = Cadastro.getCadastros("Dados/Madeira-Moodle-1.1.csv");
        for(Cadastro cadastro : cadastros){
            System.out.println(cadastro);
        }

    }
}
