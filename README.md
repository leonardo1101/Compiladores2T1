# Compiladores2-T1
Primeiro trabalho e compiladores 2

#Grupo
Leonardo Peralta - 726556
Mariana Cavichioli - 726568
Rafael Saito - 726580
Renata Sarmet - 726586

# Funcionamento de pastas
* O sistema de pastas e nomes foram selecionados para serem utilizados no script, por isso não mude.
* Na pasta ArquivosTemporarios teremos os arquivos que serão gerados para a correção, não sendo necessária a sua utilização.
* Na pasta casoDeTeste1 contêm os casos de testes.
* O arquivo compilador.jar, deverá conter o compilador contruido, para que se possa realizar a sua correção.


# Instruções para correção 
* Após abrir o projeto, é necessário realizar a configuração do ambiente no Intellij seguindo os passos a seguir:
    1. Importar a biblioteca do Antlr em Project Structure -> Project Settings -> Libraries
    2. Gerar os arquivos do Antlr para integrar o projeto com a linguagem clicando com o botão direito do mouse sobre o arquivo LA.g4 -> Generate ANTLR Recognizer. Esse comando criará uma pasta chamada gen dentro da pasta raiz do projeto; basta copiar todos os arquivos da mesma para a pasta src.
    3. É necessário também gerar artefatos para gerar o arquivo .jar posteriormente. Assim, isso pode ser feito em Project Structure -> Project Settings -> Artifacts -> + -> Jar -> From modules with dependencies -> Selecionar a classe main do projeto e dar OK.
    4. Após gerar os artefatos, deve-se dar um Build em Build -> Build Artifacts -> Build e o projeto está pronto para ser executado.
    5. Para executar o projeto, basta digitar no terminal o comando: java -jar CorretorTrabalho1.jar  "java -jar Compilador.jar " gcc ArquivosTemporarios/ casosDeTesteT1/  "726556, 726568, 726580, 726586" tudo de acordo com o caminho das pastas do projeto.
