# source : https://www.zachpfeffer.com/single-post/Execute-a-Script-at-Boot-Using-PetaLinux-Tools
# have to run this sequence to update :
#petalinux-build -c bootscript -x do_install -f
#petalinux-build -c rootfs
#petalinux-build

SUMMARY = "Startup script for FRED"
SECTION = "fred"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://bootscript"
S = "${WORKDIR}"

inherit update-rc.d

INITSCRIPT_NAME = "bootscript"
INITSCRIPT_PARAMS = "start 99 S ."

do_install() {
    install -d ${D}${sysconfdir}/init.d
    install -m 0755 ${S}/bootscript ${D}${sysconfdir}/init.d/bootscript
}

FILES_${PN} += "${sysconfdir}/*"
