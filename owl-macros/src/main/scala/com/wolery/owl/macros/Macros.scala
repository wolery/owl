//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : Header:
//*
//*
//*  Purpose :
//*
//*
//*  See Also: https://stackoverflow.com/questions/16590236/scala-2-10-octal-escape-is-deprecated-how-to-do-octal-idiomatically-now
//*            http://xahlee.info/comp/unicode_computing_symbols.html
//*
//*  Comments: This file uses a tab size of 2 spaces.
//*
//*
//****************************************************************************

package com.wolery.owl
package macros

import scala.reflect.macros.blackbox.Context

//****************************************************************************
//⌥⌘⌃◆⇧

object modifier_keys
{
  val NONE  = 0
  val SHIFT = 1
  val CNTRL = 2
  val ALT   = 4
  val META  = 8

  def parse(string: String): Either[ℕ,String] =
  {
    var keys                        = NONE
    if (string.contains('⇧')) keys |= SHIFT
    if (string.contains('⌃')) keys |= CNTRL
    if (string.contains('⌥')) keys |= ALT
    if (string.contains('◆')) keys |= META
    Left(keys)
  }

  implicit class StringContextEx(val s: StringContext) extends AnyVal
  {
    import scala.language.experimental.macros

    def keys(): ℕ = macro modifiers
  }

  def modifiers(c: Context)(): c.Expr[ℕ] =
  {
    constant(parse)(c)
  }
}

//****************************************************************************
