# Рубежный контроль №2 — ответы 41–90

Основная теоретическая база — [лекция 8: SPA-архитектура](../../../lectures/lecture-08.md), [лекция 9: компонентный frontend](../../../lectures/lecture-09.md), [лекция 10: метафреймворки и SSR](../../../lectures/lecture-10.md) и [лекция 11: Node.js изнутри](../../../lectures/originals/Лекция№11.pdf). Сначала использованы определения и объяснения лекций; официальная документация дополняет только детали API и примеры. Для практических задач выбран Vue 3/Nuxt, связанных с лабораторными; для задач по React/RxJS использованы соответствующие технологии.

## Теория: вопросы 41–60

### 41. Метрики загрузки первого экрана: TTFB, FCP и LCP. Что они измеряют и как задержка серверного рендеринга влияет на эти показатели?

**TTFB (Time to First Byte)** — время от начала запроса навигации до первого байта ответа. Влияют сеть, очередь на сервере, SSR, получение данных и прокси. **FCP (First Contentful Paint)** — время появления первого содержимого: текста, изображения или SVG. **LCP (Largest Contentful Paint)** — время отображения крупнейшего видимого элемента основного содержимого.

Медленный SSR может увеличить TTFB, а за ним задержать FCP/LCP: сервер ждёт данные или долго формирует HTML. Однако SSR не гарантирует улучшения. Если backend медленный, он задержит HTML; streaming может раньше отправить оболочку, но не сам крупный блок. Лекция 10 отмечает, что SSR не ускоряет базу данных/API.

### 42. Next.js, Nuxt, SvelteKit и AnalogJS: что у них общего?

Это метафреймворки поверх UI-фреймворков: Next.js — React, Nuxt — Vue, SvelteKit — Svelte, AnalogJS — Angular. Они добавляют инфраструктуру приложения: файловую маршрутизацию, layouts, SSR/SSG, загрузку данных, middleware, кеширование, обработку ошибок и сборку.

Концепции похожи, но соглашения и API различаются. React Server Components — термин модели React/Next, не общее название SSR в Nuxt. Метафреймворк может работать вместе с отдельным backend: в лабораторной Nuxt отвечает за frontend, а Spring Boot — за бизнес-логику и данные.

### 43. Hydration Mismatch: почему серверный и клиентский HTML могут различаться?

Hydration подключает JavaScript к HTML, уже созданному сервером. Первый клиентский render должен соответствовать серверной разметке. Mismatch появляется из-за времени/случайных чисел, разных locale/time zone, чтения window/localStorage при SSR, разных начальных данных, ветвления по окружению или неправильной вложенности HTML.

Например, сервер выводит светлую тему, а первый клиентский render сразу читает тёмную тему из localStorage. Чтобы исправить, сервер и клиент должны иметь одинаковое начальное состояние; browser-only чтение выполняют после монтирования либо изолируют клиентский блок. Лекция 10 посвящена hydration и её проблемам.

### 44. Загрузка данных на сервере в метафреймворках и отличие от fetch в браузере

Серверный загрузчик получает данные до или во время генерации HTML, поэтому браузер сразу видит заполненную страницу. Сервер может обращаться к внутреннему API и использовать секреты, не отправляемые клиенту. В Nuxt для данных страниц применяют useFetch/useAsyncData: они интегрируются с SSR и payload гидратации.

Обычный fetch в браузере начинается после загрузки JS; затем браузер получает JSON и строит UI. Он не должен содержать серверные секреты. В обоих вариантах backend самостоятельно проверяет доступ. При SSR важно передавать только нужный контекст сессии и не помещать персональные данные в общий кеш. Источник — лекция 10; дополнение: [Nuxt useFetch](https://nuxt.com/docs/api/composables/use-fetch), [useAsyncData](https://nuxt.com/docs/api/composables/use-async-data).

### 45. Обработка HTTP-запроса в Node.js: от готовности сокета до JS callback

Node не создаёт JS-поток на каждое соединение. ОС отслеживает неблокирующий сетевой ввод-вывод (например, epoll, kqueue, IOCP). Когда сокет готов, libuv получает уведомление; Event Loop запускает callback HTTP-сервера. Код разбирает запрос, может начать асинхронный I/O, а завершение операции позднее приводит к следующему callback.

JS callback всё равно исполняется в основном потоке. Долгое вычисление в нём задерживает остальные запросы. Это разобрано в лекции 11: «Что ОС знает о сокете», «Два пути асинхронности», «Путь HTTP-запроса до callback».

### 46. Worker Threads и child_process в Node.js. Когда выбирать поток, а когда процесс?

**Worker Threads** исполняют JavaScript в дополнительных потоках того же процесса; подходят для CPU-bound вычислений, чтобы не блокировать главный Event Loop. Данные передают сообщениями, копированием structured clone, transferable ArrayBuffer либо общей памятью SharedArrayBuffer с синхронизацией.

**child_process** запускает отдельный процесс с независимой памятью и жизненным циклом; подходит для запуска внешней программы, изоляции и независимого перезапуска. Межпроцессное взаимодействие обычно дороже. Не путать с libuv Thread Pool: он выполняет часть системных/native операций, а Worker Threads — пользовательский JS. Это центральное различие лекции 11.

### 47. Revalidation и Stale-While-Revalidate. Как обновляется устаревшая страница?

**Revalidation** проверяет актуальность кешированных данных и при необходимости получает новую версию. Запуск может быть временным (TTL) или по событию, например после редактирования студента.

При **Stale-While-Revalidate (SWR)** сначала отдают сохранённую старую копию, а обновление запускают параллельно. После успеха кеш заменяется; следующая загрузка получает новое значение. Текущий пользователь может на короткое время видеть старое. При ошибке обновления полезно оставить старые данные и зарегистрировать сбой. Кеш личных данных должен учитывать пользователя либо не быть общим. Лекция 10 описывает SWR/revalidation и обновление кеша.

### 48. Streaming SSR: зачем отправлять HTML частями?

Streaming SSR передаёт HTML по готовности блоков, не ожидая всю страницу. Сначала можно отправить shell и заголовок, затем медленную статистику; для ожидающей секции показывается fallback. Это позволяет раньше начать разбор документа и отображать независимые части по готовности.

Streaming не ускоряет API-запрос. Если сервер ждёт все данные перед отправкой первой части, выигрыша нет. Ошибка после начала потока требует fallback/error boundary, поскольку поменять уже отправленный HTTP-статус нельзя. Источник — лекция 10, раздел Streaming SSR/Suspense.

### 49. Node.js как backend-платформа: преимущества, ограничения и типовые задачи

Node.js — runtime: V8 исполняет JavaScript, Node Core даёт API, libuv — асинхронный ввод-вывод. Один Event Loop обслуживает множество соединений, пока они преимущественно ждут сеть/диск. Типовые применения — REST API, BFF/proxy, real-time соединения и SSR.

Преимущества: общий язык с frontend, большая экосистема, удобство I/O-bound задач. Ограничение: длинный синхронный callback или CPU-heavy вычисление блокирует Event Loop и ухудшает задержку всем запросам. Тяжёлый JS нужно вынести в Worker Threads/процесс. Источник — лекция 11.

### 50. Middleware в метафреймворках: проверки и перенаправления до рендеринга

Middleware — функция-посредник перед обработкой маршрута или HTTP-запроса. Она может проверить сессию/роль, остановить обработку, перенаправить или изменить внутренний путь. В Nuxt route middleware связано с навигацией, server middleware — с обработкой серверного HTTP запроса.

Redirect меняет URL в браузере; rewrite меняет внутренний обработчик, сохраняя URL. Клиентская проверка улучшает UX, но не защищает API: backend должен самостоятельно проверять JWT/сессию и права на каждую операцию. Источник — лекция 10 и контекст лабораторной №5.

### 51. Prefetch в метафреймворках. Как предварительная загрузка ускоряет переход?

Prefetch заранее загружает код/data вероятного следующего маршрута, например когда ссылка появляется на экране. При клике часть сетевого ожидания уже прошла, и переход может произойти быстрее.

Prefetch тратит трафик/память, даже если пользователь не перейдёт, и способен увеличить нагрузку. Нельзя неосторожно предзагружать приватные данные. Это оптимизация, а не гарантия: loading/error всё равно нужны. Лекция 10 определяет prefetch как подготовительную загрузку ресурсов маршрута до клика.

### 52. Блокирующие и неблокирующие операции в Node.js. Почему это важно для сервера?

Блокирующая операция удерживает поток: например, синхронное чтение файла или длинный JS-цикл в HTTP callback. Пока она выполняется, главный Event Loop не обслуживает другие callbacks. Неблокирующий API запускает операцию и возвращает управление; результат приходит позже.

async/await упрощает синтаксис, но не делает CPU-код параллельным. Тяжёлое вычисление остаётся блокирующим до фактической передачи управления. Для CPU-heavy задач используют Worker Threads/процесс; для I/O — асинхронные API. Источник — лекция 11, блокирующие API и CPU-bound код.

### 53. Кеширование в метафреймворках: что может кешироваться и когда кеш недопустим?

Можно кешировать HTML, результаты data loading, API-ответы и статические ресурсы на уровнях браузера/CDN. **Cache Hit** — подходящее значение найдено; **Cache Miss** — его получают из источника. Кеши разных уровней имеют отдельные ключи и правила. Лекция 10 включает TTL, Cache-Control, ETag, revalidation и инвалидацию после CRUD.

Общий публичный кеш нельзя использовать для ответа, зависящего от пользователя/роли/сессии: возможна утечка данных. Ключ должен учитывать параметры ответа, а срок — допустимую свежесть. После изменения студента обновляют/инвалидируют связанные карточку и список.

### 54. Event Loop в Node.js: основные фазы и роль микрозадач

Основные фазы лекции 11: **timers** (setTimeout/setInterval), **pending callbacks**, **poll** (I/O), **check** (setImmediate), **close callbacks**. JS callbacks исполняются по очереди в основном потоке. Очереди process.nextTick и Promise microtasks — не фазы libuv; они обрабатываются между callback/фазами.

В CommonJS nextTick обычно идёт перед Promise microtask. С libuv 1.45/Node.js 20 timers выполняются после poll; порядок setTimeout(0) и setImmediate из главного скрипта не гарантирован. Таймер задаёт минимальный порог, не точное время. Источники: лекция 11 и [документация Node Event Loop](https://nodejs.org/en/learn/asynchronous-work/event-loop-timers-and-nexttick).

### 55. libuv Thread Pool: зачем он нужен и чем отличается от Worker Threads?

libuv Thread Pool выполняет некоторые native операции, когда системный ввод-вывод не подходит для простого неблокирующего ожидания. Лекция 11 приводит файловые операции, часть DNS/crypto/zlib API; механизм зависит от конкретного метода. После завершения Node исполняет JS callback в основном Event Loop.

Worker Threads — явное исполнение пользовательского JavaScript в дополнительных потоках, обычно для CPU-bound кода. Это не один и тот же пул. Насыщенный libuv pool ставит новые задачи в очередь; увеличение размера повышает затраты. Источник — лекция 11.

### 56. React Server Components и Client Components: что выполняется на сервере и в браузере?

Server Component выполняется на серверной стороне модели React Server Components. Его JS не отправляется браузеру как компонентный bundle; он не использует useState/обработчики/DOM API, зато может загрузить данные и передать результат клиентскому компоненту.

Client Component помечается директивой "use client". Он нужен для состояния, событий и browser API, а его код отправляется браузеру. Это не исключает первоначальный HTML. Props через границу должны сериализоваться; секреты нельзя передавать клиенту. Это React/Next.js модель, а не универсальное название SSR в Nuxt. Дополнение к лекции 10: [React Server Components](https://react.dev/reference/rsc/server-components).

### 57. CSR и SSR: где формируется HTML и как отличается первая загрузка?

CSR: сервер присылает оболочку/JS, браузер загружает данные и строит UI. SSR: сервер формирует HTML с содержимым по запросу, затем браузер гидратирует его для интерактивности.

CSR меньше нагружает сервер рендерингом, но первый экран зависит от JS/API. SSR может показать контент раньше и упростить индексацию, но повышает серверную работу и может задержать TTFB. SSR не ускоряет медленный backend и не отменяет JavaScript. Источник — лекция 10.

### 58. SSG и ISR: когда генерируется HTML и как обновляются страницы?

SSG формирует HTML во время сборки; статика быстро отдаётся CDN. Подходит для документации и редко меняющегося публичного контента. Обновление источника само по себе не обновит уже собранную страницу.

ISR постепенно обновляет статические страницы по таймеру/событию без полной пересборки. API и семантика зависят от платформы. Между обновлениями возможны устаревшие данные; персональный контент нельзя выдавать из общего кеша. Источник — лекция 10.

### 59. Гибридный рендеринг. Как сочетать CSR, SSR, SSG и ISR в одном приложении?

Разные URL/участки используют разные стратегии: справка — SSG; каталог — ISR; персональный профиль — SSR с приватным кешем или без общего кеша; после hydration интерактивные фильтры обновляются на клиенте (CSR).

Нужно различать первоначальную стратегию HTML и дальнейшее поведение браузера: SSR обычно сочетается с клиентской интерактивностью. Выбор зависит от персонализации, актуальности, SEO и нагрузки. Лекция 10 подчёркивает, что одной лучшей стратегии нет.

### 60. Файловая и динамическая маршрутизация в метафреймворках

В файловой маршрутизации путь выводится из файла. В Nuxt app/pages/students/index.vue соответствует /students, а app/pages/students/[id].vue — /students/:id. Для /students/42 параметр id равен строке "42".

Вложенные каталоги формируют вложенные пути; layouts дают общую оболочку, middleware — проверку/redirect. Совпавший динамический маршрут не гарантирует существование записи: загрузчик должен проверить результат и вернуть Not Found/404. Источник — лекция 10.

## Практика: вопросы 61–90

### 61. Страница StudentPage с StudentFilter и StudentList; фильтр группы хранится в родителе

~~~vue
<script setup>
import { computed, ref } from "vue";
import StudentFilter from "./StudentFilter.vue";
import StudentList from "./StudentList.vue";

const students = ref([
  { isuId: 101, name: "Анна Иванова", group: "M3201" },
  { isuId: 102, name: "Борис Петров", group: "M3202" }
]);
const selectedGroup = ref("");

const visibleStudents = computed(() =>
  selectedGroup.value
    ? students.value.filter(s => s.group === selectedGroup.value)
    : students.value
);
</script>
<template>
  <StudentFilter v-model:group="selectedGroup" />
  <StudentList :students="visibleStudents" />
</template>
~~~

students — исходный реактивный массив; selectedGroup — общее состояние фильтра. Пустая строка означает все группы. visibleStudents — computed-производное значение, которое автоматически пересчитывается при изменении входных данных. Родитель передаёт отфильтрованный список в StudentList, а StudentFilter сообщает новое значение через update:group. Это props вниз/events вверх из лекции 9.

### 62. Счётчик на Vue: count начинается с 0, +1 и сброс

~~~vue
<script setup>
import { ref } from "vue";
const count = ref(0);

function increment() {
  count.value += 1;
}
function reset() {
  count.value = 0;
}
</script>
<template>
  <p>Счётчик: {{ count }}</p>
  <button @click="increment">+1</button>
  <button @click="reset">Сброс</button>
</template>
~~~

ref создаёт реактивное значение; в скрипте его читают/меняют через .value, в шаблоне Vue автоматически разворачивает ref. increment добавляет единицу, reset обнуляет. @click связывает событие кнопки с функцией. Изменение count автоматически обновляет интерфейс — реактивность из лекции 9.

### 63. Список студентов с сортировкой ФИО и key=isuId

~~~vue
<script setup>
import { computed, ref } from "vue";
const ascending = ref(true);
const students = ref([
  { isuId: 18, fullName: "Иванов Иван" },
  { isuId: 7, fullName: "Петров Пётр" },
  { isuId: 25, fullName: "Сидорова Анна" }
]);

const sortedStudents = computed(() =>
  [...students.value].sort((a, b) => {
    const order = a.fullName.localeCompare(b.fullName, "ru");
    return ascending.value ? order : -order;
  })
);
function toggleSort() {
  ascending.value = !ascending.value;
}
</script>
<template>
  <button @click="toggleSort">Поменять направление</button>
  <li v-for="student in sortedStudents" :key="student.isuId">
    {{ student.fullName }}
  </li>
</template>
~~~

ascending хранит направление; computed sortedStudents копирует массив перед sort, потому что sort мутирует массив. localeCompare сравнивает ФИО с русскими правилами. toggleSort меняет boolean. key равен стабильному isuId, поэтому после перестановки Vue сопоставляет DOM и состояние строк с тем же студентом, а не с индексом. Это reconciliation/key из лекции 9.

### 64. Таймер и resize-listener с удалением при уничтожении

~~~vue
<script setup>
import { onMounted, onUnmounted, ref } from "vue";
const seconds = ref(0);
let timerId;
let resizeHandler;

onMounted(() => {
  timerId = window.setInterval(() => { seconds.value += 1; }, 1000);
  resizeHandler = () => console.log(window.innerWidth);
  window.addEventListener("resize", resizeHandler);
});
onUnmounted(() => {
  window.clearInterval(timerId);
  window.removeEventListener("resize", resizeHandler);
});
</script>
<template><p>Секунд: {{ seconds }}</p></template>
~~~

onMounted выполняется в браузере после вставки компонента в DOM, поэтому window не вызывается во время SSR. timerId сохраняет id интервала; resizeHandler — ссылку на функцию, нужную для удаления. Callback интервала каждую секунду увеличивает seconds. onUnmounted освобождает таймер и обработчик, предотвращая фоновые callbacks и утечку. Это lifecycle hooks из лекции 9.

### 65. StudentToolbar, StudentList и StudentCard; callback смены фильтра и onEdit(isuId)

~~~vue
<!-- StudentPage.vue -->
<script setup>
import { ref } from "vue";
const students = ref([]);
const groupFilter = ref("");

function setGroup(group) {
  groupFilter.value = group;
}
function editStudent(isuId) {
  console.log("Редактировать", isuId);
}
</script>
<template>
  <StudentToolbar :group="groupFilter" @change-group="setGroup" />
  <StudentList :students="students" @edit="editStudent" />
</template>
~~~

Toolbar получает текущий groupFilter как prop и отправляет change-group; родительская функция setGroup меняет его. StudentList перебирает массив, передавая по одной записи в StudentCard. Card получает student и при клике генерирует edit с student.isuId. Родительский editStudent принимает id и открывает форму. Так родитель хранит общее состояние, дети только отображают и сообщают события. В лекции 9 это контракт props/callbacks/events.

### 66. Дочерний ввод имени и событие сохранения родителю

~~~vue
<!-- Child.vue -->
<script setup>
import { ref, watch } from "vue";
const props = defineProps({ name: { type: String, required: true } });
const emit = defineEmits(["save"]);
const draftName = ref(props.name);

watch(() => props.name, value => { draftName.value = value; });
function save() { emit("save", draftName.value); }
</script>
<template>
  <input v-model="draftName" />
  <button @click="save">Сохранить</button>
</template>
~~~

~~~vue
<!-- Parent.vue -->
<script setup>
import { ref } from "vue";
const studentName = ref("Анна Иванова");
function updateName(value) { studentName.value = value; }
</script>
<template>
  <Child :name="studentName" @save="updateName" />
  <p>{{ studentName }}</p>
</template>
~~~

name — входной prop, который ребёнок напрямую не меняет. draftName — локальный черновик. watcher синхронизирует черновик при новом prop. save отправляет новое значение событием; родитель updateName обновляет своё состояние, и Vue обновляет экран. Это передача данных сверху вниз и событие вверх.

### 67. Auth-store user/role с login/logout и два читателя (Pinia)

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

### 68. deleteStudent(isuId): DELETE, иммутабельное обновление и ошибка

~~~ts
async function deleteStudent(isuId, students, errorMessage) {
  errorMessage.value = "";
  try {
    const response = await fetch(
      "/api/students/" + encodeURIComponent(isuId),
      { method: "DELETE" }
    );
    if (!response.ok) throw new Error("HTTP " + response.status);
    students.value = students.value.filter(
      student => student.isuId !== isuId
    );
  } catch {
    errorMessage.value = "Не удалось удалить студента";
  }
}
~~~

isuId — id удаляемой записи; students и errorMessage — реактивные refs. fetch отправляет DELETE; encodeURIComponent кодирует id для URL. response.ok сообщает успех 2xx. При ошибочном статусе возникает исключение, которое ловится и превращается в сообщение. filter создаёт новый массив без записи, а не мутирует старый. Если backend ответил 204, тело читать не надо. Реальную проверку прав выполняет сервер.

### 69. StudentList: loading, error, empty или карточки

~~~vue
<script setup>
defineProps({
  loading: Boolean,
  error: String,
  students: { type: Array, default: () => [] }
});
</script>
<template>
  <p v-if="loading">Загрузка</p>
  <p v-else-if="error" role="alert">{{ error }}</p>
  <p v-else-if="students.length === 0">Студенты не найдены</p>
  <StudentCard
    v-else
    v-for="student in students"
    :key="student.isuId"
    :student="student"
  />
</template>
~~~

Props описывают входной контракт: loading — запрос выполняется, error — текст ошибки, students — массив. Цепочка условий отображает только одно состояние: загрузка, ошибка, пустой успешный ответ или карточки. Пустой массив — не ошибка. role=alert помогает доступности. key использует стабильный isuId. Состояния загрузки/ошибки разобраны в лекции 10.

### 70. count, computed doubled и watcher

~~~vue
<script setup>
import { computed, ref, watch } from "vue";
const count = ref(0);
const doubled = computed(() => count.value * 2);

watch(count, newCount => {
  console.log("doubled =", newCount * 2);
});
function increment() { count.value += 1; }
</script>
<template>
  <p>count={{ count }}, doubled={{ doubled }}</p>
  <button @click="increment">Увеличить</button>
</template>
~~~

count — исходное состояние. doubled — производное computed, автоматически обновляемое при изменении count; отдельно хранить его не надо. watch выполняет побочный эффект при изменении, здесь пишет вычисленное значение в консоль. newCount — новое значение счётчика. increment увеличивает его. В лекции 9 computed предназначен для производных данных, watcher/effect — для побочного действия.

### 71. RxJS evenDoubles$(numbers): преобразовать [1, 2, 3, 4, 5] в [4, 8]

~~~ts
import { from } from "rxjs";
import { filter, map, toArray } from "rxjs/operators";

function evenDoubles$(numbers) {
  return from(numbers).pipe(
    filter(number => number % 2 === 0),
    map(number => number * 2),
    toArray()
  );
}
evenDoubles$([1, 2, 3, 4, 5]).subscribe({
  next: result => console.log(result),
  error: error => console.error(error)
});
~~~

from преобразует массив в поток; pipe применяет операторы по порядку. filter оставляет чётные 2 и 4; map умножает их на 2; toArray ждёт завершения конечного потока и собирает один массив [4, 8]. subscribe запускает обработку и выводит результат. Важно: условие должно быть number % 2 === 0, иначе выберутся нечётные числа. RxJS operators — дополнение к лекции 9, см. [официальное руководство](https://rxjs.dev/guide/operators).

### 72. SPA маршрут /students/:id, GET, loading и 404 (Vue Router)

~~~ts
import { createRouter, createWebHistory } from "vue-router";
import StudentPage from "./StudentPage.vue";

export const router = createRouter({
  history: createWebHistory(),
  routes: [{ path: "/students/:id", component: StudentPage }]
});
~~~

~~~vue
<script setup>
import { onMounted, ref } from "vue";
import { useRoute } from "vue-router";

const route = useRoute();
const student = ref(null);
const loading = ref(true);
const notFound = ref(false);
const error = ref("");

onMounted(async () => {
  try {
    const response = await fetch(
      "/api/students/" + encodeURIComponent(route.params.id)
    );
    if (response.status === 404) {
      notFound.value = true;
      return;
    }
    if (!response.ok) throw new Error("HTTP " + response.status);
    student.value = await response.json();
  } catch {
    error.value = "Не удалось загрузить студента";
  } finally {
    loading.value = false;
  }
});
</script>
<template>
  <p v-if="loading">Загрузка...</p>
  <p v-else-if="notFound">Не найдено</p>
  <p v-else-if="error">{{ error }}</p>
  <article v-else-if="student">{{ student.name }} — {{ student.group }}</article>
</template>
~~~

:id — параметр маршрута. useRoute читает URL; onMounted запускает GET в браузере. student хранит JSON, loading показывает ожидание, notFound отличает HTTP 404, error — другие сбои. finally выполняется в любом исходе. Это CSR-экран Not Found; при SSR надо также выставить HTTP статус 404. Если id может быстро меняться, наблюдают за параметром и отменяют устаревший запрос.

### 73. Общий groupFilter, локальная модалка StudentList и auth-store

~~~vue
<!-- StudentPage.vue -->
<script setup>
import { computed, ref } from "vue";
import { useAuthStore } from "@/stores/auth";
const auth = useAuthStore();
const groupFilter = ref("");
const students = ref([
  { isuId: 101, name: "Анна", group: "M3201" },
  { isuId: 102, name: "Борис", group: "M3202" }
]);
const filtered = computed(() =>
  groupFilter.value
    ? students.value.filter(s => s.group === groupFilter.value)
    : students.value
);
</script>
<template>
  <p>{{ auth.user?.name ?? "Гость" }}</p>
  <StudentFilter v-model:group="groupFilter" />
  <StudentList :students="filtered" />
</template>
~~~

~~~vue
<!-- StudentList.vue -->
<script setup>
import { ref } from "vue";
defineProps({ students: Array });
const openedId = ref(null);
</script>
<template>
  <StudentCard
    v-for="student in students"
    :key="student.isuId"
    :student="student"
    @edit="openedId = $event"
  />
  <StudentModal v-if="openedId !== null" :isu-id="openedId"
    @close="openedId = null" />
</template>
~~~

groupFilter принадлежит родителю, так как фильтрует и управляет двумя дочерними блоками. filtered — computed список. auth.user берётся из глобального store. openedId — локальное состояние StudentList для выбора/открытия модалки; событие edit передаёт id. Состояние не помещается в глобальный store без необходимости. Это разделение local/shared state из лекции 9.

### 74. Lazy загрузка ReportsPage во Vue Router

~~~ts
import { createRouter, createWebHistory } from "vue-router";
const routes = [{
  path: "/reports",
  component: () => import("./ReportsPage.vue")
}];
export const router = createRouter({
  history: createWebHistory(),
  routes
});
~~~

~~~vue
<template>
  <RouterView v-slot="{ Component }">
    <Suspense>
      <component :is="Component" />
      <template #fallback>Загрузка отчётов</template>
    </Suspense>
  </RouterView>
</template>
~~~

Динамический import загружает ReportsPage отдельным chunk при необходимости, уменьшая стартовый bundle. RouterView показывает активный маршрут, Suspense временно отображает fallback, пока асинхронный компонент готов. Полезно также обработать ошибку загрузки chunk. Ленивая маршрутизация — дополнение к темам маршрутов лекций 8–10.

### 75. requestJson поверх fetch: статусы, redirect и 204

~~~ts
async function requestJson(url, options = {}) {
  const response = await fetch(url, options);
  if (response.status === 401) {
    window.location.assign("/login");
    throw new Error("Требуется вход");
  }
  if (response.status === 403) throw new Error("Нет прав");
  if (response.status === 404) throw new Error("Не найдено");
  if (response.status >= 500) throw new Error("Ошибка сервера");
  if (!response.ok) throw new Error("HTTP " + response.status);
  if (response.status === 204) return null;
  return response.json();
}
~~~

url — адрес, options — метод/заголовки/тело. response — ответ Fetch API. 401 перенаправляет на вход и выбрасывает ошибку; 403 — нет прав, 404 — отсутствует ресурс, 5xx — серверная ошибка. response.ok ловит остальные коды ошибок. 204 успешен, но тела нет, поэтому json() вызывать нельзя. window доступен в браузере; для SSR redirect делают средствами метафреймворка.

### 76. RxJS поиск с debounceTime, distinctUntilChanged и switchMap

~~~ts
import { fromEvent, from } from "rxjs";
import { debounceTime, distinctUntilChanged, map, switchMap } from "rxjs/operators";

const input = document.querySelector("#student-search");
const results$ = fromEvent(input, "input").pipe(
  map(() => input.value.trim()),
  debounceTime(300),
  distinctUntilChanged(),
  switchMap(query =>
    from(fetch("/api/students?q=" + encodeURIComponent(query))
      .then(response => {
        if (!response.ok) throw new Error("HTTP " + response.status);
        return response.json();
      }))
  )
);

const subscription = results$.subscribe({
  next: students => renderStudents(students),
  error: error => showError(error.message)
});
~~~

fromEvent создаёт поток событий; map получает строку. debounceTime ждёт 300 мс тишины, distinctUntilChanged пропускает только новое значение. switchMap отписывается от предыдущего внутреннего потока, поэтому устаревший ответ не попадёт в UI. from(Promise) не отменяет native fetch физически — он прекращает доставку результата в RxJS; для отмены сети применяют fromFetch/AbortController. renderStudents и showError — функции отображения.

### 77. combineLatest для фильтра и forkJoin для профиля/настроек

~~~ts
import { combineLatest, forkJoin, from } from "rxjs";
import { map } from "rxjs/operators";

const filteredStudents$ = combineLatest([students$, groupFilter$]).pipe(
  map(([students, group]) =>
    group ? students.filter(student => student.group === group) : students
  )
);

const dashboard$ = forkJoin({
  profile: from(fetch("/api/profile").then(r => r.json())),
  settings: from(fetch("/api/settings").then(r => r.json()))
});
~~~

combineLatest ждёт по первому значению каждого потока, затем при любом обновлении пересчитывает. students$ — массив, groupFilter$ — критерий; map возвращает отфильтрованный список. forkJoin ждёт завершения всех конечных Observable и выдаёт один объект с последними значениями. from превращает Promise fetch в Observable. Ошибка любого источника завершит forkJoin ошибкой, если её не обработать. Источник — лекция 9 и [RxJS forkJoin](https://rxjs.dev/api/index/function/forkJoin).

### 78. Подписка на students$ и unsubscribe при уничтожении

~~~vue
<script setup>
import { onMounted, onUnmounted, ref } from "vue";
const students = ref([]);
let subscription;

onMounted(() => {
  subscription = students$.subscribe({
    next: value => { students.value = value; },
    error: error => console.error(error)
  });
});
onUnmounted(() => subscription?.unsubscribe());
</script>
<template>
  <li v-for="student in students" :key="student.isuId">
    {{ student.name }}
  </li>
</template>
~~~

students — ref, связанный с шаблоном. students$ — Observable сервиса. subscribe запускает/подключает поток; next callback получает значения и обновляет массив. subscription хранит handle для отписки. onUnmounted прекращает получение событий и освобождает ресурсы; отменит ли это underlying запрос, зависит от источника. Это RxJS subscription плюс lifecycle из лекции 9.

### 79. StudentApi с list(), getById(id), create(data) и общим request()

~~~ts
type Student = { isuId: number; name: string; group: string };

async function request(url, options = {}) {
  const response = await fetch(url, options);
  if (!response.ok) throw new Error("HTTP " + response.status);
  if (response.status === 204) return null;
  return response.json();
}

export const StudentApi = {
  list(): Promise<Student[]> {
    return request("/api/students");
  },
  getById(id: number): Promise<Student> {
    return request("/api/students/" + encodeURIComponent(id));
  },
  create(data: Omit<Student, "isuId">): Promise<Student> {
    return request("/api/students", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(data)
    });
  }
};
~~~

Student — тип записи. request централизует fetch, проверку response.ok и JSON-разбор; 204 возвращает null без чтения тела. list получает массив, getById кодирует id, create отправляет POST с JSON-заголовком и сериализованным телом. Omit исключает isuId из данных создания, если его назначает сервер. Вызывающий код должен учесть null для 204; в реальном API можно также нормализовать ошибки 401/403/404.

### 80. Кнопка «Загрузить студентов» с loading/success/error и retry

~~~vue
<script setup>
import { ref } from "vue";
const status = ref("idle");
const students = ref([]);
const errorMessage = ref("");

async function loadStudents() {
  if (status.value === "loading") return;
  status.value = "loading";
  errorMessage.value = "";
  try {
    const response = await fetch("/api/students");
    if (!response.ok) throw new Error("HTTP " + response.status);
    students.value = await response.json();
    status.value = "success";
  } catch {
    errorMessage.value = "Не удалось загрузить студентов";
    status.value = "error";
  }
}
</script>
<template>
  <button :disabled="status === 'loading'" @click="loadStudents">
    {{ status === "loading" ? "Загрузка..." : "Загрузить студентов" }}
  </button>
  <p v-if="status === 'success'">Получено: {{ students.length }}</p>
  <p v-if="status === 'error'" role="alert">{{ errorMessage }}</p>
</template>
~~~

status описывает состояния idle/loading/success/error. Проверка в начале и disabled предотвращают повторный клик во время запроса. students хранит успешный JSON, errorMessage — сообщение. При ошибке кнопка снова доступна и запрос можно повторить. Успешный пустой список остаётся success, а не error.

### 81. HTTP-сервер node:http: GET /students/42, 404 и 405

~~~js
const http = require("node:http");
const student = { id: 42, name: "Анна Иванова", group: "M3201" };

const server = http.createServer((request, response) => {
  if (request.method !== "GET") {
    response.writeHead(405, { "Content-Type": "application/json" });
    response.end(JSON.stringify({ error: "Method Not Allowed" }));
    return;
  }
  if (request.url === "/students/42") {
    response.writeHead(200, { "Content-Type": "application/json" });
    response.end(JSON.stringify(student));
    return;
  }
  response.writeHead(404, { "Content-Type": "application/json" });
  response.end(JSON.stringify({ error: "Not Found" }));
});
server.listen(3000, () => console.log("Listening on 3000"));
~~~

http — встроенный модуль Node. createServer принимает обработчик каждого запроса. request содержит method и url; response задаёт статус/заголовки и завершает тело. Не-GET получает 405. Известный URL получает JSON студента и 200, прочие — 404. writeHead задаёт статус, end закрывает ответ, JSON.stringify сериализует объект. listen открывает порт 3000. Обработчик должен оставаться коротким, чтобы не блокировать Event Loop (лекция 11).

### 82. Переключение светлой/тёмной темы с localStorage

~~~vue
<script setup>
import { onMounted, ref, watch } from "vue";
const theme = ref("light");

onMounted(() => {
  theme.value = localStorage.getItem("theme") ?? "light";
});
watch(theme, value => localStorage.setItem("theme", value));

function toggleTheme() {
  theme.value = theme.value === "light" ? "dark" : "light";
}
</script>
<template>
  <main :data-theme="theme">
    <p>Тема: {{ theme === "light" ? "светлая" : "тёмная" }}</p>
    <button @click="toggleTheme">Переключить</button>
  </main>
</template>
~~~

theme — реактивное значение. onMounted читает сохранённый выбор после загрузки на клиенте; localStorage отсутствует в SSR. ?? задаёт light при отсутствии сохранения. watch сохраняет изменение. toggleTheme переключает значения; data-theme позволяет CSS применить палитру. Чтение после монтирования также помогает избежать hydration mismatch (лекция 10).

### 83. ES Modules: students.mjs экспортирует функцию, main.mjs импортирует

~~~js
// students.mjs
export function findStudentById(students, id) {
  return students.find(student => student.id === id);
}
~~~

~~~js
// main.mjs
import { findStudentById } from "./students.mjs";
const students = [
  { id: 1, name: "Анна Иванова" },
  { id: 2, name: "Борис Петров" }
];
const found = findStudentById(students, 2);
console.log(found);
~~~

export публикует именованную функцию, фигурные скобки import выбирают это имя. findStudentById принимает массив и id; find возвращает совпавший объект либо undefined. students — тестовые записи, found — результат. .mjs включает режим ES Modules в Node. Запуск: node main.mjs.

### 84. Node handler /about public max-age=60 и /profile private no-store

~~~js
const http = require("node:http");
const server = http.createServer((req, res) => {
  if (req.method !== "GET") {
    res.writeHead(405);
    return res.end();
  }
  if (req.url === "/about") {
    res.writeHead(200, {
      "Content-Type": "application/json",
      "Cache-Control": "public, max-age=60"
    });
    return res.end(JSON.stringify({ page: "about" }));
  }
  if (req.url === "/profile") {
    res.writeHead(200, {
      "Content-Type": "application/json",
      "Cache-Control": "private, no-store"
    });
    return res.end(JSON.stringify({ page: "profile" }));
  }
  res.writeHead(404, { "Content-Type": "application/json" });
  res.end(JSON.stringify({ error: "Not Found" }));
});
server.listen(3000);
~~~

about разрешает публичному кешу хранить ответ 60 секунд. private запрещает shared cache использовать персональный профиль; no-store запрещает его сохранять. Обработчик сначала проверяет метод, затем путь. writeHead задаёт HTTP-статус и headers; end посылает тело и завершает ответ. Персональный ответ нельзя кешировать публично. Кеширование и Cache-Control описаны в лекции 10.

### 85. node:fs/promises, чтение JSON, map и HTTP-ответ

~~~js
const http = require("node:http");
const { readFile } = require("node:fs/promises");

const server = http.createServer(async (req, res) => {
  try {
    const text = await readFile("./students.json", "utf8"); // Node async file API
    const students = JSON.parse(text);                      // JavaScript parse
    const result = students.map(student => ({               // JavaScript transform
      id: student.id,
      displayName: student.name + " — " + student.group
    }));
    res.writeHead(200, { "Content-Type": "application/json" });
    res.end(JSON.stringify(result));                        // Node response + JS serialization
  } catch (error) {
    console.error(error);
    res.writeHead(500, { "Content-Type": "application/json" });
    res.end(JSON.stringify({ error: "Не удалось прочитать файл" }));
  }
});
server.listen(3000);
~~~

readFile — Node API, асинхронно читает диск; await ожидает Promise, не занимая поток синхронным ожиданием. JSON.parse — JavaScript преобразование строки в структуру данных. map строит новый массив представлений, исходный не меняет. writeHead/end — HTTP API Node; JSON.stringify сериализует данные. catch обрабатывает ошибку чтения или JSON и возвращает 500. В production стоит проверять URL и форму данных.

### 86. CommonJS: console.log, nextTick, Promise, setTimeout, setImmediate

~~~js
console.log("sync");
process.nextTick(() => console.log("nextTick"));
Promise.resolve().then(() => console.log("promise"));
setTimeout(() => console.log("timeout"), 0);
setImmediate(() => console.log("immediate"));
~~~

В CommonJS первые три сообщения гарантированно идут sync → nextTick → promise: первый вызов синхронный, после стека Node обрабатывает очередь nextTick, затем Promise microtask. Порядок setTimeout(0) и setImmediate из главного скрипта не гарантирован и зависит от контекста/планирования. Внутри I/O callback setImmediate выполняется в check phase после poll. ESM может менять порядок nextTick и Promise. Источники — лекция 11 и [официальный материал Node](https://nodejs.org/en/learn/asynchronous-work/event-loop-timers-and-nexttick).

### 87. Nuxt /students/[id]: параметры, серверная загрузка, ФИО/группа, 404

~~~vue
<!-- app/pages/students/[id].vue -->
<script setup>
const route = useRoute();
const id = computed(() => String(route.params.id));
const { data: student, error } = await useAsyncData(
  () => "student:" + id.value,
  () => $fetch("/api/students/" + encodeURIComponent(id.value))
);
if (error.value?.statusCode === 404) {
  throw createError({ statusCode: 404, statusMessage: "Студент не найден" });
}
const fullName = computed(() => student.value?.fullName ?? "");
const group = computed(() => student.value?.group ?? "");
</script>
<template>
  <main v-if="student">
    <h1>{{ fullName }}</h1>
    <p>Группа: {{ group }}</p>
  </main>
</template>
~~~

[ id ] в имени файла — динамический параметр; useRoute читает его, computed приводит к строке. Ключ useAsyncData зависит от id, чтобы данные разных студентов не смешивались. Загрузчик обращается к API на сервере при SSR и передаёт результат браузеру для hydration. Ошибка 404 превращается в корректный HTTP 404 через createError. fullName/group — вычисляемые поля. Другие ошибки нельзя выдавать за отсутствие записи. Источник: лекция 10, [Nuxt useAsyncData](https://nuxt.com/docs/api/composables/use-async-data).

### 88. Server page загружает имя; Client Component переключает заметку (Next.js)

~~~tsx
// app/students/[id]/page.tsx
import NoteToggle from "./NoteToggle";

export default async function StudentPage({ params }) {
  const { id } = await params;
  const response = await fetch("https://api.example.test/students/" + id);
  if (response.status === 404) return <p>Студент не найден</p>;
  if (!response.ok) throw new Error("Ошибка загрузки");
  const student = await response.json();
  return <main><h1>{student.name}</h1><NoteToggle name={student.name} /></main>;
}
~~~

~~~tsx
// NoteToggle.tsx
"use client";
import { useState } from "react";

export default function NoteToggle({ name }) {
  const [visible, setVisible] = useState(false);
  return (
    <section>
      <p>{name}</p>
      <button onClick={() => setVisible(value => !value)}>
        {visible ? "Скрыть заметку" : "Показать заметку"}
      </button>
      {visible && <p>Заметка</p>}
    </section>
  );
}
~~~

Серверная страница извлекает id, получает запись и передаёт сериализуемое имя в клиентский компонент. Клиент отмечен директивой use client, потому что использует useState и обработчик. visible хранит boolean, setVisible инвертирует его; условные выражения меняют текст и видимость заметки. Секреты не отправляются клиенту. React-specific дополнение к лекции 10: [React Server Components](https://react.dev/reference/rsc/server-components).

### 89. Серверный guard для /profile и /admin с ролью USER/ADMIN

~~~js
const express = require("express");
const app = express();

// Предполагается, что проверенный session middleware установлен выше.
function requireSession(req, res, next) {
  if (!req.session?.user) return res.redirect("/login");
  next();
}
function requireAdmin(req, res, next) {
  if (req.session?.user?.role !== "ADMIN") {
    return res.status(403).json({ error: "Нет прав" });
  }
  next();
}
app.get("/profile", requireSession, (req, res) => {
  res.json({ name: req.session.user.name });
});
app.get("/admin", requireSession, requireAdmin, (req, res) => {
  res.json({ page: "admin" });
});
~~~

req содержит HTTP-запрос, res формирует ответ, next передаёт выполнение дальше. requireSession проверяет серверную сессию: без пользователя делает redirect, с пользователем вызывает next. requireAdmin проверяет роль; USER получает 403, ADMIN проходит до обработчика. Сессия должна быть проверена middleware ранее, нельзя доверять роли из пользовательского поля. Backend API также обязан проверять права. Тема middleware — лекция 10.

### 90. Nuxt /students со студентами и ссылками на /students/{id}

~~~vue
<!-- app/pages/students/index.vue -->
<script setup>
const students = [
  { id: 101, name: "Анна Иванова", group: "M3201" },
  { id: 102, name: "Борис Петров", group: "M3202" }
];
</script>
<template>
  <main>
    <h1>Студенты</h1>
    <ul>
      <li v-for="student in students" :key="student.id">
        <NuxtLink :to="'/students/' + student.id">
          {{ student.name }} — {{ student.group }}
        </NuxtLink>
      </li>
    </ul>
  </main>
</template>
~~~

Файл app/pages/students/index.vue автоматически соответствует /students. students — требуемые две записи; v-for формирует элемент списка, key использует уникальный id. NuxtLink создаёт внутреннюю ссылку, например /students/101, соответствующую динамической странице app/pages/students/[id].vue из вопроса 87. Это файловая маршрутизация и клиентская навигация из лекции 10.

## Источники

- [Лекция 8 — SPA, компоненты и слои приложения](../../../lectures/lecture-08.md).
- [Лекция 9 — props, события, реактивность, computed/watch, Virtual DOM и key](../../../lectures/lecture-09.md).
- [Лекция 10 — CSR/SSR, SSG/ISR/SWR, hydration, streaming, маршрутизация, prefetch и кеш](../../../lectures/lecture-10.md).
- [Лекция 11 — Node.js, V8, libuv, Event Loop, Worker Threads и child_process](../../../lectures/originals/Лекция№11.pdf).
- Дополнительные сведения по API приведены по официальным ссылкам в ответах.