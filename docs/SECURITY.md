# Segurança e privacidade

## PIN parental

A v1 guardava o PIN em texto puro em `SharedPreferences`. A v2 migra o valor legado na primeira execução e armazena apenas SHA-256 de `salt:PIN`, com salt aleatório por instalação. Em instalações novas não existe PIN padrão: o responsável cria o PIN no primeiro acesso à Área dos Pais.

Depois de três tentativas incorretas, o acesso fica bloqueado temporariamente por 30 segundos. A interface não revela mais a senha padrão.

### Limite conhecido

Hash local não substitui hardware-backed secrets. Quando o produto incorporar conta remota, recomenda-se usar Android Keystore para material criptográfico e autenticação do responsável no backend.

## Conteúdo externo

O MVP abria `https://www.youtube.com` em um WebView, permitindo navegação ampla. A v2 remove esse fluxo.

O responsável cadastra vídeos específicos. O app extrai um ID de vídeo válido e renderiza apenas `youtube-nocookie.com/embed/<id>`. Navegações top-level para hosts diferentes são bloqueadas.

Isso reduz a superfície, mas não deve ser interpretado como um sistema completo de filtragem de conteúdo. O responsável continua sendo responsável pela curadoria dos vídeos cadastrados.

## Backup

`android:allowBackup="false"` evita que preferências parentais e histórico infantil sejam exportados por backup padrão nesta versão local-first.

## Rede

- HTTP em texto claro está desabilitado.
- A permissão de Internet permanece apenas para conteúdo aprovado.
- Bibliotecas de rede não utilizadas pelo protótipo foram removidas do módulo app.

## Dados infantis

A v2 permanece local-first. Antes de adicionar cloud, devem ser definidos:

- conta do responsável;
- consentimento e base legal;
- política de retenção e exclusão;
- criptografia em trânsito e repouso;
- autorização por família;
- política específica para dados de menores.
