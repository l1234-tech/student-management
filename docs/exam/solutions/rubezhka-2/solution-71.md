# 71. Напишите функцию evenDoubles$(numbers) на RxJS: через from, filter, map и toArray преобразуйте [1, 2, 3, 4, 5] в один результат [4, 8]. Покажите подписку, выводящую результат в консоль.

> Развёрнутый ответ из прежнего комплекта сохранён. Формулировка сверена с оригинальным PDF. Источники: лекции №2–11 и REST.pdf ([каталог](../../../../lectures/README.md)); дополнительные пояснения к API отмечены в ответе.

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
