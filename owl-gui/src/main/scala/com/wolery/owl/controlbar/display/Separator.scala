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
//*                                                                     0-0
//*                                                                   (| v |)
//**********************************************************************w*w***

package com.wolery
package owl
package controlbar
package display

import javafx.scene.control.TextField
import javafx.geometry.Orientation.VERTICAL

//****************************************************************************

class Separator extends javafx.scene.control.Separator with Logging
{
  log.debug("initialize()")
  this.setOrientation(VERTICAL)
  getStyleClass.setAll("transport-display-separator")
}

//****************************************************************************
