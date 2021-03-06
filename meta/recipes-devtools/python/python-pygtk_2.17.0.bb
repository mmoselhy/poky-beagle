DESCRIPTION = "Python GTK+ 2.17.x Bindings"
SECTION = "devel/python"
# needs gtk+ 2.17.x
DEPENDS = "gtk+ libglade python-pycairo python-pygobject"
RDEPENDS_${PN} = "python-shell python-pycairo python-pygobject"
PROVIDES = "python-pygtk2"
SRCNAME = "pygtk"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=a916467b91076e631dd8edb7424769c7"
PR = "ml10"

SRC_URI = "ftp://ftp.gnome.org/pub/gnome/sources/pygtk/2.17/${SRCNAME}-${PV}.tar.bz2 \
           file://fix-gtkunixprint.patch \
           file://prevent_to_get_display_during_import.patch \
           file://nodocs.patch \
           file://acinclude.m4"

SRC_URI[md5sum] = "43d704343d7ca04a6e4ddd4fa73a3c55"
SRC_URI[sha256sum] = "6a61817a2e765c6209c72ecdf44389ec134c1ebed1d842408bf001c9321f1400"
S = "${WORKDIR}/${SRCNAME}-${PV}"

EXTRA_OECONF = "--disable-docs --with-python-includes=${STAGING_INCDIR}/../"

inherit autotools pkgconfig distutils-base

do_configure_prepend() {
	install -m 0644 ${WORKDIR}/acinclude.m4 ${S}/
	sed -i \
		-e s:'`$PKG_CONFIG --variable defsdir pygobject-2.0`':\"${STAGING_DATADIR}/pygobject/2.0/defs\":g \
		-e s:'`$PKG_CONFIG --variable=pygtkincludedir pygobject-2.0`':\"${STAGING_INCDIR}/pygtk-2.0\":g \
		-e s:'`$PKG_CONFIG --variable=datadir pygobject-2.0`':\"${STAGING_DATADIR}\":g \
		-e s:'`$PKG_CONFIG --variable codegendir pygobject-2.0`':\"${STAGING_DATADIR}/pygobject/2.0/codegen\":g \
		-e s:'`$PKG_CONFIG --variable=fixxref pygobject-2.0`':\"${STAGING_DATADIR}/pygobject/xsl/fixxref.py\":g \
		${S}/configure.ac
	sed -i 's:tests docs:tests:' Makefile.am
}

# dirty fix #1: remove dependency on python-pygobject-dev
do_install_append() {
	find ${D} -name "*.la"|xargs rm -f
	rm -f ${D}/${bindir}/pygtk-codegen-2.0
	rm -rf ${D}/${libdir}/pkgconfig
}

# dirty fix #2: fix build system paths leaking in
require fix-path.inc

PACKAGES =+ "${PN}-demo"
FILES_${PN}-demo = " ${bindir}/pygtk-demo ${libdir}/pygtk "
RDEPENDS_${PN}-demo = "python-pygtk python-stringold python-lang"

# todo: revamp packaging, package demo seperatly
FILES_${PN}-dev += " ${libdir}/pygtk/2.0 ${bindir}/pygtk-* ${datadir}/pygtk/2.0"

