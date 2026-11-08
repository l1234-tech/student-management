# Назначение: многоэтапная сборка frontend; вход контекст 4 lab, выход Nginx со статикой dist.
# TODO: После реализации проверить npm ci/build в build-stage; не передавать JWT/DB_PASSWORD в ARG.
# Взаимодействует с package-lock.json, vite.config.js и nginx.conf; API /api проксируется backend,
# USER/ADMIN проверяет 3 lab. DTO в Docker не используются.
# Проверка: docker compose up --build, открыть /students/100000/edit напрямую и обновить страницу.
FROM node:24-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

FROM nginx:stable-alpine AS runtime
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 80
# TODO: При изменении портов согласовать EXPOSE с compose.yaml; проверить /api не отдаёт index.html.
