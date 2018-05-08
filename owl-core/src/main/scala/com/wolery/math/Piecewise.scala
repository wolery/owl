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
import scala.reflect.ClassTag

/**
 * @author Jonathon Bell
 */
object Piecewise
{
  /**
   *
   */
  def apply[α: Ordering: ClassTag,β](min: α ⇒ β,pieces: (α,α ⇒ β)*): Function[α,β] = new Function[α,β]
  {
    val (v,p) =
    {
      val (v,p) = pieces.unzip

      (v.toArray,(min +: p).toArray)
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
  def apply[α: Ordering: ClassTag,β](max: β,pieces: (α,β)*): Function[α,β] =
  {
    val pairs = for ((a,b) ← pieces) yield
                     (a, (_: α) ⇒ b)
    apply((_: α) ⇒ max,pairs: _*)
  }
}

//****************************************************************************
