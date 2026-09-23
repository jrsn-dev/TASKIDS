# TASKIDS v3 — Game Mode

## Objetivo

Transformar a experiência infantil do TASKIDS de uma lista de tarefas ilustrada em um jogo de progressão real, sem dependência de imagens rasterizadas, personagens gerados ou assets 3D.

Toda a camada visual infantil é construída em tempo real com Jetpack Compose e Canvas.

## Pilares

1. **Rotina real vira missão.**
2. **Missão concluída gera XP e estrelas.**
3. **A criança avança em um mapa.**
4. **O avatar é procedural e personalizável.**
5. **Mundos são desenhados pelo app.**
6. **Não existe ranking entre crianças.**
7. **Área dos Pais continua utilitária e clara.**

## Fluxo infantil

### 1. Game Home

A Home foi substituída por um mapa de aventura.

Elementos:

- avatar procedural;
- nível;
- XP;
- combo;
- streak;
- estrelas;
- conquistas;
- próxima missão;
- mapa de missões;
- recompensas.

### 2. Missão

Cada tarefa vira uma missão.

A tela apresenta:

- categoria vetorial;
- objetivo;
- tempo restante;
- estrelas;
- XP;
- combo atual;
- pausar;
- adicionar tempo;
- concluir.

### 3. Conclusão

Ao concluir uma missão:

- estrelas são adicionadas;
- XP é adicionado;
- combo aumenta;
- bônus de combo pode ser aplicado;
- bônus de dia perfeito pode ser aplicado;
- partículas procedurais são exibidas;
- progresso de nível é atualizado.

## Motor de jogo

Arquivo:

`game/GameEngine.kt`

### Nível

Cada 500 XP aumenta um nível.

Títulos iniciais:

1. Explorador(a)
2. Descobridor(a)
3. Aventureiro(a)
4. Guardião(ã)
5. Mestre das Missões
6. Lenda TASKIDS

Após o nível 6, o título permanece Lenda TASKIDS enquanto o número do nível continua crescendo.

### Combo

Cada missão concluída incrementa o combo.

Bônus:

- combo 3: +3 estrelas;
- combo 5: +5 estrelas;
- combo 7: +7 estrelas;
- combo 10: +10 estrelas.

O combo é reiniciado quando as tarefas do perfil são reiniciadas.

### Dia perfeito

Quando todas as missões ativas do dia são concluídas:

- +20 estrelas;
- +50 XP.

## Avatar procedural

Arquivo:

`ui/game/GameAvatar.kt`

Não existe PNG/JPG para o personagem.

O avatar é desenhado com:

- círculos;
- retângulos arredondados;
- linhas;
- paths;
- animações de Compose.

Configurações persistidas:

- tom de pele;
- estilo de cabelo;
- cor do cabelo;
- cor da roupa.

A estrutura não depende de gênero. Os responsáveis configuram a aparência por características visuais.

## Mundos

Arquivo:

`ui/game/GameWorldBackground.kt`

Temas:

- SKY;
- SPACE;
- FOREST;
- ISLAND.

Todos são desenhados por Canvas.

Nenhum cenário depende de imagem externa.

## Ícones de missão

Arquivo:

`ui/game/GameIcon.kt`

Categorias iniciais:

- GENERIC;
- BOOK;
- BATH;
- MEAL;
- HOMEWORK;
- TOYS;
- TOOTH;
- SPORT;
- CLEAN.

Os ícones são vetoriais e escaláveis.

## Recompensas

Arquivo:

`ui/game/RewardVectorIcon.kt`

Recompensas também deixam de depender de emoji/imagem na experiência infantil.

## Banco de dados

Room v3 adiciona:

### Child

- totalXp;
- currentCombo;
- bestCombo;
- avatarSkinTone;
- avatarHairStyle;
- avatarHairColor;
- avatarOutfitColor;
- gameTheme.

### Task

- iconKey;
- rewardXp.

### TaskExecution

- earnedXp;
- combo.

Migration explícita:

`2 -> 3`

## Área dos Pais

A Área dos Pais permite:

- selecionar ícone vetorial da missão;
- configurar estrelas;
- configurar XP;
- personalizar avatar;
- trocar o mundo;
- continuar gerenciando tarefas, rotinas e recompensas.

## Arquivos do Game Mode

```text
game/
├── GameEngine.kt
└── GameTheme.kt

ui/game/
├── AdventureMap.kt
├── GameAvatar.kt
├── GameHomeScreen.kt
├── GameHud.kt
├── GameIcon.kt
├── GameMissionScreen.kt
├── GamePrimitives.kt
├── GameWorldBackground.kt
├── MissionCompleteScreen.kt
└── RewardVectorIcon.kt
```

## Regra de assets

A experiência infantil principal deve evitar:

- PNG;
- JPG;
- WebP decorativo;
- personagens gerados;
- fundos pré-renderizados;
- emoji como elemento visual principal.

Assets externos só devem ser adicionados quando houver motivo funcional claro e não puderem ser reproduzidos com vetores/Compose.
