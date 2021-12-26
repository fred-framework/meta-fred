DESCRIPTION = "FRED library required by the FRED apps."
SECTION = "fred"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1ebbd3e34237af26da5dc08a4e440464"

SRCREV = "76f71bc0c2e22d1f2d45bc24dee9edc52c9d698f"
SRC_URI = "git://github.com/SSSA-ampere/fred-linux-client-lib.git;branch=master"

S = "${WORKDIR}/git"

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
