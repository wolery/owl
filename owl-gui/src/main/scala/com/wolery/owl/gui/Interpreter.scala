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

import java.io.{PrintWriter,Writer}

import scala.tools.nsc.{ConsoleWriter,Settings}
import scala.tools.nsc.interpreter.IMain
import com.wolery.owl.util.Logging

//****************************************************************************

object interpreter extends Logging
{
  object m_out extends PrintWriter(new ConsoleWriter)
  {
    val con           : Writer = out
    def get           : Writer = out
    def set(w: Writer): Unit   = out = if (w == null) con else w
  }

  val m_int =
  {
    def classPath(classes: String*): String =
    {
      import java.lang.Class.forName
      import java.io.File.pathSeparator

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

  def writer                         : Writer       = m_out.get
  def writer_=(writer: Writer = null): Unit         = m_out.set(writer)

  def interpret(line: String)                       = m_int.interpret(line)
  def bind[α]  (name: String,tipe: String,value: α) = m_int.bind(name,tipe,value)
}

//****************************************************************************
