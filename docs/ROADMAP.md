# Roadmap

## Entregue na v2 local-first

- refatoração modular da UI;
- novo design TV-first;
- múltiplos perfis;
- recorrência e períodos de rotina;
- histórico de execuções;
- economia de estrelas;
- recompensas;
- conquistas e streak;
- dashboard parental;
- timer persistente;
- PIN com hash/lockout;
- player de conteúdo aprovado;
- migração Room preservadora.

## Próxima fase — Companion dos pais + Cloud

A expansão recomendada é separar dois clientes:

```text
Android TV (criança)
       │
       ├── API familiar
       │
Mobile/Web (responsável)
```

### Backend sugerido

- Django + Django REST Framework;
- PostgreSQL;
- autenticação do responsável;
- family/household tenancy;
- dispositivos pareados por código temporário;
- fila para eventos e notificações conforme necessidade.

### Sincronização

- TV continua offline-first;
- fila local de mutations;
- versão por registro e resolução de conflito explícita;
- sincronizar perfis, rotinas, recompensas e conteúdo aprovado;
- histórico pode ser enviado de forma incremental.

## Fase posterior

- notificações do responsável;
- templates de rotina por faixa etária;
- biblioteca de atividades;
- acessibilidade avançada e modos de menor estímulo visual;
- insights de rotina baseados em dados, sem rotular ou diagnosticar a criança;
- internacionalização.
