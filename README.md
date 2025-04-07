# ES-2024_25-2Sem-Quarta-LEI-G
Projeto de Engenharia de Software 2024/25

## Identificação da Equipa
- Grupo G
- Disciplina: Engenharia de Software
- Semestre: 2º Semestre 2024/25

| Nome               | Número de Estudante | GitHub Username   |
|--------------------|---------------------|-------------------|
| [Guilherme Penedo] | [111222]            | [GuilhermePenedo] |
| [Pedro Pacheco]    | [111039]            | [pcp2003]         |
| [João Antunes]     | [111139]            | [deavenZ]         |
| [Rafael Lopes]]    | [111110]            | [tranquilizante]  |


## Descrição do Projeto
O projeto consiste num sistema de gerenciamento de cadastros imobiliários que processa dados geográficos a partir de arquivos CSV. O sistema utiliza tecnologias modernas para processamento de dados geoespaciais e logging robusto, com uma interface gráfica para a visualização dos dados.

### Funcionalidades Principais
- Importação de dados cadastrais de arquivos CSV
- Processamento de geometrias MultiPolygon usando JTS (Java Topology Suite)
- Sistema de logging completo com output para arquivo
- Validação de dados geométricos
- Detecção de adjacências físicas entre propriedades
- Ordenação de cadastros por diferentes critérios (ID, comprimento, área, proprietário)
- Interface gráfica para visualização e interação com os dados
- Visualização de formas geométricas em painel dedicado

### Tecnologias Utilizadas
- Java 24
- Apache Commons CSV (1.9.0) para processamento de arquivos CSV
- JTS Core (1.19.0) para manipulação de dados geométricos
- JUnit Jupiter (5.8.2) para testes unitários
- Maven para gerenciamento de dependências e build

### Estrutura do Projeto
```
src/
├── main/
│   ├── java/
│   │   ├── cadastro/
│   │   │   ├── importer/     # Importação e processamento de dados
│   │   │   │   ├── Cadastro.java
│   │   │   │   └── CadastroConstants.java
│   │   │   ├── graph/        # Representação em grafo
│   │   │   │   ├── PropertyGraph.java
│   │   │   │   ├── PropertyGraphConstants.java
│   │   │   │   └── Main.java
│   │   │   └── gui/          # Interface gráfica
│   │   │       ├── GUI.java
│   │   │       ├── GUIConstants.java
│   │   │       ├── ShapePanel.java
│   │   │       └── Main.java
└── test/
    ├── java/
    │   ├── cadastro/
    │   │   ├── importer/
    │   │   │   ├── CadastroTest.java
    │   │   │   └── CadastroTestLogger.java
    │   │   └── graph/
    │   │       ├── PropertyGraphTest.java
    │   │       └── PropertyGraphTestLogger.java
```

### Classes Principais

#### Cadastro
- Representa um cadastro imobiliário
- Processa dados de arquivos CSV
- Valida e processa geometrias MultiPolygon
- Gerencia informações como ID, comprimento, área, forma geométrica, proprietário e localização
- Implementa ordenação por diferentes critérios
- Utiliza constantes definidas em CadastroConstants

#### PropertyGraph
- Representa um grafo de propriedades
- Vértices são cadastros imobiliários
- Arestas representam adjacências físicas entre propriedades
- Implementa algoritmos para detecção de adjacências
- Fornece métodos para consulta de propriedades adjacentes
- Utiliza constantes definidas em PropertyGraphConstants

#### GUI
- Implementada a interface gráfica do usuário utilizando Java Swing
- Visualização de cadastros e suas propriedades
- Painel dedicado para visualização de formas geométricas
- Interação com o grafo de propriedades
- Utiliza constantes definidas em GUIConstants

### Classes de Suporte

#### Classes de Constantes
- `CadastroConstants`: Centraliza constantes e mensagens de erro do Cadastro
- `PropertyGraphConstants`: Centraliza constantes e mensagens de erro do PropertyGraph
- `GUIConstants`: Centraliza constantes e mensagens da interface gráfica

#### Classes de Logging
- `CadastroTestLogger`: Gerencia logs dos testes do Cadastro
- `PropertyGraphTestLogger`: Gerencia logs dos testes do PropertyGraph

### Como Executar
1. Certifique-se de ter Java 24 instalado
2. Clone o repositório
3. Execute o projeto usando Maven:
   ```bash
   mvn clean install
   # Para executar a interface gráfica
   mvn exec:java -Dexec.mainClass="cadastro.gui.Main"
   # Para executar o processamento em modo texto
   mvn exec:java -Dexec.mainClass="cadastro.graph.Main"
   ```

### Testes
O projeto inclui testes unitários abrangentes:
- Testes de criação e validação de cadastros
- Testes de processamento de geometrias
- Testes de detecção de adjacências
- Testes de ordenação de cadastros
- Testes de interface gráfica

Execute os testes com:
```bash
mvn test
```

### Logs
O sistema gera logs detalhados que podem ser encontrados em:
- `test-results/CadastroTest.log`: Logs dos testes do Cadastro
- `test-results/PropertyGraphTest.log`: Logs dos testes do PropertyGraph

Cada arquivo de log inclui:
- Timestamp de cada operação
- Separação visual entre testes
- Mensagens de sucesso e erro
- Detalhes do setup e execução dos testes

### Configuração do Ambiente
- Java 24
- Maven 3.8.1 ou superior
- UTF-8 encoding
- Dependências gerenciadas via Maven

