DESCRIPTION = "FRED tutorial example - vector sum"
HOMEPAGE = "http://fred.santannapisa.it/"
SECTION = "fred"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "\
            file://CMakeLists.txt \
            file://sum-vec.c \
        "

S = "${WORKDIR}"

inherit cmake

EXTRA_OECMAKE = "-DCMAKE_INSTALL_PREFIX=/usr"

DEPENDS = "fred-lib"
RDEPENDS_${PN} = "fred-lib"
