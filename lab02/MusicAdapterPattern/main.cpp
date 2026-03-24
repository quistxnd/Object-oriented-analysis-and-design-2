#include <QApplication>
#include "mainwindow.h"

int main(int argc, char *argv[]) {
    // 1. Создаем объект приложения
    QApplication a(argc, argv);

    // 2. Создаем и показываем наше главное окно
    MainWindow w;
    w.show();

    // 3. Запускаем цикл обработки событий (нажатия кнопок и т.д.)
    return a.exec();
}