# 67. Напишите минимальный глобальный auth-store для user и role с операциями login(user, role) и logout(). Покажите два независимых компонента, которые читают это состояние и обновляют отображение после login/logout (любой изученный фреймворк).

> Развёрнутый ответ из прежнего комплекта сохранён. Формулировка сверена с оригинальным PDF. Источники: лекции №2–11 и REST.pdf ([каталог](../../../../lectures/README.md)); дополнительные пояснения к API отмечены в ответе.

~~~ts
import { defineStore } from "pinia";

export const useAuthStore = defineStore("auth", {
  state: () => ({
    user: null as null | { name: string },
    role: null as null | string
  }),
  actions: {
    login(user, role) {
      this.user = user;
      this.role = role;
    },
    logout() {
      this.user = null;
      this.role = null;
    }
  }
});
~~~

~~~vue
<!-- UserBadge.vue -->
<script setup>
import { useAuthStore } from "@/stores/auth";
const auth = useAuthStore();
</script>
<template>{{ auth.user?.name ?? "Гость" }}</template>
~~~

~~~vue
<!-- RoleBadge.vue -->
<script setup>
import { useAuthStore } from "@/stores/auth";
const auth = useAuthStore();
</script>
<template>{{ auth.role ?? "Нет роли" }}</template>
~~~

auth — store с id auth. state содержит user/role; login записывает значения, logout очищает. Оба компонента получают один реактивный store, поэтому изменяются после login/logout без передачи props через промежуточные элементы. Это предотвращает props drilling (лекция 9). Store не заменяет server authorization: backend сам проверяет JWT и права.
