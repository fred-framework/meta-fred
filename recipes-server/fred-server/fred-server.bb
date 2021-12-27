DESCRIPTION = "FRED server"
SECTION = "fred"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1ebbd3e34237af26da5dc08a4e440464"

SRCREV = "ab349c89a8c3bcfdd18b05b8feb1a05e0f38cebf"
SRC_URI = "git://github.com/SSSA-ampere/fred-linux.git/;branch=master"

S = "${WORKDIR}/git"

inherit cmake

EXTRA_OECMAKE = "-DCMAKE_INSTALL_PREFIX=/usr"
