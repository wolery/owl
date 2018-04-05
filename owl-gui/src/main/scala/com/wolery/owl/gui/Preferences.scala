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

package com.wolery
package owl
package gui

//****************************************************************************

object preferences extends Preferences(owl.getClass)
{
  val compiler      = string("compiler","-deprecation -feature -Xlint")
  val prompt1       = string("prompt1","owl> ")
  val prompt2       = string("prompt2","   | ")

  val theme         = string("theme","Owl")

  val eol           = System.lineSeparator
}

//****************************************************************************
