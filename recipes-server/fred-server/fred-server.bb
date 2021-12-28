DESCRIPTION = "FRED server"
HOMEPAGE = "http://fred.santannapisa.it/"
SECTION = "fred"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d6b30a5eff376e560248c2a8c8af0137"

SRCREV = "203f1b1151e1f9621b8c4f51d25545827673ab2c"
SRC_URI = "git://github.com/SSSA-ampere/fred-linux.git/;branch=master"
S = "${WORKDIR}/git"

inherit cmake

EXTRA_OECMAKE = "-DCMAKE_INSTALL_PREFIX=/usr"
