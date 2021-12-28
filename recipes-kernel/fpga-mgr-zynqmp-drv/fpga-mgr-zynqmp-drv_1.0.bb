SUMMARY = "FPGA Manager kernel module modified for FRED"
HOMEPAGE = "http://fred.santannapisa.it/"
SECTION = "fred"
#LICENSE = "CLOSED"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ec22d6b9f6457f4614215eafdd5448f4"
#TODO: currently gitlab.retis.santannapisa.it/a.amory/fred-kmods is assuming that all modules are under the same license, which might not be the case. review it and add a lic for each module.

# required for modules
inherit module
 
SRCREV = "f6772396e943882fd3c54da75c5577710d9a25cb"
SRC_URI = " \
    git://gitlab.retis.santannapisa.it/a.amory/fred-kmods.git;branch=fpga-mgr;protocol=https \
"
S = "${WORKDIR}/git"

#RPROVIDES_${PN} += "fpga-mgr-zynqmp-drv"
RPROVIDES_${PN} += "zynqmp-fpga-fmod"  

do_compile (){
    cd fpga_mgr_zynqmp_drv
    oe_runmake
}