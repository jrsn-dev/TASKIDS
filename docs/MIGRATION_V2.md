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


# Migration 2 -> 3 — Game Mode

A versão 3 adiciona progressão de jogo e personalização procedural.

## children

Novas colunas:

- totalXp INTEGER NOT NULL DEFAULT 0
- currentCombo INTEGER NOT NULL DEFAULT 0
- bestCombo INTEGER NOT NULL DEFAULT 0
- avatarSkinTone INTEGER NOT NULL DEFAULT 2
- avatarHairStyle INTEGER NOT NULL DEFAULT 0
- avatarHairColor INTEGER NOT NULL DEFAULT 0
- avatarOutfitColor INTEGER NOT NULL DEFAULT 0
- gameTheme TEXT NOT NULL DEFAULT 'SKY'

## tasks

Novas colunas:

- iconKey TEXT NOT NULL DEFAULT 'GENERIC'
- rewardXp INTEGER NOT NULL DEFAULT 100

## task_executions

Novas colunas:

- earnedXp INTEGER NOT NULL DEFAULT 0
- combo INTEGER NOT NULL DEFAULT 1

A aplicação registra as migrations em sequência:

```text
1 -> 2 -> 3
```

Nenhuma migration destrutiva é utilizada.
