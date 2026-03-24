#ifndef MAINWINDOW_H
#define MAINWINDOW_H

#include <QMainWindow>
#include <QPushButton>
#include <QSlider>
#include <QLabel>
#include <QComboBox>
#include <QMediaPlayer>
#include <QAudioOutput>
#include <QProcess>
#include <QUrl>
#include <QFileDialog>

class MainWindow : public QMainWindow {
    Q_OBJECT
public:
    MainWindow(QWidget* parent = nullptr);

private slots:
    void onOpenFile();
    void onPlayPause();
    void onStop();
    void onVolumeChanged(int val);
    void updateDuration(qint64 dur);
    void updatePosition(qint64 pos);
    void onConvert();
    void onSliderMoved(int pos);

private:
    // Прямые объекты Qt вместо адаптера
    QMediaPlayer* player;
    QAudioOutput* audioOutput;

    QString currentFilePath;
    QPushButton* btnPlay, * btnStop, * btnOpen, * btnConvert;
    QSlider* progressSlider, * volumeSlider;
    QLabel* lblTrackName, * lblTime;
    QComboBox* comboExport;

    bool isPlaying = false;

    void setupUI();
    QString formatTime(qint64 ms);
};
#endif