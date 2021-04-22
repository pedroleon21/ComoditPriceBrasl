# API para Análise de Preços de Combustíveis

O projeto consiste no desenvolvimento de uma API 
que possibilita a análise conjunta dos preços médios
de revenda de combustíveis divulgados pela Agência 
Nacional do Petróleo, Gás Natural e Biocombustíveis 
(ANP), cotação do Dólar Americano em relação ao Real 
e cotação do Barril de Petróleo Brent. Inicialmente, 
a aplicação permite o cadastro e consulta de preços 
de combustíveis por tipo e município, com as correspondentes 
cotações de dólar e petróleo na data informada. Além 
disso, é possível verificar os municípios com menor 
valor por estado.

## *Endpoints*

A partir da porta 9999 do *localhost* estão disponíveis 
os seguintes *endpoints*:

* POST - /commoditie/combustivel - Cadastra Preço de Combustível
* POST - /commoditie/moeda - Cadastra Cotação do Dólar
* POST - /commoditie/materiaprima - Cadastra Cotação do Barril de Petróleo Brent
* GET - /precos - Consultar Preços e Cotações
* GET - /precos/estado - Consultar Menor Preço por Estado

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

## Próximas etapas

Para a carga dos dados históricos das três referências 
em análise (combustíveis, dólar e petróleo) será desenvolvida 
uma aplicação de *web scraping* para coleta dos valores 
nas seguintes páginas na internet:

* **Combustíveis**: https://www.gov.br/anp/pt-br/assuntos/precos-e-defesa-da-concorrencia/precos/precos-revenda-e-de-distribuicao-combustiveis/serie-historica-do-levantamento-de-precos
* **Dólar**: https://dadosabertos.bcb.gov.br/dataset/dolar-americano-usd-todos-os-boletins-diarios/resource/ae69aa94-4194-45a6-8bae-12904af7e176
* **Petróleo**: https://br.investing.com/commodities/brent-oil-historical-data

Além disso, uma versão que possibilita o cadastro de 
um município como uma classe separada do preço de 
combustível está em desenvolvimento (branch "Luciano").