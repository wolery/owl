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

import javafx.event.ActionEvent
import javafx.scene.control._//{ContextMenu, Menu , MenuItem, SeparatorMenuItem}

import scala.tools.nsc.interpreter.Results.Incomplete

import com.wolery.fx.control.Console

import preferences.{prompt1, prompt2}

//****************************************************************************

class InterpreterConsole(id: String = "console") extends Console
{
  var m_partial: String = ""

  setId(id)
  setWrapText(true)
  setFocusTraversable(false)
  setOnAccept        (onAccept(_))
  setOnComplete      (onComplete(_))

  interpreter.writer = writer
  prompt             = prompt1()


  setContextMenu(
  new ContextMenu(
    new MenuItem("Cut"),
    new MenuItem("Select Overlapped Regions/Events"),
    new Menu("Submenu",null,
      new MenuItem("Cut"),
      new MenuItem("Disabled")    {setDisabled(true)},
      new CheckMenuItem("checked"){setSelected(true)},
      new RadioMenuItem("radio")  {setSelected(true)},
      new SeparatorMenuItem(),
      new MenuItem("Select All")),
    new MenuItem("Paste"),
    new SeparatorMenuItem(),
    new MenuItem("Select All")))

  def onAccept(e: ActionEvent): Unit =
  {
    log.info("onAccept({})",e)

    m_partial += input.trim

    if (m_partial.isEmpty)
    {
      appendText(prompt1())
    }
    else
    if (m_partial.startsWith(":"))
    {
      addCommand(m_partial)
      onCommand(m_partial)
      appendText(prompt1())
      m_partial = ""
    }
    else
    if (interpreter.interpret(m_partial) == Incomplete)
    {
      appendText(prompt2())
    }
    else
    {
      addCommand(m_partial)
      appendText(prompt1())
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

    this.appendText(arguments(0))
    this.appendLine(": no such command.  Type :help for help.")
  }

  def onHelp(arguments: Seq[String]): Unit =
  {
    log.debug("onHelp({})",arguments)

    val help = """
      |:help [command]          print this summary or command-specific help
      |:history [num]           show the history (optional num is commands to show)
    """.stripMargin

    this.appendLine(help)
  }

  def onHistory(arguments: Seq[String]): Unit =
  {
    log.debug("onHistory({})",arguments)

    listCommands()
  }

  def onClose(): Unit =
  {
    log.debug("onClose()")

    interpreter.writer = null
  }
}

//****************************************************************************
