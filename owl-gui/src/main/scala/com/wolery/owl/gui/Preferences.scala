//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : Header:
//*
//*
//*  Purpose :
//*
//*
//*  Comments: This file uses a tab size of 2 spaces.
//*
//*
//****************************************************************************

package com.wolery.owl
package gui

import com.wolery.owl.util.Preferences

//****************************************************************************

object preferences extends Preferences(owl.getClass)
{
  val compiler = string("compiler","-deprecation -feature -Xlint")
  val prompt1  = string("prompt1","owl> ")
  val prompt2  = string("prompt2","   | ")

  val eol      = System.lineSeparator

  val scala_library = string("scala_library","/opt/scala-2.12.3/lib/scala-library.jar")
}

//****************************************************************************
