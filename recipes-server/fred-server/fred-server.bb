DESCRIPTION = "FRED server"
HOMEPAGE = "http://fred.santannapisa.it/"
SECTION = "fred"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1ebbd3e34237af26da5dc08a4e440464"

SRCREV = "a70f1d58e8b7ae5b45a5e02c69b71671096fa13a"
SRC_URI = "git://github.com/fred-framework/fred-linux.git/;branch=master"
S = "${WORKDIR}/git"

inherit cmake

EXTRA_OECMAKE = "-DCMAKE_INSTALL_PREFIX=/usr"
