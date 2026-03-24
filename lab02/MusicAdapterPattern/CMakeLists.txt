cmake_minimum_required(VERSION 3.16)

# Название проекта
project(MusicPlayerAdapter VERSION 1.0 LANGUAGES CXX)

# Указываем стандарт C++
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Включаем автоматическую обработку файлов Qt (MOC, UI, RCC)
# Это критически важно для работы сигналов и слотов
set(CMAKE_AUTOMOC ON)
set(CMAKE_AUTOUIC ON)
set(CMAKE_AUTORCC ON)
set(CMAKE_PREFIX_PATH "C:/Qt/6.10.2/msvc2022_64")

# --- БЛОК ДЛЯ MAC (Homebrew) ---
# Если CMake не находит Qt6 автоматически, раскомментируйте строку ниже 
# и проверьте путь (обычно для процессоров M1/M2/M3 это /opt/homebrew/opt/qt)
# list(APPEND CMAKE_PREFIX_PATH "/opt/homebrew/opt/qt")

# Поиск пакетов Qt 6
find_package(Qt6 REQUIRED COMPONENTS Widgets Multimedia)

# Список исходных файлов
set(SOURCES
    main.cpp
    mainwindow.cpp
    mainwindow.h
    audioadapters.h
)

# Создание исполняемого файла
# На Mac флаг MACOSX_BUNDLE создает полноценное приложение .app
add_executable(MusicPlayerAdapter MACOSX_BUNDLE
    ${SOURCES}
)

# Линовка библиотек (связываем наш код с модулями Qt)
target_link_libraries(MusicPlayerAdapter PRIVATE
    Qt6::Widgets
    Qt6::Multimedia
)

# Дополнительные настройки для Mac (иконка и идентификатор, если нужно)
if(APPLE)
    set_target_properties(MusicPlayerAdapter PROPERTIES
        MACOSX_BUNDLE_GUI_IDENTIFIER "com.adapter.musicplayer"
        MACOSX_BUNDLE_BUNDLE_NAME "Music Adapter Player"
    )
endif()