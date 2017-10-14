//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : Header:
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
package macros

import scala.reflect.macros.blackbox
import scala.language.experimental.{macros ⇒ mm}

//****************************************************************************

object macros
{
  implicit class OctallerContext(sc: StringContext)
  {
    def o(args:Any*): Int = macro octal
    def b(): Int = macro hex
  }

  def octal(c: blackbox.Context)(args:Any*): c.Expr[Int] =
    oImpl(s =>Integer.decode("0" + s).toInt)(c)
  def hex  (c: blackbox.Context)(): c.Expr[Int] =
    oImpl(_=>78)(c)

  def oImpl(parse: String ⇒ Int)(c: blackbox.Context): c.Expr[Int] =
  {
    import c.universe._

    c.Expr(q"${

      c.prefix.tree match
      {
        case Apply(_,Apply(_,Literal(Constant(text: String))::Nil)::Nil) ⇒

          parse(text)

        case _ ⇒ c.abort(c.enclosingPosition,"Bad integer literal.")
      }

    }")
  }

  def constant(parse: String ⇒ Int)(c: blackbox.Context): c.Expr[Int] =
  {
    import c.universe._

    c.Expr(q"${

      c.prefix.tree match
      {
        case Apply(_,Apply(_,Literal(Constant(text: String))::Nil)::Nil) ⇒

          parse(text)

        case _ ⇒ c.abort(c.enclosingPosition,"Bad integer literal.")
      }

    }")
  }
}

//****************************************************************************
