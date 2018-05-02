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

import javafx.geometry.Orientation.VERTICAL
import javafx.scene.control._

//****************************************************************************

class Separator extends javafx.scene.control.Separator(VERTICAL)
{
// setId("")
// getStyleClass.setAll("transport-display-separator")
}

class Label(text: String) extends javafx.scene.control.Label(text)
{
//setId("")
  getStyleClass.setAll("transport-display-segment-label")
}

class DropDown(id: String,tip: String,item: MenuItem*) extends MenuButton
{
  setId(id)
  setText("KEEP")
  getStyleClass.setAll("transport-display-drop-down")
  setTooltip(new Tooltip(tip))
  getItems.addAll(item:_*)
}

class Counter(id: String,tip: String) extends TextField
{
  setId(id)
  getStyleClass.setAll("transport-display-counter")
  setTooltip(new Tooltip(tip))
  setText("001")
}

//****************************************************************************
