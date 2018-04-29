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

import javafx.scene.layout.HBox

import com.wolery.owl.gui.util.load
import com.wolery.fx.geometry.Insets

//****************************************************************************

class Display extends HBox with Logging
{
  load.node("ControlBar-Display-Beats & Project (Large)",this)

  HBox.setMargin(this,Insets(5))


  def initialize(): Unit =
  {
    log.debug("initialize()");
  }

}

//****************************************************************************
