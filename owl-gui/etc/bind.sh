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
#**
#**
#*****************************************************************************

cat etc/launch.sh target/try-*.jar > try && chmod +x try

#*****************************************************************************
