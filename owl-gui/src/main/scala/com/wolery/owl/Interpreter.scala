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

import java.io.{ PrintWriter, Writer }

import scala.tools.nsc.{ ConsoleWriter, Settings }
import scala.tools.nsc.interpreter.IMain

//****************************************************************************

object interpreter
{
  val m_con: Writer = new ConsoleWriter()
  var m_imp: Writer = m_con
  val m_out = new Writer
  {
    def close: Unit = m_imp.close()
    def flush: Unit = m_imp.flush()
    def write(array: Array[Char],offset: ℕ,length: ℕ): Unit = m_imp.write(array,offset,length)
  }
  val m_set = new Settings(){processArgumentString(preferences.compiler())}
  val m_int = new IMain(m_set,new PrintWriter(m_out))

  def writer: Writer                                = m_imp
  def writer_=(writer: Writer = null): Unit         = m_imp = if(writer == null) m_con else writer

  def interpret(line: String)                       = m_int.interpret(line)
  def bind[α]  (name: String,tipe: String,value: α) = m_int.bind(name,tipe,value)
}

//****************************************************************************
