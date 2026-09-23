# TASKIDS v2 — Migração de banco

## Origem

Versão anterior:

`Room version = 1`

Entidade principal:

- tasks.

## Destino

`Room version = 2`

Novas tabelas:

- children;
- task_executions;
- rewards;
- reward_redemptions;
- routines;
- routine_tasks.

Novas colunas em `tasks`:

- childId;
- rewardStars;
- scheduledTime;
- recurrenceDays;
- isRecurring.

## Compatibilidade de dados antigos

Tarefas existentes recebem:

- `childId = 1`;
- `rewardStars = 10`;
- `recurrenceDays = ""`;
- `isRecurring = false`.

Assim, instalações existentes permanecem associadas ao primeiro perfil sem perder tarefas.

## Política

A aplicação não usa mais `fallbackToDestructiveMigration()`.

Qualquer nova alteração de schema deve possuir Migration explícita.

## Seed

Quando as tabelas novas estão vazias:

- são criados perfis demonstrativos;
- tarefas demonstrativas são inseridas apenas para perfis sem tarefas;
- recompensas padrão são criadas apenas quando o catálogo está vazio.

Isso evita duplicação após atualizações.
