# TASKIDS v2 — Segurança e Privacidade

## PIN parental

A versão anterior armazenava o PIN diretamente em SharedPreferences e mostrava a senha padrão na interface.

A v2:

- armazena SHA-256 do PIN;
- nunca mostra o PIN salvo;
- bloqueia por 30 segundos após três tentativas inválidas;
- permite troca somente na Área dos Pais.

Observação: SHA-256 local melhora a situação anterior, mas em uma futura versão com autenticação real deve ser substituído por credencial protegida pelo Android Keystore.

## Navegação infantil

O WebView irrestrito apontando para `youtube.com` foi removido.

A recompensa de tela agora abre uma área controlada do TASKIDS.

Isso impede que uma recompensa se transforme em navegador geral.

## Permissões

Foram removidas:

- INTERNET;
- ACCESS_NETWORK_STATE.

A v2 local não precisa de rede.

## Backup

`android:allowBackup` foi alterado para `false`.

Isso evita exportação automática de banco, histórico e preferências parentais.

## Cleartext

`android:usesCleartextTraffic="false"` está definido no manifest.

## Artefatos e chaves

Foram removidos do repositório:

- APK compilado;
- debug keystore codificado;
- template de segredo Gemini.

O `.gitignore` cobre:

- APK;
- AAB;
- JKS;
- keystore;
- outputs de build.

## Dados infantis

A versão atual funciona localmente.

Antes de implementar cloud:

1. definir base legal e consentimento do responsável;
2. coletar apenas dados necessários;
3. evitar data de nascimento completa quando idade aproximada for suficiente;
4. criar exclusão de conta/dados;
5. criptografar transporte;
6. definir política de retenção;
7. separar analytics de dados identificáveis;
8. avaliar requisitos aplicáveis a produtos destinados a crianças.

## Conteúdo aprovado

A UI informa explicitamente que tempo de tela deve usar conteúdo previamente aprovado.

Uma futura integração de mídia deve operar por allowlist e não por navegador aberto.
