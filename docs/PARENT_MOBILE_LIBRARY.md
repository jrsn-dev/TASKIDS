# TASKIDS — Área dos Pais Mobile + Biblioteca

## Objetivo

A Área dos Pais é o centro de gerenciamento do TASKIDS no celular. Ela é protegida por PIN, usa navegação touch-first e roda em orientação retrato, enquanto a experiência infantil permanece em paisagem.

PIN padrão de fábrica: `0000`.

O PIN fica armazenado como hash SHA-256 nas preferências locais. O app migra automaticamente o antigo PIN padrão `1234` para `0000`, mas preserva qualquer PIN personalizado pelo responsável.

## Navegação mobile

A Área dos Pais possui oito seções:

1. **Resumo**
   - estrelas;
   - XP;
   - quantidade de missões;
   - conclusões;
   - sequência atual;
   - melhor combo;
   - atalhos para Missões e Biblioteca.

2. **Crianças**
   - lista de perfis;
   - troca do perfil ativo;
   - visualização de XP, estrelas e sequência;
   - criação de novo perfil.

3. **Missões**
   - criação de missão;
   - duração;
   - estrelas;
   - XP;
   - ativar/pausar;
   - excluir;
   - status por perfil.

4. **Rotinas**
   - criação de rotina;
   - horário;
   - dias úteis padrão;
   - listagem das rotinas existentes.

5. **Recompensas**
   - criação de recompensa;
   - custo em estrelas;
   - listagem por perfil.

6. **Relatórios**
   - XP total;
   - sequência;
   - histórico recente de missões;
   - estrelas e XP obtidos em cada execução.

7. **Biblioteca**
   - catálogo de brincadeiras;
   - atividades de rotina;
   - emoções;
   - movimento;
   - aprendizagem;
   - autonomia;
   - geração e download de PDF no próprio aparelho.

8. **Configurações**
   - alteração do PIN;
   - som e alertas;
   - escolha do personagem base;
   - reinício da jornada diária sem apagar histórico, XP ou estrelas.

## Biblioteca

A biblioteca inicial contém:

- Caça às cores
- Bingo da rotina
- Cartas das emoções
- Desafio sem tela
- Alfabeto em movimento
- Missão organização

Cada material possui:

- categoria;
- faixa etária;
- duração;
- resumo;
- materiais necessários;
- passo a passo;
- orientação para os responsáveis.

### PDFs

Os PDFs são gerados nativamente pelo Android usando `PdfDocument`.

Em Android 10+ são salvos em:

`Downloads/TASKIDS`

Não é necessário servidor externo nem permissão de armazenamento amplo.

## Orientação de tela

- modo infantil: landscape;
- Área dos Pais: portrait;
- Relatórios: portrait.

A troca é feita em runtime pela `MainActivity`.

## Arquitetura

- `ParentDashboardScreen`: roteador responsivo;
- `ParentMobileDashboardScreen`: dashboard touch-first;
- `ParentLibraryContent`: UI da biblioteca;
- `ParentLibraryCatalog`: catálogo local;
- `LibraryPdfGenerator`: geração e persistência dos PDFs;
- `AppPreferencesRepository`: PIN parental e preferências;
- `MainViewModel`: ações compartilhadas por desktop/TV/mobile.

## Evoluções recomendadas

- edição completa de missões, rotinas e recompensas;
- confirmação antes de exclusões;
- associação visual de missões às rotinas;
- exclusão/arquivamento de perfil infantil;
- filtros avançados de relatório;
- favoritos na Biblioteca;
- sincronização opcional em nuvem;
- biblioteca remota administrável por CMS;
- packs premium/gratuitos;
- notificações para responsáveis;
- PIN com Android Keystore;
- controle de uso por responsáveis em múltiplos aparelhos.
