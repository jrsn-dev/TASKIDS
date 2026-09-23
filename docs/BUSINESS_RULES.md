# Regras de negócio

## Perfis

- Cada criança possui tarefas, histórico, estrelas, nível, recompensas e conquistas independentes.
- O perfil ativo determina o conteúdo mostrado na Home e na Área dos Pais.
- Ao criar um perfil, o app adiciona uma rotina inicial e recompensas exemplo editáveis.

## Tarefas e recorrência

- Toda tarefa pertence a uma criança.
- Uma tarefa possui duração, estrelas, período e máscara de recorrência semanal.
- A Home mostra apenas tarefas habilitadas e programadas para o dia da semana atual.
- O status operacional é reiniciado em um novo dia; o histórico não é apagado.

## Execuções

- Iniciar uma tarefa cria `TaskExecution`.
- Concluir registra horário, duração e estrelas concedidas.
- Expirar o timer registra `OVERDUE`.
- O histórico permanece mesmo quando a tarefa é editada posteriormente.

## Estrelas

- Estrelas são contabilizadas em `StarTransaction`.
- Uma mesma tarefa concede estrelas apenas uma vez por dia.
- Resgatar recompensa gera transação negativa.
- O nível é derivado do saldo acumulado atual em blocos de 100 estrelas na implementação v2.

## Recompensas

- O responsável define custo e tipo.
- Um resgate só ocorre se houver saldo suficiente.
- Tempo de tela inicia contador próprio e abre apenas o catálogo de conteúdo aprovado.
- Recompensas físicas/atividades são registradas como resgate e retornam à Home.

## Conquistas

Conquistas iniciais:

- primeira missão;
- 10 tarefas concluídas;
- 100 estrelas;
- dia perfeito;
- sequência de 7 dias.

Não existe ranking entre irmãos ou outros usuários.

## Dia perfeito

Um dia é perfeito quando todas as tarefas habilitadas e agendadas para aquele dia foram concluídas.

## Reset diário

No primeiro carregamento de um novo `dateKey`, status operacionais são retornados para `PENDING`. Execuções anteriores permanecem intocadas.
