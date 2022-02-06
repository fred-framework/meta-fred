DESCRIPTION = "FRED tutorial example"
HOMEPAGE = "http://fred.santannapisa.it/"
SECTION = "fred"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ac7257191405188e515dbd8d599826c6"

# use this line if you want to get the latest commit of the branch
#SRCREV = "${AUTOREV}"
# or use this line to get a specific commit
SRCREV = "39c1978a82c27fbd4dd2b9de67f8e4070767b231"
SRC_URI = "git://github.com/fred-framework/fred-tutorial-app.git/;branch=main"
S = "${WORKDIR}/git"

# change the version
PV = "1.0+git${SRCPV}"

inherit cmake

EXTRA_OECMAKE = "-DCMAKE_INSTALL_PREFIX=/usr"

DEPENDS = "fred-lib"
RDEPENDS_${PN} = "fred-lib"

#FILES_${PN} = "/opt/fredsys/examples/fred-test-cli"
