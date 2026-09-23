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
