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
package gui

import javafx.application.Platform
import javafx.scene.Scene
import javafx.scene.control.MenuBar
import javafx.scene.layout.BorderPane
import javafx.stage.{Stage, StageStyle}

import com.wolery.fx.control.menu
import com.wolery.owl.gui.util.load
import com.wolery.owl.transport.TransportView
import com.wolery.owl.controlbar.ControlBar

//****************************************************************************

class MainController(stage: Stage) extends Logging
{
  @fx var m_menubar: MenuBar = _

  def initialize(): Unit =
  {
    log.debug("initialize()")

    menu.setApplicationMenu(m_menubar)
  }

  def onAbout()      : Unit = {AboutView(stage)}
  def onPreferences(): Unit = {log.debug("onPreferences()")}
  def onQuit()       : Unit = {Platform.exit()}
}

//****************************************************************************

object MainView extends Logging
{
  def apply(): Unit =
  {
    val transport = owl.open(load.sequence("time"))
    val stage     = new Stage(StageStyle.DECORATED)

    val (mv,_) = load.view("MainView",new MainController(stage))

    mv.asInstanceOf[BorderPane].setTop   (new ControlBar())
    mv.asInstanceOf[BorderPane].setCenter(owl.console())

    setSystemColors(mv)

    stage.setScene    (new Scene(mv))
    stage.setTitle    ("Owl")
    stage.show        ()
    stage.setMinWidth (mv.getMinWidth  + stage.getWidth  - mv.getWidth)
    stage.setMinHeight(mv.getMinHeight + stage.getHeight - mv.getHeight)
  }

  def setSystemColors(root: Node): Unit =
  {
    import java.awt.SystemColor.{ menu ⇒ menus, _ }

    val s = new StringBuffer()

    for ((name,color) ← Seq(
      "desktop"                 → desktop,
      "active-caption"          → activeCaption,
      "active-caption-Text"     → activeCaptionText,
      "active-caption-border"   → activeCaptionBorder,
      "inactive-caption"        → inactiveCaption,
      "inactive-caption-text"   → inactiveCaptionText,
      "inactive-caption-border" → inactiveCaptionBorder,
      "window"                  → window,
      "window-border"           → windowBorder,
      "window-text"             → windowText,
      "menu"                    → menus,
      "menu-text"               → menuText,
      "text"                    → text,
      "text-text"               → textText,
      "text-highlight"          → textHighlight,
      "text-highlight-text"     → textHighlightText,
      "text-inactive-text"      → textInactiveText,
      "control"                 → control,
      "control-text"            → controlText,
      "control-highlight"       → controlHighlight,
      "control-lt-highlight"    → controlLtHighlight,
      "control-shadow"          → controlShadow,
      "control-dk-shadow"       → controlDkShadow,
      "scrollbar"               → scrollbar,
      "info"                    → info,
      "info-text"               → infoText)
    )
    {
      val c = color.getRGB & 0x00FFFFFF

      s.append(f"-sys-$name-color: #$c%06X;")
    }

    root.setStyle(s.toString)
  }
}

//****************************************************************************
