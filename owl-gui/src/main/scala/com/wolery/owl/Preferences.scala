//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : $Header:$
//*
//*
//*  Purpose :
//*
//*
//*  Comments: This file uses a tab size of 2 spaces.
//*                                                                     0-0
//*                                                                   (| v |)
//**********************************************************************w*w***

package com.wolery
package owl

//****************************************************************************

object preferences extends Preferences(owl.getClass)
{
  val compiler      = string("compiler","-deprecation -feature -Xlint")
  val prompt1       = string("prompt1","owl> ")
  val prompt2       = string("prompt2","   | ")

  val theme         = string("theme","Logic Pro X V10.4")
}

//****************************************************************************
