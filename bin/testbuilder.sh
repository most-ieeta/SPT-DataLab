#!/bin/sh

#to change L&F if desired.  Blank is default
JAVA_LOOKANDFEEL="-Dswing.defaultlaf=javax.swing.plaf.metal.MetalLookAndFeel"
#JAVA_LOOKANDFEEL=""

JAVA_OPTS="-Xms256M -Xmx1024M"

APP_OPTS=""

if test "x$JTS_LIB_DIR" = "x"; then
        JTS_LIB_DIR=`dirname $0`/../modules/app/target
		#JTS_LIB_DIR=C:/Users/bjpsi/Desktop/Investigacao/jts/modules/app/target
fi

#---------------------------#
# run the program           #
#---------------------------#
java -jar ${JAVA_OPTS} ${JAVA_LOOKANDFEEL} -jar ${JTS_LIB_DIR}/jts-app-1.16.1-SNAPSHOT ${APP_OPTS}
