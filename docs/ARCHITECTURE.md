# TASKIDS v2 — Arquitetura

## Objetivo

A versão anterior concentrava praticamente toda a UI em `MainActivity.kt`.

A v2 transforma `MainActivity` em shell de navegação e separa domínio, dados, estado e apresentação.

## Estrutura

```text
app/src/main/java/com/example/
├── MainActivity.kt
├── model/
│   ├── Child.kt
│   ├── Task.kt
│   ├── TaskExecution.kt
│   ├── Reward.kt
│   ├── RewardRedemption.kt
│   ├── Routine.kt
│   ├── DefaultProfiles.kt
│   └── DefaultTasks.kt
├── data/
│   ├── local/
│   │   ├── TaskDatabase.kt
│   │   ├── TaskDao.kt
│   │   ├── ChildDao.kt
│   │   ├── TaskExecutionDao.kt
│   │   ├── RewardDao.kt
│   │   └── RoutineDao.kt
│   ├── preferences/
│   │   └── AppPreferencesRepository.kt
│   └── repository/
│       └── TaskRepository.kt
├── viewmodel/
│   └── MainViewModel.kt
└── ui/
    ├── navigation/
    ├── design/
    ├── components/
    ├── dialogs/
    └── screens/
```

## Fluxo

```text
Compose Screen
    ↓
MainViewModel
    ↓
TaskRepository
    ↓
Room DAOs / Preferences
```

## Entidades

### Child

Perfil infantil e saldo.

### Task

Definição reutilizável da tarefa.

### TaskExecution

Histórico imutável de uma execução concluída.

Isso evita o problema antigo em que o próprio `Task.status` era a única fonte histórica.

### Reward

Item configurável do catálogo.

### RewardRedemption

Registro de consumo de estrelas.

### Routine / RoutineTask

Estrutura de agrupamento e ordenação de tarefas recorrentes.

## Timer

O timer antigo dependia de decremento em memória.

A v2 persiste:

- taskId;
- startedAt;
- endAt;
- remainingSeconds;
- paused.

Ao restaurar o processo, o ViewModel recalcula o tempo pelo timestamp final.

## Navegação

`AppScreen` define:

- Home;
- Timer;
- RewardUnlocked;
- Rewards;
- ScreenTime;
- Parent;
- Reports.

Não foi adicionada uma biblioteca de navegação porque o fluxo é pequeno e TV-first. Isso reduz dependências e preserva estado simples.

## Banco

Room foi atualizado de versão 1 para 2.

A migração é explícita.

`fallbackToDestructiveMigration()` foi removido.

## Próxima evolução arquitetural

Quando houver backend/cloud:

```text
Domain repository interface
      ↑
Local repository ── Remote repository
      ↑                  ↑
Room                 API/Auth
```

Nesse momento vale introduzir injeção de dependência formal e separar interfaces de domínio das implementações.
