# ES-2024_25-2Sem-Quarta-LEI-G
Projeto de Engenharia de Software 2024/25

## Descrição do Projeto
Este projeto é um sistema de gerenciamento de cadastros imobiliários que processa dados geográficos a partir de arquivos CSV. O sistema utiliza tecnologias modernas para processamento de dados geoespaciais e logging robusto.

### Funcionalidades Principais
- Importação de dados cadastrais de arquivos CSV
- Processamento de geometrias MultiPolygon usando JTS (Java Topology Suite)
- Sistema de logging completo com output para console e arquivo
- Validação de dados geométricos

### Tecnologias Utilizadas
- Java 24
- Apache Commons CSV para processamento de arquivos CSV
- JTS (Java Topology Suite) para manipulação de dados geométricos
- SLF4J + Logback para logging

### Estrutura do Projeto
```
src/main/
├── java/
│   ├── cadastro/importer/
│   │   └── Cadastro.java
│   └── org/example/
│       └── Main.java
└── resources/
    └── logback.xml
```

### Como Executar
1. Certifique-se de ter Java 24 instalado
2. Clone o repositório
3. Execute o projeto usando Maven:
   ```bash
   mvn clean install
   mvn exec:java -Dexec.mainClass="org.example.Main"
   ```

### Logs
O sistema gera logs detalhados que podem ser encontrados em:
- Console: Para monitoramento em tempo real
- Arquivo: `logs/cadastro.log` (novo arquivo a cada execução)

## Equipe
- Grupo G
- Disciplina: Engenharia de Software
- Semestre: 2º Semestre 2024/25
