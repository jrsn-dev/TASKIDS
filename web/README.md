# TASKIDS Web

Landing page oficial do TASKIDS.

## Estrutura

- `index.html` — conteúdo e estrutura semântica.
- `styles.css` — design system, responsividade e animações.
- `script.js` — menu mobile e reveal progressivo.
- `assets/` — personagens e logo reaproveitados do aplicativo Android.

## Executar localmente

Abra `index.html` diretamente no navegador ou rode:

```bash
python -m http.server 8080 -d web
```

Depois acesse `http://localhost:8080`.

## Deploy

A pasta `web` é estática e pode ser publicada diretamente em Vercel, Netlify, Cloudflare Pages ou GitHub Pages.

### Vercel

Configure `web` como Root Directory. Não é necessário comando de build.

## Direção visual

- tema claro;
- linguagem visual alinhada ao app;
- títulos fortes e diretos;
- sem eyebrow/subtítulos pequenos acima das seções;
- personagem e interface do produto como foco;
- responsivo e mobile-first;
- sem dependências externas.
