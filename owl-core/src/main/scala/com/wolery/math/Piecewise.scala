//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : Header:
//*
//*
//*  Purpose : https://en.wikipedia.org/wiki/Piecewise.
//*
//*
//*  Comments: This file uses a tab size of 2 spaces.
//*                                                                     0-0
//*                                                                   (| v |)
//**********************************************************************w*w***

package com.wolery
package math

import scala.collection.Searching._

/**
 * @author Jonathon Bell
 */
object Piecewise
{
  /**
   *
   */
  def apply[α: Ordering,β](min: α ⇒ β,pieces: (α,α ⇒ β)*): Function[α,β] = new Function[α,β]
  {
    val (v,p) =
    {
      val (v,p) = pieces.unzip

      (v.to,(min +: p).to)
    }

    def apply(a: α): β = v.search(a) match
    {
      case Found(i)          ⇒ p(i+1)(a)
      case InsertionPoint(i) ⇒ p(i)  (a)
    }
  }

  /**
   *
   */
  def apply[α: Ordering,β](max: β,pieces: (α,β)*): Function[α,β] =
  {
    val pairs = for ((a,b) ← pieces) yield
                     (a, (_: α) ⇒ b)
    apply((_: α) ⇒ max,pairs: _*)
  }
}

//****************************************************************************
