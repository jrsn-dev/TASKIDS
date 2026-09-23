# Migração Room 1 → 2

A v1 usava `fallbackToDestructiveMigration()`, o que poderia apagar o banco em alterações de schema. A v2 registra `MIGRATION_1_2` explicitamente.

## Compatibilidade de instalação

O namespace Kotlin/Android foi refatorado para `com.taskids.app`, mas o `applicationId` publicado pela v1 (`com.aistudio.kidstasks.pqwxzt`) foi preservado. Essa distinção é necessária para que o Android trate a v2 como atualização do mesmo aplicativo e permita que o arquivo Room existente seja migrado.

Se no futuro houver decisão de criar um novo listing/app ID, isso deve ser tratado como instalação separada e não como migração automática.

## Colunas adicionadas a `tasks`

- `childId`
- `rewardPoints`
- `recurrenceMask`
- `routinePeriod`
- `isEnabled`
- `dueHour`
- `dueMinute`

Tarefas existentes são associadas ao perfil `id=1` para preservar comportamento.

## Novas tabelas

- `children`
- `task_executions`
- `star_transactions`
- `achievement_unlocks`
- `rewards`
- `reward_redemptions`
- `approved_content`

## Perfis de compatibilidade

A migração cria dois perfis de demonstração, Alex e Luna. Tarefas existentes permanecem em Alex. No bootstrap, perfis sem tarefas recebem os templates padrão.

## PIN

O PIN não faz parte do Room. `AppPreferencesRepository` lê o valor legado `kid_task_prefs/parental_pin`, calcula o hash com salt e remove o valor em texto puro.
