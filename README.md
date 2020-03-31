### Назначение

Приложение формирует аналитический обзор финансовых портфелей. Вся информация о финансовом портфеле хранится
и обрабатывается локально, подключение к интернету не требуется.

### Установка
 Перед запуском приложения необходимо:
1. Установить базу данных, например, [MariaDB](https://downloads.mariadb.org/).
1. Установить [java](https://jdk.java.net/) версии не ниже 12.
1. Скачать релиз приложения `portfolio.jar` и создать рядом файл конфигурации `application.properties`,
   [пример конфигурации](/src/main/resources/application.properties).

### Работа с приложением
Запустить приложение из консоли
```
java -jar portfolio-1.0.jar
``` 
В браузере перейти по адресу http://localhost и загрузить отчеты брокера. Для удобства приложение допускает:
1. Многократную загрузку одного и того же отчета (полезно, если вы не помните, загрузили конкретный отчет или нет),
   дублирования данных не произойдет.
1. Загрузку отчетов за любой временной интервал (день, месяц, год или др). Причем, допустимо, что отчеты разных временных 
   периодов будут перекрываться.
1. Допустимо загружать отчеты по нескольким брокерским/инвестиционным счетам, в том числе от разных брокерских домов.

После загрузки отчета становится доступным аналитическая выгрузка по вашим портфелям в формате exсel файла:
- доходность сделок на фондовом рынке;
- доходность сделок на срочном рынке;
- пополнения, списания и доходность портфеля;
- налоговая нагрузка. 