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
package transport

import javafx.geometry.{Insets ⇒ jInsets}
import javafx.scene.control.{ ContextMenu, MenuItem }
import javafx.scene.input.ContextMenuEvent
import javafx.scene.layout.HBox

import com.wolery.fx.control.menu.MenuItemBuilder
import com.wolery.owl.gui.util.load
import javafx.scene.layout.VBox
import javafx.scene.control.TextField
import javafx.scene.control.Separator
import javafx.geometry.Orientation
import javafx.scene.control.Label
import javafx.scene.control.SeparatorMenuItem

//****************************************************************************
// 'companion' object for class javafx.geometry.Inset

object Insets
{
  def apply()                   : jInsets = javafx.geometry.Insets.EMPTY
  def apply(t: ℝ)               : jInsets = new javafx.geometry.Insets(t,t,t,t);
  def apply(t: ℝ,r: ℝ)          : jInsets = new javafx.geometry.Insets(t,r,t,r);
  def apply(t: ℝ,r: ℝ,b: ℝ,l: ℝ): jInsets = new javafx.geometry.Insets(t,r,b,l);
}

//****************************************************************************

abstract class TransportDisplaySegment extends VBox with Logging
{
  getStyleClass.add("transport-display-segment")
}

class TransportDisplaySeparator extends Separator(Orientation.VERTICAL)
{
  getStyleClass.add("transport-display-separator")
}

class TransportDisplayCounter extends TextField
{
  getStyleClass.add("transport-display-counter")
}

class TransportDisplayDropdown(id: String,menu: ContextMenu) extends Label
{
  setId(id)
  getStyleClass.add("transport-display-dropdown")
}






class TransportDisplayTempo extends TransportDisplaySegment
{
  val m_tempo = new TransportDisplayCounter()
//val m_mode  = new TransportDisplayDropDown()
  // KEEP - Keep Project Tempo
  // ADAPT - Adapt Project Tempo
  // AUTO - Automatic Mode
  // ----
  // Smart Tempo Project Settings
}

class TransportDisplayMode extends
      TransportDisplayDropdown(
  "transport-display-mode",
  new ContextMenu(
  new MenuItem("Beats & Project"),
  new MenuItem("Beats & Project (Large)"),
  new MenuItem("Beats & Time"),
  new MenuItem("Beats & Time (Large)"),
  new MenuItem("Beats"),
  new MenuItem("Time"),
  new MenuItem("Custom"),
  new SeparatorMenuItem(),
  new MenuItem("Open Giant Beats Display"),
  new MenuItem("Open Giant Time Display"),
  new SeparatorMenuItem(),
  new MenuItem("Use SMPTE View Offset"),
  new SeparatorMenuItem(),
  new MenuItem("Customize Control Bar and Display..."),
  new MenuItem("Apply Defaults")))
{}

class TransportDisplay extends HBox with Logging
{
  setId("transport-display")

  HBox.setMargin(this,Insets(5));

  load.node("TransportDisplay",this)

  val m_mode = new TransportDisplayDropdown(
  "transport-display-mode",
  new ContextMenu(
  new MenuItem("Beats & Project"),
  new MenuItem("Beats & Project (Large)"),
  new MenuItem("Beats & Time"),
  new MenuItem("Beats & Time (Large)"),
  new MenuItem("Beats"),
  new MenuItem("Time"),
  new MenuItem("Custom"),
  new SeparatorMenuItem(),
  new MenuItem("Open Giant Beats Display"),
  new MenuItem("Open Giant Time Display"),
  new SeparatorMenuItem(),
  new MenuItem("Use SMPTE View Offset"),
  new SeparatorMenuItem(),
  new MenuItem("Customize Control Bar and Display..."),
  new MenuItem("Apply Defaults")))

  getChildren.add(m_mode)

  def initialize(): Unit =
  {
    log.debug("initialize()");
  }


  def getTransportDisplayMode() =
  {
    new TransportDisplayDropdown(
    "transport-display-mode",
    new ContextMenu(
    new MenuItem("Beats & Project"),
    new MenuItem("Beats & Project (Large)"),
    new MenuItem("Beats & Time"),
    new MenuItem("Beats & Time (Large)"),
    new MenuItem("Beats"),
    new MenuItem("Time"),
    new MenuItem("Custom"),
    new SeparatorMenuItem(),
    new MenuItem("Open Giant Beats Display"),
    new MenuItem("Open Giant Time Display"),
    new SeparatorMenuItem(),
    new MenuItem("Use SMPTE View Offset"),
    new SeparatorMenuItem(),
    new MenuItem("Customize Control Bar and Display..."),
    new MenuItem("Apply Defaults")))
  }
}

//****************************************************************************

class ControlBar extends HBox with Logging
{
  val m_display  = new TransportDisplay()
  val m_display2 = new TransportDisplay()

  this.setId("control-bar")
  this.getChildren.addAll(m_display,m_display2)
  this.setOnContextMenuRequested(onContextMenuRequested)

  def onContextMenuRequested(e: ContextMenuEvent): Unit =
  {
    log.debug("onContextMenuRequested({})",e);

    new ContextMenu(
    new MenuItem("Customize Control Bar and Display...").onAction(onCustomize),
    new MenuItem("Apply Defaults")                      .onAction(onApplyDefaults),
    new MenuItem("Save As Defaults")                    .onAction(onSaveAsDefaults)
    ).show(this.getScene.getWindow,e.getScreenX,e.getScreenY)
  }

  def onCustomize()     : Unit = {log.debug("onCustomize()")}
  def onApplyDefaults() : Unit = {log.debug("onApplyDefaults()")}
  def onSaveAsDefaults(): Unit = {log.debug("onSaveAsDefaults()")}
}

//****************************************************************************
