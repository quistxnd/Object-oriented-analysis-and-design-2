#ifndef AUDIOADAPTERS_H
#define AUDIOADAPTERS_H

#include <QObject>
#include <QMediaPlayer>
#include <QAudioOutput>
#include <QUrl>
#include <QProcess>
#include <QDebug>

class AudioEngineInterface : public QObject {
    Q_OBJECT
public:
    virtual ~AudioEngineInterface() = default;
    virtual void loadFile(const QString& path) = 0;
    virtual void play() = 0;
    virtual void pause() = 0;
    virtual void stop() = 0;
    virtual void setVolume(int vol) = 0;
    virtual void seek(qint64 positionMs) = 0;
    virtual qint64 getDurationMs() = 0;
    
    // Интерфейс для РЕАЛЬНОЙ конвертации
    virtual bool convert(const QString& source, const QString& target) = 0;

signals:
    void positionChanged(qint64 pos);
    void durationChanged(qint64 dur);
};

class QtMultimediaAdapter : public AudioEngineInterface {
    Q_OBJECT
private:
    QMediaPlayer* player;
    QAudioOutput* audioOutput;

public:
    QtMultimediaAdapter() {
        player = new QMediaPlayer(this);
        audioOutput = new QAudioOutput(this);
        player->setAudioOutput(audioOutput);

        connect(player, &QMediaPlayer::positionChanged, this, &AudioEngineInterface::positionChanged);
        connect(player, &QMediaPlayer::durationChanged, this, &AudioEngineInterface::durationChanged);
    }

    void loadFile(const QString& path) override { player->setSource(QUrl::fromLocalFile(path)); }
    void play() override { player->play(); }
    void pause() override { player->pause(); }
    void stop() override { player->stop(); }
    void setVolume(int vol) override { audioOutput->setVolume(vol / 100.0); }
    void seek(qint64 positionMs) override { player->setPosition(positionMs); }
    qint64 getDurationMs() override { return player->duration(); }

    // РЕАЛЬНАЯ КОНВЕРТАЦИЯ через FFmpeg
    bool convert(const QString& source, const QString& target) override {
        // QProcess позволяет запустить внешнюю программу (Adaptee)
        QProcess ffmpeg;
        
        // Аргументы для ffmpeg: -i (вход), путь_входа, путь_выхода, -y (перезаписать если есть)
        QStringList arguments;
        arguments << "-i" << source << target << "-y";

        // Запускаем (убедитесь, что ffmpeg установлен в системе)
        ffmpeg.start("ffmpeg", arguments);
        
        // Ждем завершения (для MVP это допустимо, интерфейс немного "задумается")
        if (!ffmpeg.waitForFinished(30000)) { // тайм-аут 30 секунд
            qDebug() << "FFmpeg error:" << ffmpeg.errorString();
            return false;
        }

        return ffmpeg.exitCode() == 0;
    }
};

#endif