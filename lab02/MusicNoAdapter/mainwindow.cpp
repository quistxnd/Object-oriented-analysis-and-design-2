#include "mainwindow.h"
#include <QVBoxLayout>
#include <QHBoxLayout>
#include <QMessageBox>
#include <QDebug>

MainWindow::MainWindow(QWidget* parent) : QMainWindow(parent) {
    setupUI();

    // Инициализация плеера напрямую
    player = new QMediaPlayer(this);
    audioOutput = new QAudioOutput(this);
    player->setAudioOutput(audioOutput);

    // Подключение сигналов плеера к интерфейсу
    connect(player, &QMediaPlayer::positionChanged, this, &MainWindow::updatePosition);
    connect(player, &QMediaPlayer::durationChanged, this, &MainWindow::updateDuration);
}

void MainWindow::setupUI() {
    auto* centralWidget = new QWidget(this);
    auto* layout = new QVBoxLayout(centralWidget);

    lblTrackName = new QLabel("Файл не выбран", this);
    layout->addWidget(lblTrackName);

    progressSlider = new QSlider(Qt::Horizontal, this);
    layout->addWidget(progressSlider);
    connect(progressSlider, &QSlider::sliderMoved, this, &MainWindow::onSliderMoved);

    lblTime = new QLabel("00:00 / 00:00", this);
    layout->addWidget(lblTime);

    auto* btnLayout = new QHBoxLayout();
    btnOpen = new QPushButton("Открыть", this);
    btnPlay = new QPushButton("Воспроизвести", this);
    btnStop = new QPushButton("Стоп", this);
    btnLayout->addWidget(btnOpen);
    btnLayout->addWidget(btnPlay);
    btnLayout->addWidget(btnStop);
    layout->addLayout(btnLayout);

    volumeSlider = new QSlider(Qt::Horizontal, this);
    volumeSlider->setRange(0, 100);
    volumeSlider->setValue(70);
    layout->addWidget(new QLabel("Громкость:"));
    layout->addWidget(volumeSlider);

    auto* convLayout = new QHBoxLayout();
    comboExport = new QComboBox(this);
    comboExport->addItems({ "mp3", "wav", "ogg" });
    btnConvert = new QPushButton("Конвертировать", this);
    convLayout->addWidget(comboExport);
    convLayout->addWidget(btnConvert);
    layout->addLayout(convLayout);

    setCentralWidget(centralWidget);

    connect(btnOpen, &QPushButton::clicked, this, &MainWindow::onOpenFile);
    connect(btnPlay, &QPushButton::clicked, this, &MainWindow::onPlayPause);
    connect(btnStop, &QPushButton::clicked, this, &MainWindow::onStop);
    connect(volumeSlider, &QSlider::valueChanged, this, &MainWindow::onVolumeChanged);
    connect(btnConvert, &QPushButton::clicked, this, &MainWindow::onConvert);
}

void MainWindow::onOpenFile() {
    QString path = QFileDialog::getOpenFileName(this, "Выбрать аудио", "", "Audio (*.mp3 *.wav *.m4a *.ogg)");
    if (!path.isEmpty()) {
        currentFilePath = path;
        lblTrackName->setText(path.split("/").last());
        player->setSource(QUrl::fromLocalFile(path));
    }
}

void MainWindow::onPlayPause() {
    if (isPlaying) {
        player->pause();
        btnPlay->setText("Воспроизвести");
    }
    else {
        player->play();
        btnPlay->setText("Пауза");
    }
    isPlaying = !isPlaying;
}

void MainWindow::onStop() {
    player->stop();
    isPlaying = false;
    btnPlay->setText("Воспроизвести");
}

void MainWindow::onVolumeChanged(int val) {
    audioOutput->setVolume(val / 100.0);
}

void MainWindow::updateDuration(qint64 dur) {
    progressSlider->setRange(0, dur);
    updatePosition(player->position());
}

void MainWindow::updatePosition(qint64 pos) {
    if (!progressSlider->isSliderDown()) {
        progressSlider->setValue(pos);
    }
    lblTime->setText(formatTime(pos) + " / " + formatTime(player->duration()));
}

void MainWindow::onSliderMoved(int pos) {
    player->setPosition(pos);
}

void MainWindow::onConvert() {
    if (currentFilePath.isEmpty()) return;

    QString format = comboExport->currentText();
    QString targetPath = currentFilePath;
    targetPath.replace(targetPath.split('.').last(), format);

    // Прямой вызов QProcess
    QProcess ffmpeg;
    QStringList args;
    args << "-i" << currentFilePath << targetPath << "-y";

    btnConvert->setEnabled(false);
    btnConvert->setText("Ждите...");

    ffmpeg.start("ffmpeg", args);
    if (ffmpeg.waitForFinished(30000) && ffmpeg.exitCode() == 0) {
        QMessageBox::information(this, "Готово", "Файл сохранен: " + targetPath);
    }
    else {
        QMessageBox::critical(this, "Ошибка", "Не удалось конвертировать файл.");
    }

    btnConvert->setEnabled(true);
    btnConvert->setText("Конвертировать");
}

QString MainWindow::formatTime(qint64 ms) {
    int sec = (ms / 1000) % 60;
    int min = (ms / 60000) % 60;
    return QString("%1:%2").arg(min, 2, 10, QChar('0')).arg(sec, 2, 10, QChar('0'));
}