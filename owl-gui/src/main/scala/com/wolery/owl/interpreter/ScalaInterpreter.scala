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
package interpreter

import java.io.{PrintWriter,Writer}

import scala.tools.nsc.{ConsoleWriter,Settings}
import scala.tools.nsc.interpreter.IMain

import com.wolery.owl.preferences

//****************************************************************************

object ScalaInterpreter extends Interpreter
{
  private
  object m_out extends PrintWriter(new ConsoleWriter)
  {
    val con           : Writer = out
    def get           : Writer = out
    def set(w: Option[Writer]): Unit = out = w.getOrElse(con)
  }

  private
  val m_int =
  {
    def classPath(classes: String*): String =
    {
      import java.io.File.pathSeparator
      import java.lang.Class.forName

      classes.map(forName(_).getProtectionDomain
                            .getCodeSource
                            .getLocation
                            .toString)
             .mkString(java.io.File.pathSeparator)
    }

    val s = new Settings

    s.usejavacp.value     = true
    s.bootclasspath.value = classPath("scala.tools.nsc.Interpreter",
                                      "scala.Some")
    s.processArgumentString(preferences.compiler.value)

    new IMain(s,m_out)
  }

  private
  def result(r: scala.tools.nsc.interpreter.Results.Result): Result = r match
  {
    case scala.tools.nsc.interpreter.Results.Success    ⇒ Success
    case scala.tools.nsc.interpreter.Results.Error      ⇒ Error
    case scala.tools.nsc.interpreter.Results.Incomplete ⇒ Incomplete
  }

  def console()                                     : Node = new InterpreterConsole(this)

  def writer                                        : Writer = m_out.get
  def writer_=(writer: Option[Writer])              : Unit   = m_out.set(writer)

  def interpret(line: String)                       : Result = result(m_int.interpret(line))
  def bind[α]  (name: String,tipe: String,value: α) : Result = result(m_int.bind(name,tipe,value))
}

//****************************************************************************
