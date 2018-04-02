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

App=Owl                                                  # Application title
app=owl                                                  # Application class

#*****************************************************************************

css=target/classes/css                                   # The CSS directory
jar=$(basename $(ls -1 target/$app*.jar))                # The executable jar

# Compile the contents of the 'css' directory into binary 'bss' files.

compile-css()
{
  echo "Compiling style sheets"                          # Trace our progress

  javapackager                                           \
    -createbss                                           \
    -srcdir     $css                                     \
    -outdir     $css                                     \
     >/dev/null                                          # Suppress warning
}

# Replace the 'css' style sheets with their binary 'bss' counterparts.

update-jar()
{
  echo "Updating style sheets"                           # Trace our progress
  (
    cd   target/classes                                  # Change into subdir 
    zip -qd ../$jar css/*                                # Remove style sheets
    jar  uf ../$jar css/*.bss                            # Update the binaries
    rm              css/*.bss                            # Remove the binaries
  )
}

# Package the results up as a self contained application.

package()
{
  if [[ -d target/$App.app ]]                            # Does target exist?
  then
    rm -rf target/$App.app                               # ...then remove it
  fi

  javapackager                                           \
    -deploy                                              \
    -srcdir       target                                 \
    -outdir       target                                 \
    -native       image                                  \
    -srcfiles     $jar                                   \
    -outfile      $App                                   \
    -name         $App                                   \
    -title        $App                                   \
    -appclass     com.wolery.$app.$app                   \
    -Bicon=src/main/resources/$app.icns
}

if [[ -d $css ]]                                         # Do we have sheets? 
then 
  compile-css &&  update-jar                             # ...compile + update
fi

package                                                  # Package application

#*****************************************************************************
