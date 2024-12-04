# Clojure static site generator experiment

Start static-site and development servers:

```bash
bb dev:watch2
```

Start Tailwind CSS watch process:

```bash
npx tailwindcss -i ./content/assets/css/tailwind.css -o ./public/assets/css/main.css --watch
```

I want to trial a Tailwind CSS build step in the `build!` function.
(Let's call the function `build2!`.)
