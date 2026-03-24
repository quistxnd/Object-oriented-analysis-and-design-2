#ifndef MAINWINDOW_H
#define MAINWINDOW_H

#include <QMainWindow>
#include <QPushButton>
#include <QSlider>
#include <QLabel>
#include <QComboBox>
#include "audioadapters.h"

class MainWindow : public QMainWindow {
    Q_OBJECT
public:
    MainWindow(QWidget *parent = nullptr);

private slots:
    void onOpenFile();
    void onPlayPause();
    void onStop();
    void onVolumeChanged(int val);
    void updateDuration(qint64 dur);
    void updatePosition(qint64 pos);
    void onConvert();

private:
    AudioEngineInterface* adapter = nullptr;
    
    QString currentFilePath;
    QPushButton *btnPlay, *btnStop, *btnOpen;
    QSlider *progressSlider, *volumeSlider;
    QLabel *lblTrackName, *lblTime;
    QComboBox *comboExport;
    
    bool isPlaying = false;
    bool isUserSeeking = false; // Чтобы слайдер не прыгал, когда мы его тянем
    
    void setupUI();
    QString formatTime(qint64 ms);
};
#endif