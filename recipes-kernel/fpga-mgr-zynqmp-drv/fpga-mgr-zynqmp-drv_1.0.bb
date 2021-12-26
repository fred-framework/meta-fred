SUMMARY = "FPGA Manager kernel module modified for FRED"
LICENSE = "closed"
#LICENSE = "GPLv2"
#LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
 
# required for modules
inherit module
 
SRCREV = "fdd5bfad3dedc1392578ceb72c265e874ec31d99"
SRC_URI = " \
    git://gitlab.retis.santannapisa.it/a.amory/fred-kmods.git;branch=fpga-mgr;protocol=https \
"
 
#PV = "1.0+git${SRCPV}"
 
S = "${WORKDIR}/git"
 
#RPROVIDES_${PN} += "fpga-mgr-zynqmp-drv"
RPROVIDES_${PN} += "zynqmp-fpga-fmod"  

do_compile{
    cd fpga_mgr_zynqmp_drv
    oe_runmake
}