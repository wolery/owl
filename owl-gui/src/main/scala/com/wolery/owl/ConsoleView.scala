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
//*
//*
//****************************************************************************

package com.wolery.owl

import scala.tools.nsc.interpreter.Results.Incomplete

import com.wolery.owl.control.{ Console, NewlineEvent }
import com.wolery.owl.utils.load
import preferences._
import javafx.fxml.{FXML ⇒ fx}
import javafx.scene.Scene
import javafx.stage.Stage
import javafx.scene.input.{KeyCode,KeyEvent,KeyCombination,KeyCodeCombination}

//****************************************************************************

class ConsoleView
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

  def onNewline(e: NewlineEvent): Unit =
  {
    m_buff += e.line.trim

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
