# TASKIDS v2 — Plano de testes

## Automação

Workflow:

`.github/workflows/android-ci.yml`

Valida:

- assembleDebug;
- testDebugUnitTest.

Ambiente:

- JDK 17;
- Gradle 9.3.1;
- Android SDK 36.

## Smoke test

### Inicialização

- instalar sobre banco vazio;
- confirmar criação dos perfis demonstrativos;
- alternar entre perfis;
- confirmar tarefas independentes.

### Migração

- instalar versão v1;
- criar tarefas customizadas;
- atualizar para v2;
- confirmar preservação das tarefas;
- confirmar associação ao perfil 1.

### Home

- tarefa pausada não aparece na experiência infantil;
- progresso usa apenas tarefas ativas;
- tarefa concluída não pode ser iniciada novamente.

### Timer

- iniciar tarefa;
- pausar;
- retomar;
- adicionar 5 minutos;
- fechar/reabrir processo;
- confirmar restauração;
- deixar expirar;
- confirmar estado OVERDUE.

### Estrelas

- concluir tarefa;
- verificar saldo;
- verificar TaskExecution;
- resgatar recompensa;
- verificar desconto;
- verificar RewardRedemption.

### PIN

- PIN correto libera;
- PIN incorreto não libera;
- três erros geram bloqueio temporário;
- novo PIN funciona;
- PIN não aparece em texto na interface.

### Perfis

- criar perfil com avatar;
- criar tarefas para perfil;
- alternar perfil;
- confirmar isolamento de tarefas e estrelas.

### Relatórios

- concluir tarefas em dias distintos;
- verificar cards;
- verificar histórico;
- verificar gráfico.

### Segurança infantil

- confirmar ausência de navegador/WebView;
- confirmar ausência de permissão INTERNET;
- confirmar que Back não encerra a área de tempo controlado diretamente.


## Game Mode v3

### Motor

- XP 0 inicia no nível 1;
- cada 500 XP aumenta o nível;
- combo 3, 5, 7 e 10 aplica bônus;
- combo não ultrapassa 10;
- dia perfeito só dispara quando todas as missões ativas estão concluídas.

### Avatar

- trocar tom de pele;
- trocar cabelo;
- trocar cor do cabelo;
- trocar roupa;
- reiniciar app;
- confirmar persistência.

### Mundos

Validar:

- SKY;
- SPACE;
- FOREST;
- ISLAND.

Nenhum mundo deve depender de asset rasterizado.

### Missões

- primeira missão disponível;
- próximas bloqueadas enquanto a anterior não for concluída;
- missão concluída atualiza caminho;
- voltar ao mapa não reinicia timer ativo;
- reabrir missão ativa continua o mesmo timer.

### Migration 2 -> 3

- instalar v2;
- criar perfil, tarefa e histórico;
- atualizar para v3;
- confirmar preservação;
- confirmar defaults de XP/avatar/mundo;
- concluir nova missão;
- confirmar earnedXp e combo no histórico.
