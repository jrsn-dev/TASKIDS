# TASKIDS 2.0

TASKIDS é um gerenciador de rotina infantil gamificado, desenhado primeiro para Android TV e pensado para uso familiar. A experiência da criança prioriza autonomia, previsibilidade e reforço positivo; a Área dos Pais concentra configuração, relatórios e controles de segurança.

## O que mudou na v2

- UX/UI refeita seguindo o conceito visual TASKIDS: azul profundo para a experiência infantil, cards grandes e coloridos, timer de alto contraste, celebração e painel parental claro.
- Perfis independentes para meninos e meninas, com avatares inclusivos e progresso individual.
- Tarefas recorrentes por dia da semana e período (manhã, tarde, noite ou qualquer horário).
- Histórico de execuções separado da definição da tarefa.
- Carteira real de estrelas, transações e catálogo de recompensas.
- Conquistas e streaks sem ranking entre crianças.
- Dashboard parental de 7 dias.
- Timer persistente baseado em timestamp, capaz de se recuperar após reabertura do app.
- PIN parental armazenado como hash com salt e bloqueio temporário após tentativas incorretas.
- Conteúdo de tela restrito a vídeos explicitamente aprovados pelos responsáveis; não existe navegador YouTube livre.
- Migração Room 1 → 2 sem `fallbackToDestructiveMigration`.
- Namespace do código padronizado em `com.taskids.app`; o `applicationId` da v1 foi mantido para permitir atualização in-place e preservação dos dados existentes.
- Dependências e artefatos do protótipo AI Studio foram removidos.

## Stack

- Kotlin
- Jetpack Compose
- Room
- ViewModel + StateFlow
- Coroutines
- Android TV / Android em landscape

## Estrutura

```text
app/src/main/java/com/taskids/app/
├── data/
│   ├── local/
│   ├── preferences/
│   └── repository/
├── domain/model/
├── ui/
│   ├── components/
│   ├── home/
│   ├── timer/
│   ├── rewards/
│   ├── parental/
│   ├── player/
│   └── theme/
└── viewmodel/
```

## Executar

1. Abra o projeto no Android Studio.
2. Aguarde o sync do Gradle.
3. Execute em Android TV Emulator ou dispositivo Android em landscape.
4. O banco local é criado automaticamente e dados existentes da v1 são migrados para a v2 quando a atualização ocorre sobre a mesma instalação.

Em instalações existentes, o PIN legado é migrado automaticamente para hash + salt e deixa de ser mantido em texto puro. Em instalações novas, o responsável cria um PIN de 4 dígitos no primeiro acesso à **Área dos Pais**; não existe PIN padrão conhecido.

## Documentação

- `docs/ARCHITECTURE.md`
- `docs/UX_UI.md`
- `docs/BUSINESS_RULES.md`
- `docs/SECURITY.md`
- `docs/MIGRATION_V2.md`
- `docs/ROADMAP.md`

## Estado da v2

A v2 implementa a evolução local/TV do produto. Sincronização cloud e aplicativo companion para responsáveis permanecem como próxima camada arquitetural, documentada no roadmap, para não introduzir backend e autenticação sem uma estratégia de contas, consentimento e privacidade infantil definida.
