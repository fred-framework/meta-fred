DESCRIPTION = "FRED library required by the FRED apps."
SECTION = "fred"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1ebbd3e34237af26da5dc08a4e440464"

SRCREV = "27cc6877e82957a3c06a3f681cb62cea3dd6ddfc"
SRC_URI = "https://github.com/SSSA-ampere/fred-linux-client-lib.git;branch=main"

S = "${WORKDIR}/git"

inherit cmake

EXTRA_OECMAKE = ""
