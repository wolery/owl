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
import javafx.event._

import com.wolery.owl.gui.util.load
import com.wolery.owl.controlbar.display.Display
import com.wolery.fx.control.menu._

//****************************************************************************

object foo extends Enumeration
{
  val customize      = Value("Customize Control Bar And Display...")
  val applyDefaults  = Value("Apply Defaults")
                       Value("")
  val saveAsDefaults = Value("Save As Defaults")
  
  
  def menuItem(v: Value): MenuItem = v.toString match
  {
    case "" => new SeparatorMenuItem
    case s  => new MenuItem(s)//.onAction(e=>e)
  }

  def contextMenu: ContextMenu = 
  {
    new ContextMenu(values.map(menuItem).toSeq:_*)
  }
}

//****************************************************************************

final class ControlBar extends HBox with Logging
{
  val m_display = new Display

  this.setId("control-bar")
  this.getChildren.add(m_display)
  this.setOnContextMenuRequested(onContextMenuRequested)

  def onContextMenuRequested(e: ContextMenuEvent): Unit =
  {
    log.debug("onContextMenuRequested({})",e);
    val m_menu = foo.contextMenu
    m_menu.show(this.getScene.getWindow,e.getScreenX,e.getScreenY)
  }

  def onCustomize()     : Unit = {log.debug("onCustomize()")}
  def onApplyDefaults() : Unit = {log.debug("onApplyDefaults()")}
  def onSaveAsDefaults(): Unit = {log.debug("onSaveAsDefaults()")}
  
  
}

//****************************************************************************
