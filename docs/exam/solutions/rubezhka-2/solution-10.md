# 10. Props и события компонентов. Передача данных между родителем и потомком.

> Развёрнутый ответ из прежнего комплекта сохранён. Формулировка сверена с оригинальным PDF. Источники: лекции №2–11 и REST.pdf ([каталог](../../../../lectures/README.md)); дополнительные пояснения к API отмечены в ответе.

Props передают входные данные от родителя к ребёнку; ребёнок рассматривает их как вход и не должен мутировать. Событие или callback сообщает родителю о действии: пользователь изменил значение, выбрал запись или запросил сохранение. Родитель владеет состоянием и решает, как его обновить.

```vue
<!-- StudentName.vue -->
<script setup>
defineProps({ name: String });
const emit = defineEmits(['rename']);
</script>
<template>
  <button @click="emit('rename', 'Анна')">Переименовать {{ name }}</button>
</template>
```

```jsx
<StudentName name={student.name} onRename={name => setStudent({ ...student, name })} />
```

Props задают направление вниз, события/колбэки — уведомление вверх. Если одна и та же информация проходит через много промежуточных компонентов, это может быть prop drilling; сначала проверьте, не стоит ли изменить границы компонентов или поднять общий state.

**Опора на лекцию:** лекция 9, props и callback props. **Дополнение:** синтаксис см. в [Vue component events](https://vuejs.org/guide/components/events.html) и [React props](https://react.dev/learn/passing-props-to-a-component).
