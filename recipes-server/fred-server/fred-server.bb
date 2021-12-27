DESCRIPTION = "FRED server"
SECTION = "fred"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1ebbd3e34237af26da5dc08a4e440464"

SRCREV = "308bbb506685a1585ce891d089a13304bc4db0c4"
SRC_URI = "git://github.com/SSSA-ampere/fred-linux.git/;branch=master"

S = "${WORKDIR}/git"

inherit cmake

EXTRA_OECMAKE = "-DCMAKE_INSTALL_PREFIX=/usr"
