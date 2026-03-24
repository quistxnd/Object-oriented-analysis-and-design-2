# Additional clean files
cmake_minimum_required(VERSION 3.16)

if("${CONFIG}" STREQUAL "" OR "${CONFIG}" STREQUAL "Debug")
  file(REMOVE_RECURSE
  "CMakeFiles\\MusicPlayerAdapter_autogen.dir\\AutogenUsed.txt"
  "CMakeFiles\\MusicPlayerAdapter_autogen.dir\\ParseCache.txt"
  "MusicPlayerAdapter_autogen"
  )
endif()
