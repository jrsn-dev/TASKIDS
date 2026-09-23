# TASKIDS

TASKIDS é um gerenciador de rotina infantil gamificado para Android TV, com experiência separada para crianças e responsáveis.

A versão 2 reorganiza o projeto como produto: perfis independentes, tarefas recorrentes, histórico, estrelas persistentes, recompensas, streak, conquistas, relatórios parentais e timer resiliente.

## Principais fluxos

### Criança

- escolher perfil;
- visualizar tarefas do dia;
- iniciar missão;
- acompanhar timer;
- pausar ou adicionar tempo;
- concluir;
- receber estrelas;
- acompanhar sequência e conquistas;
- resgatar recompensas aprovadas.

### Responsáveis

A Área dos Pais é protegida por PIN e permite:

- gerenciar tarefas;
- criar horários e recorrência;
- criar rotinas;
- configurar recompensas;
- criar perfis;
- consultar relatórios;
- alterar PIN;
- ativar/desativar som;
- reiniciar somente o estado diário.

## Stack

- Kotlin 2.2.10
- Jetpack Compose
- Material 3
- Room
- StateFlow / Coroutines
- Android TV / D-Pad
- AGP 9.1.1

## Requisitos de desenvolvimento

- Android Studio compatível com AGP 9.1.1
- JDK 17
- Android SDK 36
- Gradle 9.3.1

O projeto não depende mais de chave Gemini, Firebase ou serviços externos para executar localmente.

## Executar

1. Abra o repositório no Android Studio.
2. Aguarde a sincronização do Gradle.
3. Selecione um emulador Android TV ou dispositivo compatível.
4. Execute o módulo `app`.

Build por linha de comando, quando Gradle 9.3.1 estiver instalado:

```bash
gradle :app:assembleDebug
```

Testes:

```bash
gradle :app:testDebugUnitTest
```

## Estrutura

```text
app/src/main/java/com/example/
├── MainActivity.kt
├── model/
├── data/
├── viewmodel/
└── ui/
    ├── components/
    ├── design/
    ├── dialogs/
    ├── navigation/
    └── screens/
```

## Banco

Room versão 3.

A atualização da versão anterior usa migration explícita e preserva tarefas já existentes.

`fallbackToDestructiveMigration()` não é utilizado.

## Segurança infantil

- PIN parental armazenado como hash;
- lockout depois de tentativas inválidas;
- WebView irrestrito removido;
- permissão de internet removida da versão local;
- backup Android desativado;
- artefatos de assinatura/build ignorados pelo Git.

## Documentação

- [Produto v2](docs/PRODUCT_V2.md)
- [UX/UI](docs/UX_UI_GUIDELINES.md)
- [Arquitetura](docs/ARCHITECTURE.md)
- [Segurança e privacidade](docs/SECURITY_PRIVACY.md)
- [Migração v2](docs/MIGRATION_V2.md)
- [Plano de testes](docs/TEST_PLAN.md)
- [Roadmap](docs/ROADMAP.md)

## CI

O workflow `.github/workflows/android-ci.yml` executa build debug e testes unitários para pushes/PRs elegíveis.

## Próxima fase

A próxima evolução recomendada é separar a experiência dos responsáveis em aplicativo mobile conectado a uma camada cloud, mantendo o Android TV como experiência principal da criança.


## Game Mode v3

A experiência infantil principal não depende de imagens PNG/JPG ou personagens pré-renderizados.

O jogo é construído em Compose/Canvas:

- avatar procedural;
- mapa de missões;
- mundos SKY, SPACE, FOREST e ISLAND;
- ícones vetoriais;
- XP;
- níveis;
- combos;
- partículas de conclusão;
- conquistas.

Documentação: [Game Mode v3](docs/GAME_MODE_V3.md)
