#!/bin/bash
#***************************** Copyright © Jonathon Bell. All rights reserved.
#**
#**
#**  Version : $Header:$
#**
#**
#**  Purpose : Binds the executable JAR to its launcher script. 
#**
#**
#**  Comments: This file uses a tab size of 3 spaces.
#**                                                                     0-0
#**                                                                   (| v |)
#***********************************************************************w*w***

cat etc/launch.sh target/owl-gui-*.jar > target/Owl && chmod +x target/Owl

#*****************************************************************************
