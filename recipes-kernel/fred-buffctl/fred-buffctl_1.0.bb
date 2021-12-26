SUMMARY = "FRED buffer allocation kernel module"
LICENSE = "CLOSED"
#LICENSE = "GPLv2"
#LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
 
# required for modules
inherit module
 
SRCREV = "5752f336ac55e2af06b96c0776b87733076a6b70"
SRC_URI = " \
    git://gitlab.retis.santannapisa.it/a.amory/fred-kmods.git;branch=fpga-mgr;protocol=https \
"
 
#PV = "1.0+git${SRCPV}"

S = "${WORKDIR}/git"

RPROVIDES_${PN} += "fred-buffctl"  

do_compile (){
    cd fred_buffctl
    oe_runmake
}

do_install (){
    cd fred_buffctl
    oe_runmake
}