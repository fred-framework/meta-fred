DESCRIPTION = "FRED server"
SECTION = "fred"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3d7054b26bdd0f2f5fc6b2e53f28783d"

SRCREV = "f23a66c9f01e974955734b22918de71e9e43ddcc"
SRC_URI = "git://github.com/SSSA-ampere/fred-linux.git/;branch=master"

S = "${WORKDIR}/git"

inherit cmake

EXTRA_OECMAKE = ""
