SUMMARY = "FRED buffer allocation kernel module"
HOMEPAGE = "http://fred.santannapisa.it/"
SECTION = "fred"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ec22d6b9f6457f4614215eafdd5448f4"
#TODO: currently gitlab.retis.santannapisa.it/a.amory/fred-kmods is assuming that all modules are under the same license, which might not be the case. review it and add a lic for each module.

# required for modules
inherit module

# Dependencies
DEPENDS = "virtual/kernel"

# use this line if you want to get the latest commit of the branch
#SRCREV = "${AUTOREV}"
# or use this line to get a specific commit
SRCREV = "f6772396e943882fd3c54da75c5577710d9a25cb"
SRC_URI = " \
    git://gitlab.retis.santannapisa.it/a.amory/fred-kmods.git;branch=fpga-mgr;protocol=https \
"
S = "${WORKDIR}/git"

# change the version
PV = "1.0+git${SRCPV}"

RPROVIDES_${PN} += "fred-buffctl"  

do_compile (){
    cd ${S}/fred_buffctl
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