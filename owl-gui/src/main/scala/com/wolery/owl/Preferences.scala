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

//****************************************************************************

object preferences extends utils.Preferences(owl.getClass)
{
  val compiler = string("compiler","-deprecation -feature -Xlint")
  val prompt1  = string("prompt1","owl> ")
  val prompt2  = string("prompt2","   | ")


  val eol      = System.lineSeparator
}

//****************************************************************************
