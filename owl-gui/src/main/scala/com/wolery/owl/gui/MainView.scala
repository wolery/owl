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

import javafx.scene.Scene
import javafx.stage.{Stage,StageStyle}
import util.load

//****************************************************************************

class MainController(stage:Stage) extends Logging
{
  def initialize(): Unit =
  {
    log.debug("initialize()")
  }
@fx
  def onAbout(): Unit =
  {
    log.debug("onAbout()")
    AboutView(stage)
  }
}

//****************************************************************************

object MainView extends Logging
{
  def apply(): Unit =
  {
    val s     = new Stage(StageStyle.DECORATED)
    val (m,_) = load.view("MainView",new MainController(s))

    s.setTitle("Owl")
    s.setScene(new Scene(m))
    s.show    ()
  }
}

//****************************************************************************
