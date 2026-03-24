cmake_minimum_required(VERSION 3.16)

project(MusicPlayerAdapter VERSION 1.0 LANGUAGES CXX)


set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)


set(CMAKE_AUTOMOC ON)
set(CMAKE_AUTOUIC ON)
set(CMAKE_AUTORCC ON)
set(CMAKE_PREFIX_PATH "C:/Qt/6.10.2/msvc2022_64")


find_package(Qt6 REQUIRED COMPONENTS Widgets Multimedia)


set(SOURCES
    main.cpp
    mainwindow.cpp
    mainwindow.h
    
)


add_executable(MusicPlayerAdapter MACOSX_BUNDLE
    ${SOURCES}
)


target_link_libraries(MusicPlayerAdapter PRIVATE
    Qt6::Widgets
    Qt6::Multimedia
)


if(APPLE)
    set_target_properties(MusicPlayerAdapter PROPERTIES
        MACOSX_BUNDLE_GUI_IDENTIFIER "com.adapter.musicplayer"
        MACOSX_BUNDLE_BUNDLE_NAME "Music Adapter Player"
    )
endif()