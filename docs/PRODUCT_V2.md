# TASKIDS v2 — Produto

## Visão

TASKIDS é uma experiência de rotina infantil gamificada, com foco em autonomia, previsibilidade e participação da família.

A TV é a superfície principal da criança. A Área dos Pais concentra configuração, acompanhamento e regras.

## Princípios

1. **Criança entende o próximo passo rapidamente.**
2. **Pais configuram sem interferir em toda interação.**
3. **Recompensa não significa apenas tempo de tela.**
4. **Meninos e meninas devem se reconhecer no produto.**
5. **Gamificação reforça consistência, não competição entre crianças.**
6. **Nenhuma experiência infantil deve abrir navegação irrestrita.**

## Perfis infantis

Cada criança possui:

- nome;
- avatar;
- carteira de estrelas;
- sequência atual;
- maior sequência;
- tarefas próprias;
- rotinas próprias;
- histórico de execuções;
- recompensas compartilhadas ou específicas.

O projeto inclui perfis iniciais de Alex e Sofia apenas como demonstração. Novos perfis podem ser criados na Área dos Pais.

## Tarefas

Cada tarefa possui:

- título;
- descrição;
- duração;
- ícone;
- ordem;
- estrelas;
- horário opcional;
- dias recorrentes;
- estado;
- vínculo com uma criança.

Estados:

- PENDING;
- IN_PROGRESS;
- COMPLETED;
- OVERDUE.

Tarefas pausadas pelo responsável não entram na experiência infantil nem no cálculo de conclusão diária.

## Rotinas

Rotinas agrupam contexto e recorrência, por exemplo:

- rotina da manhã;
- rotina da tarde;
- rotina da noite;
- rotina escolar;
- rotina de fim de semana.

A base de dados já possui `Routine` e `RoutineTask`, permitindo evoluir a associação visual de tarefas a rotinas sem nova migração estrutural.

## Estrelas

Estrelas são saldo persistente do perfil.

São conquistadas ao concluir tarefas e consumidas ao resgatar recompensas.

O saldo é armazenado em `Child.totalStars`.

O histórico de origem é preservado em `TaskExecution`. O histórico de consumo é preservado em `RewardRedemption`.

## Recompensas

O catálogo aceita recompensas como:

- vídeos aprovados;
- escolher o filme;
- videogame;
- atividade em família;
- passeio;
- sobremesa;
- recompensa personalizada.

O tipo `SCREEN_TIME` abre somente a área controlada de tempo de tela do TASKIDS.

## Streak e nível

A sequência é atualizada quando existe conclusão em dias consecutivos.

O nível exibido na interface é derivado da quantidade de estrelas acumuladas para fornecer feedback simples à criança.

A intenção é mostrar evolução individual, não ranking entre irmãos.

## Relatórios

A Área dos Pais exibe:

- tarefas concluídas;
- estrelas conquistadas;
- tempo médio;
- sequência;
- gráfico dos últimos sete dias;
- histórico recente.

## Fora de escopo desta versão

A camada cloud e o aplicativo remoto dos responsáveis permanecem como próxima fase. A arquitetura local foi organizada para permitir essa evolução sem reescrever a experiência infantil.


## Game Mode v3

A experiência infantil deixa de ser uma lista de tarefas e passa a ser uma jornada de missões.

O responsável continua cadastrando atividades reais, mas a criança vê:

- mapa;
- avatar;
- missão atual;
- XP;
- nível;
- combo;
- streak;
- estrelas;
- conquistas;
- recompensas.

A apresentação infantil não depende de imagens externas. A identidade visual é construída em tempo real pelo aplicativo.
