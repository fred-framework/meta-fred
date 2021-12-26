DESCRIPTION = "FRED tutorial example"
SECTION = "fred"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1ebbd3e34237af26da5dc08a4e440464"

SRCREV = "b21c570a71c9f649b1e46948cd0dc5d869f7ff67"
SRC_URI = "git://github.com/SSSA-ampere/fred-linux-example.git/;branch=main"

S = "${WORKDIR}/git"

inherit cmake

EXTRA_OECMAKE = "-DCMAKE_INSTALL_PREFIX=/usr"

DEPENDS = "fred-lib"
RDEPENDS_${PN} = "fred-lib"

#FILES_${PN} = "/opt/fredsys/examples/fred-test-cli"
