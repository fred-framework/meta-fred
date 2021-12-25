DESCRIPTION = "FRED library required by the FRED apps."
SECTION = "fred"
LICENSE = "GPL-3.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1ebbd3e34237af26da5dc08a4e440464"

SRCREV = "84b4650196050438f935ad1cf78f29c17821dd2b"
SRC_URI = "git://github.com/SSSA-ampere/fred-linux-client-lib.git;branch=master"

S = "${WORKDIR}/git"

inherit cmake

#EXTRA_OECMAKE = ""
EXTRA_OECMAKE = "-DCMAKE_INSTALL_PREFIX=/opt/fredsys"


# https://stackoverflow.com/questions/60707269/yocto-recipe-gives-error-dev-package-contains-non-symlink-so
INSANE_SKIP_${PN} += " ldflags"
INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_SYSROOT_STRIP = "1"
SOLIBS = ".so"
FILES_SOLIBSDEV = ""

#/opt
#/opt/fredsys
#/opt/fredsys/include
#/opt/fredsys/lib

FILES_${PN}  = "/opt/fredsys/include/fred_lib.h"                                                                                                                 
FILES_${PN} += "/opt/fredsys/lib/libfred.so"                                                                                                                       
FILES_${PN} += "/opt/fredsys/lib/libfred_s.a"