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

//****************************************************************************

class Counter extends TextField with Logging
{
  log.debug("initialize()")

  getStyleClass.setAll("transport-display-counter")
}

//****************************************************************************