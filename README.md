O objetivo desse projeto é fazer um esquema de enviar duas pastas, e o proprio java vai garantir a sincronia dessas pastas.
O que deve ter:
    - Uma tela html onde sera selecionado a pasta origem e destino, mas essas pastas são dentro do servidor.
    - Ter uma tabela com a diferença entre as pastas, nessa tabela deve ter o item de origem, o item de destino e o status, e o md5 do item.
    - Uma tabela de oparações onde cada arquivo copiado e deletado deve ser mostrado.
    - O serviço deve continuar mesmo após reiniciar


- COPY
  Só copia arquivos novos do source para o target
- SYNC
  Copia arquivos novos do source para o target e deleta os arquivos que não existem mais no source