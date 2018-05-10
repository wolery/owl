//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : $Header:$
//*
//*
//*  Purpose : An abstract interface to an embedded language interpreter.
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

import java.io.Writer

//****************************************************************************

/**
 * An abstract interface to an embedded language interpreter.
 *
 * @author Jonathon Bell
 */
trait Interpreter
{
  /**
   * Returns the object to which this interpreter currently writes results and
   * error messages.
   *
   * @return The current output writer.
   */
  def writer: Option[Writer]

  /**
   * Updates the object to which this interpreter currently writes results and
   * error messages, or resets it to the default writer.
   *
   * @param  writer  The object to which this interpreter should write results
   *                 and error messages,
   */
  def writer_= (writer: Option[Writer]): Unit

  /**
   * Interprets a single line of input.
   *
   * Results and error messages are written to the associated `Writer` object.
   *
   * @param  line  The line of text to interpret.
   *
   * @return A code indicating whether the evaluation succeeded or not.
   */
  def interpret(line: String): Result

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
  def bind[α](name: String,tipe: String,value: α): Result
}

/**
 * Describes the result of interpreting a single line of input.
 */
abstract sealed class Result

/**
 * The line was erroneous in some way.
 */
case object error extends Result

/**
 * The line was interpreted successfully.
 */
case object success extends Result

/**
 * The line was syntactically incomplete; more input is needed to interpret it.
 */
case object incomplete extends Result

//****************************************************************************
