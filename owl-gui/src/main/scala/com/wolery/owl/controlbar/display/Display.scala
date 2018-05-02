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
import javafx.scene.layout.Region

//****************************************************************************
// move to package and make implicit? promote to com.wolery.fx.?

class RegionSyntax[P <: Pane](val p: P) extends AnyVal
{
  def add(nodes: Node*): P  = {p.getChildren.addAll(nodes:_*);p}
}

//****************************************************************************

class Display extends HBox with Logging
{
  this.getChildren.addAll(new BeatsProjectLargeView())

//load.node("ControlBar-Display-Beats & Project (Large)",this)

  HBox.setMargin(this,Insets(5))

  def initialize(): Unit =
  {
    log.debug("initialize()");
  }
}

//****************************************************************************
