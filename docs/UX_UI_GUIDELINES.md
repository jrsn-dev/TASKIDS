# TASKIDS v2 — UX/UI

## Referência visual

A refatoração segue o conceito visual aprovado:

- fundo azul profundo;
- cards grandes e arredondados;
- branco para superfícies parentais;
- azul, verde, amarelo e roxo como cores funcionais;
- estrelas como moeda visual;
- tarefas representadas por ícones grandes;
- foco claro para D-Pad/controle remoto;
- timer em painel branco de alto contraste;
- celebração visual após conclusão.

## Child mode

### Home

Estrutura:

1. marca TASKIDS;
2. seletor de perfis;
3. saldo de estrelas;
4. saudação;
5. streak e nível;
6. cards de tarefas;
7. progresso diário;
8. acesso a recompensas.

Cada card mostra:

- ícone;
- nome;
- duração;
- estrelas;
- horário quando configurado;
- estado concluído.

### Timer

Layout em duas colunas:

- coluna esquerda: contexto visual da tarefa;
- coluna direita: contador, progresso e ações.

Ações principais:

- pausar/retomar;
- +5 min;
- concluir.

### Recompensas

Cards mostram custo e disponibilidade.

Recompensa indisponível não executa resgate.

### Celebração

A tela de sucesso reforça:

- tarefa concluída;
- estrelas recebidas;
- sequência;
- escolha de recompensa.

## Parent mode

A Área dos Pais usa fundo claro para criar separação cognitiva da experiência infantil.

Menu:

- Visão Geral;
- Tarefas;
- Rotinas;
- Recompensas;
- Perfis;
- Configurações;
- Relatórios.

## Inclusão

A interface não assume que o usuário infantil é menino.

Diretrizes:

- perfis com avatares variados;
- linguagem neutra quando o nome não está disponível;
- tarefas não são categorizadas por gênero;
- paleta não associa rosa/azul a gênero;
- exemplos de Alex e Sofia são apenas perfis demonstrativos;
- gamificação evita ranking entre crianças.

## TV-first

Elementos interativos devem:

- ter alvo grande;
- possuir estado visual de foco;
- evitar menus pequenos;
- evitar dependência de gesto;
- funcionar com D-Pad;
- manter textos importantes acima de 12sp em superfícies grandes.

## Acessibilidade

- contraste alto para informação crítica;
- cor nunca deve ser o único indicador de estado;
- cards concluídos usam símbolo ✓;
- textos curtos e linguagem simples;
- botões críticos usam verbo explícito;
- painel parental usa rótulos além de ícones.

## Tokens principais

### Dark

- Navy 900: `#071A3D`
- Navy 800: `#0B2A63`
- Navy 700: `#123B84`

### Functional

- Blue: `#2F80ED`
- Green: `#27AE60`
- Yellow: `#F2C94C`
- Orange: `#F2994A`
- Purple: `#9B51E0`
- Pink/alert: `#EB5757`

### Parent surfaces

- Soft background: `#F4F7FC`
- Ink: `#102A56`
- Muted: `#7383A3`

## Arquivos de UI

- `ui/design/TaskIdsDesign.kt`
- `ui/components/TaskIdsComponents.kt`
- `ui/dialogs/ParentDialogs.kt`
- `ui/screens/HomeScreen.kt`
- `ui/screens/TimerScreenV2.kt`
- `ui/screens/RewardUnlockedScreen.kt`
- `ui/screens/RewardsScreen.kt`
- `ui/screens/ScreenTimeScreen.kt`
- `ui/screens/ParentDashboardScreen.kt`
- `ui/screens/ReportsScreen.kt`
