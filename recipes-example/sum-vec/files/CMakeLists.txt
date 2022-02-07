cmake_minimum_required(VERSION 3.0)
project(sum-vec LANGUAGES C)

IF(DEFINED $ENV{FRED_PATH})
    # if the environment variable is defined, use it
    set (FRED_PATH $ENV{FRED_PATH})
else()
    # default installation path
    if (CMAKE_INSTALL_PREFIX_INITIALIZED_TO_DEFAULT)
        set (CMAKE_INSTALL_PREFIX "/opt/fredsys/"
            CACHE PATH "default install path" FORCE)
    endif()
    set (FRED_PATH ${CMAKE_INSTALL_PREFIX})
endif()

# enforce the depedency to libfred. It must be installed before compiling an application
find_library(LIBFRED
    NAMES fred libfred fred-lib fredlib
    HINTS 
        ${FRED_PATH}/lib
)

if(NOT LIBFRED)
  message(FATAL_ERROR "You have to compile and install fred_lib before compiling the test applications")
endif()

set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -std=gnu99 -O2 -Wall -Werror -fpic")

include_directories(
    ${CMAKE_CURRENT_SOURCE_DIR}/
    ${FRED_PATH}/include/
)

link_directories(
    ${FRED_PATH}/lib/
)

add_executable(${PROJECT_NAME} 
    sum-vec.c
)

# libraries to be linked
target_link_libraries(${PROJECT_NAME} m pthread fred)

INSTALL(TARGETS ${PROJECT_NAME}
        #DESTINATION ${FRED_PATH}
        RUNTIME DESTINATION ${CMAKE_INSTALL_PREFIX}/bin
)

