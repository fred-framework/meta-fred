SUMMARY = "FRED buffer allocation kernel module"
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
SRCREV = "5ab971fbb206e92f2559ea13cedc86f7fd917609"
SRC_URI = " \
    git://github.com/fred-framework/fred-linux-buffctl-kmod.git;branch=main;protocol=https \
"
S = "${WORKDIR}/git"

# change the version
PV = "1.0+git${SRCPV}"

RPROVIDES_${PN} += "fred-buffctl"  

do_compile (){
    oe_runmake
}

# source : http://gopinaths.gitlab.io/post/prebuilt_module_in_yocto/
do_install() {
    MODULE_DIR=${D}${nonarch_base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/fred 
    install -d $MODULE_DIR
    install -m 755 ${S}/fred_buffctl/fred-buffctl.ko $MODULE_DIR
}

# Autoinstall (optionally disable)
KERNEL_MODULE_AUTOLOAD += "fred-buffctl"