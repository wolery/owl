#!/bin/bash
#***************************** Copyright © Jonathon Bell. All rights reserved.
#**
#**
#**  Version : $Header:$
#**
#**
#**  Purpose : Packages the executable JAR as a self contained application. 
#**
#**
#**  See Also: https://docs.oracle.com/javase/9/tools/javapackager.htm#JSWOR719
#**
#**
#**  Comments: This file uses a tab size of 2 spaces.
#**                                                                     0-0
#**                                                                   (| v |)
#***********************************************************************w*w***

jar=$(basename $(ls -1 target/owl*.jar))                 # The executable jar

if [[ -d target/Owl.app ]]                               # Does target exist?
then
  rm -rf target/Owl.app                                  # ...then remove it
fi

javapackager                                             \
  -deploy                                                \
  -srcdir       target                                   \
  -outdir       target                                   \
  -native       image                                    \
  -srcfiles     $jar                                     \
  -outfile      Owl                                      \
  -name         Owl                                      \
  -title        Owl                                      \
  -appclass     com.wolery.owl.owl                       \
  -Bicon=src/main/resources/icns/owl.icns

#*****************************************************************************
