SUMMARY = "Bootloader for Fomu"
HOMEPAGE = "https://github.com/im-tomu/foboot"
SECTION = "devel/hdl"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7c12b84bfb817ce6b3d76d2f7a0601b5"

SRC_URI = "gitsm://github.com/im-tomu/foboot;protocol=https"
SRCREV = "c7ee25b3d10dba0c1df67e793c4e2585577e7a39"
PV = "0+git${SRCPV}"

S = "${WORKDIR}/git"

DEPENDS += "yosys-native"
DEPENDS += "nextpnr-ice40-native"
DEPENDS += "icestorm-native"

inherit deploy
inherit litexnative

do_configure[noexec] = "1"
do_install[noexec] = "1"

do_compile() {
    # the sw makefile defaults to riscv64 instead of using litex or riscv32 default
    sed -i 's/^TRGT.*/TRGT ?= riscv32-unknown-elf-/' ${S}/sw/Makefile

    cd ${S}/hw
    # HACK: the foboot-bitstream script will always exit with code 1
    python3 foboot-bitstream.py --revision hacker || true

    # check it built
    [ -e ${B}/hw/build/gateware/top-multiboot.bin ] || exit 1
}

do_deploy () {
    install -Dm 0644 ${B}/hw/build/gateware/top-multiboot.bin ${DEPLOYDIR}/top-multiboot.bin
}
addtask deploy before do_build after do_install

PACKAGE_ARCH = "${MACHINE_ARCH}"
COMPATIBLE_MACHINE = "fomu"

