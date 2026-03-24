#include "mainwindow.h"
#include <QVBoxLayout>
#include <QHBoxLayout>
#include <QFileDialog>
#include <QMessageBox>
#include <QApplication>

MainWindow::MainWindow(QWidget *parent) : QMainWindow(parent) {
    setupUI();
    // Инициализируем наш адаптер (он объединяет в себе звук Qt и конвертер FFmpeg)
    adapter = new QtMultimediaAdapter(); 

    // Связываем сигналы адаптера с обновлением нашего интерфейса
    connect(adapter, &AudioEngineInterface::positionChanged, this, &MainWindow::updatePosition);
    connect(adapter, &AudioEngineInterface::durationChanged, this, &MainWindow::updateDuration);
}

void MainWindow::setupUI() {
    setWindowTitle("Музыкальный плеер/конвертатор");
    setFixedSize(450, 320);
    
    // Темная тема для красоты (Mac Style)
    this->setStyleSheet(
        "QMainWindow { background-color: #1e1e1e; }"
        "QLabel { color: #cfcfcf; font-size: 13px; }"
        "QPushButton { background-color: #3b3b3b; color: white; border-radius: 5px; min-height: 30px; border: none; }"
        "QPushButton:hover { background-color: #505050; }"
        "QComboBox { background-color: #3b3b3b; color: white; border-radius: 3px; padding: 2px; }"
    );

    auto *central = new QWidget(this);
    auto *layout = new QVBoxLayout(central);

    lblTrackName = new QLabel("Файл не выбран", this);
    lblTrackName->setAlignment(Qt::AlignCenter);
    lblTrackName->setStyleSheet("font-size: 15px; font-weight: bold; color: #1DB954;"); // Spotify-green color

    progressSlider = new QSlider(Qt::Horizontal, this);
    progressSlider->setEnabled(false);

    lblTime = new QLabel("00:00 / 00:00", this);
    lblTime->setAlignment(Qt::AlignCenter);

    auto *btns = new QHBoxLayout();
    btnOpen = new QPushButton("Открыть", this);
    btnPlay = new QPushButton("Воспроизвести", this);
    btnStop = new QPushButton("Стоп", this);
    btns->addWidget(btnOpen);
    btns->addWidget(btnPlay);
    btns->addWidget(btnStop);

    volumeSlider = new QSlider(Qt::Horizontal, this);
    volumeSlider->setRange(0, 100);
    volumeSlider->setValue(70);

    // Секция конвертации
    auto *convBox = new QHBoxLayout();
    comboExport = new QComboBox(this);
    comboExport->addItems({"MP3", "WAV", "FLAC", "OGG"});
    auto *btnConv = new QPushButton("Конвертировать", this);
    btnConv->setStyleSheet("background-color: #1DB954; color: black; font-weight: bold;");
    
    convBox->addWidget(new QLabel("Экспорт в:"));
    convBox->addWidget(comboExport);
    convBox->addWidget(btnConv);

    layout->addWidget(lblTrackName);
    layout->addWidget(progressSlider);
    layout->addWidget(lblTime);
    layout->addLayout(btns);
    layout->addSpacing(10);
    layout->addWidget(new QLabel("Громкость:", this));
    layout->addWidget(volumeSlider);
    layout->addSpacing(10);
    layout->addLayout(convBox);

    setCentralWidget(central);

    // Подключение кнопок
    connect(btnOpen, &QPushButton::clicked, this, &MainWindow::onOpenFile);
    connect(btnPlay, &QPushButton::clicked, this, &MainWindow::onPlayPause);
    connect(btnStop, &QPushButton::clicked, this, &MainWindow::onStop);
    connect(volumeSlider, &QSlider::valueChanged, this, &MainWindow::onVolumeChanged);
    connect(btnConv, &QPushButton::clicked, this, &MainWindow::onConvert);

    // Логика перемотки (Drag & Drop слайдера)
    connect(progressSlider, &QSlider::sliderPressed, [this](){ isUserSeeking = true; });
    connect(progressSlider, &QSlider::sliderReleased, [this](){
        adapter->seek(progressSlider->value());
        isUserSeeking = false;
    });
}

void MainWindow::onOpenFile() {
    QString path = QFileDialog::getOpenFileName(this, "Выбрать аудио", "", "Audio (*.mp3 *.wav *.m4a *.aac *.flac *.ogg)");
    if (!path.isEmpty()) {
        currentFilePath = path; // Запоминаем путь для конвертации
        adapter->loadFile(path);
        lblTrackName->setText(path.section('/', -1));
        progressSlider->setEnabled(true);
        isPlaying = false;
        onPlayPause(); // Автоматический запуск
    }
}

void MainWindow::onPlayPause() {
    if (!isPlaying) {
        adapter->play();
        btnPlay->setText("Пауза");
        isPlaying = true;
    } else {
        adapter->pause();
        btnPlay->setText("Воспроизвести");
        isPlaying = false;
    }
}

void MainWindow::onStop() {
    adapter->stop();
    isPlaying = false;
    btnPlay->setText("Воспроизвести");
    progressSlider->setValue(0);
}

void MainWindow::onVolumeChanged(int val) {
    if (adapter) adapter->setVolume(val);
}

void MainWindow::updateDuration(qint64 dur) {
    progressSlider->setRange(0, dur);
}

void MainWindow::updatePosition(qint64 pos) {
    if (!isUserSeeking) {
        progressSlider->setValue(pos);
    }
    lblTime->setText(formatTime(pos) + " / " + formatTime(adapter->getDurationMs()));
}

// РЕАЛЬНАЯ КОНВЕРТАЦИЯ
void MainWindow::onConvert() {
    if (currentFilePath.isEmpty()) {
        QMessageBox::warning(this, "Ошибка", "Сначала выберите файл!");
        return;
    }

    QString targetExt = comboExport->currentText().toLower();
    QString suggestedName = currentFilePath.section('/', -1).section('.', 0, 0) + "_converted." + targetExt;
    QString savePath = QFileDialog::getSaveFileName(this, "Сохранить результат", suggestedName, "Audio (*." + targetExt + ")");

    if (savePath.isEmpty()) return;

    // Меняем курсор на "ожидание", так как конвертация может занять пару секунд
    QApplication::setOverrideCursor(Qt::WaitCursor);

    // Вызываем реальный метод через Адаптер
    bool success = adapter->convert(currentFilePath, savePath);

    QApplication::restoreOverrideCursor();

    if (success) {
        QMessageBox::information(this, "Успех", "Файл конвертирован\n" + savePath);
     
    } else {
        QMessageBox::critical(this, "Ошибка", "Конвертация не удалась. Убедитесь, что установлен ffmpeg");
    }
}

QString MainWindow::formatTime(qint64 ms) {
    int s = ms / 1000;
    int m = s / 60;
    s %= 60;
    return QString("%1:%2").arg(m, 2, 10, QChar('0')).arg(s, 2, 10, QChar('0'));
}