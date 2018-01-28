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

package com.wolery
package owl
package macros

import scala.reflect.macros.blackbox.Context

//****************************************************************************

object constant
{
  type Parser[α] = String ⇒ Either[α, String]

  def apply[α: c.universe.Liftable](parse: Parser[α])(c: Context): c.Expr[α] =
  {
    import c.universe._                                  //

    val text: String = c.prefix.tree match               //
    {
      case Apply(_,Apply(_,Literal(Constant(s: String))::Nil)::Nil) ⇒ s
    }

    c.Expr(q"${
      parse(text) match                                  //
      {
        case Left (value) ⇒ value                        //
        case Right(error) ⇒ c.abort(c.enclosingPosition, //
                                    error)               //
      }}")
  }
}

//****************************************************************************
