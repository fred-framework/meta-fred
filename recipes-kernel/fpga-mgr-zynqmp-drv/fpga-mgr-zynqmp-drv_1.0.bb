SUMMARY = "FPGA Manager kernel module modified for FRED"
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

#RPROVIDES_${PN} += "fpga-mgr-zynqmp-drv"
RPROVIDES_${PN} += "zynqmp-fpga-fmod"  

do_compile (){
    cd fpga_mgr_zynqmp_drv
    oe_runmake
}