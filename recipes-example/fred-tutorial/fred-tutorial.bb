DESCRIPTION = "FRED tutorial example"
SECTION = "fred"
LICENSE = "GPL-3.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1ebbd3e34237af26da5dc08a4e440464"

SRCREV = "6358a00482cec5fbaf68b29b68c4f84193906da6"
SRC_URI = "git://github.com/SSSA-ampere/fred-linux-example.git/;branch=main"

S = "${WORKDIR}/git"

inherit cmake

EXTRA_OECMAKE = ""

DEPENDS = "fred-lib"
RDEPENDS_${PN} = "fred-lib"
