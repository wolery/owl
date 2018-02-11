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

import scala.tools.nsc.interpreter.Results.Incomplete

import com.wolery.util.Logging
import com.wolery.fx.control.Console
import com.wolery.owl.gui.util.load

import javafx.event.ActionEvent
import javafx.scene.Scene
import javafx.stage.Stage
import preferences.{ prompt1, prompt2 }
import scala.util.Try

//****************************************************************************

class ConsoleView extends Logging
{
 @fx
  var m_console: Console = _
  var m_partial: String  = ""

  def initialize(): Unit =
  {
    interpreter.writer = m_console.writer
    m_console.prompt   = prompt1()
  }

  def onAccept(e: ActionEvent): Unit =
  {
    log.info("onAccept({})",e)

    m_partial += m_console.input.trim

    if (m_partial.isEmpty)
    {
      m_console.appendText(prompt1())
    }
    else
    if (m_partial.startsWith(":"))
    {
      m_console.addCommand(m_partial)
      onCommand(m_partial)
      m_console.appendText(prompt1())
      m_partial = ""

    }
    else
    if (interpreter.interpret(m_partial) == Incomplete)
    {
      m_console.appendText(prompt2())
    }
    else
    {
      m_console.addCommand(m_partial)
      m_console.appendText(prompt1())
      m_partial = ""
    }
  }

  def onComplete(e: ActionEvent): Unit =
  {
    log.info("onCompleteEvent({})",e)
  }

  def onCommand(command: String): Unit =
  {
    val arguments = command.split("\\s+")

// def int   (i: ℕ,default: ℤ)     : ℤ      = Try(arguments(i).toInt)   .getOrElse(default)
// def real  (i: ℕ,default: ℝ)     : ℝ      = Try(arguments(i).toDouble).getOrElse(default)
// def string(i: ℕ,default: String): String = Try(arguments(i)).getOrElse(default)

    arguments(0) match
    {
      case ":help"    ⇒ onHelp      (arguments)
      case ":history" ⇒ onHistory   (arguments)
      case   bad      ⇒ onBadCommand(arguments)
    }
  }

  def onBadCommand(arguments: Seq[String]): Unit =
  {
    log.debug("onBadCommmand({})",arguments)

    m_console.appendText(arguments(0))
    m_console.appendLine(": no such command.  Type :help for help.")
  }

  def onHelp(arguments: Seq[String]): Unit =
  {
    log.debug("onHelp({})",arguments)

    val help = """
      |:help [command]          print this summary or command-specific help
      |:history [num]           show the history (optional num is commands to show)
    """.stripMargin

    m_console.appendLine(help)
  }

  def onHistory(arguments: Seq[String]): Unit =
  {
    log.debug("onHistory({})",arguments)

    m_console.listCommands()
  }

  def onClose(): Unit =
  {
    log.debug("onClose()")

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
