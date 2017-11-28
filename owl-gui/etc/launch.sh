#!/bin/bash
#***************************** Copyright © Jonathon Bell. All rights reserved.
#**
#**
#**  Version : $Header:$
#**
#**
#**  Purpose : Implements a generic launcher script for executable JAR files.
#**
#**
#**  Usage   : 1. Create an executable JAR  e.g. foo.jar
#**            2. Append it to this script  e.g. cat launch.sh foo.jar > foo
#**            3. Mark the file executable  e.g. chmod +x foo
#**
#**  See Also: https://coderwall.com/p/ssuaxa/how-to-make-a-jar-file-linux-executable
#**
#**
#**  Comments: This file uses a tab size of 3 spaces.
#**                                                                     0-0
#**                                                                   (| v |)
#***********************************************************************w*w***

# formats the given argument string as an error message and exits the script.

error()
{
   echo $0":"
   echo 
   echo "  "$1
   echo 
   echo "  This program requires Java version 1.8 or higher. If you have more" 
   echo "  than one version installed you can specify which to use by setting"
   echo "  the JAVA_HOME environment variable."
   echo
   echo "  You can download the latest version of the JVM from:"
   echo "  http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html"
   echo
   exit 1
}

# returns the path to the java executable.

jvm()
{
   if [ "$JAVA_HOME" = "" ]; then
      JVM=$(type -p java)
   
      if [ $? != 0 ]; then
        error "Can't find the Java executable."
      fi
   else
      JVM=$JAVA_HOME/bin/java
   fi

   if [ ! -e "$JVM" ]; then
      error "Can't find the Java executable at JAVA_HOME=$JAVA_HOME/bin"
   fi
   
   echo $JVM
}

# formats the major and minor digits of the version number as an integer.

version()
{
   `jvm` -version 2>&1 | sed -n ';s/.* version "\(.*\)\.\(.*\)\..*"/\1\2/p;'
}

# returns the path to the self executing jar  that is appended to this script.

jar()
{
   JAR=$(which "$0" 2>/dev/null)
   
   if (( $? > 0 )) && [ -f "$0" ]; then
      JAR="./$0"
   fi

   echo $JAR
}

# verify that we are running on a sufficiently recent JVM.  

if (( `version` < 18 )); then
   error "Can't use the Java executable at JAVA_HOME=${JAVA_HOME}/bin"
fi

#*****************************************************************************

exec `jvm` -jar `jar` "$@"
exit $?

#*****************************************************************************
