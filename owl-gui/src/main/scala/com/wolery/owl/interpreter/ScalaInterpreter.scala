//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : $Header:$
//*
//*
//*  Purpose : An embedded interpreter for the language Scala.
//*
//*
//*  Comments: This file uses a tab size of 2 spaces.
//*                                                                     0-0
//*                                                                   (| v |)
//**********************************************************************w*w***

package com.wolery
package owl
package interpreter

//****************************************************************************

import java.io.{PrintWriter,Writer}

import scala.tools.nsc.{ConsoleWriter,Settings}
import scala.tools.nsc.interpreter.IMain
import scala.tools.nsc.interpreter.Results.{Result⇒IResult,_}

import com.wolery.owl.preferences.compiler

//****************************************************************************

/**
 * An embedded interpreter for the language Scala.
 *
 * @author Jonathon Bell
 */
object ScalaInterpreter extends Interpreter
{
  /**
   * A `PrintWriter` object that initially sends characters to the Java system
   * console, but that also can be redirected to send its output elsewhere.
   *
   * Class `IMain` requires that a writer be supplied at construction but does
   * not allow this object to be changed thereafter.  The host application may
   * wish to close the console window and reopen it again later, however. This
   * object extends the `PrintWriter`  base class to provide mutable access to
   * the protected 'out' member, thus allowing us to redirect the output of an
   * interpreter without recreating the interpreter itself.
   */
  private
  object m_out extends PrintWriter(new ConsoleWriter)
  {
    def writer                  : Writer = out           // Return the writer
    def writer_=(writer: Writer): Unit   = out = writer  // Update the update
  }

  /**
   *
   */
  private
  val m_int =
  {
    def cpath(classes: Class[_]*): String =
    {
      classes.map(_.getProtectionDomain
                   .getCodeSource
                   .getLocation
                   .toString)
             .mkString(java.io.File.pathSeparator)
    }

    val s = new Settings

    s.usejavacp.value     = true
    s.bootclasspath.value = cpath(classOf[IMain],
                                  classOf[Some[_]])
    s.processArgumentString(compiler.value)

    new IMain(s,m_out)
  }

  /**
   * Convert the given Scala interpreter result code to our own format.
   */
  private
  def result(iresult: IResult): Result = iresult match
  {
    case Error      ⇒ error
    case Success    ⇒ success
    case Incomplete ⇒ incomplete
  }

  /**
   * Returns the object to which this interpreter currently writes results and
   * error messages.
   *
   * @return The current output writer.
   */
  def writer: Option[Writer] =
  {
    Option(m_out.writer)                                 // Return the writer
  }

  /**
   * Updates the object to which this interpreter currently writes results and
   * error messages, or resets it to the default object, which appends text to
   * the Java system console
   *
   * @param  writer  The object to which this interpreter should write results
   *                 and error messages,
   */
  def writer_=(writer: Option[Writer]): Unit =
  {
    m_out.writer = writer.getOrElse(new ConsoleWriter)   // Update the writer
  }

  /**
   * Interprets a single line of input.
   *
   * Results and error messages are written to the associated `Writer` object.
   *
   * @param  line  The line of text to interpret.
   *
   * @return A code indicating whether the evaluation succeeded or not.
   */
  def interpret(line: String): Result =
  {
    result(m_int.interpret(line))                        // Interpret the line
  }

  /**
   * Binds a variable with  the given name and type to an initial value in the
   * intepreter's top level context where it can subsequently be referenced by
   * name.
   *
   * @param  name   The name of the variable to bind.
   * @param  tipe   The fully package qualified type name of the value to bind
   *                to.
   * @param  value  The value to bind to.
   *
   * @return A code indicating whether the binding succeeded or not.
   */
  def bind[α](name: String,tipe: String,value: α): Result =
  {
    result(m_int.bind(name,tipe,value))                  // Bind name to value
  }
}

//****************************************************************************
