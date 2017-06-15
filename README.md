# scmuHomeSecuritySystem

O repositorio é composto pelo relatório, firmware para correr no arduino, um projeto de um web server a correr no eclipse EE e pelo codigo da aplicação android:

# Setup do servidor intermédio

Para correr o servidor é necessário ter no computador: 
- Eclipse EE : http://www.eclipse.org/downloads/eclipse-packages/ esta versão do EE é especial para web developing, permitindo executar programas java em servidores;
- Apache tomcat 8.5 : https://tomcat.apache.org/download-80.cgi
- Plugin Maven : É possivel a versão Neon já ter o plugin instalado por defeito

Instruções
- Instalar o tomcat 8.5 num local à escolha; 
- Colocar a pasta scmu na workspace do Eclipse EE
- Carregar com o botão direito do rato na pasta raiz do projeto (scmu) e selecionar Run As -> Run on Server
- Na janela que aparece selecionar o endereço ipv4 da máquina onde é para correr o webserver

