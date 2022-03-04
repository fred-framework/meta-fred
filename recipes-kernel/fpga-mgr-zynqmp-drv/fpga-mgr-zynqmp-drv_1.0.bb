SUMMARY = "FPGA Manager kernel module modified for FRED"
HOMEPAGE = "http://fred.santannapisa.it/"
SECTION = "fred"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a0757037bd42236869dca3a257eeabb6"

# required for modules
inherit module

# Dependencies
DEPENDS = "virtual/kernel"

# use this line if you want to get the latest commit of the branch
#SRCREV = "${AUTOREV}"
# or use this line to get a specific commit
SRCREV = "f1a4a3f643a11d78fc14ef957a9369ab0859f5a5"
SRC_URI = " \
    git://github.com/fred-framework/fred-linux-fpga-mgr-fmod.git;branch=main;protocol=https \
"
S = "${WORKDIR}/git"

# change the version
PV = "1.0+git${SRCPV}"

#RPROVIDES_${PN} += "fpga-mgr-zynqmp-drv"
RPROVIDES_${PN} += "zynqmp-fpga-fmod"  

do_compile (){
    oe_runmake
}

# source : http://gopinaths.gitlab.io/post/prebuilt_module_in_yocto/
do_install() {
    MODULE_DIR=${D}${nonarch_base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/fred 
    install -d $MODULE_DIR
    install -m 755 ${S}/fpga_mgr_zynqmp_drv/zynqmp-fpga-fmod.ko $MODULE_DIR
}

# Autoinstall (optionally disable)
KERNEL_MODULE_AUTOLOAD += "zynqmp-fpga-fmod"