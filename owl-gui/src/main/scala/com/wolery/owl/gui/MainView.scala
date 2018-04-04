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
import javafx.stage.{Stage,StageStyle}

import com.wolery.fx.control.menu
import com.wolery.owl.gui.util.load

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

    val (mv,_) = load.view("MainView",     new MainController(stage))
    val (tv,_) = load.view("TransportView",new TransportController(transport))

    mv.asInstanceOf[BorderPane].setTop(tv)
    mv.asInstanceOf[BorderPane].setCenter(new InterpreterConsole)

    stage.setScene    (new Scene(mv))
    stage.setMinWidth (mv.getMinWidth)
    stage.setMinHeight(mv.getMinHeight)
  //stage.setMaximized(true)
    stage.setTitle    ("Owl")
    stage.show        ()
  }
}

//****************************************************************************
