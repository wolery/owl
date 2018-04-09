//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : $Header:$
//*
//*
//*  Purpose : Specializes class `Console` for use with an interpreter.
//*
//*
//*  Comments: This file uses a tab size of 2 spaces.
//*                                                                     0-0
//*                                                                   (| v |)
//**********************************************************************w*w***

package com.wolery
package owl
package interpreter

import javafx.event.ActionEvent
import com.wolery.fx.control.Console

/**
 * Specializes class `Console` for use with an interpreter.
 *
 * Class `InterpreterConsole` specializes class `Console` to implement a 'read
 * -eval-print' loop for the given interpreter.
 *
 * @param  m_int  The interpreter to interact with.
 *
 * @author Jonathon Bell
 */
class InterpreterConsole(m_int: Interpreter) extends Console
{
  val m_prompt1: String = preferences.prompt1()          // Main prompt string
  val m_prompt2: String = preferences.prompt2()          // Need more input...
  var m_partial: String = ""                             // Partial input line

  setId("console")
  setWrapText(true)
  setFocusTraversable(false)
  setOnAccept(onAccept(_))
  setOnComplete(onComplete(_))

  m_int.writer = Some(writer)
  prompt       = m_prompt1

  def onAccept(e: ActionEvent): Unit =
  {
    log.info("onAccept({})",e)                           // Trace our progress

    m_partial += input.trim                              //

    if (m_partial.isEmpty)
    {
      appendText(m_prompt1)
    }
    else
    if (m_partial.startsWith(":"))
    {
      addCommand(m_partial)
      onCommand (m_partial)
      appendText(m_prompt1)
      m_partial = ""
    }
    else
    if (m_int.interpret(m_partial) == incomplete)
    {
      appendText(m_prompt2)
    }
    else
    {
      addCommand(m_partial)
      appendText(m_prompt1)
      m_partial = ""
    }
  }

  def onComplete(e: ActionEvent): Unit =
  {
    log.info("onCompleteEvent({})",e)                    // Trace our progress
  }

  def onCommand(command: String): Unit =
  {
    log.debug("onCommmand({})",command)                  // Trace our progress

    val arguments = command.split("\\s+")

    arguments(0) match
    {
      case ":help"    ⇒ onHelp      (arguments)
      case ":history" ⇒ onHistory   (arguments)
      case otherwise  ⇒ onBadCommand(arguments)
    }
  }

  def onBadCommand(arguments: Seq[String]): Unit =
  {
    log.debug("onBadCommmand({})",arguments)             // Trace our progress

    this.appendText(arguments(0))
    this.appendLine(": no such command.  Type :help for help.")
  }

  def onHelp(arguments: Seq[String]): Unit =
  {
    log.debug("onHelp({})",arguments)                    // Trace our progress

    val help = """
      |:help [command]          print this summary or command-specific help
      |:history [num]           show the history (optional num is commands to show)
    """.stripMargin

    this.appendLine(help)
  }

  def onHistory(arguments: Seq[String]): Unit =
  {
    log.debug("onHistory({})",arguments)                 // Trace our progress

    listCommands()
  }

  def onClose(): Unit =
  {
    log.debug("onClose()")                               // Trace our progress

    m_int.writer = None
  }
}

//****************************************************************************
