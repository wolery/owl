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
package controlbar

import javafx.scene.control._
import javafx.scene.input.ContextMenuEvent
import javafx.scene.layout.HBox

import com.wolery.fx.control.menu.MenuItemBuilder
import com.wolery.owl.controlbar.display.Display

//****************************************************************************

final class ControlBar extends HBox with Logging
{
  val m_display = Display()

  this.setId("control-bar")
  this.getChildren.add(m_display)
  this.setOnContextMenuRequested(onContextMenuRequested)

  def onContextMenuRequested(e: ContextMenuEvent): Unit =
  {
    log.debug("onContextMenuRequested({})",e);

    val m = new ContextMenu(
      new MenuItem("Customize Control Bar And Display...") .onAction(onCustomize()),
      new MenuItem("Apply Defaults")                       .onAction(onApplyDefaults()),
      new MenuItem("Save As Defaults")                     .onAction(onSaveAsDefaults()))

    m.show(this.getScene.getWindow,e.getScreenX,e.getScreenY)
  }

  def onCustomize()     : Unit = {log.debug("onCustomize()")}
  def onApplyDefaults() : Unit = {log.debug("onApplyDefaults()")}
  def onSaveAsDefaults(): Unit = {log.debug("onSaveAsDefaults()")}
}

//****************************************************************************
