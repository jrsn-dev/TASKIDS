# Arquitetura TASKIDS 2.0

## Objetivos

A v2 reduz o acoplamento do MVP original, no qual uma única `MainActivity.kt` concentrava navegação, componentes, telas e parte das regras. O projeto passa a ter fronteiras claras entre apresentação, estado, domínio e persistência.

## Camadas

### Presentation

`ui/` contém telas e componentes Jetpack Compose. Telas não acessam Room nem `SharedPreferences` diretamente. Elas observam `StateFlow` do `MainViewModel` e enviam eventos.

### State / orchestration

`MainViewModel` coordena navegação local, perfil ativo, timer, resgate de recompensa, controle de PIN e agregações de relatório. Estados que precisam sobreviver ao processo são persistidos no repositório de preferências.

### Data

`KidsRepository` é o ponto de acesso aos DAOs. Operações que afetam múltiplas tabelas, como concluir uma tarefa ou resgatar uma recompensa, usam transação Room.

### Persistence

Room armazena dados estruturados. `AppPreferencesRepository` guarda preferências pequenas, seleção de perfil, estado do timer e credenciais parentais derivadas (hash + salt), nunca o PIN em texto puro.

## Modelo principal

```text
ChildProfile
  ├── Task
  │    └── TaskExecution
  ├── Reward
  │    └── RewardRedemption
  ├── StarTransaction
  └── AchievementUnlock

ApprovedContent  (catálogo familiar de vídeos liberados)
```

### Por que separar Task de TaskExecution

`Task` descreve uma rotina reutilizável. `TaskExecution` registra o que aconteceu em um dia específico. Isso permite histórico, relatórios, streaks e futuras sincronizações sem sobrescrever o passado.

## Navegação

A navegação continua leve e controlada por estado porque o app tem poucos destinos TV-first:

- Home
- Timer
- Rewards
- Parents
- ApprovedPlayer

A estrutura permite migrar para Navigation Compose se deep links, múltiplos fluxos ou companion app exigirem.

## Timer

O timer usa `endAtMillis` como fonte temporal. O número mostrado é derivado de `endAtMillis - now`. Ao pausar, persiste o restante. Isso evita drift relevante e permite recuperação após recriação de Activity/processo.

## Decisões deliberadas

- Não foi adicionado Hilt nesta etapa. O grafo é pequeno e o factory atual é explícito. Hilt passa a ser vantajoso quando houver backend, autenticação, workers e módulos remotos.
- Não foi adicionado backend apenas para “sincronizar”. Dados infantis exigem política de contas, consentimento e retenção antes da implementação cloud.
- Recompensas de tela não abrem um navegador genérico. O player recebe apenas IDs de vídeos previamente aprovados.
