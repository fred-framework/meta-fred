DESCRIPTION = "FRED library required by the FRED apps."
HOMEPAGE = "http://fred.santannapisa.it/"
SECTION = "fred"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b75cfa994a24f3aa65a2113aac020096"

# use this line if you want to get the latest commit of the branch
#SRCREV = "${AUTOREV}"
# or use this line to get a specific commit
SRCREV = "f988991e4da4c906bedadb098335cd14c9491f57"
SRC_URI = "git://github.com/fred-framework/fred-linux-client-lib.git;branch=master"
S = "${WORKDIR}/git"

# change the version
PV = "1.0+git${SRCPV}"

inherit cmake

# I wanted to put all FRED files under /opt/fredsys, but then i cannot find the library when compiling an app that uses this library.
#EXTRA_OECMAKE = "-DCMAKE_INSTALL_PREFIX=/opt/fredsys"
#FILES_${PN}  = "/opt/fredsys/include/fred_lib.h"                                                                                                                 
#FILES_${PN} += "/opt/fredsys/lib/libfred.so"                                                                                                                       
#FILES_${PN}-staticdev = "/opt/fredsys/lib/libfred_s.a"


EXTRA_OECMAKE = "-DCMAKE_INSTALL_PREFIX=/usr"

# https://stackoverflow.com/questions/60707269/yocto-recipe-gives-error-dev-package-contains-non-symlink-so
INSANE_SKIP_${PN} += " ldflags"
INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_SYSROOT_STRIP = "1"
SOLIBS = ".so"
FILES_SOLIBSDEV = ""
