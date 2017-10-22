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

package com.wolery.owl
package gui

import scala.tools.nsc.interpreter.Results.Incomplete

import com.wolery.owl.control.Console
import com.wolery.owl.gui.util.load
import com.wolery.owl.util.Logging

import javafx.event.ActionEvent
import javafx.scene.Scene
import javafx.stage.Stage
import preferences.{ prompt1, prompt2 }

//****************************************************************************

class ConsoleView extends Logging
{
 @fx
  var m_cons : Console = _
  var m_buff : String  = ""

  def initialize(): Unit =
  {
    interpreter.bind("xx","Double",7.8)
    interpreter.writer = m_cons.writer
    m_cons.appendText(prompt1())
  }

  def onNewline(e: ActionEvent): Unit =
  {
    log.info("onNewlinEvent({})",e)

    m_buff += m_cons.buffer.trim

    if (m_buff.isEmpty)
    {
      m_cons.appendText(prompt1())
    }
    else
    if (interpreter.interpret(m_buff) == Incomplete)
    {
      m_cons.appendText(prompt2())
    }
    else
    {
      m_cons.appendText(prompt1())
      m_buff = ""
    }
  }

  def onComplete(e: ActionEvent): Unit =
  {
    log.info("onCompleteEvent({})",e)
  }

  def onClose(): Unit =
  {
    interpreter.writer = null
  }
}

//****************************************************************************

object ConsoleView
{
  def apply(stage: Stage): Unit =
  {
    val (r,c) = load.view[ConsoleView]("ConsoleView")

    stage.setTitle         ("Owl - Console")
    stage.setMinWidth      (r.getPrefWidth)
    stage.setMinHeight     (r.getPrefHeight)
    stage.setScene         (new Scene(r))
    stage.setOnCloseRequest(_ ⇒ c.onClose())
    stage.show             ()
  }
}

//****************************************************************************
