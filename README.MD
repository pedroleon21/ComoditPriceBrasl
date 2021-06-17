# API para Análise de Preços de Combustíveis

O projeto consiste no desenvolvimento de uma API 
que possibilita a análise conjunta dos preços médios
de revenda de combustíveis divulgados pela Agência 
Nacional do Petróleo, Gás Natural e Biocombustíveis 
(ANP), cotação do Dólar Americano em relação ao Real 
e cotação do Barril de Petróleo Brent. A aplicação 
possibilita que seja consultada a média de preços de
combustíveis em um determinado estado, com base em uma
data informada. Além disso, é possível obter a informação
do menor preço disponível em um município na data informado, 
acompanhado dos dados do estabelecimento comercial ofertante.
Por fim, há também a funcionalidade de calcular uma função de
regressão no histórico de preços e, posteriormente, fazer
previsões com base em valores informados para a cotação do
dólar e do petróleo brent.

## *Endpoints*

A partir da porta 8080 do *localhost* ou do endereço Heroku
https://precoscombustiveisapi.herokuapp.com estão disponíveis os seguintes *endpoints*:

* **POST - /usuario - Cadastrar um usuário** 
  
  Permite a criação de perfil de usuário para acessar as funcionalidades
  da API. Deve ser enviado um objeto do formato *JSON*, conforme exemplificado
  abaixo:
        
        {
            nome: "nome",
            email: "email@email.com",
            senha: "senha"
        }
  
* **GET  - /usuario/ativo - Consultar usuário ativo**
  
  Retorna as informações do usuário ativo se houver. Não é necessário enviar 
  nenhuma informação.
  
* **POST - /login - Efetuar login com email e senha**
  
  Permite efetuar a autenticação do usuário para acesso às funcionalidades da
  API. Devem ser informados o e-mail e a senha cadastradas em um objeto do 
  formato *JSON*, comforme exemplificado abaixo:
        
        {
            email: "email@email.com",
            senha: "senha"
        }
  
* **POST - /commoditie/combustivel/local - Cadastrar Local do Combustivel**
  
  Permite cadastrar os dados de uma revenda de combustíveis. Devem ser
  informados os dados exemplificados abaixo em um objeto do formato *JSON*:
        
        {
            regiao: "CO",
            siglaEstado: "DF",
            municipio: "BRASILIA",
            nome: "POSTO POSTO",
            cnpj: "00.000.000/0001-00",
            bandeira: "BRANCA"
        }
  
* **POST - /commoditie/combustivel - Cadastrar Preço de Combustível**
  
  Permite cadastrar um registro de preço de combustível, informando-se os
  dados exemplificados abaixo, em um objeto do formato *JSON*:
  
        {
            data: "11/06/2021",
            valor: 5.50,
            tipo: "GASOLINA"
        }
  
  São permitidos apenas os seguintes tipos de combustíveis:
        
        * GASOLINA
        * GASOLINA ADITIVADA
        * ETANOL
        * DIESEL
        * DIESEL S10
  
  As informações da revenda ofertante são obtidas automaticamente do 
  objeto cadastrado no endpoint anterior.
  Os dados são gravados em um arquivo CSV.
  
* **GET  - /precos/media - Consultar Preço Médio por Estado**
  
  Permite consultar o preço médio do tipo de combustível desejado, no
  estado e data informados. A requisição da consulta deve ser enviada
  com as seguintes informações em um objeto do formato *JSON*:
  
        {
            tipoCombustivel: "GASOLINA",
            data: "11/06/2021",
            UF: "DF"
        }
  
  São permitidos apenas os tipos de combustíveis citados anteriormente.
  Havendo dados disponíveis acerca da consulta solicitada, serão retornadas
  a média, o menor e o maior valor verificados. Caso não hajam dados disponíveis,
  nada será retornado. Os arquivos CSV que contém os registros disponíveis estão
  na pasta "dadosHistoricos" do projeto, contemplando informações de 01/01/2020 a 
  31/05/2021.
  
* **GET  - /precos/menor - Consultar Menor Preço no Muncípio**

  Permite consultar o menor preço disponível do tipo de combustível desejado, no
  município e data informados. A requisição da consulta deve ser enviada
  com as seguintes informações em um objeto do formato *JSON*:

        {
            tipoCombustivel: "GASOLINA",
            data: "11/06/2021",
            UF: "DF",
            municipio: "BRASILIA"
        }

  São permitidos apenas os tipos de combustíveis citados anteriormente.
  Havendo dados disponíveis acerca da consulta solicitada, serão retornados
  o valor, o nome, CNPJ e bandeira da revenda ofertante. Caso não hajam 
  dados disponíveis, nada será retornado.
  
* **GET  - /precos/modelo - Treinar Modelo de Regressão**
  
  Permite calcular uma função de regressão linear que estima a relação das
  cotações do Dólar Americano e do Barril de Petróleo Brent na formação do 
  preço dos combustíveis. Retorna os coeficientes angular e de inclinação da
  reta para o cálculo das previsões. A requisição deve ser feita em um objeto
  do formato *JSON*, conforme exemplificado abaixo:

        {
            tipoCombustivel: "GASOLINA",
            siglaEstado: "DF"
        }

  São permitidos apenas os tipos de combustíveis citados anteriormente.
  Caso não hajam dados disponíveis para os parâmetros informados,
  nada será retornado.
  
* **GET  - /precos/modelo/previsao - Calcular Previsão do Preço do Combustível**

  A partir da função de regressão calculada no endpoint anterior, possibilita
  o cálculo de previsões do preço de combustível solicitado, com base em valores 
  informados para a cotação do Dólar e do Petróleo Brent. A requisição deve 
  ser feita em um objeto do formato *JSON*, conforme exemplificado abaixo:

        {
            tipoCombustivel: "GASOLINA",
            siglaEstado: "DF",
            valorPetroleo: 70.00,
            valorDolar: 5.12
        }

  São permitidos apenas os tipos de combustíveis citados anteriormente.
  É retornado o preço previsto no formato *Double*.
  
## Integrantes do Projeto

* Bruno de Jesus Viana
* Diego Alexandre da Silva
* Luciano Junio Morais do Nascimento
* Pedro Leon Paranayba Clerot

## Ambiente de Desenvolvimento e Compilação

Para o desenvolvimento do projeto foi utilizada a 
linguagem de programação **Kotlin**. Para compilação, 
utilizou-se o **Gradle** com o **Java JDK 1.8**. 
Além disso, foram utilizadas as seguintes dependências 
do *plugin* **Ktor**:

* io.ktor:ktor-server-core
* io.ktor:ktor-gson
* io.ktor:ktor-server-netty
* io.ktor:ktor-html-builder
* ch.qos.logback:logback-classic
* io.ktor:ktor-server-tests

Outras bibliotecas utilizadas:

* Jsoup - Versão 1.11.3
* Selenium-Java - Versão 3.141.59
* Selenium-Crhome-Driver - Versão 3.141.59
* Kotlin-Statistics - Versão 1.2.1
* Krangl - Versão 0.16.2

Para execução da função de *web scraping* que busca as cotações do Dólar 
e do Petróleo Brent, é necessário dispor do arquivo **chromedriver** 
compatível com a versão do sistema operacional utilizado. Na pasta 
raiz do projeto, estão disponíveis as versões para Linux e Windows.

## Fontes de Dados

Foram utilizadas as seguintes fontes de dados:

* **Combustíveis**: https://www.gov.br/anp/pt-br/centrais-de-conteudo/dados-abertos/serie-historica-de-precos-de-combustiveis
* **Dólar**: https://br.investing.com/currencies/usd-brl-historical-data
* **Petróleo**: https://br.investing.com/commodities/brent-oil-historical-data